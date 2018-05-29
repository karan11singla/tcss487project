/* Enoch Chan and Karan Singla
 * TCSS 487
 * Written from borrowed implementation from https://github.com/romus/sha
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class RunKeccak {

	public static void main(String[] args) throws FileNotFoundException {
		String line;
		KMACXOF256 k = new KMACXOF256();
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Type Input Name: ");
		String inputName = sc.next();
		Scanner input = new Scanner(new File(inputName));
		
		System.out.print("Type Output Name: ");
		String outputName = sc.next();
		PrintStream output = new PrintStream(new File(outputName));
		
		while (input.hasNextLine()) {
			line = input.nextLine();
			output.println(k.getHash(line, Parameters.SHAKE256));
		}
		
		System.out.println("Done.");
		
		sc.close();
		input.close();
	}
}
