import java.math.BigDecimal;
import java.math.RoundingMode;

public class Tracker {
	
	final static int LEFT = 0;
	final static int RIGHT = 1;
	
	private int x;
	private int size;
	private int cellsHorizontal;
	private int mode;
	private int fitness;
	
	NeuralNetwork neuralNetwork;
	
	public Tracker(int mode, int x, int size, int cellsHorizontal,
			double[] weights, double threshold, double[] taus, double[] gains){
		this.x = x;
		this.size = size;
		this.cellsHorizontal = cellsHorizontal;
		this.mode = mode;
		this.fitness = 0;
		
		neuralNetwork = new NeuralNetwork(weights, threshold, taus, gains, mode);
	}
	
	private int chooseDirection(int timeStep, int[] sensors){
		neuralNetwork.runNetwork(sensors, timeStep, x, cellsHorizontal-x);
		double[] output = neuralNetwork.getOutput();
		//int direction = (int) (output[0] * -1.0d + output[1]);
		int direction = new BigDecimal((Double.toString(output[0] * -1.0d + output[1]))).setScale(2,RoundingMode.HALF_UP).intValue();
		//System.out.println("direction: " + direction + " out1: " + output[0] + " out2: " + output[1]);
		return direction;
	}
	
	public int move(int timeStep, int[] sensors) {
		int direction = chooseDirection(timeStep, sensors);
		direction /= 2;
		direction = direction > 4 ? 4 : direction < -4 ? -4 : direction;
		BeerTracker.direction = direction;
		switch(mode) {
		case BeerTracker.STANDARD:
			if (direction < 0)
				x = (x+direction < 0 ? cellsHorizontal-1+(x+direction) : x+direction);
			else
				x = (x+direction >= cellsHorizontal ? direction-1 : x+direction);
			break;
		case BeerTracker.PULL:
			if (direction < 0)
				x = (x+direction < 0 ? cellsHorizontal-1+(x+direction) : x+direction);
			else
				x = (x+direction >= cellsHorizontal ? direction-1 : x+direction);
			break;
		case BeerTracker.NO_WRAP:
			if (direction < 0)
				x = (x+direction < 0 ? x-direction : x+direction);
			else
				x = (x+direction >= cellsHorizontal-1 ? x - direction : x+direction);
			break;
		}
		return x;
	}
	
	public int move(int direction) {
		direction = direction > 4 ? 4 : direction < -4 ? -4 : direction;
		switch(mode) {
		case BeerTracker.STANDARD:
			if (direction < 0)
				x = (x+direction < 0 ? cellsHorizontal-1+(x+direction) : x+direction);
			else
				x = (x+direction >= cellsHorizontal ? direction-1 : x+direction);
			break;
		case BeerTracker.PULL:
			if (direction < 0)
				x = (x+direction < 0 ? cellsHorizontal-1+(x+direction) : x+direction);
			else
				x = (x+direction >= cellsHorizontal ? direction-1 : x+direction);
			break;
		case BeerTracker.NO_WRAP:
			if (direction < 0)
				x = (x+direction < 0 ? x-direction : x+direction);
			else
				x = (x+direction >= cellsHorizontal-1 ? x - direction : x+direction);
			break;
		}
		return x;
	}
	
	public void updateFitness(int f) {
		this.fitness += f;
	}
	
	public int getFitness() {
		return this.fitness;
	}
}
