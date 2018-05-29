/* Enoch Chan and Karan Singla
 * TCSS 487
 * Written from borrowed implementation from https://github.com/romus/sha
 */


import java.math.BigInteger;
import java.util.Arrays;

public class KMACXOF256 {
    public static final int DEFAULT_PERM_WIDTH = 1600;

    private static BigInteger LARGEST_LONG = new BigInteger("18446744073709551615");
    
    private BigInteger[] ROUND_CONSTANTS = new BigInteger[] {
        new BigInteger("0000000000000001", 16),
        new BigInteger("0000000000008082", 16),
        new BigInteger("800000000000808A", 16),
        new BigInteger("8000000080008000", 16),
        new BigInteger("000000000000808B", 16),
        new BigInteger("0000000080000001", 16),
        new BigInteger("8000000080008081", 16),
        new BigInteger("8000000000008009", 16),
        new BigInteger("000000000000008A", 16),
        new BigInteger("0000000000000088", 16),
        new BigInteger("0000000080008009", 16),
        new BigInteger("000000008000000A", 16),
        new BigInteger("000000008000808B", 16),
        new BigInteger("800000000000008B", 16),
        new BigInteger("8000000000008089", 16),
        new BigInteger("8000000000008003", 16),
        new BigInteger("8000000000008002", 16),
        new BigInteger("8000000000000080", 16),
        new BigInteger("000000000000800A", 16),
        new BigInteger("800000008000000A", 16),
        new BigInteger("8000000080008081", 16),
        new BigInteger("8000000000008080", 16),
        new BigInteger("0000000080000001", 16),
        new BigInteger("8000000080008008", 16)
    };

    private int[][] rotation_offsets = new int[][] {
        {0, 36, 3, 41, 18},
        {1, 44, 10, 45, 2},
        {62, 6, 43, 15, 61},
        {28, 55, 25, 21, 56},
        {27, 20, 39, 8, 14}
    };

    private int w;
    private int n;

    public KMACXOF256() {
        initialize(DEFAULT_PERM_WIDTH);
    }

    public KMACXOF256(int b) {
        initialize(b);
    }

    public String getHash(String input, Parameters p) {
        BigInteger[][] S = new BigInteger[5][5];
        BigInteger[][] P = pad(input, p);
        String Z = "";

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                S[i][j] = new BigInteger("0", 16);
            }
        }

        for (BigInteger[] Pi : P) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if ((i + j * 5) < (p.getR() / w)) {
                        S[i][j] = S[i][j].xor(Pi[i + j * 5]);
                    }
                }
            }
            execute_keccackf(S);
        }

        do {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if ((5 * i + j) < (p.getR() / w)) {
                        Z = Z + addZero(HexHelper.convertByteToHex(S[j][i].toByteArray()), 16).substring(0, 16);
                    }
                }
            }
            execute_keccackf(S);
        } while (Z.length() < p.getOutputLength() * 2);
        int outputLength = p.getOutputLength();
        return Z.substring(0, outputLength * 2);
    }



    private BigInteger[][] execute_keccackf(BigInteger[][] A) {
        for (int i = 0; i < n; i++) {
            A = roundB(A, ROUND_CONSTANTS[i]);
        }
        return A;
    }
//    public String decrypt(String itemToDecrypt, String passphrase) {
//
//
//    }
    private String addZero(String s, int length) {
        String result = s;
        for (int i = 0; i < length - s.length(); i++) {
            result += "0";
        }
        return result;
    }

    private void initializeArr(BigInteger[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new BigInteger("0", 16);
        }
    }

    private void initialize(int b) {
        w = b / 25;
        int l = (int) (Math.log(w) / Math.log(2));
        n = 12 + 2 * l;
    }

    private BigInteger[][] roundB(BigInteger[][] A, BigInteger ROUND_CONSTANTS) {
        BigInteger[] C = new BigInteger[5];
        BigInteger[] D = new BigInteger[5];
        BigInteger[][] B = new BigInteger[5][5];

        for (int i = 0; i < 5; i++) {
            C[i] = A[i][0].xor(A[i][1]).xor(A[i][2]).xor(A[i][3]).xor(A[i][4]);
        }

        for (int i = 0; i < 5; i++) {
            D[i] = C[(i + 4) % 5].xor(rotate(C[(i + 1) % 5], 1));
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                A[i][j] = A[i][j].xor(D[i]);
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                B[j][(2 * i + 3 * j) % 5] = rotate(A[i][j], rotation_offsets[i][j]);
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                A[i][j] = B[i][j].xor(B[(i + 1) % 5][j].not().and(B[(i + 2) % 5][j]));
            }
        }

        A[0][0] = A[0][0].xor(ROUND_CONSTANTS);

        return A;
    }

    private BigInteger rotate(BigInteger x, int n) {
        n = n % w;
        BigInteger leftShift = getShiftLeft64(x, n);
        BigInteger rightShift = x.shiftRight(w - n);

        return leftShift.or(rightShift);
    }

    private BigInteger getShiftLeft64(BigInteger value, int shift) {
        BigInteger returnVal = value.shiftLeft(shift);
        BigInteger tempVal = value.shiftLeft(shift);

        if (returnVal.compareTo(LARGEST_LONG) > 0) {
            for (int i = 64; i < 64 + shift; i++) {
                tempVal = tempVal.clearBit(i);
            }
            tempVal = tempVal.setBit(64 + shift);
            returnVal = tempVal.and(returnVal);
        }

        return returnVal;
    }

    private BigInteger[][] pad(String input, Parameters p) {
        int size;
        input = input + p.getD();

        while (((input.length() / 2) * 8 % p.getR()) != ((p.getR() - 8))) {
        	input = input + "00";
        }

        input = input + "80";
        size = (((input.length() / 2) * 8) / p.getR());

        BigInteger[][] inputArr = new BigInteger[size][];
        inputArr[0] = new BigInteger[1600 / w];
        initializeArr(inputArr[0]);

        int count = 0;
        int i = 0;
        int j = 0;

        for (int _n = 0; _n < input.length(); _n++) {
            if (j > (p.getR() / w - 1)) {
                j = 0;
                i++;
                inputArr[i] = new BigInteger[1600 / w];
                initializeArr(inputArr[i]);
            }
            count++;

            if ((count * 4 % w) == 0) {
                String subString = input.substring((count - w / 4), (w / 4) + (count - w / 4));
                inputArr[i][j] = new BigInteger(subString, 16);
                String revertString = HexHelper.convertByteToHex(inputArr[i][j].toByteArray());
                revertString = addZero(revertString, subString.length());
                inputArr[i][j] = new BigInteger(revertString, 16);
                j++;
            }
        }
        return inputArr;
    }



}