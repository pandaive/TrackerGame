import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Neuron {
	private ArrayList<Neuron> input;
	private double threshold;
	private double weight = 0.0d;
	private boolean isFired;
	public double output = 0.0d;
	private double tau;
	private double dy = 0.0d;
	private double y = 0.0d;
	private double gain;
	private double externalInput;
	
	//normal neuron
	public Neuron(double thr, ArrayList<Neuron> input,
			double tau, double gain, double timeStep){
		this.input = input;
		this.threshold = thr;
		this.isFired = false;
		this.tau = tau;
		this.gain = gain;
		this.externalInput = 0;
	}
	
	//bias neuron or input
	public Neuron(double output) {
		this.output = output;
		this.isFired = true;
	}
	
	public void addInput(Neuron n) {
		this.input.add(n);
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	public double getOutput() {
		return this.output;
	}
	
	public double sigmoid(double t){
		return 1.0/(1.0+Math.pow(Math.E, -t));
	}
	
	public double sigmoidGain(double t, double gain){
		return 1.0/(1.0+Math.pow(Math.E, -(gain*t)));
	}
	
	public void fire(){
		double total = 0.0d;
		if (!input.isEmpty()) {
			for (Neuron n : input) {
				if (n.isFired)
					total += n.getWeight()*n.getOutput();
			}
			total = sigmoid(total);
			if (total >= threshold) {
				this.isFired = true;
				output = total;
			}
		}
		else
			if (weight >= threshold) {
				isFired = true;
			}
	}
	
	public void fireCTRNN(double timeStep){
		this.isFired = true;
		double total = 0.0d;
		if (!input.isEmpty()) {
			for (Neuron n : input) {
				if (n.isFired)
					total += n.getOutput() * n.getWeight();
			}
			total += externalInput; //neuron's I
			dy = (1/(tau+0.1*Math.pow(0.1, 5))) * (-y + total);
			y += round1(dy,8);
			output = round1(sigmoidGain(dy, gain), 8);
			//System.out.println("out: " + output + " rounded: " + round1(output, 10));
		}
	}
	
	public void setInput(ArrayList<Neuron> newInput){
		this.input = newInput;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public Neuron copy(){
		return new Neuron(output);
	}
	
	public Double round1(Double val, int max){
		String v = val.toString();
		v = v.substring(0, v.length() <= max ? v.length() : max);
		try {
		return Double.parseDouble(v);
		} catch (NumberFormatException e) {
			return Double.parseDouble(v.substring(0, 3));
		}
	}
	
	public Double round2(Double val) {
	    return new BigDecimal(val.toString()).setScale(2,RoundingMode.HALF_UP).doubleValue();
	}
}