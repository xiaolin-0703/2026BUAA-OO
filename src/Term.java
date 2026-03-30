import java.util.ArrayList;

public class Term {
    private ArrayList<CellFactor> factors;

    public Term() {
        factors = new ArrayList<>();
    }

    public void addFactor(CellFactor factor1) {
        factors.add(factor1);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(factors.get(0).toString());
        for (int i = 1;i < factors.size();i++) {
            sb.append("*");
            sb.append(factors.get(i));
        }

        return sb.toString();
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        poly.addPoly(factors.get(0).toPoly());
        for (int i = 1;i < factors.size();i++) {
            poly.mulPoly(factors.get(i).toPoly());
        }

        return poly;
    }

}
