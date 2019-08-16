package agent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import demo.Client;
import demo.Constant;
import demo.Demo;
import map.GameMap;
import map.MapElement;
import map.Meteor;
import map.Player;
import map.Power;
import map.Tunnel;
import map.Wormhole;

/**
 * @category 决策
 * @author admin
 *
 */
public class AgentBrain {
	public AgentPerception perception;
	public Player player;

	public AgentBrain(AgentPerception perception) {
		this.perception = perception;
		this.player = this.perception.player;
	}

	public Queue<String> findNearestPath(GameMap map) {
		Power maxPower = this.perception.findMaxPointPower(map);
//		System.err.println("roundId:" + map.roundId + ",playerId:" + this.player.getId() + ",power" + maxPower);
		if (maxPower == null)
			return null;
		Player p = this.perception.player;
		int space_x = maxPower.x - p.x;
		int space_y = maxPower.y - p.y;
		Queue<String> path = this.calcPathBySpace(space_x, space_y);
		return path;
	}

	public Queue<String> calcPathBySpace(int space_x, int space_y) {
		Queue<String> path = new LinkedList<String>();
		String dir = "";
		if (space_x > 0) {
			dir = Constant.RIGHT;
		} else {
			dir = Constant.LEFT;
		}
		for (int i = 0; i < Math.abs(space_x); i++) {
			path.offer(dir);
		}
		if (space_y > 0) {
			dir = Constant.DOWN;
		} else {
			dir = Constant.UP;
		}

		for (int i = 0; i < Math.abs(space_y); i++) {
			path.offer(dir);
		}
		return path;
	}

	public String findPowerNextMove(GameMap map) {
		Power maxPower = this.perception.findMaxPointPower(map);
		String dir = this.calcNextMoveByTarget(map, maxPower);
		return dir;
	}

	public String findWormholeNextMove(GameMap map) {
		Wormhole target = this.perception.findNearestWormhole(map);
		String dir = this.calcNextMoveByTarget(map, target);
		System.err.println(map.roundId + ", " + this.player.getId() + ", " + target + ", " + dir);
		return dir;
	}

	public String calcNextMoveByTarget(GameMap map, MapElement target) {
		String dir = "";
		if (target != null) {
			Player p = this.perception.player;
			int space_x = target.x - p.x;
			int space_y = target.y - p.y;
			if (space_x > 0) {
				dir = Constant.RIGHT;
			} else if (space_x < 0) {
				dir = Constant.LEFT;
			} else if (space_y > 0) {
				dir = Constant.DOWN;
			} else if (space_y < 0) {
				dir = Constant.UP;
			}
		}

		if (this.isBlockedByMeteor(map, dir)) {
			if (dir == Constant.RIGHT) {
				String[] moves = { Constant.UP, Constant.DOWN, Constant.LEFT };
				dir = this.randomStepByMoves(map, moves);
			} else if (dir == Constant.LEFT) {
				String[] moves = { Constant.UP, Constant.DOWN, Constant.RIGHT };
				dir = this.randomStepByMoves(map, moves);
			} else if (dir == Constant.DOWN) {
				String[] moves = { Constant.UP, Constant.LEFT, Constant.RIGHT };
				dir = this.randomStepByMoves(map, moves);
			} else if (dir == Constant.UP) {
				String[] moves = { Constant.DOWN, Constant.LEFT, Constant.RIGHT };
				dir = this.randomStepByMoves(map, moves);
			}
		}

		return dir;
	}

	public String randomStepByMoves(GameMap map, String[] moves) {
		String dir = "";
		int x;
		int y;
		do {
			int ran = (int) (Math.random() * moves.length);
			dir = moves[ran];
			x = this.player.x;
			y = this.player.y;
			if (dir.equals(Constant.UP)) {
				y--;
			} else if (dir.equals(Constant.DOWN)) {
				y++;
			} else if (dir.equals(Constant.LEFT)) {
				x--;
			} else {
				x++;
			}
		} while (map.isOutOfMap(x, y) || map.scene[x][y] instanceof Meteor);
		return dir;
	}

	public String randomStep(GameMap map) {
		String[] moves = { Constant.UP, Constant.DOWN, Constant.LEFT, Constant.RIGHT };
		String dir = this.randomStepByMoves(map, moves);
		return dir;
	}

	/**
	 * 判断前进的道路是否被陨石阻挡
	 * 
	 * @param map
	 * @param move
	 * @return
	 */
	public boolean isBlockedByMeteor(GameMap map, String move) {
		boolean isBlocked = false;
		int x = this.player.x;
		int y = this.player.y;
		if (move.equals(Constant.UP)) {
			if (map.scene[x][y - 1] instanceof Meteor)
				isBlocked = true;
		} else if (move.equals(Constant.DOWN)) {
			if (map.scene[x][y + 1] instanceof Meteor)
				isBlocked = true;
		} else if (move.equals(Constant.LEFT)) {
			if (map.scene[x - 1][y] instanceof Meteor)
				isBlocked = true;
		} else {
			if (map.scene[x + 1][y] instanceof Meteor)
				isBlocked = true;
		}
		return isBlocked;
	}

	public Queue<String> findPathByAStart(GameMap map, MapElement target) {
		return this.findPathByAStart(map, this.player, target);
	}
	
	public Queue<String> findPathByAStart(GameMap map,Player player,MapElement target) {
		map.clearPrevisou();
		Queue<String> path = new LinkedList<String>();
		Stack<String> stack = new Stack<String>();
		List<MapElement> open = new LinkedList<MapElement>();
		List<MapElement> closed = new LinkedList<MapElement>();
		MapElement originalSquare = player;
		originalSquare.G = 0;
		originalSquare.H = this.calcDistance(originalSquare, target);
		open.add(originalSquare);
		do {
			MapElement currentSquare = this.getTheLowestFSquare(open);
			closed.add(currentSquare);
			open.remove(currentSquare);
			if (closed.contains(target)) {
				// PATH FOUND
				while (target.previous != null) {
					MapElement previous = target.previous;
					if (target.previous instanceof Tunnel) {
						previous = ((Tunnel) target.previous).export;
					}
					if (target.previous instanceof Wormhole) {
						previous = ((Wormhole) target.previous).bro;
					}
					if (previous.y + 1 == target.y) { // previous 在 target的上方
						stack.push(Constant.DOWN);
					} else if (previous.y - 1 == target.y) {
						stack.push(Constant.UP);
					} else if (previous.x + 1 == target.x) {
						stack.push(Constant.RIGHT);
					} else {
						stack.push(Constant.LEFT);
					}
					target = target.previous;
				}
				while (!stack.isEmpty()) {
					path.offer(stack.pop());
				}
				break;
			}
			List<MapElement> adjacentSquares = this.walkableAdjacentSquares(map, currentSquare);

			for (MapElement square : adjacentSquares) {
				if (closed.contains(square))
					continue;
				if (!open.contains(square)) {
					this.computeScore(square, currentSquare, target);
					square.previous = currentSquare;
					open.add(square);
				} else {
					int currentG = currentSquare.G + 1;
					if ((currentG + square.H) < square.getF()) {
						square.G = currentG;
						square.previous = currentSquare;
					}
				}
			}

		} while (open.size() > 0);
		long end = System.currentTimeMillis();
		return path;
	}

	public List<MapElement> walkableAdjacentSquares(GameMap map, MapElement currentSquare) {
		List<MapElement> adjacentSquares = new LinkedList<MapElement>();
		int x = currentSquare.x;
		int y = currentSquare.y;
		if (currentSquare instanceof Tunnel) {
			x = ((Tunnel) currentSquare).export.x;
			y = ((Tunnel) currentSquare).export.y;
		}
		if (currentSquare instanceof Wormhole) {
			x = ((Wormhole) currentSquare).bro.x;
			y = ((Wormhole) currentSquare).bro.y;
		}
		// 上
		if (y != 0 && !(map.scene[x][y - 1] instanceof Meteor)) {
			MapElement tas = map.scene[x][y - 1];
			if (tas instanceof Tunnel && map.scene[x][y] != ((Tunnel) tas).export) {
				adjacentSquares.add(tas);
			} else if (!(tas instanceof Tunnel)) {
				adjacentSquares.add(tas);
			}
		}
		// 下
		if (y != map.getHeight() - 1 && !(map.scene[x][y + 1] instanceof Meteor)) {
			MapElement tas = map.scene[x][y + 1];
			if (tas instanceof Tunnel && map.scene[x][y] != ((Tunnel) tas).export) {
				adjacentSquares.add(tas);
			} else if (!(tas instanceof Tunnel)) {
				adjacentSquares.add(tas);
			}
		}
		// 左
		if (x != 0 && !(map.scene[x - 1][y] instanceof Meteor)) {
			MapElement tas = map.scene[x - 1][y];
			if (tas instanceof Tunnel && map.scene[x][y] != ((Tunnel) tas).export) {
				adjacentSquares.add(tas);
			} else if (!(tas instanceof Tunnel)) {
				adjacentSquares.add(tas);
			}
		}
		// 右
		if (x != map.getWidth() - 1 && !(map.scene[x + 1][y] instanceof Meteor)) {
			MapElement tas = map.scene[x + 1][y];
			if (tas instanceof Tunnel && map.scene[x][y] != ((Tunnel) tas).export) {
				adjacentSquares.add(tas);
			} else if (!(tas instanceof Tunnel)) {
				adjacentSquares.add(tas);
			}
		}

		return adjacentSquares;
	}

	private void computeScore(MapElement square, MapElement currentSquare, MapElement target) {
		square.G = currentSquare.G + 1;
		if (square instanceof Tunnel) {
			square.H = this.calcDistance(((Tunnel) square).export, target);
		} else if (square instanceof Wormhole) {
			if (square == target) {
				square.H = 0;
			} else {
				square.H = this.calcDistance(((Wormhole) square).bro, target);
			}
		} else {
			square.H = this.calcDistance(square, target);
		}
	}

	public int calcDistance(MapElement m1, MapElement m2) {
		return Math.abs(m1.x - m2.x) + Math.abs(m1.y - m2.y);
	}

	/**
	 * 找到open中F值最小的Square
	 * 
	 * @param open
	 * @return
	 */
	private MapElement getTheLowestFSquare(List<MapElement> open) {
		if (open.size() == 1) {
			return open.get(0);
		} else {
			MapElement lowest = open.get(open.size() - 1);
			for (int i = open.size() - 2; i >= 0; i--) {
				if (lowest.getF() > open.get(i).getF())
					lowest = open.get(i);
			}
			return lowest;
		}
	}

	public void catchEnemy(GameMap map) {
		Player maxScoreEnemy = this.perception.findMaxScoreEnemy(map);
		//没发现敌人
		if(maxScoreEnemy == null) return;
		
		Client client = Client.getInstance();
		for(Player player : map.selfPlayers) {
			client.moveMap.put(Integer.valueOf(player.getId()), this.findPathByAStart(map, player, maxScoreEnemy));
		}
	}

	public Queue<String> avoidEnemy(GameMap map) {
		Queue<String> path = new LinkedList<String>();
		int avoid_distance = 3;
		List<Player> enemies = this.perception.findNearestEnemies(map, avoid_distance);
		if (!enemies.isEmpty()) { // 需要躲避
			List<MapElement> adjenct_player = this.walkableAdjacentSquares(map, this.player);
			List<MapElement> adjenct_enemies = new LinkedList<MapElement>();
			for (Player enemy : enemies) {
				if (adjenct_enemies.isEmpty()) {
					adjenct_enemies = this.walkableAdjacentSquares(map, enemy);
				} else {
					List<MapElement> temp = this.walkableAdjacentSquares(map, enemy);
					for (MapElement square : temp) {
						if (!adjenct_enemies.contains(square))
							adjenct_enemies.add(square);
					}
				}
			}
			for (MapElement square : adjenct_enemies) {
				if (adjenct_player.contains(square))
					adjenct_player.remove(square);
			}
			if (adjenct_player.size() > 0) {
				List<String> moves = new ArrayList<String>();
				for (MapElement square : adjenct_player) {
					if ((this.player.y - 1) == square.y) {
						moves.add(Constant.UP);
					} else if ((this.player.y + 1) == square.y) {
						moves.add(Constant.DOWN);
					} else if ((this.player.x - 1) == square.y) {
						moves.add(Constant.LEFT);
					} else {
						moves.add(Constant.RIGHT);
					}
				}
				String dir = this.findBestAvoidWay(map, enemies, moves);
				path.offer(dir);
			}
		}
		return path;
	}

	/**
	 * 1从moves中选择一个方向前进
	 * 
	 * @param map
	 * @param enemies
	 * @param moves
	 * @return
	 */
	public String findBestAvoidWay(GameMap map, List<Player> enemies, List<String> moves) {
		String dir = "";
		if (moves.isEmpty()) {
			dir = this.randomStep(map);
		} else if (moves.size() == 1) {
			dir = moves.get(0);
		} else {
			int maxValue = 0;
			for (String move : moves) {
				if (dir.equals("")) {
					dir = move;
					maxValue = this.awayValue(enemies, dir);
				} else {
					if (maxValue < this.awayValue(enemies, move))
						dir = move;
				}
			}
		}
		return dir;
	}

	/**
	 * 沿着dir方向原理敌人的值
	 * 
	 * @param enemies
	 * @param dir
	 * @return
	 */
	private int awayValue(List<Player> enemies, String dir) {
		int value = 0;
		MapElement tmp = new MapElement(this.player.x, this.player.y);
		if (dir.equals(Constant.UP)) {
			tmp.y--;
		} else if (dir.equals(Constant.DOWN)) {
			tmp.y++;
		} else if (dir.equals(Constant.LEFT)) {
			tmp.x--;
		} else {
			tmp.x++;
		}
		for (Player enemy : enemies) {
			value += this.calcDistance(enemy, tmp);
		}
		return value;
	}
}
