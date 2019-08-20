package agent;

import cmd.Action;
import demo.Client;
import map.Player;

/**
 * @category 行为
 * @author hc
 *
 */
public class AgentAction {
	public AgentBrain agentBrain;
	public Player player;
	public AgentPerception perception;
	public AgentAction(AgentBrain agentBrain) {
		this.agentBrain = agentBrain;
		this.player = this.agentBrain.player;
		this.perception = this.agentBrain.perception;
	}
	
	
	public Action playerAction() {
		Client client = Client.getInstance();
		int beginCatch = 75;
		int avoidDistance = 3;
		int round = client.roundId > 149 ? client.roundId - 149 : client.roundId;
		Integer playerId = this.player.getId();
		//吃能量 优势：回合数小于等于beginCatch或没发现敌人 劣势:敌人距离大于avoidDistance
		//逃跑 劣势:敌人距离小于avoidDistance
		//追击 优势:回合数大于beginCatch且发现敌人
		if(client.isAnvantage()) {
			if(round <= beginCatch || this.perception.findMaxScoreEnemy(client.map) == null) {
				client.stateMap.get(playerId).setState(State.EAT_POWER);
			} else {
				client.stateMap.get(playerId).setState(State.CATCH);
			}
		} else {
			if(this.perception.findNearestEnemies(client.map, avoidDistance).isEmpty()) {
				client.stateMap.get(playerId).setState(State.EAT_POWER);
			} else {
				client.stateMap.get(playerId).setState(State.RUN_AWAY);
			}
		}
		Action action = new Action(player.getTeam(), player.getId(), "");
		return action;
	}
	
}
