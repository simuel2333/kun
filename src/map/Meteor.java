package map;

import net.sf.json.JSONObject;

public class Meteor extends MapElement{

	public Meteor(JSONObject object) {
		this.x = object.getInt("x");
		this.y = object.getInt("y");
		System.out.printf("meteor x %d, y %d\n", this.x, this.y);
		
	}
	
	public String toString() {
		return "‘… Ø£∫(" + this.x + "," + this.y + ")";
	}
}
