package binpacking;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

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
	static BinManager original;
	/** The start time of the chosen heuristic */
	static long startTime;
	/** The total amount of time that the chosen heuristic took to finish running */
	static long totalTime;
	/** True if the items in the solution should be printed out, false otherwise */
	static boolean pVertices = true;
	/** True if the branching tree should be printed out, false otherwise. */
	static boolean traceTree = false;
	/** The chosen heuristic to run for this program */
	static String heuristic = "basicbnb";
	
	public static final String USAGE = "Usage: java -jar BinpackSolver.jar path/to/your/file [method] [-q]\n"
			+ "Must include an input file argument.\n"
			+ "optional \"method\" argument determines how the BP problem is solved.\n"
			+ "Methods are:\n"
			+ "  -simplebnb\n"
			+ "      Runs an naive implementation of branch and bound.  Guaranteed optimal solution, and rather slow.\n"
			+ "  -fastbnb\n"
			+ "      Runs an advanced implementation of branch and bound.  Guaranteed optimal solution, and relatively fast.\n"
			+ "      Currently in development, buggy.\n"
			+ "  -ffapprox\n"
			+ "      Runs a first-fit approximation.  Suboptimal solution but very fast.\n"
			+ "The optional -q argument omits the solution from the output to reduce output size.";

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
			for (int i = 1; i < args.length; i++){
				switch (args[i]) {
				case "-q":
					pVertices = false;
					traceTree = false;
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
	
	public static long dispatch() {
		switch (BinpackSolver.heuristic) {
		case "basicbnb":
			return BinpackSolver.basic();
		case "fastbnb":
			return BinpackSolver.fast();
		case "ffapprox":
			return BinpackSolver.firstFitApprox();
		}
		return -1;
	}
	
	public static void startBPProgram(String args[]) {
		BinpackSolver.resolveArgs(args);
		BinpackSolver.dispatch();
		BinpackSolver.outputInfo(args[0]);
	}

	/**
	 * The implementation of the BinpackSolver
	 * 
	 * @return the optimal bin packed solution
	 */
	private static long basic() {
		startTime = System.currentTimeMillis();
		long k = original.unrefined();
		totalTime = System.currentTimeMillis() - startTime;
		return k;
	}

	private static long fast() {
		startTime = System.currentTimeMillis();
		long k = original.fast();
		totalTime = System.currentTimeMillis() - startTime;
		return k;
	}
	
	private static long firstFitApprox() {
		startTime = System.currentTimeMillis();
		long k = original.ffapprox();
		totalTime = System.currentTimeMillis() - startTime;
		return k;
	}


	/**
	 * Used to output information concerning both the solved knapsack, the number of
	 * items, and the vertex cover graph, the runtime of the program, the file used,
	 * and the heuristic used.
	 * 
	 * @param inputFile the file used to create the original graph
	 */
	public static void outputInfo(String inputFile) {
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
	}
}