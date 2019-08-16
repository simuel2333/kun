package agent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

	public AgentPerception(Player player) {
		this.player = player;
	}

	/**
	 * 判断己方是否处于优势对局
	 * 
	 * @return
	 */
	public Wormhole findNearestWormhole(GameMap map) {
		List<Wormhole> whs = map.wormholes;
		Wormhole target = null;
		for (Wormhole wh : whs) {
			if (this.isReachable(map, wh)) {
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
			if (map.scene[x][e.y] instanceof Tunnel)
				break; // 说明e左边有Tunnel
			if (x == 0)
				return false;
		}
		// 向右
		for (int x = e.x + 1; x < map.getWidth(); x++) {
			if (map.scene[x][e.y] instanceof Tunnel)
				break;// 说明e右边有Tunnel
			if (x == map.getWidth() - 1)
				return false;
		}
		// 向上
		for (int y = e.y - 1; y >= 0; y--) {
			if (map.scene[e.x][y] instanceof Tunnel)
				break;
			if (y == 0)
				return false;
		}
		// 向下
		for (int y = e.y + 1; y < map.getHeight(); y++) {
			if (map.scene[e.x][y] instanceof Tunnel)
				break;
			if (y == map.getHeight() - 1)
				return false;
		}
		return true;
	}

	public int calcDistance(MapElement m1, MapElement m2) {
		return Math.abs(m1.x - m2.x) + Math.abs(m1.y - m2.y);
	}

	public Power findMaxPointPower(GameMap map) {
		if (map.powers.isEmpty())
			return null;
		AgentPerception ap = this;
		map.powers.sort(new Comparator<Power>() {

			@Override
			public int compare(Power p1, Power p2) {
				if (ap.calcDistance(ap.player, p1) != ap.calcDistance(ap.player, p2)) {
					return ap.calcDistance(ap.player, p1) - ap.calcDistance(ap.player, p2);
				} else {
					return p2.point - p1.point;
				}
			}

		});
		Power maxPower = null;
		for (Power power : map.powers) {
			if(!power.isTarget) {
				maxPower = power;
				maxPower.isTarget = true;
				break;
			}
		}
		return maxPower;
	}

	/**
	 * a找到距离为distance以内的所有敌军
	 * 
	 * @param map
	 * @return
	 */
	public List<Player> findNearestEnemies(GameMap map, int distance) {
		List<Player> nearestEnemies = new ArrayList<Player>();
		if (map.enemies.isEmpty())
			return nearestEnemies;
		for (Player enemy : map.enemies) {
			if (this.calcDistance(this.player, enemy) <= distance) {
				nearestEnemies.add(enemy);
			}

		}
		return nearestEnemies;
	}
	/**
	 * @param map
	 * @return 地图上所有与当前player重叠的己方player
	 */
	public List<Player> overlapPlayers(GameMap map, List<Player> players) {
		List<Player> overlapPlayers = new ArrayList<Player>();
		for(int i = 0; i < players.size(); i++) {
			if(this.isOverlap(this.player, players.get(i)));
		}
		return overlapPlayers;
	}
	
	public boolean isOverlap(MapElement m1, MapElement m2) {
		if(m1.x == m2.x && m1.y == m2.y) {
			return true;
		} else {
			return false;
		}
	}

	public Player findMaxScoreEnemy(GameMap map) {
		Player maxScorePalyer = null;
		if(map.enemies.isEmpty()) return null;
		for(Player enemy : map.enemies) {
			if(maxScorePalyer == null) {
				maxScorePalyer = enemy;
			} else if(maxScorePalyer.score < enemy.score){
				maxScorePalyer = enemy;
			}
		}
		return maxScorePalyer;
	}

}
