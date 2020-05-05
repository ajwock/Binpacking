package binpacking.controller;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import binpacking.interfaces.BinPackingSolution;
import binpacking.model.Bin;
import binpacking.model.BinManager;

/**
 * Handles the main method, as well as the output of information concerning the
 * knapsack.
 * 
 * @author Gabe Reynolds
 * @author Andrew Wock
 *
 */
public class BinpackSolver {
	/** The knapsack that's to be solved */
	public static BinManager original;
	/** The start time of the chosen heuristic */
	public static long startTime;
	/** The total amount of time that the chosen heuristic took to finish running */
	public static long totalTime;
	/** True if the items in the solution should be printed out, false otherwise */
	static boolean pVertices = true;
	/** True if the branching tree should be printed out, false otherwise. */
	static boolean traceTree = false;
	/** True if there should be no output. */
	static boolean silent = false;
	/** True if only the runtime should be printed. */
	static boolean runtime = false;
	/** The chosen heuristic to run for this program */
	static String heuristic = "basicbnb";

	public static final String USAGE = "Usage:\n\n" + 
			"    java -jar Binpacking.jar filename [-q|v] [--silent|runtime]\n" + 
			"                                   [-simplebnb|-fastbnb|-ffapprox]\n" + 
			"\n" + 
			"This program generates some solution to a binpacking instance contained in \n" + 
			"the file specified as filename.  The type of algorithm used to produce a\n" + 
			"solution can be specified using (default) -simplebnb, -fastbnb, or -ffapprox.\n" + 
			"\n" + 
			"REQUIRED:\n" + 
			"\n" + 
			"    filename    Name of the file containing the binpacking instance.\n" + 
			"\n" + 
			"OPTIONS:\n" + 
			"\n" + 
			"    -q         Suppress solution output.\n" + 
			"\n" + 
			"    -v         Show branching tree info if appropriate.\n" + 
			"\n" + 
			"    --silent   Suppress all output.\n" + 
			"\n" + 
			"    --runtime  Suppress output except for program runtime.\n" + 
			"\n" + 
			"    -simplebnb Use old branch and bound algorithm.\n" + 
			"\n" + 
			"    -fastbnb   Use new, faster branch and bound algorithm.\n" + 
			"\n" + 
			"    -ffapprox  Use first-fit descending approximation.\n" +
			"";

	/**
	 * Takes the given input file, and creates a knapsack using the chosen
	 * heuristic. Then outputs the knapsack solution, as well as other information
	 * concerning the program.
	 * 
	 * @param args[] the first argument should be the input file, the second should
	 *               be the heuristic, and the third is optional and will stop each
	 *               individual item in the solution from being listed
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		startBPProgram(args);
	}
	
	/**
	 * Reads the given arguments from the user and adjusts the program accordingly
	 * @param args the given arguments from the user
	 */
	public static void resolveArgs(String args[]) {
		if (args.length >= 1) {
			// The input file
			String inputFile = args[0];
			// Checks to see if heuristic and input file is correct
			try {
				original = SnapReader.readInputFile(inputFile);
			} catch (Exception e) {
				System.out.println(USAGE);
				System.out.println("ERROR:  Could not open file :" + inputFile);
				System.exit(1);
			}
			// Checks to see if the vertices in the cover should be printed
			silent = false;
			traceTree = false;
			pVertices = true;
			for (int i = 1; i < args.length; i++) {
				switch (args[i]) {
				case "-q":
					pVertices = false;
					break;
				case "--silent":
					silent = true;
					traceTree = false;
					break;
				case "--runtime":
					silent = true;
					traceTree = false;
					runtime = true;
					break;
				case "-v":
					traceTree = true;
					break;
				case "-simplebnb":
					BinpackSolver.heuristic = "basicbnb";
					break;
				case "-fastbnb":
					BinpackSolver.heuristic = "fastbnb";
					break;
				case "-ffapprox":
					BinpackSolver.heuristic = "ffapprox";
					break;
				}
			}
			original.setTraceTree(traceTree);
		} else {
			System.out.println(USAGE);
			System.exit(1);
		}
	}
	
	/**
	 * Handles calling the appropriate bin packing solver mode chosen by the user
	 * @return the optimal solution to the bin packing problem according to the method given by the user
	 */
	public static BinPackingSolution dispatch() {
		switch (BinpackSolver.heuristic) {
		case "basicbnb":
			return BinpackSolver.basic();
		case "fastbnb":
			return BinpackSolver.fast();
		case "ffapprox":
			return BinpackSolver.firstFitApprox();
		}
		return null;
	}
	
	/**
	 * Starts the bin packing solver with the given arguments given by the user
	 * @param args the arguments given by the user
	 * @return the optimal solution to the bin packing problem according to the method given by the user
	 */
	public static BinPackingSolution startBPProgram(String args[]) {
		BinpackSolver.resolveArgs(args);
		BinPackingSolution sol = BinpackSolver.dispatch();
		BinpackSolver.outputInfo(args[0]);
		return sol;
	}

	/**
	 * Calls implementation of the basic BinpackSolver
	 * 
	 * @return the optimal bin packed solution
	 */
	private static BinPackingSolution basic() {
		startTime = System.currentTimeMillis();
		BinPackingSolution k = original.unrefined();
		totalTime = System.currentTimeMillis() - startTime;
		return k;
	}
	
	/**
	 * Calls implementation of the optimized BinpackSolver with the
	 * optimized node implementation
	 * 
	 * @return the optimal bin packed solution
	 */
	private static BinPackingSolution fast() {
		startTime = System.currentTimeMillis();
		BinPackingSolution k= original.fast();
		totalTime = System.currentTimeMillis() - startTime;
		return k;
	}
	/**
	 * Calls the first fit approximation of the bin packing solver
	 * 
	 * @return the best solution found by the first fit approximation
	 */
	private static BinPackingSolution firstFitApprox() {
		startTime = System.currentTimeMillis();
		BinPackingSolution k = original.ffapprox();
		totalTime = System.currentTimeMillis() - startTime;
		return k;
	}
	
	public static void printRuntime() {
		System.out.print(totalTime);
	}

	/**
	 * Used to output information concerning both the solved knapsack, the number of
	 * items, and the vertex cover graph, the runtime of the program, the file used,
	 * and the heuristic used.
	 * 
	 * @param inputFile the file used to create the original graph
	 */
	public static void outputInfo(String inputFile) {
		if (!silent) {
			System.out.println("filename	" + inputFile);
			System.out.println("items		" + original.getNumItems());
			System.out.println("capacity	" + original.getCapacity());
			System.out.println("bins 		" + original.getNumBins());
			System.out.println("mode		" + BinpackSolver.heuristic);
			// Prints out the chosen heuristic
			System.out.println("runtime  	" + totalTime);
			System.out.println("branches 	" + original.getBranches());
			if (pVertices) {
				for (Bin bin : original.binList()) {
					System.out.println(bin);
				}
			}
		} else if (runtime) {
			printRuntime();
		}
	}
}