package agent;

import java.util.LinkedList;
import java.util.Queue;

import map.Map;
import map.Player;
import map.Power;

/**
 * @category ¾ö²ß
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

	public Queue<String> findNearestPath(Map map) {
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
			dir = "right";
		} else {
			dir = "left";
		}
		for (int i = 0; i < Math.abs(space_x); i++) {
			path.offer(dir);
		}
		if (space_y > 0) {
			dir = "down";
		} else {
			dir = "up";
		}

		for (int i = 0; i < Math.abs(space_y); i++) {
			path.offer(dir);
		}
		return path;
	}
}
