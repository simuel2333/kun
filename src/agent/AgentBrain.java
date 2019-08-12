package agent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import demo.Constant;
import map.GameMap;
import map.MapElement;
import map.Meteor;
import map.Player;
import map.Power;
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
		Queue<String> path = new LinkedList<String>();
		List<MapElement> open = new LinkedList<MapElement>();
		List<MapElement> closed = new LinkedList<MapElement>();
		MapElement originalSquare = this.player;
		open.add(originalSquare);
		do {
			MapElement currentSquare = this.getTheLowestFSquare(open);
			closed.add(currentSquare);
			open.remove(currentSquare);
			if(closed.contains(target)) {
				//PATH FOUND
				break;
			}
			List<MapElement> adjacentSquares = this.walkableAdjacentSquares(map, currentSquare);

//			foreach (aSquare in adjacentSquares) {
//		 
//				if ([closedList contains:aSquare]) { // if this adjacent square is already in the closed list ignore it
//					continue; // Go to the next adjacent square
//				}
//		 
//				if (![openList contains:aSquare]) { // if its not in the open list
//		 
//					// compute its score, set the parent
//					[openList add:aSquare]; // and add it to the open list
//		 
//				} else { // if its already in the open list
//		 
//					// test if using the current G score make the aSquare F score lower, if yes update the parent because it means its a better path
//		 
//				}

		
		} while (open.size() > 0);
		return path;
	}
	
	public List<MapElement> walkableAdjacentSquares(GameMap map,MapElement currentSquare) {
		List<MapElement> adjacentSquares = new LinkedList<MapElement>();
		//等待施工
		return adjacentSquares;
	}
	
	/**
	 * 找到open中F值最小的Square
	 * @param open
	 * @return
	 */
	private MapElement getTheLowestFSquare(List<MapElement> open) {
		if(open.size() == 1) {
			return open.get(0);
		} else {
			MapElement lowest = open.get(open.size()-1);
			for(int i=open.size()-2; i>=0; i--) {
				if(lowest.getF() > open.get(i).getF()) lowest = open.get(i);
			}
			return lowest;
		}
	}
}
