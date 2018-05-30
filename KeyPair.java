package EllipticKeyPair;

public class KeyPair {

    private PubK pubK;
    private PrivK privK;

    public KeyPair(PubK pubK, PrivK privK) {
        this.pubK = pubK;
        this.privK = privK;
    }

    public PubK getPubK() {
        return pubK;
    }

    public void setPublicKey(PubK pubK) {
        this.pubK = pubK;
    }

    public PrivK getPrivK() {
        return privK;
    }

    public void setPrivK(PrivK privK) {
        this.privK = privK;
    }
}