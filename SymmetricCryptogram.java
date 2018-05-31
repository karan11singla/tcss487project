/**
 * Enoch Chan and Karan Singla
 * TCSS 487
 * Project
 */

public class SymmetricCryptogram {
    byte[] z;
    byte[] c;
    byte[] t;
    public SymmetricCryptogram(byte[] zarg, byte[] carg, byte[] targ) {
        z = zarg;
        c = carg;
        t = targ;
    }
    public byte[] getZ() {
        return this.z;
    }
    public byte[] getC() {
        return this.c;
    }
    public byte[] getT() {
        return this.t;
    }
}