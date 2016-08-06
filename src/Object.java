
public class Object {
	
	private int x, y;
	private int size;
	private int caught;
	private boolean isOn = false;
	private boolean pull = false;
	private int cellsVertical;
	
	public Object(int x, int y, int size, int cellsVertical){
		this.x = x;
		this.y = y;
		this.size = size;
		this.cellsVertical = cellsVertical;
		caught = 0;
	}
	
	public void move() {
		if (pull) {
			this.y = cellsVertical-1;
			off();
		}
		else
			this.y++;
	}
	
	public void on() {
		this.isOn = true;
	}
	
	public void off() {
		this.isOn = false;
	}
	
	public void pull(){
		this.pull = true;
	}
	
	public boolean isPulled(){
		return pull;
	}
	
	public int getX() { return x; }
	
	public int getY() { return y; }
	
	public int getSize() { return size; }
	
	public boolean isCaught() { return caught!=0; }
}
