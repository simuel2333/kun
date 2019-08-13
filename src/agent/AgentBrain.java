package agent;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import demo.Constant;
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
			switch (dir) {
			case Constant.UP:
				y--;
				break;
			case Constant.DOWN:
				y++;
				break;
			case Constant.LEFT:
				x--;
				break;
			case Constant.RIGHT:
				x++;
			}
		} while (x < 0 || y < 0 || x >= map.getWidth() || y >= map.getHeight());
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
		switch (move) {
		case Constant.RIGHT:
			if (map.scene[x + 1][y] instanceof Meteor)
				isBlocked = true;
			break;
		case Constant.LEFT:
			if (map.scene[x - 1][y] instanceof Meteor)
				isBlocked = true;
			break;
		case Constant.UP:
			if (map.scene[x][y - 1] instanceof Meteor)
				isBlocked = true;
			break;
		case Constant.DOWN:
			if (map.scene[x][y + 1] instanceof Meteor)
				isBlocked = true;
			break;
		}
		return isBlocked;
	}

	public Queue<String> findPathByAStart(GameMap map, MapElement target) {
		map.clearPrevisou();
		Queue<String> path = new LinkedList<String>();
		Stack<String> stack = new Stack<String>();
		List<MapElement> open = new LinkedList<MapElement>();
		List<MapElement> closed = new LinkedList<MapElement>();
		MapElement originalSquare = this.player;
		originalSquare.G = 0;
		originalSquare.H = this.calcDistance(originalSquare, target);
		open.add(originalSquare);
		do {
			MapElement currentSquare = this.getTheLowestFSquare(open);
			closed.add(currentSquare);
			open.remove(currentSquare);
			if (closed.contains(target)) {
				if(target instanceof Wormhole && (target.x==6 || target.x == 13)){
					System.err.println(target);
				}
				// PATH FOUND
				while(target.previous != null) {
					if(target.previous.y + 1 == target.y) { //previous 在 target的上方
						stack.push(Constant.DOWN);
					} else if(target.previous.y - 1 == target.y) {
						stack.push(Constant.UP);
					} else if(target.previous.x + 1 == target.x) {
						stack.push(Constant.RIGHT);
					} else {
						stack.push(Constant.LEFT);
					}
					target = target.previous;
				}
				while(!stack.isEmpty()) {
					path.offer(stack.pop());
				}
				break;
			}
			List<MapElement> adjacentSquares = this.walkableAdjacentSquares(map, currentSquare, target);

			for (MapElement square : adjacentSquares) {
				if (closed.contains(square))
					continue;
				if (!open.contains(square)) {
					this.computeScore(square, currentSquare, target);
					square.previous = currentSquare;
					open.add(square);
				} else {
					int currentG = currentSquare.G + 1;
					if((currentG + square.H) < square.getF()) {
						square.G = currentG;
						square.previous = currentSquare;
					}
				}
			}

		} while (open.size() > 0);
		return path;
	}

	public List<MapElement> walkableAdjacentSquares(GameMap map, MapElement currentSquare, MapElement target) {
		List<MapElement> adjacentSquares = new LinkedList<MapElement>();
		int x = currentSquare.x;
		int y = currentSquare.y;
		// 上
		if (y != 0 && !(map.scene[x][y - 1] instanceof Meteor)) {
			adjacentSquares.add(this.findTureAdjacentSquare(map, currentSquare, map.scene[x][y - 1], target));
		}
		// 下
		if (y != map.getHeight() - 1 && !(map.scene[x][y + 1] instanceof Meteor)) {
			adjacentSquares.add(this.findTureAdjacentSquare(map, currentSquare, map.scene[x][y + 1], target));
		}
		// 左
		if (x != 0 && !(map.scene[x - 1][y] instanceof Meteor)) {
			adjacentSquares.add(this.findTureAdjacentSquare(map, currentSquare, map.scene[x - 1][y], target));
		}
		// 右
		if (x != map.getWidth() - 1 && !(map.scene[x + 1][y] instanceof Meteor)) {
			adjacentSquares.add(this.findTureAdjacentSquare(map, currentSquare, map.scene[x + 1][y], target));
		}

		return adjacentSquares;
	}

	/**
	 * 寻找真正的邻居方块，并设置GH值
	 * 
	 * @param map
	 * @param currentSquare
	 * @param square
	 * @param target
	 * @return
	 */
	private MapElement findTureAdjacentSquare(GameMap map, MapElement currentSquare, MapElement square,
			MapElement target) {
		MapElement tureAdjacentSquare = square;
		if (square instanceof Tunnel) {
			tureAdjacentSquare = map.getExportFromTunnel((Tunnel) square);
		}

		return tureAdjacentSquare;
	}

	private void computeScore(MapElement square, MapElement currentSquare, MapElement target) {
		square.G = currentSquare.G + 1;
		square.H = this.calcDistance(square, target);
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
}
