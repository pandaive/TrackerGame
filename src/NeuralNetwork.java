import java.util.ArrayList;
import java.util.Arrays;

public class NeuralNetwork {
	
	int[] sensors;
	double[] weights;
	double threshold;
	double[] taus;
	double[] gains;
	
	double[] output;
	
	Neuron shadow1, shadow2, shadow3, shadow4, shadow5;
	Neuron hidden1, tempHidden1, hidden2, tempHidden2;
	Neuron output1, tempOutput1, output2, tempOutput2;
	Neuron bias;
	
	int mode;
	
	
	public NeuralNetwork(double[] weights, double threshold, double[] taus, double gains[], int mode) {
		this.weights = weights;
		this.threshold = threshold;
		this.taus = taus;
		this.gains = gains;
		this.mode = mode;
		
		hidden1 = new Neuron(threshold, null, taus[0], gains[0], 0);
		hidden2 = new Neuron(threshold, null, taus[1], gains[1], 0);
		output1 = new Neuron(threshold, null, taus[2], gains[2], 0);
		output2 = new Neuron(threshold, null, taus[3], gains[3], 0);
		bias = new Neuron(1);
		
	}
	
	public void runNetwork(int[] sensors, int timeStep, int leftWall, int rightWall){
		this.sensors = sensors;
		
		shadow1 = new Neuron(sensors[0]);
		shadow2 = new Neuron(sensors[1]);
		shadow3 = new Neuron(sensors[2]);
		shadow4 = new Neuron(sensors[3]);
		shadow5 = new Neuron(sensors[4]);
		
		tempHidden1 = hidden1.copy();
		tempHidden2 = hidden2.copy();
		
		ArrayList<Neuron> inputsHidden1 = new ArrayList<Neuron>();
		shadow1.setWeight(weights[0]);
		inputsHidden1.add(shadow1);
		shadow2.setWeight(weights[1]);
		inputsHidden1.add(shadow2);
		shadow3.setWeight(weights[2]);
		inputsHidden1.add(shadow3);
		shadow4.setWeight(weights[3]);
		inputsHidden1.add(shadow4);
		shadow5.setWeight(weights[4]);
		inputsHidden1.add(shadow5);
		bias.setWeight(weights[22]);
		inputsHidden1.add(bias);
		tempHidden1.setWeight(weights[12]);
		inputsHidden1.add(tempHidden1);
		tempHidden2.setWeight(weights[10]);
		inputsHidden1.add(tempHidden2);
		hidden1.setInput(inputsHidden1);
		hidden1.fireCTRNN(timeStep);
		
		ArrayList<Neuron> inputsHidden2 = new ArrayList<Neuron>();
		shadow1.setWeight(weights[5]);
		inputsHidden2.add(shadow1);
		shadow2.setWeight(weights[6]);
		inputsHidden2.add(shadow2);
		shadow3.setWeight(weights[7]);
		inputsHidden2.add(shadow3);
		shadow4.setWeight(weights[8]);
		inputsHidden2.add(shadow4);
		shadow5.setWeight(weights[9]);
		inputsHidden2.add(shadow5);
		bias.setWeight(weights[23]);
		inputsHidden2.add(bias);
		tempHidden1.setWeight(weights[11]);
		inputsHidden2.add(tempHidden1);
		tempHidden2.setWeight(weights[13]);
		inputsHidden2.add(tempHidden2);
		hidden2.setInput(inputsHidden2);
		hidden2.fireCTRNN(timeStep);
		
		tempOutput1 = output1.copy();
		tempOutput2 = output2.copy();
		
		ArrayList<Neuron> inputsOutput1 = new ArrayList<Neuron>();
		if (mode == BeerTracker.NO_WRAP)
			if (rightWall < 4) {
				Neuron run = new Neuron(4-rightWall);
				run.setWeight(10.0d);
				inputsOutput1.add(run);
			}
		hidden1.setWeight(weights[14]);
		inputsOutput1.add(hidden1);
		hidden2.setWeight(weights[16]);
		inputsOutput1.add(hidden2);
		bias.setWeight(weights[24]);
		inputsOutput1.add(bias);
		tempOutput1.setWeight(weights[20]);
		inputsOutput1.add(tempOutput1);
		tempOutput2.setWeight(weights[18]);
		inputsOutput1.add(tempOutput2);
		output1.setInput(inputsOutput1);
		output1.fireCTRNN(timeStep);
		
		ArrayList<Neuron> inputsOutput2 = new ArrayList<Neuron>();
		if (mode == BeerTracker.NO_WRAP)
			if (leftWall < 4){
				Neuron run = new Neuron(4-leftWall);
				run.setWeight(10.0d);
				inputsOutput2.add(run);
			}
		hidden1.setWeight(weights[15]);
		inputsOutput2.add(hidden1);
		hidden2.setWeight(weights[17]);
		inputsOutput2.add(hidden2);
		bias.setWeight(weights[25]);
		inputsOutput2.add(bias);
		tempOutput1.setWeight(weights[19]);
		inputsOutput2.add(tempOutput1);
		tempOutput2.setWeight(weights[21]);
		inputsOutput2.add(tempOutput2);
		output2.setInput(inputsOutput2);
		output2.fireCTRNN(timeStep);
		//System.out.println("out1: " + output1.output + " out2: " + output2.output);
		
		output = getOutput();
	}
	
	public double[] getOutput(){
		double[] out = new double[2];
		out[0] = output1.getOutput();
		out[1] = output2.getOutput();
		return out;
	}
}