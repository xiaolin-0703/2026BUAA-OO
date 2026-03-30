import java.math.BigInteger;
import java.util.Objects;

public class Mono {
    private BigInteger coefficient;
    private BigInteger exponent;
    private Poly expoly;
    private BigInteger yexponent;

    public Mono(BigInteger coefficient, BigInteger exponent,BigInteger yexponent,Poly expoly) {
        this.coefficient = coefficient;
        this.exponent = exponent;
        this.yexponent = yexponent;
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
        BigInteger newyexponent = this.yexponent.add(m.yexponent);
        Poly newExp = null;
        if (this.expoly != null && m.expoly != null) {
            newExp = this.expoly.add(m.getExpoly());
        } else if (this.expoly != null) {
            newExp = this.expoly;
        } else if (m.expoly != null) {
            newExp = m.expoly;
        }
        return new Mono(newco,newexponent,newyexponent,newExp);
    }

    public BigInteger getExponent() {
        return exponent;
    }

    public BigInteger getYexponent() {
        return yexponent;
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    public Poly getExpoly() {
        return expoly;
    }

    public Poly Dermono(String var) {
        Poly result = new Poly();

        if (this.coefficient.compareTo(BigInteger.ZERO) == 0) {
            return result;
        }
        BigInteger targetExp = (var.equals("x")) ? this.exponent : this.yexponent;
        if (targetExp.compareTo(BigInteger.ZERO) > 0) {
            BigInteger newCoefficient = this.coefficient.multiply(targetExp);
            BigInteger newExponent = (var.equals("x")) ?
                    this.exponent.subtract(BigInteger.ONE) : this.exponent;
            BigInteger newyExponent = (var.equals("y")) ?
                    this.yexponent.subtract(BigInteger.ONE) : this.yexponent;
            Mono newMono1 =  new Mono(newCoefficient,newExponent,newyExponent,this.expoly);
            result.addMono(newMono1);
        }
        if (this.expoly != null) {
            Poly innerDer = this.expoly.DerPoly(var);
            if (!innerDer.getMonos().isEmpty()) {
                Poly poly1 = new Poly();
                poly1.addMono(new Mono(this.coefficient,this.exponent,this.yexponent,this.expoly));
                Poly poly2 = poly1.mulPoly(innerDer);
                result.addPoly(poly2);
            }
        }
        return result.standardPoly();
    }

    public Poly substitute(Poly xreplacement) {
        Poly result = new Poly();
        if (this.coefficient.compareTo(BigInteger.ZERO) == 0) {
            return result;
        }
        result.addMono(new Mono(this.coefficient,BigInteger.ZERO,this.yexponent,null));
        if (this.exponent.compareTo(BigInteger.ZERO) > 0) {
            Poly xpoly1 = xreplacement.power(this.exponent);
            result = result.mulPoly(xpoly1);
        }
        if (this.expoly != null) {
            Poly expInner =  this.expoly.substitute(xreplacement);
            if (!expInner.getMonos().isEmpty()) {
                Poly newExpoly = new Poly();
                Mono newmono = new Mono(BigInteger.ONE,BigInteger.ZERO,BigInteger.ZERO,expInner);
                newExpoly.addMono(newmono);
                result = result.mulPoly(newExpoly);
            }
        }
        return result.standardPoly();
    }

    public boolean isSimmilar(Mono m) {
        if (!this.exponent.equals(m.exponent) || !this.yexponent.equals(m.yexponent)) {
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
                    this.exponent.equals(mono.exponent) && this.yexponent.equals(mono.yexponent);
        }
        if (this.expoly == null || mono.expoly == null) {
            return false;
        }
        return  thisco.compareTo(otherco) == 0 && this.yexponent.compareTo(mono.yexponent) == 0 &&
                this.exponent.equals(mono.exponent) && this.expoly.equals(mono.expoly);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coefficient,exponent,yexponent,expoly);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(coefficient).append("x^").append(exponent);
        sb.append("*y^").append(yexponent);
        if (expoly != null) {
            sb.append("exp(").append(expoly.toString()).append(")");
        }
        return sb.toString();
    }

}