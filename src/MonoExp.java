import java.math.BigInteger;
import java.util.Objects;

public class MonoExp {
    private final BigInteger xexponent;

    private final Poly expoly;

    public MonoExp(BigInteger xexponent, Poly expoly) {
        this.xexponent = xexponent;
        this.expoly = expoly;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MonoExp newmono = (MonoExp) o;
        return xexponent.compareTo(newmono.xexponent) == 0 && Objects.equals(expoly,newmono.expoly);
    }

    public int hashCode() {
        return Objects.hash(xexponent, expoly);
    }

    public BigInteger getxExponent() {
        return xexponent;
    }

    public Poly getExpoly() {
        return expoly;
    }

}
