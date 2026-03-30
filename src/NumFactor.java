import java.math.BigInteger;

public class NumFactor implements Factor {
    private final BigInteger num;

    public NumFactor(BigInteger num) {
        this.num = num;
    }

    public Poly toPoly() {
        Mono mono = new Mono(num,BigInteger.ZERO,BigInteger.ZERO,null);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }

    @Override
    public String toString() {
        return num.toString();
    }

}
