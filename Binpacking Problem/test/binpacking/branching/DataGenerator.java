package binpacking.branching;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.Test;

import binpacking.controller.BinpackSolver;

class DataGenerator {

	@Test
	void test() {
		String[] slowerArgs = { "", "-basicbnb", "-v" };
		String output = getUniqueFileName("GeneratedData/basicbnbData", ".csv");
		//generateData(output, slowerArgs, 128, 8, 1);
		String[] quickerArgs = { "", "-fastbnb", "-v" };
		output = getUniqueFileName("GeneratedData/fastbnbData", ".csv");
		generateData(output, quickerArgs, 2, 2, 1);
	}

	void generateData(String filename, String[] args, int maxSize, int sizeJump, int dataPerSize) {
		try (PrintWriter output = new PrintWriter(filename)) {
			StringBuilder base = new StringBuilder("testfiles/temp/test");
			ProcessBuilder pb = new ProcessBuilder();
			for (Integer i = sizeJump; true; i += sizeJump) {
				output.print(i);
				for (Integer j = 1; j <= dataPerSize; j++) {
					StringBuilder testFileName = new StringBuilder(base);
					testFileName.append(i);
					testFileName.append("_no");
					testFileName.append(j);
					testFileName.append(".bp");
					pb.command("python", "testfiles/generateInstance.py", testFileName.toString(), i.toString());
					Process process = pb.start();
					process.waitFor();
					args[0] = testFileName.toString();
					BinpackSolver.resolveArgs(args);
					BinpackSolver.dispatch();
					long timems = BinpackSolver.totalTime;
					output.print("," + timems);
				}
				if (i >= maxSize) {
					break;
				}
				output.print("\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	String getUniqueFileName(String filename, String extension) {
		StringBuilder base = new StringBuilder(filename);
		StringBuilder sb = new StringBuilder(base);
		sb.append(extension);
		try {
			Integer i = 1;
			File file = new File(sb.toString());
			while (!file.createNewFile()) {
				sb = new StringBuilder(base);
				sb.append(i);
				sb.append(extension);
				file = new File(sb.toString());
				i++;
			}
			return sb.toString();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}	
	}

}
