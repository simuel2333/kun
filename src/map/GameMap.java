package map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import demo.Constant;

public class GameMap {

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

	public GameMap(int width, int height) {
		this.width = width;
		this.height = height;
		this.initScene();
		this.meteors = new ArrayList<Meteor>();
		this.tunnels = new ArrayList<Tunnel>();
		this.wormholes = new ArrayList<Wormhole>();
		this.powers = new LinkedList<Power>();
		this.players = new ArrayList<Player>();
	}

	private void initScene() {
		this.scene = new MapElement[width][height];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				this.scene[i][j] = new MapElement(i, j);
			}
		}
	}

	public void clear() {
		this.powers.clear();
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
//		this.tunnelExport();
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
	
	public void clearPrevisou() {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				this.scene[i][j].previous = null;
			}
		}
	}
	
	public void tunnelExport() {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				MapElement m = this.scene[i][j];
				if(m instanceof Tunnel) {
					System.err.println("in:"+m+",out:"+this.getExportFromTunnel((Tunnel) m));
				}
			}
		}
	}

	public MapElement getExportFromTunnel(Tunnel t) {
		MapElement export = t;
		while (export instanceof Tunnel) {
			export = this.getNextByTunnel((Tunnel) export);
		}
		return export;
	}

	public MapElement getNextByTunnel(Tunnel t) {
		if (t.direction.equals(Constant.UP) ) {
			return this.scene[t.x][t.y - 1];
		} else if (t.direction.equals(Constant.DOWN)) {
			return this.scene[t.x][t.y + 1];
		} else if (t.direction.equals(Constant.LEFT)) {
			return this.scene[t.x - 1][t.y];
		} else {
			return this.scene[t.x + 1][t.y];
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
