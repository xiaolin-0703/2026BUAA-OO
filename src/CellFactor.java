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
        int exp = Integer.parseInt(factors.get(1).toString());
        if (exp == 0) {
            newPoly.addMono(new Mono(BigInteger.ONE,0));
            return newPoly;
        }
        Poly poly1 = factors.get(0).toPoly();
        newPoly.addPoly(poly1);
        for (int i = 1;i < exp;i++) {
            newPoly.mulPoly(poly1);
        }
        return newPoly;
    }

    public String toString() {
        return "";
    }
}
