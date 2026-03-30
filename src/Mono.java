import java.math.BigInteger;

public class Mono {
    private BigInteger coefficient;
    private int exponent;

    private int sign = 1;

    public Mono(BigInteger coefficient, int exponent) {
        this.coefficient = coefficient;
        this.exponent = exponent;
    }

    public void addMono(BigInteger co) {
        this.coefficient = this.coefficient.add(co);
    }

    public void mergeSign(int sign1) {
        this.sign *= sign1;
    }

    public Mono mulMono(Mono m) {
        Mono newMono = new Mono(coefficient.multiply(m.coefficient), exponent + m.exponent);
        newMono.mergeSign(this.sign);
        newMono.mergeSign(m.sign);
        return newMono;
    }

    public int getSign() {
        return sign;
    }

    public String toString() {
        return coefficient + "*x^" + exponent;
    }

    public int getExponent() {
        return exponent;
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

}