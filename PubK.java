package EllipticKeyPair;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PubK {

    private EllipticEquation eq;
    private Point pK;

    public PubK(EllipticEquation c, Point pK) {
        this.eq = c;
        this.pK = pK;
    }

    public PubK(String pathFile) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(pathFile), StandardCharsets.UTF_8);
            BigInteger a = new BigInteger(lines.get(0), 16);
            BigInteger b = new BigInteger(lines.get(1), 16);
            BigInteger p = new BigInteger(lines.get(2), 16);
            BigInteger g1 = new BigInteger(lines.get(3), 16);
            BigInteger g2 = new BigInteger(lines.get(4), 16);
            BigInteger pK1 = new BigInteger(lines.get(5), 16);
            BigInteger pK2 = new BigInteger(lines.get(6), 16);
            EllipticEquation eC = new EllipticEquation(a, b, p, new Point(g1, g2));
            Point eCP = new Point(pK1, pK2);
            this.eq = eC;
            this.pK = eCP;
        } catch (Exception e) {

        }
    }

    public EllipticEquation getCurve() {
        return eq;
    }

    public void setCurve(EllipticEquation eq) {
        this.eq = eq;
    }

    public Point getKey() {
        return pK;
    }

    public void setKey(Point pK) {
        this.pK = pK;
    }

    public Point getBasePoint() {
        return eq.getBasePoint();
    }
}