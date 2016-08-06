import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class Main extends JComponent implements Runnable {
	
	public static int animationSpeed = 1;
	private static boolean hidden = false;
	private static int mode = 0;

	private static int cellSize = 30;
	private static int cellsHorizontal = 30;
	private static int cellsVertical = 15;
	private static int width = cellSize*(cellsHorizontal+1);
	private static int height = cellSize*(cellsVertical+1)+20;
	
	private static int stepsLeft = 600;
	
	private static int trackerSize = 5;
	public static int maxObjectSize = 6;
	public static boolean start = false;

	
	private static BeerTracker beerTracker;
	
	public static int nbWeights = 22;
	public static int nbBiasWeights = 4;
	public static int nbTaus = 4;
	public static int nbGains = 4;
	public static int nbAll = nbWeights + nbBiasWeights + nbTaus + nbGains;
	
	private static double[] weights = new double[nbWeights+nbBiasWeights];
	private static double threshold = 0.5d;
	private static double[] taus = new double[nbTaus];
	private static double[] gains = new double[nbGains];
	
	public static double weightsMin = -5.0d;
	public static double weightsMax = 5.0d;
	public static double biasMin = -10.0d;
	public static double biasMax = 0.0d;
	public static double gainMin = 1.0d;
	public static double gainMax = 5.0d;
	public static double tauMin = 1.0d;
	public static double tauMax = 2.0d;
	
	public Main() {
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		try {
			while (!start) { System.out.print("");}
			int timeStep = 0;
			while(stepsLeft > 0) {
				stepsLeft--;
				if (!beerTracker.objectOn)
					beerTracker.createObject(maxObjectSize);
				else {
					beerTracker.moveObject();
				}
				beerTracker.moveTracker(timeStep);
				timeStep++;
				beerTracker.updateFitness();
				repaint();
				Thread.sleep(1000 / (animationSpeed/2+1));
			}
			System.out.println("Fitness: " + beerTracker.getTrackerFitness());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setPaint(Color.BLUE);
		g2D.setStroke(new BasicStroke(5.0f));
		
		Object o = beerTracker.getCurrentObject();
		for (int i = 0; i < o.getSize(); i++) {
			Rectangle2D.Double rectangle = new Rectangle2D.Double((o.getX()+i)*cellSize, o.getY()*cellSize, cellSize, cellSize);
			g2D.draw(rectangle);
		}
		g2D.setPaint(Color.RED);
		if (o.isPulled())
			g2D.setPaint(Color.YELLOW);
		int tracker = beerTracker.getTrackerPosition();
		for (int i = 0; i < trackerSize; i++) {
			int x = (tracker + i >= cellsHorizontal ? (tracker+i)-cellsHorizontal : tracker + i);
			Rectangle2D.Double rectangle = new Rectangle2D.Double(x*cellSize, (cellsVertical-1)*cellSize, cellSize, cellSize);
			g2D.draw(rectangle);
		}
	}
	
	public static void getRandomParams(){
		for (int i = 0; i < 22; i++)
			weights[i] = getRandom(0.0, 10.0)-5.0;
		for (int i = 22; i < 26; i++)
			weights[i] = getRandom(0, 10.0)-10.0;
		for (int i = 0; i < 4; i++)
			gains[i] = getRandom(gainMin, gainMax);
		for (int i = 0; i < 4; i++)
			taus[i] = getRandom(tauMin, tauMax);
	}
	
	public static double getRandom(double min, double max) {
		Random random = new Random();
		return (double)random.nextInt((int)max-1) + random.nextDouble();
	}
	
	private static void getParameters() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("parameters.txt"));
		for (int i = 0; i < nbWeights+nbBiasWeights; i++)
			weights[i] = Double.parseDouble(br.readLine());
		for (int i = 0; i < nbTaus; i++)
			taus[i] = Double.parseDouble(br.readLine());
		for (int i = 0; i < nbGains; i++)
			gains[i] = Double.parseDouble(br.readLine());
		br.close();
	}
	
	private static void saveParameters() throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("parameters.txt", "UTF-8");
		for (int i = 0; i < nbWeights+nbBiasWeights; i++)
			writer.println(weights[i]);
		for (int i = 0; i < nbTaus; i++)
			writer.println(taus[i]);
		for (int i = 0; i < nbGains; i++)
			writer.println(gains[i]);
		
		writer.close();
	}
	
	private static void runVisualization(){
		JFrame s = new JFrame("Parameters");
	    s.add(new SpeedSlider(animationSpeed));
	    s.setSize(280, 700);
	    s.setVisible(true);
	    
	    //getRandomParams();
		beerTracker = new BeerTracker(mode, cellsHorizontal, cellsVertical, trackerSize,
				weights, threshold, taus, gains);
		JFrame f = new JFrame("BeerTracker");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new Main());
		f.setSize(width, height);
		f.setVisible(true);
	}
	
	private static void saveParameters(double[] params) {
		for (int i = 0; i < Main.nbWeights+Main.nbBiasWeights; i++)
			weights[i] = params[i];
		for (int i = 0; i < Main.nbTaus; i++)
			taus[i] = params[i+Main.nbWeights+Main.nbBiasWeights];
		for (int i = 0; i < Main.nbGains; i++)
			gains[i] =params[i+Main.nbWeights+Main.nbBiasWeights+Main.nbTaus];
	}
	
	private static String getConsoleInput(String text) throws IOException{
		System.out.println(text);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		return reader.readLine();		
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		mode = Integer.parseInt(getConsoleInput("Choose mode:\n1-Standard\n2-Pull\n3-No-wrap"));
		getParameters();
		if (Integer.parseInt(getConsoleInput("0-Visualisation\n1-EA")) == 0)
			runVisualization();
		else {
			EvolutionaryAlgorithm ea = new EvolutionaryAlgorithm(cellsHorizontal, cellsVertical, trackerSize, stepsLeft, mode);
			double[] params = ea.getBestParams();
			saveParameters(params);

			//runVisualization();
			saveParameters();
		}
		
	}

}