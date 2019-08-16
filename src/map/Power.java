package map;

import net.sf.json.JSONObject;

public class Power extends MapElement {

	public int point;
	public boolean isTarget; //是否被设为目标

	public Power(JSONObject object) {
		this.x = object.getInt("x");
		this.y = object.getInt("y");
		this.point = object.getInt("point");
		this.isTarget = false;
		System.out.printf("power x %d, y %d, point %d\n", this.x, this.y, this.point);

	}

	public String toString() {
		return "power:(" + this.x + "," + this.y + ") point:" + this.point;
	}

}
