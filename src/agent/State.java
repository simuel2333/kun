package agent;

public class State {
	private int state = -1;
	public static final int RUN_AWAY = 0;
	public static final int CATCH = 1;
	public static final int EAT_POWER = 2;
	private int preState = -1;
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.preState = this.state;
		this.state = state;
	}

	public boolean isChange() {
		if(this.preState == this.state) {
			return false;
		} else {
			return true;
		}
	}
	
}
