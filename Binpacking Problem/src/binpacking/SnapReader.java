package binpacking;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Handles all input for .snap files
 * 
 * @author Gabe Reynolds
 *
 */
public class SnapReader {
	// Builds a graph from input

	/**
	 * Reads the given file and creates a knapsack along with a list of items.
	 * Ignore all comments (lines that have a '#' in them).
	 * 
	 * @param filepath the given file
	 * @return a graph created from the given file
	 * @throws IOException
	 */
	public static BinManager readInputFile(String filepath) throws IOException {
		BinManager g;
		// Gets the directory this program is located in and adds it to the given
		// filepath
		File input = new File(filepath);
		try (Scanner scan = new Scanner(input)) {
			int numItems = 0;
			int kCapacity = 0;
			boolean kStop = false;
			while (scan.hasNextLine() && !kStop) {
				// Skips lines with comments
				if (scan.findInLine("#") == null) {
					if (scan.hasNext("k")) {
						scan.next("k");
					}
					if (scan.hasNextInt()) {
						numItems = scan.nextInt();
					}
					if (scan.hasNextInt()) {
						kCapacity = scan.nextInt();
					}
					kStop = true;
				} else {
					scan.nextLine();
				}
			}
			g = new BinManager(kCapacity, numItems);
			while (scan.hasNextLine()) {
				int itemNum = -1;
				int itemWeight = -1;
				if (scan.findInLine("#") == null && scan.hasNextInt()) {
					itemNum = scan.nextInt();
					if (scan.hasNextInt()) {
						itemWeight = scan.nextInt();
						Item temp = new Item(itemNum, itemWeight);
						g.addItem(temp);
					}
				} else
					scan.nextLine();
			}
		} catch (Exception e) {
			// throw new IllegalArgumentException("File not found: " + e.getMessage());
			throw e;
		}
		return g;
	}
}
