package map;

import java.util.LinkedList;
import java.util.Queue;

import net.sf.json.JSONObject;

public class Player extends MapElement {
	private int id;
	private int team;
	public int score;
	private int sleep;
	private String force;
	public Queue<String> moves = new LinkedList<String>();

	public Player(JSONObject object) {
		this.id = object.getInt("id");
		this.team = object.getInt("team");
		this.x = object.getInt("x");
		this.y = object.getInt("y");
		this.score = object.getInt("score");
		this.sleep = object.getInt("sleep");
		System.out.printf("player id %d team %d x %d, y %d, score %d, sleep %d\n", this.id, this.team, this.x, this.y,
				this.score, this.sleep);
	}

	public int getId() {
		return this.id;
	}

	public int getTeam() {
		return this.team;
	}

	public String getForce() {
		return force;
	}

	public void setForce(String force) {
		this.force = force;
	}

	public int getSleep() {
		return sleep;
	}

	public void setSleep(int sleep) {
		this.sleep = sleep;
	}
	
	public String toString() {
		return "id:"+this.id +",("+this.x+","+this.y+")" + " F:"+ this.getF();
	}
	
	public boolean isEnemy(Player player) {
		if(player.team != this.team) {
			return true;
		} else {
			return false;
		}
	}


}
