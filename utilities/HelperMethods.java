/**
 * Enoch Chan and Karan Singla
 * TCSS 487
 * Project
 */

package utilities;

public class HelperMethods {
    private static final char[] HEX_LIBRARY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    
    /* Source: Paulo S. L. M. Barreto */
    public static byte[] asciiStringToByteArray(String s) {
        byte[] val = new byte[s.length()];
        for (int i = 0; i < val.length; i++) {
            char c = s.charAt(i);
            if (c >= 256) {
                throw new RuntimeException("Non-ASCII character found");
            }
            val[i] = (byte)c;
        }
        return val;
    }
    
    public static byte[] addPadding(byte[] arr, int size) {
        byte[] result = new byte[arr.length + size - arr.length % size];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i];
        }
        if (size - arr.length % size == 1) { //case: only one char to pad
            result[result.length - 1] = (byte) 0x01; //length - 1 to prevent off-by-one error
        } else { //case: append one and then loop zeroes onto the end until we reach the end
            result[arr.length] = (byte) 0x01; //array.length is the index number of the first new value
            for (int i = arr.length + 1; i < result.length; i++) {
                result[i] = (byte) 0x00; //append zero in every index to the end of the new array for padding.
            }
        }
        return result;
    }
    
    public static byte[] removePadding(byte[] arr) {
        boolean done = false;
        int i = arr.length;

        while (!done) {
            if (arr[i] == (byte) 0x00) {
                i--;
            } else if (arr[i] == (byte) 0x01) {
                done = true;
            }
        }
        byte[] result = new byte[i];

        for (int k = 0; k < i; k++) {
            result[k] = arr[k];
        }
        
        return result;
    }
    
    public static String bytesToHex(byte[] arr) {
        char[] result = new char[arr.length * 2];
        for (int i = 0; i < arr.length; i++ ) {
            int j = arr[i] & 0xFF;
            result[i * 2] = HEX_LIBRARY[j >>> 4];
            result[(i * 2) + 1] = HEX_LIBRARY[j & 0x0F];
        }
        return new String(result);
    }
    
    private static byte[] reverseArr(byte[] arr) {
        for(int i = 0; i < arr.length / 2; i++)
        {
            byte temp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = temp;
        }
        
        return arr;
    }
    
    public static String reverseBytesToHex(byte[] in) {
        return bytesToHex(reverseArr(in));
    }
}