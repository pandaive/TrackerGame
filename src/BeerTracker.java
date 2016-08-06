import java.util.ArrayList;
import java.util.Random;

public class BeerTracker {

	final static int STANDARD = 1;
	final static int PULL = 2;
	final static int NO_WRAP = 3;
	
	final static int CAPTURE = 10;
	final static int AVOID = 10;
	
	private int mode;
	
	private int cellsHorizontal;
	private int cellsVertical;
	private int trackerSize;
	private int trackerX;

	private ArrayList<Object> objects;
	public boolean objectOn = false;
	
	Tracker tracker;
	double[] weights;
	double threshold;
	double[] taus;
	double[] gains;
	
	public static int direction = 0;
	
	Random random = new Random();
	
	public BeerTracker(int mode, int cellsHorizontal, int cellsVertical,
			int trackerSize, 
			double[] weights, double threshold, double[] taus, double[] gains) {
		this.mode = mode;
		this.cellsHorizontal = cellsHorizontal;
		this.cellsVertical = cellsVertical;
		this.trackerSize = trackerSize;
		
		this.weights = weights;
		this.threshold = threshold;
		this.taus = taus;
		this.gains = gains;
		
		objects = new ArrayList<Object>();
		trackerX = (cellsHorizontal/2)-2;
		tracker = new Tracker(mode, trackerX, trackerSize, cellsHorizontal,
				weights, threshold, taus, gains);
	}

	public void createObject(int maxSize){
		Random random = new Random();
		int size = random.nextInt(maxSize)+1;
		int x = random.nextInt(cellsHorizontal-size/2);
		Object o = new Object(x, 0, size, cellsVertical);
		o.on();
		objectOn = true;
		objects.add(o);
	}
	
	public void moveObject(){
		int i = objects.size()-1;
		objects.get(i).move();
		if (objects.get(i).getY() == cellsVertical-1) {
			objects.get(i).off();
			objectOn = false;
		}
	}
	
	public void moveTracker(int timeStep){
		int oldX = trackerX;
		int[] sensors = getSensorData();
		if (!objects.get(objects.size()-1).isPulled())
			if (isSmallerObject() && (mode!=PULL || !smallerObject())) {
				if (sensors[4] == 10)
					trackerX = tracker.move(random.nextInt(2));
				else if (sensors[0] == 10)
					trackerX = tracker.move(-random.nextInt(2));
				else
					trackerX = tracker.move(random.nextInt(3)-1);
			}
			else
				trackerX = tracker.move(timeStep, sensors);
		if (mode == PULL && oldX == trackerX && smallerObject()) {
			objects.get(objects.size()-1).pull();
		}
	}
	
	public int[] getSensorData(){
		if (!objectOn) {
			int[] toReturn = {0,0,0,0,0}; 
			return toReturn;
		}
		int[] sensor = new int[trackerSize];
		Object o = getCurrentObject();
		for (int i = 0; i < trackerSize; i++) {
			sensor[i] = 0;
			int x = (trackerX + i >= cellsHorizontal ? (trackerX+i)-cellsHorizontal : trackerX + i);
			for (int j = o.getX(); j < o.getX()+o.getSize(); j++) {
				if (x == j)
					sensor[i] = 10;
			}
		}
		return sensor;
	}
	
	public int getFitness(){
		int fitness = 0;
		Object o = getCurrentObject();
		if (o.isPulled())
			return 2*CAPTURE;
		if (o.getY() == cellsVertical-1) {
			int count = 0;
			for (int i = trackerX; i < trackerX+trackerSize; i++)
				for (int j = o.getX(); j < o.getX()+o.getSize(); j++)
					if (i == j)
						count++;
			if (o.getSize() >= trackerSize)
				fitness = count > 0 ? -5*AVOID : 1*AVOID;
			else
				fitness = (count == o.getSize() ? CAPTURE : -CAPTURE);
		}
		return fitness;
	}
	
	public void updateFitness(){
		tracker.updateFitness(getFitness());
	}
	
	public int getTrackerFitness(){
		return tracker.getFitness();
	}
	
	public Object getCurrentObject(){
		try {
		return objects.get(objects.size()-1);
		}catch(ArrayIndexOutOfBoundsException e) {
			createObject(6);
			return objects.get(objects.size()-1);
		}
	}
	
	public int getTrackerPosition(){
		return trackerX;
	}
	
	private boolean smallerObject(){
		Object o = getCurrentObject();
		if (o.getY() < cellsVertical) {
			int count = 0;
			for (int i = trackerX; i < trackerX+trackerSize; i++)
				for (int j = o.getX(); j < o.getX()+o.getSize(); j++)
					if (i == j)
						count++;
			//System.out.println(count + ", " + o.getSize() + ", " + trackerSize);
			if (count == o.getSize() && o.getSize() < trackerSize)
				return true;
		}
		return false;
	}
	
	private boolean isSmallerObject(){
		Object o = getCurrentObject();
		if (o.getY() < cellsVertical) {
			int count = 0;
			for (int i = trackerX; i < trackerX+trackerSize; i++)
				for (int j = o.getX(); j < o.getX()+o.getSize(); j++)
					if (i == j)
						count++;
			//System.out.println(count + ", " + o.getSize() + ", " + trackerSize);
			if (count > 0 && o.getSize() < trackerSize)
				return true;
		}
		return false;
	}
}