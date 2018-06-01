/**
 * Enoch Chan and Karan Singla
 * TCSS 487
 * Project
 */

import static utilities.HelperMethods.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class SymmetricCrypt {
	
    private SecureRandom r;
    
    private static byte[] EmptyByteArray = asciiStringToByteArray("");
    private static byte[] SByteArray = asciiStringToByteArray("S");
    private static byte[] SKEByteArray = asciiStringToByteArray("SKE");
    private static byte[] SKAByteArray = asciiStringToByteArray("SKA");
    
    public SymmetricCrypt() {
        r = new SecureRandom();
    }
    
    public SymmetricCryptogram encrypt(String m, String pw) {
    	
    	/* z <- Random(512) */
    	byte[] z = new byte[512];
        r.nextBytes(z);
        
        /* (ke || ka) <- KMACXOF256(z || pw, “”, 1024, “S”) */
        byte[] zpw = new byte[z.length + pw.getBytes().length];
        System.arraycopy(z, 0, zpw, 0, z.length);
        System.arraycopy(pw.getBytes(), 0, zpw, z.length, pw.getBytes().length);
        byte[] keka = SHAKE.KMACXOF256(zpw, EmptyByteArray, 1024, SByteArray);
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length / 2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length / 2, keka.length);
        
        /* c <- KMACXOF256(ke, “”, |m|, “SKE”) xor m */
        byte[] paddedm = addPadding(m.getBytes(), 8);
        byte[] c = SHAKE.KMACXOF256(ke, EmptyByteArray, paddedm.length, SKEByteArray);
        String cHex = bytesToHex(c);
        BigInteger cBigInt = new BigInteger(cHex, 16);
        String mHex = bytesToHex(paddedm);
        BigInteger mBigInt = new BigInteger(mHex, 16);
        cBigInt = cBigInt.xor(mBigInt);
        c = cBigInt.toByteArray();

        /* t <- KMACXOF256(ka, m, 512, “SKA”) */
        byte[] t = SHAKE.KMACXOF256(ka, pw.getBytes(), 512, SKAByteArray);
        
        return new SymmetricCryptogram(z, c ,t);
    }
    
    public byte[] decrypt(SymmetricCryptogram c, String pw) {
    	
    	/* (ke || ka) <- KMACXOF256(z || pw, “”, 1024, “S”) */
        byte[] zpw = new byte[c.getZ().length + pw.getBytes().length];
        System.arraycopy(c.getZ(), 0, zpw, 0, c.getZ().length);
        System.arraycopy(pw.getBytes(), 0, zpw, c.getZ().length, pw.getBytes().length);
        byte[] keka = SHAKE.KMACXOF256(zpw, EmptyByteArray, 1024, SByteArray);
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length / 2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length / 2, keka.length);

        /* m <- KMACXOF256(ke, “”, |c|, “SKE”) xor c */
        byte[] m = SHAKE.KMACXOF256(ke, EmptyByteArray, c.getC().length, SKEByteArray);
        String mHex = bytesToHex(m);
        BigInteger mBigInt = new BigInteger(mHex, 16);
        String cHex = bytesToHex(c.getC());
        BigInteger cBigInt = new BigInteger(cHex, 16);
        mBigInt = mBigInt.xor(cBigInt);
        m = mBigInt.toByteArray();

        /* t’ <- KMACXOF256(ka, m, 512, “SKA”) */
        byte[] t2 = SHAKE.KMACXOF256(ka, m, 512, SKAByteArray);

        /* accept if, and only if, t’ = t */
        String tHex = bytesToHex(c.getT());
        String t2Hex = bytesToHex(t2);
        
        //System.out.println("INSIDE DECRYPT METHOD  " + tHex + "&&  " + t2Hex);
//        if (!tHex.equals(t2Hex)) {
//            throw new RuntimeException("FAIL");
//        }
        
        //byte[] result = removePadding(m);
        return m;
    }
}