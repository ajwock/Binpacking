package snippet;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Handles the main method, as well as the output of information concerning the
 * knapsack.
 * 
 * @author Gabe Reynolds
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
	/** The chosen heuristic to run for this program */
	static String heuristic = "plain";

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
		String inputFile = "";
		if (args.length >= 1 && args.length <= 2) {
			// The input file
			inputFile = args[0];
			// Checks to see if heuristic and input file is correct
			try {
				original = SnapReader.readInputFile(inputFile);
			} catch (Exception e) {
				System.out.println("Usage: java -jar BinpackSolver.jar 'path-to-file' 'heuristic' '(optional) -q'\n"
						+ "The first parameter is the name of the input file, with the path to it included.\n"
						+ "The second and final parameter is optional. Simply type '-q' if you don't wish for the list of all items included in the\n"
						+ "cover to be printed.");
			}
			// Checks to see if the vertices in the cover should be printed
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("-q")) {
					pVertices = false;
				}
			}
			BinpackSolver.plain();
		} else {
			System.out.println("Usage: java -jar BinpackSolver.jar 'path-to-file' 'heuristic' '(optional) -q'\n"
					+ "The first parameter is the name of the input file, with the path to it included.\n"
					+ "The second and final parameter is optional. Simply type '-q' if you don't wish for the list of all items included in the\n"
					+ "cover to be printed.");
		}
		BinpackSolver.outputInfo(args[0]);
	}

	/**
	 * The implementation of the BinpackSolver
	 * 
	 * @return the optimal bin packed solution
	 */
	private static long plain() {
		startTime = System.currentTimeMillis();
		long k = original.unrefined();
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
	private static void outputInfo(String inputFile) {
		System.out.println("filename	" + inputFile);
		System.out.println("items		" + original.getNumItems());
		System.out.println("capacity	" + original.getCapacity());
		System.out.println("bins 		" + original.getNumBins());
		System.out.print("mode		");
		// Prints out the chosen heuristic
		System.out.println("runtime  	" + totalTime);
		System.out.println("branches 	" + original.getBranches());
		if (pVertices) {
			Iterator<Bin> storedIDs = original.binList().listIterator();
			while (storedIDs.hasNext()) {
				storedIDs.next().printContents();
			}
		}
	}
}