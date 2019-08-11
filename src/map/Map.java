package map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Map {

	private int width;
	private int height;
	private int vision;
	public List<Meteor> meteors;
	public List<Tunnel> tunnels;
	public List<Wormhole> wormholes;
	public List<Power> powers;
	public List<Player> players;
	public int roundId;

	public MapElement[][] scene;

	public Map(int width, int height) {
		this.width = width;
		this.height = height;
		this.scene = new MapElement[width][height];
		this.meteors = new ArrayList<Meteor>();
		this.tunnels = new ArrayList<Tunnel>();
		this.wormholes = new ArrayList<Wormhole>();
		this.powers = new LinkedList<Power>();
		this.players = new ArrayList<Player>();
	}
	
	public void clear() {
//		this.powers.clear();
		this.players.clear();
	}

	public void UpdataStaticScene() {
		// Meteor
		for (Meteor m : this.meteors) {
			this.scene[m.x][m.y] = m;
		}
		// Tunnel
		for (Tunnel t : this.tunnels) {
			this.scene[t.x][t.y] = t;
		}
		// Wormhole
		for (Wormhole w : this.wormholes) {
			this.scene[w.x][w.y] = w;
		}
	}

	public void UpdateDynamicScene() {

		// Power
		for (Power p : this.powers) {
			this.scene[p.x][p.y] = p;
		}
		// Player
		for (Player p : this.players) {
			this.scene[p.x][p.y] = p;
		}
	}
	
	

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getVision() {
		return vision;
	}

	public void setVision(int vision) {
		this.vision = vision;
	}

}
