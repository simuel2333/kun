package demo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import map.MapElement;
import map.Power;
import map.Tunnel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test {
	static MapElement[][] a = new MapElement[2][2];
	public static void main(String[] args) {
//		String str = "{'power':[{x:5,y:2,point:1},{x:5,y:5,point:2}]}";
//		JSONObject json = null;
//		try {
//			json = JSONObject.fromObject(str);
//		} catch (Exception e) {
//			System.out.println(e.toString());
//		}
//		JSONArray powerArray = json.getJSONArray("power");
//		MapElement[] mp = new MapElement[100];
//		for (int i = 0; i < powerArray.size(); i++) {
//			JSONObject object = powerArray.getJSONObject(i);
//			Power power = new Power(object);
//			mp[i] = power;
//		}
//		System.out.println(mp[0] instanceof Tunnel);
		int r = (int) (Math.random() * 4);
		System.out.println(r);
		
//		String dir = "left";
//		List<String> path = new ArrayList<String>();
//		path.add(dir);
//		dir = "right";
//		path.add(dir);
//		System.out.println(path);
		System.out.println(true ^ true);
	}

	public static void sortR(List<Rank> rr) {
		rr.sort(new Comparator<Rank>() {

			@Override
			public int compare(Rank o1, Rank o2) {
				// TODO Auto-generated method stub
				return o1.x - o2.x;
			}

		});
	}

}

class Rank {
	public int x;

	public Rank(int x) {
		this.x = x;
	}

	public String toString() {
		return this.x + "";
	}
}