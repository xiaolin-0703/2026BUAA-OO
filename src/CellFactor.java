import java.math.BigInteger;
import java.util.ArrayList;

public class CellFactor implements Factor {
    private ArrayList<Factor> factors;

    public CellFactor() {
        factors = new ArrayList<>();
    }

    public void addFactor(Factor factor) {
        factors.add(factor);
    }

    public ArrayList<Factor> getFactors() {
        return factors;
    }

    public Poly toPoly() {
        Poly newPoly = new Poly();
        BigInteger exp = new BigInteger(factors.get(1).toString());
        if (exp.compareTo(BigInteger.ZERO) == 0) {
            newPoly.addMono(new Mono(BigInteger.ONE,BigInteger.ZERO,BigInteger.ZERO,null));
            return newPoly;
        }
        Poly poly1 = factors.get(0).toPoly();
        poly1 = poly1.standardPoly();
        return power(poly1,exp);
        //      newPoly.addPoly(poly1);
        //     for (BigInteger i = BigInteger.ONE; i.compareTo(exp) < 0;i = i.add(BigInteger.ONE)) {
        //          newPoly.mulPoly(poly1);
        //      }
        //      return newPoly;
    }

    public Poly power(Poly base, BigInteger exp) {
        Poly res = new Poly();
        res.addMono(new Mono(BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO,null));
        Poly b = base;
        BigInteger e = exp;
        if (e.compareTo(BigInteger.ONE) == 0) { return b; }
        while (e.compareTo(BigInteger.ZERO) > 0) {
            if (e.mod(new BigInteger("2")).equals(BigInteger.ONE)) {
                res = res.mulPoly(b);
            }
            b = b.mulPoly(b);
            e = e.divide(new BigInteger("2"));
        }
        return res;
    }

    public String toString() {
        return "";
    }
}
