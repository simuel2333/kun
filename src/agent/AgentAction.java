package agent;

import cmd.Action;
import demo.Client;
import map.Player;

/**
 * @category ��Ϊ
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
		//������ ���ƣ��غ���С�ڵ���beginCatch��û���ֵ��� ����:���˾������avoidDistance
		//���� ����:���˾���С��avoidDistance
		//׷�� ����:�غ�������beginCatch�ҷ��ֵ���
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
		if(client.moveMap.get(playerId) == null || client.moveMap.get(playerId).isEmpty() || client.stateMap.get(playerId).isChange()) {
			if(client.stateMap.get(playerId).getState() == State.EAT_POWER) {
				this.agentBrain.eatPower();
			} else if(client.stateMap.get(playerId).getState() == State.RUN_AWAY) {
				this.agentBrain.runAway();
			}
		}
		String to = client.moveMap.get(playerId).poll();
		Action action = new Action(player.getTeam(), player.getId(), to);
		return action;
	}
	
}
