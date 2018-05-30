package EllipticKeyPair;

import java.math.BigInteger;

public class EllipticEquation {

    private BigInteger a;
    private BigInteger b;
    private BigInteger p;

    private Point basePoint = null;
    private static BigInteger THREE = new BigInteger("3");

    public EllipticEquation(BigInteger a, BigInteger b, BigInteger p, Point g) {
        this.a = a;
        this.b = b;
        this.p = p;
        this.basePoint = g;
    }

    public EllipticEquation(BigInteger a, BigInteger b, BigInteger p) {
        this(a, b, p, null);
    }

    public EllipticEquation(long a, long b, long p, Point g) {
        this(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(p), g);
    }

    public EllipticEquation(long a, long b, long p) {
        this(a, b, p, null);
    }

    public BigInteger getA() {
        return a;
    }

    public BigInteger getB() {
        return b;
    }

    public BigInteger getP() {
        return p;
    }

    public Point getBasePoint() {
        return basePoint;
    }
    public void setBasePoint(Point p) {
        basePoint = p;
    }

    public boolean contains(Point point) {
        if (point.isInfinity()) {
            return true;
        }

        return point.getX().multiply(point.getX()).mod(p).add(a).multiply(point.getX()).add(b)
                .mod(p).subtract(point.getY().multiply(point.getY())).mod(p)
                .compareTo(BigInteger.ZERO) == 0;
    }

    public Point add(Point p1, Point p2) {
        if (p1 == null || p2 == null) {
            return null;
        }

        if (p1.isInfinity()) {
            return new Point(p2);
        } else if (p2.isInfinity()) {
            return new Point(p1);
        }

        BigInteger lambda;
        if (p1.getX().subtract(p2.getX()).mod(p).compareTo(BigInteger.ZERO) == 0) {
            if (p1.getY().subtract(p2.getY()).mod(p).compareTo(BigInteger.ZERO) == 0) {
                BigInteger nom = p1.getX().multiply(p1.getX()).multiply(THREE).add(a);
                BigInteger den = p1.getY().add(p1.getY());
                lambda = nom.multiply(den.modInverse(p));
            } else {
                return Point.getInfinity();
            }
        } else {
            BigInteger nom = p2.getY().subtract(p1.getY());
            BigInteger den = p2.getX().subtract(p1.getX());
            lambda = nom.multiply(den.modInverse(p));
        }
        
        BigInteger xr = lambda.multiply(lambda).subtract(p1.getX()).subtract(p2.getX()).mod(p);
        BigInteger yr = lambda.multiply(p1.getX().subtract(xr)).subtract(p1.getY()).mod(p);
        return new Point(xr, yr);
    }
    
    public Point subtract(Point p1, Point p2) {
        if (p1 == null || p2 == null) {
            return null;
        }
        return add(p1, p2.negate());
    }
    
    public Point multiply(Point p1, BigInteger n) {
        if (p1.isInfinity()) {
            return Point.getInfinity();
        }

        Point result = Point.getInfinity();
        int bitLength = n.bitLength();
        for (int i = bitLength - 1; i >= 0; --i) {
            result = add(result, result);
            if (n.testBit(i)) {
                result = add(result, p1);
            }
        }

        return result;
    }

    public Point multiply(Point p1, long n) {
        return multiply(p1, BigInteger.valueOf(n));
    }
    
    public BigInteger calculateRhs(BigInteger x) {
        return x.multiply(x).mod(p).add(a).multiply(x).add(b).mod(p);
    }
    
}