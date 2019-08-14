package demo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.MapElement;

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
//		int r = (int) (Math.random() * 4);
//		System.out.println(r);
		
//		String dir = "left";
//		List<String> path = new ArrayList<String>();
//		path.add(dir);
//		dir = "right";
//		path.add(dir);
//		System.out.println(path);
//		System.out.println(true ^ true);
//		int[] arr = {1,2,3,4};
//		String[] str = new String[4];
//		try {
//			System.out.println(str[3]);
//		} catch (ArrayIndexOutOfBoundsException e) {
//			// TODO: handle exception
//		}
		
		
//		Map<Integer, String> map = new HashMap<Integer, String>();
//		map.put(Integer.valueOf(1), "hahahah");
//		map.put(Integer.valueOf(2), "22222");
//		Integer id =Integer.valueOf(3);
//		System.out.println(map.get(id));
//		System.out.println(Constant.UP == new String("up"));
		
		Child c = new Child();
		Father c1 = c;
		System.out.println(c==c1);
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

class Father{
	
}

class Child extends Father{
	
}