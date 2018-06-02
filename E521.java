import java.math.BigInteger;

public class E521 {
	static BigInteger d;
    static BigInteger x;
    static BigInteger y;
    static BigInteger p;

    public E521() {
        SetupD();
        x = BigInteger.valueOf(0);
        y = BigInteger.valueOf(1);
    }

    public E521(int x1, int y1) {
        SetupD();
        x = BigInteger.valueOf(x1);
        y = BigInteger.valueOf(y1);

    }

    public E521(int x1) {
        SetupD();
        x = BigInteger.valueOf(x1);
        y = solveForY(x1);
    }

    public static BigInteger solveForY(int x) {
        BigInteger a = BigInteger.valueOf(0);
        BigInteger xSquare = BigInteger.valueOf(x);
        xSquare = xSquare.multiply(xSquare);
        BigInteger topPart = BigInteger.valueOf(1).subtract(xSquare);
        BigInteger bottomPart = d.multiply(xSquare).add(BigInteger.valueOf(1));
        BigInteger together = topPart.divide(bottomPart);
        return sqrt(together, p, false);
    }

    public static void SetupD() {
        d = BigInteger.valueOf(376014);
        d = d.negate();
        int exponent = 521;
        BigInteger bi1 = new BigInteger("2");
        BigInteger bi2 = bi1.pow(exponent);
        p = bi2.subtract(BigInteger.valueOf(1));
    }

    public static void main(String[] args) {
        int exponent = 521;
        SetupD();

        System.out.println(solveForY(18));
    }

    public E521 addPoint(E521 temp) {
        BigInteger tempx = x.add(temp.x);
        BigInteger tempy = y.add(temp.y);
        tempx = tempx.negate();
        return new E521(tempx.intValue(), tempy.intValue());
    }

    public static void pointMulitplication(BigInteger times) {

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
