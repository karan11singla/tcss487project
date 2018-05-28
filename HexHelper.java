package keccak;

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

}