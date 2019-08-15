package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import agent.AgentBrain;
import agent.AgentPerception;
import cmd.Action;
import cmd.RoundAction;
import map.GameMap;
import map.MapElement;
import map.Meteor;
import map.Player;
import map.Power;
import map.Score;
import map.Tunnel;
import map.Wormhole;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author hc
 *
 */
public class Client {
	private int team_id = 0;
	private String team_name = "";
	public Team self = null;
	public Team enemy = null;
	public int roundId = 0;
	private String mode = null;
	private List<Player> players = new ArrayList<Player>();
	private GameMap map;
	public int vision;
	public Map<Integer, Queue<String>> moveMap = new HashMap<Integer, Queue<String>>();
	public Map<Integer, String> playerStatus = new HashMap<Integer, String>();


	public Client(int team_id, String team_name) {
		this.team_id = team_id;
		this.team_name = team_name;
	}

	public void legStart(JSONObject data) {
		System.out.println("leg start");

		JSONObject mapData = data.getJSONObject("map");
		int width = mapData.getInt("width");
		int height = mapData.getInt("height");
		this.vision = mapData.getInt("vision");
		System.out.printf("map width:%d, map height %d, map vision %d\n", width, height, vision);
		this.map = new GameMap(width, height);

		try {
			JSONArray meteorArray = mapData.getJSONArray("meteor");
			for (int i = 0; i < meteorArray.size(); i++) {
				JSONObject object = meteorArray.getJSONObject(i);
				Meteor meteor = new Meteor(object);
				this.map.meteors.add(meteor);
			}
		} catch (Exception e) {
			System.out.printf("donot get meteor");
		}

//		try {
//			JSONArray cloudArray = mapData.getJSONArray("cloud");
//			for (int i = 0; i < cloudArray.size(); i++) {
//				JSONObject object = cloudArray.getJSONObject(i);
//				Cloud cloud = new Cloud(object);
//			}
//		} catch (Exception e) {
//			System.out.printf("donot get cloud");
//		}

		try {
			JSONArray tunnelArray = mapData.getJSONArray("tunnel");
			for (int i = 0; i < tunnelArray.size(); i++) {
				JSONObject object = tunnelArray.getJSONObject(i);
				Tunnel tunnel = new Tunnel(object);
				this.map.tunnels.add(tunnel);
			}
		} catch (Exception e) {
			System.out.printf("donot get tunnel");
		}

		try {
			JSONArray wormholeArray = mapData.getJSONArray("wormhole");
			for (int i = 0; i < wormholeArray.size(); i++) {
				JSONObject object = wormholeArray.getJSONObject(i);
				Wormhole wormhole = new Wormhole(object);
				this.map.wormholes.add(wormhole);
			}
		} catch (Exception e) {
			System.out.printf("donot get wormhole");
		}
		this.findWoreholePair(this.map.wormholes); // 虫洞配对
		try {
			JSONArray teams = data.getJSONArray("teams");

			for (int i = 0; i < 2; i++) {
				JSONObject team = teams.getJSONObject(i);
				int team_id = team.getInt("id");
				if (this.team_id == team_id) {
					System.out.println("self team");
					this.self = new Team(team);
				} else {
					System.out.println("enemy team");
					this.enemy = new Team(team);
				}
			}
		} catch (Exception e) {
			System.out.printf("donot get teams");
		}
		this.map.UpdataStaticScene();
	}

	public void legEnd(JSONObject data) {
		System.out.println("leg end");

		try {
			JSONArray results = data.getJSONArray("teams");
			for (int i = 0; i < results.size(); i++) {
				Result result = new Result(results.getJSONObject(i));
			}
		} catch (Exception e) {
			System.out.printf("donot get legEnd teams");
		}
	}

	public void round(JSONObject data) {
		this.roundId = data.getInt("round_id");
		this.map.roundId = this.roundId;
		this.mode = data.getString("mode");
		System.out.printf("round %d, mode %s\n", this.roundId, this.mode);
		this.map.clear();
		try {
			JSONArray powerArray = data.getJSONArray("power");
			for (int i = 0; i < powerArray.size(); i++) {
				JSONObject object = powerArray.getJSONObject(i);
				Power power = new Power(object);
				this.map.powers.add(power);
			}
		} catch (Exception e) {
			System.out.println("donot get round power");
		}

		try {
			this.players.clear();
			JSONArray players = data.getJSONArray("players");
			for (int i = 0; i < players.size(); i++) {
				JSONObject object = players.getJSONObject(i);
				Player player = new Player(object);
				player.setForce(this.getPlayForce(player));
				if (player.getTeam() == this.team_id) {
					this.playerStatus.put(Integer.valueOf(player.getId()), Constant.ACTIVE);
					this.players.add(player);
				} else {
					this.map.enemies.add(player);
				}
				this.map.players.add(player);
			}
			//清除path
			for(Integer id : this.playerStatus.keySet()) {
				if(this.playerStatus.get(id).equals(Constant.SLEEP)) this.moveMap.put(id, null);
			}
			
		} catch (Exception e) {
			System.out.printf("donot get round players");
		}

		try {
			JSONArray scores = data.getJSONArray("teams");
			for (int i = 0; i < scores.size(); i++) {
				JSONObject object = scores.getJSONObject(i);
				Score score = new Score(object);
				if (this.self.getId() == score.getId()) {
					this.self.score = score;
				} else {
					this.enemy.score = score;
				}
			}
		} catch (Exception e) {
			System.out.printf("donot get round teams");
		}
		this.map.UpdateDynamicScene();
	}

	/**
	 * 查看player是哪种kun
	 * 
	 * @param p
	 * @return
	 */
	public String getPlayForce(Player p) {
		if (this.self.getId() == p.getId()) {
			return this.self.getForce();
		} else {
			return this.enemy.getForce();
		}
	}

	public void findWoreholePair(List<Wormhole> list) {
		for (int i = 0; i < list.size(); i++) {
			Wormhole wh = list.get(i);
			if (wh.bro != null)
				continue;
			for (int j = 0; j < list.size(); j++) {
				Wormhole wh2 = list.get(j);
				if (Math.abs(wh.getName().charAt(0) - wh2.getName().charAt(0)) == 32) {
					wh.bro = wh2;
					wh2.bro = wh;
					break;
				}
			}
		}
	}

	public boolean isAnvantage() {
		if (this.self.getForce().equals(this.mode)) {
			return true;
		} else {
			return false;
		}
	}

	public RoundAction act() {

		List<Action> actions = new ArrayList<Action>();
		for (Player player : this.players) {
			
			Integer playerId = Integer.valueOf(player.getId());
			
			AgentPerception agentPerception = new AgentPerception(player);
			AgentBrain agentBrain = new AgentBrain(agentPerception);
			if(this.moveMap.get(playerId) == null || this.moveMap.get(playerId).isEmpty()) {
				MapElement target = agentPerception.findMaxPointPower(this.map);
				if(target == null) {
					target = agentPerception.findNearestWormhole(this.map);
				}
				Queue<String> path = agentBrain.findPathByAStart(this.map, target);
				if(path.isEmpty()) {
					path.offer(agentBrain.randomStep(map));
//					System.err.println(this.roundId+",target:"+target+", player:"+player+",path:"+path);
				}
				this.moveMap.put(playerId, path);
//				System.err.println(this.roundId+","+target+",path:"+path);
			}
//			System.err.println("roundId:"+this.roundId+",playerId:"+playerId+", "+this.moveMap.get(playerId));
			if(this.isAnvantage()) {
				
			} else {
				agentBrain.avoidEnemy(map, this.moveMap.get(playerId));
			}
			String to = this.moveMap.get(playerId).poll();
			this.playerStatus.put(playerId, Constant.SLEEP);
			actions.add(new Action(player.getTeam(), player.getId(), to));
		}

		RoundAction roundAction = new RoundAction(this.roundId, actions);
		return roundAction;
	}



}
