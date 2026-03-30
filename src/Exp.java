import java.math.BigInteger;

public class Exp implements Factor {
    private Factor factor;

    public Exp(Factor factor) {
        this.factor = factor;
    }

    public Poly toPoly() {
        Mono mono = new Mono(BigInteger.ONE,BigInteger.ZERO,factor.toPoly());
        Poly poly1 = new Poly();
        poly1.addMono(mono);
        return poly1;
    }

}
