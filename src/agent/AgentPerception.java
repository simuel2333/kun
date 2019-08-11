package agent;

import java.util.ArrayList;
import java.util.List;

import demo.Client;
import map.Map;
import map.MapElement;
import map.Player;
import map.Power;

/**
 * @category ��֪
 * @author hc
 *
 */
public class AgentPerception {
	public Player player;
	public Client client;

	public AgentPerception(Player player) {
		this.player = player;
	}

	/**
	 * �жϼ����Ƿ������ƶԾ�
	 * 
	 * @return
	 */
	public boolean isAnvantage() {
		return this.client.isAnvantage();
	}

	/**
	 * ���ݵ�ǰ����Ѱ���������
	 */
	public List<Power> findNearestPowers(Map map) {
//		int vision = map.getVision();
		// ����
		int x = -1;
		int y = -1;
		List<Power> np = new ArrayList<Power>();
		// Ѱ�����������
		// ��+1~+vision�������Ƿ�������
		f1: for (int i = 1; i <= map.getHeight(); i++) {
			int count = (2*i+1)* (2*i+1);
			f2: for (int j = 0; j < count; j++) {
				x = this.player.x - i + (j % (i * 2 + 1));
				y = this.player.y - i + (j / (i * 2 + 1));
//				if(map.roundId == 0 && this.player.getId() == 4) {
//					System.err.println("player:"+"("+this.player.x+","+this.player.y+"); pointer:("+x+","+y+")");
//				}
				if (x < 0 || y < 0 || x >= map.getWidth() || y >= map.getWidth()) {
					continue f2;
				}
					
				if (map.scene[x][y] instanceof Power) {
					np.add((Power) map.scene[x][y]);
				}
			}

			if (np.size() > 0)
				break f1;
		}
//		System.err.println("roundId:" + map.roundId + ",playerId:" + this.player.getId() + ",power" + np);

		return np;
	}

	public Power findMaxPointPower(Map map) {
		List<Power> np = this.findNearestPowers(map);
		if (np.size() == 0)
			return null;
		Power maxPower = null;
		for (Power power : np) {
			if (maxPower == null) {
				maxPower = power;
			} else if (maxPower.point < power.point) {
				maxPower = power;
			}
		}
		return maxPower;
	}
}
