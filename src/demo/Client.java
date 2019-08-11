package demo;

import java.util.ArrayList;
import java.util.List;

import agent.AgentAction;
import agent.AgentBrain;
import agent.AgentPerception;
import cmd.Action;
import cmd.RoundAction;
import map.Map;
import map.Meteor;
import map.Player;
import map.Power;
import map.Score;
import map.Tunnel;
import map.Wormhole;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author admin
 *
 */
public class Client {
	private int team_id = 0;
	private String team_name = "";
	public Team self = null;
	public Team enemy = null;
	private int roundId = 0;
	private String mode = null;
	private int moveIdx = 0;
	private List<String> moves = new ArrayList<String>();
	private List<Player> players = new ArrayList<Player>();
	private Map map;
	public int vision;

	public Client(int team_id, String team_name) {
		this.team_id = team_id;
		this.team_name = team_name;
		moves.add("up");
		moves.add("right");
		moves.add("down");
		moves.add("left");
	}

	public void legStart(JSONObject data) {
		System.out.println("leg start");

		JSONObject mapData = data.getJSONObject("map");
		int width = mapData.getInt("width");
		int height = mapData.getInt("height");
		this.vision = mapData.getInt("vision");
		System.out.printf("map width:%d, map height %d, map vision %d\n", width, height, vision);
		this.map = new Map(width, height);

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
					this.players.add(player);
				}
				this.map.players.add(player);
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
		if (this.self.getForce() == this.mode) {
			return true;
		} else {
			return false;
		}
	}

	public RoundAction act() {

		List<Action> actions = new ArrayList<Action>();
//		int idx = getIdx();
		for (Player player : this.players) {
			AgentPerception agentPerception = new AgentPerception(player);
			AgentBrain agentBrain = new AgentBrain(agentPerception);
			AgentAction agentAction = new AgentAction(agentBrain);
			agentAction.setPlayerMoves(map);
			String to = "";
			try {
				to = player.moves.peek();
			} catch (Exception e) {

			}
			if (to == "") {
				to = this.randomStep(player);
			}
			actions.add(new Action(player.getTeam(), player.getId(), to));
		}

		RoundAction roundAction = new RoundAction(this.roundId, actions);
		return roundAction;
	}

	public String randomStep(Player player) {

		int x;
		int y;
		String dir = "";
		do {
			int r = (int) (Math.random() * 4);
			dir = this.moves.get(r);
			x = player.x;
			y = player.y;
			switch (dir) {
			case "up":
				y--;
				break;
			case "down":
				y++;
				break;
			case "left":
				x--;
				break;
			case "right":
				x++;
			}
//			System.err.println(player.getId() + " " + dir + " ,(" + x + "," + y + ")"+"height:"+this.map.getHeight());
		} while (x < 0 || y < 0 || x >= this.map.getWidth() || y >= this.map.getHeight());

		return dir;
	}

}
