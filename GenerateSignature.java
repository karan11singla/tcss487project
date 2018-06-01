/**
 * Enoch Chan and Karan Singla
 * TCSS 487
 * Project
 */

import static utilities.HelperMethods.*;

import java.math.BigInteger;

public class GenerateSignature {
    private static byte[] EmptyByteArray = asciiStringToByteArray("");
	private static byte[] KByteArray = asciiStringToByteArray("K");
	private static byte[] NByteArray = asciiStringToByteArray("N");
	private static byte[] TByteArray = asciiStringToByteArray("T");
	private static BigInteger FourBigInt = new BigInteger("4");

	public Signature generateSignature(String m, String pw) {

		/* s <- KMACXOF256(pw, “”, 512, “K”); s <- 4s */
		byte[] pwByteArr = asciiStringToByteArray(pw);
		byte[] s = SHAKE.KMACXOF256(pwByteArr, EmptyByteArray, 512, KByteArray);
		s = multiplyByFour(s);

		/* k <- KMACXOF256(s, m, 512, “N”); k <- 4k */
		byte[] mByteArr = asciiStringToByteArray(m);
		byte[] k = SHAKE.KMACXOF256(s, mByteArr, 512, NByteArray);

		/* U <- k*G; */

		/* h <- KMACXOF256(Ux, m, 512, “T”); z <- (k – hs) mod r */
		byte[] hByteArr = SHAKE.KMACXOF256(Ux, mByteArr, 512, TByteArray);

		/* σ <- (h,z) */
		Signature result = new Signature(hByteArr, z);
	}

	public byte[] multiplyByFour(byte[] input) {
		String iHex = bytesToHex(input);
		BigInteger iBigInteger = new BigInteger(iHex, 16);
		iBigInteger.multiply(FourBigInt);
		input = iBigInteger.toByteArray();
		return input;
	}
}
