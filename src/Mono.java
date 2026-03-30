import java.math.BigInteger;
import java.util.Objects;

public class Mono {
    private BigInteger coefficient;
    private BigInteger exponent;
    private Poly expoly;

    public Mono(BigInteger coefficient, BigInteger exponent,Poly expoly) {
        this.coefficient = coefficient;
        this.exponent = exponent;
        this.expoly = expoly;
    }

    public void addMono(BigInteger co) {
        this.coefficient = this.coefficient.add(co);
    }

    public Mono negateMono() {
        this.coefficient = this.coefficient.negate();
        return this;
    }

    public Mono mulMono(Mono m) {
        BigInteger newco = this.coefficient.multiply(m.coefficient);
        BigInteger newexponent = this.exponent.add(m.exponent);
        Poly newExp = null;
        if (this.expoly != null && m.expoly != null) {
            newExp = this.expoly.add(m.getExpoly());
        } else if (this.expoly != null) {
            newExp = this.expoly;
        } else if (m.expoly != null) {
            newExp = m.expoly;
        }
        return new Mono(newco,newexponent,newExp);
    }

    public BigInteger getExponent() {
        return exponent;
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    public Poly getExpoly() {
        return expoly;
    }

    public boolean isSimmilar(Mono m) {
        if (!this.exponent.equals(m.exponent)) {
            return false;
        }
        if (this.expoly == null &&  m.expoly == null) {
            return true;
        }
        if (this.expoly == null || m.expoly == null) {
            return false;
        }
        return this.expoly.equals(m.expoly);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Mono mono = (Mono) o;
        BigInteger thisco = this.coefficient;
        BigInteger otherco = mono.coefficient;
        if (this.expoly == null &&  mono.expoly == null) {
            return thisco.compareTo(otherco) == 0 &&
                    this.exponent.equals(mono.exponent);
        }
        if (this.expoly == null || mono.expoly == null) {
            return false;
        }
        return  thisco.compareTo(otherco) == 0 &&
                this.exponent.equals(mono.exponent) && this.expoly.equals(mono.expoly);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coefficient, exponent, expoly);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(coefficient).append("x^").append(exponent);
        if (expoly != null) {
            sb.append("exp(").append(expoly.toString()).append(")");
        }
        return sb.toString();
    }
}