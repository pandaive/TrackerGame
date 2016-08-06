import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EvolutionaryAlgorithm implements EAProblem {

	//adult selection
	static final int FULLGENERATIONREPLACEMENT = 0;
	static final int OVERPRODUCTIONREPLACEMENT = 1;
	static final int GENERATIANOALMIXING = 2;

	//parent selection
	static final int BESTFITNESS = 0;
	static final int FITNESSPROPORTIONATE = 1;
	static final int SIGMASCALING = 2;
	static final int TOURNAMENT = 3;
	
	static final int SCENARIO_1 = 1;
	static final int SCENARIO_2 = 2;
	static final int SCENARIO_3 = 3;
	
	int mode;
	
	static double mutationThreshold = 0.01;
	static double crossoverThreshold = 0.4;
	
	int populationNumber = 300;
	int generationsNumber = 30;
	int generation;
	
	AdultSelection adultSelection;
	ParentSelection parentSelection;
	Crossover crossover;
	Mutation mutation;
	Development development;
	
	List<Vector> parents;
	
	BeerTracker beerTracker;
	int trackerSize;
	int cellsHorizontal, cellsVertical;
	int maxSteps;
	double threshold = 0.1d;
	
	int trackerPos;
	
	int[] fitnessResults;
	double[][] weightResults;
	
	public EvolutionaryAlgorithm(int cellsHorizontal, int cellsVertical, int trackerSize, int maxSteps, int mode) throws FileNotFoundException, UnsupportedEncodingException {
		this.cellsHorizontal = cellsHorizontal;
		this.cellsVertical = cellsVertical;
		this.trackerSize = trackerSize;
		this.maxSteps = maxSteps;
		
		this.mode = mode;
		
		adultSelection = new AdultSelection(GENERATIANOALMIXING, populationNumber);
		parentSelection = new ParentSelection(BESTFITNESS);
		crossover = new Crossover();
		mutation = new Mutation();
		development = new Development(Main.nbAll);
		
		fitnessResults = new int[generationsNumber];
		weightResults = new double[generationsNumber][Main.nbAll];
		
		List<Vector> population = createPopulation(populationNumber);
		generation = 0;
		for (int k = 0; k < generationsNumber; k++) {
			System.out.println("Generation " + (k+1));
			population = getFitnessValues(population);
			Collections.sort(population);
			population = adultSelection.selectAdults(population);
			parents = parentSelection.selectParents(population, populationNumber/2);
			
			for (Vector v : population) {
				v.generation += 1;
			}

			for (int i = 0; i < parents.size()-1; i += 2) 
			{
				population.addAll(mutation.mutateAll(crossover.cross(Vector.vectorToInt(parents.get(i).vector), Vector.vectorToInt(parents.get(i+1).vector), crossoverThreshold), mutationThreshold));
			}
			generation++;
		}
		saveResults();
	}
	
	@Override
	public List<Vector> createPopulation(int n) {
		List<Vector> population = new ArrayList<Vector>();
		for (int i = 0; i < n; i++) {
			Vector v = new Vector(Main.nbWeights, Main.nbBiasWeights, Main.nbTaus, Main.nbGains);
			population.add(v);
		}
		return population;
	}

	@Override
	public List<Vector> getFitnessValues(List<Vector> population) {
		int count = 0;
		double[] bestWeights = new double[Main.nbAll];
		int bestFitness = Integer.MIN_VALUE;
		for (Vector v : population) {
			double[] params = development.getPhenotypes(v);
			beerTracker = new BeerTracker(mode, cellsHorizontal, cellsVertical, trackerSize, getWeights(params), threshold, getTaus(params), getGains(params));
			int stepsLeft = maxSteps;
			int timeStep = 0;
			while(stepsLeft > 0) {
				stepsLeft--;
				if (!beerTracker.objectOn)
					beerTracker.createObject(Main.maxObjectSize);
				else {
					beerTracker.moveObject();
				}
				beerTracker.moveTracker(timeStep);
				timeStep++;
				beerTracker.updateFitness();
			}
			population.get(count).fitness = beerTracker.getTrackerFitness();
			//System.out.println(population.get(count).fitness);
			if (population.get(count).fitness > bestFitness) {
				bestFitness = population.get(count).fitness;
				bestWeights = params;
			}
			count++;
		}
		count=0;
		System.out.println(bestFitness);
		fitnessResults[generation] = bestFitness;
		weightResults[generation] = bestWeights;
		return population;
	}

	@Override
	public boolean foundSolution(List<Vector> population) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void saveResults() throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("results_scenario" + mode + ".txt", "UTF-8");
		for (int i = 0; i < generationsNumber; i++) {
			writer.println(fitnessResults[i]);
		}
		writer.close();
		
		writer = new PrintWriter("params_scenario" + mode + ".txt", "UTF-8");
		for (int i = 0; i < generationsNumber; i++) {
			writer.println(Arrays.toString(weightResults[i]));
		}
		writer.close();
	}
	
	private double[] getWeights(double[] all){
		double[] d = new double[Main.nbWeights+Main.nbBiasWeights];
		for (int i = 0; i < Main.nbWeights+Main.nbBiasWeights; i++)
			d[i] = all[i];
		return d;
	}
	
	private double[] getTaus(double[] all) {
		double[] d = new double[Main.nbTaus];
		for (int i = 0; i < Main.nbTaus; i++)
			d[i] = all[i+Main.nbWeights+Main.nbBiasWeights];
		return d;
	}
	
	private double[] getGains(double[] all) {
		double[] d = new double[Main.nbGains];
		for (int i = 0; i < Main.nbGains; i++)
			d[i] = all[i+Main.nbWeights+Main.nbBiasWeights+Main.nbTaus];
		return d;
	}
	
	public double[] getBestParams() {
		int best = 0;
		int max = 0;
		for (int i = fitnessResults.length/4; i < fitnessResults.length; i++)
			if (fitnessResults[i] > max) {
				max = fitnessResults[i];
				best = i;
			}
				
		//return weightResults[weightResults.length-1];
		return weightResults[best];
	}
}