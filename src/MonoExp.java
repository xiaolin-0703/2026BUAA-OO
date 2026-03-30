import java.math.BigInteger;
import java.util.Objects;

public class MonoExp {
    private final BigInteger xexponent;
    private final BigInteger yexponent;
    private final Poly expoly;

    public MonoExp(BigInteger xexponent,BigInteger yexponent, Poly expoly) {
        this.xexponent = xexponent;
        this.yexponent = yexponent;
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
        return xexponent.compareTo(newmono.xexponent) == 0 && Objects.equals(expoly,newmono.expoly)
                && yexponent.compareTo(newmono.yexponent) == 0;
    }

    public int hashCode() {
        return Objects.hash(xexponent,yexponent,expoly);
    }

    public BigInteger getxExponent() {
        return xexponent;
    }

    public BigInteger getyExponent() {
        return yexponent;
    }

    public Poly getExpoly() {
        return expoly;
    }

}
