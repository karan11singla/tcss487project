/* Enoch Chan and Karan Singla
 * TCSS 487
 * Written from borrowed implementation from https://github.com/romus/sha
 */

import java.math.BigInteger;

public class HexHelper {

    private static final char[] DIGITS =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String convertByteToHex(byte[] input) {
        final int l = input.length;
        final char[] output = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
        	output[j++] = DIGITS[(0xF0 & input[i]) >>> 4];
        	output[j++] = DIGITS[0x0F & input[i]];
        }
        return new String(output);
    }

    public static String convertByteToReverseHex(byte[] input) {
        return convertByteToHex(reverseBits(input));
    }

    private static byte[] reverseBits(byte[] input) {
        int i = 0;
        int j = input.length - 1;
        byte temp;

        while (j > i) {
            temp = input[j];
            input[j] = input[i];
            input[i] = temp;
            j--;
            i++;
        }
        return input;
    }

    public static BigInteger sqrt(BigInteger v, BigInteger p, boolean lsb) {
        assert (p.testBit(0) && p.testBit(1)); // p = 3 (mod 4)
        if (v.signum() == 0) {
            return BigInteger.ZERO;
        }
        BigInteger r = v.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
        if (r.testBit(0) != lsb) {
            r = p.subtract(r); // correct the lsb
        }
        return (r.multiply(r).subtract(v).mod(p).signum() == 0) ? r : null;
    }

}