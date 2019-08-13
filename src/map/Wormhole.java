package map;

import net.sf.json.JSONObject;

public class Wormhole extends MapElement {

	private String name;
	public Wormhole bro = null;

	public Wormhole(JSONObject object) {
		this.x = object.getInt("x");
		this.y = object.getInt("y");
		this.name = object.getString("name");
		System.out.printf("wormhole x %d, y %d, name %s\n", this.x, this.y, this.name);

	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return "ºÚ¶´:(" + this.x + "," + this.y + ")";
	}

}
