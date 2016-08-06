import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * mutation of a genotype, each gene is mutated with given probabilty (threshold)
 */
public class Mutation {

	public int[] mutate(int[] a, double threshold){
		int[] mutated = new int[a.length];
		Random random = new Random();
		
		for (int i = 0; i < a.length; i++)
		{
			if (random.nextDouble() > threshold)
				mutated[i] = a[i];
			else {
				int tempI = mutated[i];
				if (i < Main.nbWeights) {
					int r = random.nextInt(Main.nbWeights);
					mutated[i] = mutated[r];
					mutated[r] = tempI;
				}
				else if (i < Main.nbWeights+Main.nbBiasWeights) {
					int r = random.nextInt(Main.nbBiasWeights)+Main.nbWeights;
					mutated[i] = mutated[r];
					mutated[r] = tempI;
				}
				else if (i < Main.nbWeights+Main.nbBiasWeights+Main.nbTaus) {
					int r = random.nextInt(Main.nbTaus)+Main.nbWeights+Main.nbBiasWeights;
					mutated[i] = mutated[r];
					mutated[r] = tempI;
				}
				else {
					int r = random.nextInt(Main.nbGains)+Main.nbTaus+Main.nbWeights+Main.nbBiasWeights;
					mutated[i] = mutated[r];
					mutated[r] = tempI;
				}
			}
		}
		return mutated;
	}
	
	public List<Vector> mutateAll(List<Vector> population, double threshold){
		List<Vector> mutated = new ArrayList<Vector>();
		for (Vector v : population)
			mutated.add(new Vector(mutate(Vector.vectorToInt(v.vector), threshold)));
		
		return mutated;
	}
}