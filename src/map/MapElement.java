package map;
/**
 * 
 * @author admin
 *
 */
public class MapElement implements Comparable<MapElement>{
	public int x;
	public int y;
	public int G;
	public int H;
	
	public MapElement() {
		
	}
	
	public MapElement(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getF() {
		return this.G + this.H;
	}
	
	@Override
	public int compareTo(MapElement square) {
		return this.getF() - square.getF();
	}
}
