/**
 * Enoch Chan and Karan Singla
 * TCSS 487
 * Project
 */

public class Signature {
	byte[] h;
    byte[] z;
    public Signature(byte[] harg, byte[] zarg) {
    	h = harg;
        z = zarg;
    }
    public byte[] geth() {
        return this.h;
    }
    public byte[] getz() {
        return this.z;
    }
}
