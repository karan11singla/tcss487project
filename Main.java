/* Enoch Chan and Karan Singla
 * TCSS 487
 */


import static utilities.HelperMethods.bytesToHex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Scanner;

import utilities.HelperMethods.*;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		String line;
		SymmetricCrypt k = new SymmetricCrypt();
		
		System.out.println("Encrypting original.txt file...");
		

		Scanner input = new Scanner(new File("original.txt"));

		String outputName = "encrypted.txt";
		PrintStream output = new PrintStream(new File(outputName));

		
		SymmetricCryptogram o = null;
		while (input.hasNextLine()) {
			line = input.nextLine();
			o = k.encrypt(line, "test");
			

			output.println(bytesToHex(o.getT()));
		}
		System.out.println("Encypted file name - encrypted.txt");
		System.out.println("Decrypting...");
		
		
		String decryptedFile = "decrypted.txt";
		PrintStream decryptedText = new PrintStream(new File(decryptedFile));
		
		try {
			String str = new String(k.decrypt(o, "test"), "UTF-8");
			decryptedText.println(str);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Decrypted file name - decrypted.txt");
		System.out.println("Done.");

		//sc.close();
		input.close();
	}
}
