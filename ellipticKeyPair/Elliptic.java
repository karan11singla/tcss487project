package EllipticKeyPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Elliptic {

    public static int PAD = 5;
    public static final Random r = new Random();

    private HashMap<Point, Integer> pointTable;
    private HashMap<Integer, Point> charTable;

    private Encrypter mEncrypter;
    private Decrypter mDecrypter;

    public Elliptic(EllipticEquation eq) {
        initializeCodeTable(eq);
        this.mEncrypter = new Encrypter(charTable);
        this.mDecrypter = new Decrypter(pointTable);
    }

    public static Random getRandom() {
        return r;
    }

    private int[] encrypt(String msg, PubK key) {
        EllipticEquation c = key.getCurve();
        Point g = c.getBasePoint();
        Point pubKey = key.getKey();
        BigInteger p = c.getP();
        int numBits = p.bitLength();
        BigInteger k;
        do {
            k = new BigInteger(numBits, getRandom());
        } while (k.mod(p).compareTo(BigInteger.ZERO) == 0);
        Point sharedSecret = c.multiply(pubKey, k);

        Point keyHint = c.multiply(g, k);

        Matrix mMatrix = mEncrypter.encrypt(msg);
        mMatrix.performAddition(Utilities.toBinary(sharedSecret));
        return mMatrix.toArray(Utilities.toBinary(keyHint));
    }

    private String decrypt(int[] cipherText, PrivK key) {
        EllipticEquation c = key.getCurve();
        BigInteger privK = key.getKey();

        Point keyHint = Point.make(cipherText);
        Point sharedSecret = c.multiply(keyHint, privK);

        Matrix mMatrix = Matrix.make(cipherText);
        mMatrix.performSubstraction(Utilities.toBinary(sharedSecret));
        return mDecrypter.decrypt(mMatrix);
    }
    
    public static KeyPair generateKeyPair(EllipticEquation c) {
        BigInteger p = c.getP();
        BigInteger privK;
        do {
            privK = new BigInteger(p.bitLength(), getRandom());
        } while (privK.mod(p).compareTo(BigInteger.ZERO) == 0);

        Point g = c.getBasePoint();
        Point pubKey = c.multiply(g, privK);

        return new KeyPair(
                new PubK(c, pubKey),
                new PrivK(c, privK)
        );
    }

    public final void initializeCodeTable(EllipticEquation curve) {
        charTable = new HashMap<>();
        pointTable = new HashMap<>();
        Point p = curve.getBasePoint();
        for (int i = 1; i < 27; i++) {
            do {
                p = curve.multiply(curve.getBasePoint(), i);
            } while (p.isInfinity());
            charTable.put(i + 96, p);
        }
        charTable.put(32, Point.getInfinity());
        int[] codeAscii = new int[]{10, 13, 39, 40, 41, 44, 46, 58, 59};
        for (int i : codeAscii) {
            p = curve.add(p, curve.getBasePoint());
            charTable.put(i, p);
        }

        for (Integer key : charTable.keySet()) {
            pointTable.put(charTable.get(key), key);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        EllipticEquation eq = new EllipticEquation(4, 20, 29, new Point(1, 5));
        Elliptic e = new Elliptic(eq);
        Scanner sc = new Scanner(System.in);
                
        KeyPair keys = generateKeyPair(eq);
        
        boolean encryptChosen;
        String line = "";
        int num;
        
        System.out.println("Encrypt (E) or Decrypt (D)?");
        System.out.println("Press 'E' or 'D': ");
        char choice = sc.next().charAt(0);
        if (choice == 'e' || choice == 'E') {		// encrypt
        	encryptChosen = true;
        } else {									// decrypt
        	encryptChosen = false;
        }
        
        System.out.print("Type Input Name: ");
		String inputName = sc.next();
		Scanner input = new Scanner(new File(inputName));
		
		System.out.print("Type Output Name: ");
		String outputName = sc.next();
		PrintStream output = new PrintStream(new File(outputName));
		
		if (encryptChosen) {
			while (input.hasNextLine()) {
				line = input.nextLine();
				int[] cipherText = e.encrypt(line, keys.getPubK());
				output.println(Utilities.print(cipherText));
			}
		} else {
			int[] cipherText = new int[10000];
			int index = 0;
			while (input.hasNextInt()) {
				num = input.nextInt();
				cipherText[index] = num;
				index++;
			}
			String plainText = e.decrypt(cipherText, keys.getPrivK());
			output.println(plainText);
		}
		
		System.out.println("Done.");
		
        sc.close();
        input.close();
    }
}
