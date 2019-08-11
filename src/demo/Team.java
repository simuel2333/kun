package demo;

import map.Player;
import map.Score;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Team {
	private int id;
	private int[] players;
	private String force;
	public Score score;

	public Team(JSONObject team) {
		this.id = team.getInt("id");
		System.out.printf("team id %d\n", this.id);

		JSONArray array = team.getJSONArray("players");
		this.players = new int[array.size()];
		for (int i = 0; i < players.length; i++) {
			players[i] = array.getInt(i);
			System.out.printf("player id %d\n", players[i]);
		}
		this.force = team.getString("force");
		System.out.printf("team force %s\n", this.force);
	}
	
	public void revivePlayer(Player player) {
		if(this.score.getLife() > 0 && player.getSleep() != 0) {
			player.setSleep(0);
			this.score.setLife(this.score.getLife()-1);
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int[] getPlayers() {
		return players;
	}

	public void setPlayers(int[] players) {
		this.players = players;
	}

	public void setForce(String force) {
		this.force = force;
	}

	public String getForce() {
		return this.force;
	}
}
