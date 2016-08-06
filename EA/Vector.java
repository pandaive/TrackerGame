import java.util.Random;

public class Vector implements Comparable<Vector> 
{

	String[] vector;
	int fitness;
	int generation;
	
	public Vector(int weights, int biasWeights, int taus, int gains)
	{
		vector = new String[weights+biasWeights+taus+gains];
		init(weights, biasWeights, taus, gains);
		fitness = 0;
		generation = 0;
	}
	
	public Vector(String[] v) {
		vector = v;
		fitness = 0;
		generation = 0;
	}
	
	public Vector(int[] v) {
		this.vector = new String[v.length];
		for (int i = 0; i < v.length; i++)
			vector[i] = getBinary(v[i]);
	} 
	
	private void init(int weights, int biasWeights, int taus, int gains)
	{
		int iter = 0;
		int iterStop = iter + weights;
		for (int i = iter; i < iterStop; i++)
			vector[i] = getBinary(getRandom(0.0, 10.0)-50+120);
		iter = iterStop;
		iterStop = iter + biasWeights;
		for (int i = iter; i < iterStop; i++)
			vector[i] = getBinary(getRandom(0.0, 10.0)-100+120);
		iter = iterStop;
		iterStop = iter + taus;
		for (int i = iter; i < iterStop; i++)
			vector[i] = getBinary(getRandom(Main.tauMin, Main.tauMax)+120);
		iter = iterStop;
		iterStop = iter + gains;
		for (int i = iter; i < iterStop; i++)
			vector[i] = getBinary(getRandom(Main.gainMin, Main.gainMax)+120);
			
	}
	
	private String getBinary(int x) {
		return Integer.toBinaryString(x);
	}
	
	public static int[] vectorToInt(String[] toConvert){
		int[] vectorArray = new int[toConvert.length];
		for (int i = 0; i < toConvert.length; i++)
			vectorArray[i] = Integer.parseInt(toConvert[i], 2);
		return vectorArray;
	}
	
	public int getRandom(double min, double max) {
		Random random = new Random();
		int range = (int)(max - min + 1);
		double rand = (double) random.nextInt(range)+min + random.nextDouble();
		rand = (rand > max ? rand-1.0 : rand);
		return (int)(rand*10.0d);
		
	}

	
	@Override
	public int compareTo(Vector o) 
	{
		// TODO Auto-generated method stub
		return this.fitness - o.fitness;
	}
}