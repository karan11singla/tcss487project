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

    private Encoder mEncoder;
    private Decoder mDecoder;

    public Elliptic(EllipticEquation c) {
        initCodeTable(c);
        this.mEncoder = new Encoder(charTable);
        this.mDecoder = new Decoder(pointTable);
    }

    public static Random getRandom() {
        return r;
    }

    private int[] encrypt(String msg, PublicKey key) {
        EllipticEquation c = key.getCurve();
        Point g = c.getBasePoint();
        Point publicKey = key.getKey();
        BigInteger p = c.getP();
        int numBits = p.bitLength();
        BigInteger k;
        do {
            k = new BigInteger(numBits, getRandom());
        } while (k.mod(p).compareTo(BigInteger.ZERO) == 0);
        Point sharedSecret = c.multiply(publicKey, k);

        Point keyHint = c.multiply(g, k); // key to send

        Matrix mMatrix = mEncoder.encode(msg);
        mMatrix.performAddition(Helpers.toBinary(sharedSecret));
        return mMatrix.toArray(Helpers.toBinary(keyHint));
    }

    private String decrypt(int[] cipherText, PrivateKey key) {
        EllipticEquation c = key.getCurve();
        BigInteger privateKey = key.getKey();

        Point keyHint = Point.make(cipherText);
        Point sharedSecret = c.multiply(keyHint, privateKey);

        //get the decrypted matrix
        Matrix mMatrix = Matrix.make(cipherText);
        //subtract the key form the matrix
        mMatrix.performSubstraction(Helpers.toBinary(sharedSecret));
        //decode the matrix
        return mDecoder.decode(mMatrix);
    }

    /**
     * Generate a random key-pair, given the elliptic curve being used.
     */
    public static KeyPair generateKeyPair(EllipticEquation c) {
        // Randomly select the private key, such that it is relatively prime to p
        BigInteger p = c.getP();
        BigInteger privateKey;
        do {
            privateKey = new BigInteger(p.bitLength(), getRandom());
        } while (privateKey.mod(p).compareTo(BigInteger.ZERO) == 0);

        // Calculate the public key, k * g.
        Point g = c.getBasePoint();
        Point publicKey = c.multiply(g, privateKey);

        return new KeyPair(
                new PublicKey(c, publicKey),
                new PrivateKey(c, privateKey)
        );
    }

    public final void initCodeTable(EllipticEquation curve) {
        charTable = new HashMap<>();
        pointTable = new HashMap<>();
        Point p = curve.getBasePoint();
        for (int i = 1; i < 27; i++) {
            do {
                p = curve.multiply(curve.getBasePoint(), i);
            } while (p.isInfinity());
            charTable.put(i + 96, p); // 0 here refers to char 97 witch is a
        }
        //special characters
        charTable.put(32, Point.getInfinity()); //space
        int[] codeAscii = new int[]{10, 13, 39, 40, 41, 44, 46, 58, 59};
        for (int i : codeAscii) {
            p = curve.add(p, curve.getBasePoint());
            charTable.put(i, p);
        }

        //populate the points symbol table
        for (Integer key : charTable.keySet()) {
            pointTable.put(charTable.get(key), key);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        EllipticEquation eq = new EllipticEquation(4, 20, 29, new Point(1, 5));
        Elliptic e = new Elliptic(eq);
        Scanner sc = new Scanner(System.in);
                
        // generate pair of keys
        KeyPair keys = generateKeyPair(eq);
        
        boolean encryptChosen;
        String line;
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
				int[] cipherText = e.encrypt(line, keys.getPublicKey());
				output.println(Helpers.print(cipherText));
			}
		} else {
			int[] cipherText = new int[10000];
			int index = 0;
			while (input.hasNextInt()) {
				num = input.nextInt();
				cipherText[index] = num;
				index++;
			}
			String plainText = e.decrypt(cipherText, keys.getPrivateKey());
			output.println(plainText);
		}
		
		System.out.println("Done.");
		
        sc.close();
        input.close();
    }
}
