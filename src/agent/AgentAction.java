package agent;

import java.util.Queue;

import map.Map;
import map.Player;

/**
 * @category ÐÐÎª
 * @author hc
 *
 */
public class AgentAction {
	public AgentBrain agentBrain;
	public Player player;
	public AgentAction(AgentBrain agentBrain) {
		this.agentBrain = agentBrain;
		this.player = this.agentBrain.player;
	}
	
	public void setPlayerMoves(Map map) {
		Queue<String> moves = this.agentBrain.findNearestPath(map);
		this.player.moves = moves;
	}
	
}
