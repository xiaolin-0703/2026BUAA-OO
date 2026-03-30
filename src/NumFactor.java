import java.math.BigInteger;

public class NumFactor implements Factor {
    private final BigInteger num;

    public NumFactor(BigInteger num) {
        this.num = num;
    }

    public String toString() {
        return this.num.toString();
    }

    public Poly toPoly() {
        Mono mono = new Mono(num,0);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }
}
