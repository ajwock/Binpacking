package binpacking;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class BinPackingTest {

	@Test
	void test() {
		ProcessBuilder pb = new ProcessBuilder();
		final String basename = "test16_no";
		try {
			for (int i = 0; i < 16; i++) {
				String testFileName = basename + i + ".bp";
				pb.command("python", "testfiles/generateInstance.py", testFileName);
				Process process = pb.start();
				int exitVal = process.waitFor();
			}
			
		} catch (IOException | InterruptedException e) {
			System.out.println(e.getStackTrace());
		}
	}

}
