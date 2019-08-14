package agent;

import java.util.ArrayList;
import java.util.List;

import demo.Client;
import map.GameMap;
import map.MapElement;
import map.Player;
import map.Power;
import map.Tunnel;
import map.Wormhole;

/**
 * @category 感知
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
	 * 判断己方是否处于优势对局
	 * 
	 * @return
	 */
	public boolean isAnvantage() {
		return this.client.isAnvantage();
	}

	public Wormhole findNearestWormhole(GameMap map) {
		List<Wormhole> whs = map.wormholes;
		Wormhole target = null;
		for (Wormhole wh : whs) {
			if(this.isReachable(map, wh)) {
				if (target == null) {
					target = wh;
				} else {
					if (this.calcDistance(target, this.player) > this.calcDistance(wh, this.player)) {
						target = wh;
					}
				}
			}
			
		}
		return target;
	}
	
	public boolean isReachable(GameMap map, MapElement e) {
		return !(this.isSurroundedByTunnel(map, e) ^ this.isSurroundedByTunnel(map, this.player));
	}

	public boolean isSurroundedByTunnel(GameMap map, MapElement e) {
		// 向左
		for (int x = e.x - 1; x >= 0; x--) {
			if(map.scene[x][e.y] instanceof Tunnel) break; //说明e左边有Tunnel
			if(x == 0) return false;
		}
		// 向右
		for (int x = e.x + 1; x < map.getWidth(); x++) {
			if(map.scene[x][e.y] instanceof Tunnel) break;//说明e右边有Tunnel
			if(x == map.getWidth()-1) return false;
		}
		// 向上
		for(int y = e.y -1; y >=0; y--) {
			if(map.scene[e.x][y] instanceof Tunnel) break;
			if(y == 0) return false;
		}
		// 向下
		for(int y = e.y +1; y < map.getHeight(); y++) {
			if(map.scene[e.x][y] instanceof Tunnel) break;
			if(y == map.getHeight()-1) return false;
		}
		return true;
	}

	public int calcDistance(MapElement m1, MapElement m2) {
		return Math.abs(m1.x - m2.x) + Math.abs(m1.y - m2.y);
	}

	/**
	 * 根据当前坐标寻找最近能量
	 */
	public List<Power> findNearestPowers(GameMap map) {
		int x = -1;
		int y = -1;
		List<Power> np = new ArrayList<Power>();
		// 寻找最近的能量
		// 看+1~+vision距离内是否有能量
		f1: for (int i = 1; i <= map.getHeight(); i++) {
			int count = (2 * i + 1) * (2 * i + 1);
			f2: for (int j = 0; j < count; j++) {
				x = this.player.x - i + (j % (i * 2 + 1));
				y = this.player.y - i + (j / (i * 2 + 1));
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

		return np;
	}

	public Power findMaxPointPower(GameMap map) {
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
	
	public List<Player> findNearestEnemies(GameMap map) {
		List<Player> nearestEnemies = new ArrayList<Player>();
		if(map.enemies.isEmpty()) return nearestEnemies;
		Player nearestEnemy = null;
		for(Player enemy : map.enemies) {
			if(nearestEnemy == null) {
				nearestEnemy = enemy;
			} else {
				if(this.calcDistance(this.player, nearestEnemy) > this.calcDistance(this.player, enemy)) {
					nearestEnemy = enemy;
				}
			}
		}
		nearestEnemies.add(nearestEnemy);
		for(Player enemy : map.enemies) {
			if(this.calcDistance(this.player, nearestEnemy) == this.calcDistance(this.player, enemy)) {
				if(enemy != nearestEnemy) nearestEnemies.add(enemy);
			}

		}
		return nearestEnemies;
	}
	
}
