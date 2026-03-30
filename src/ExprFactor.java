import java.util.ArrayList;

public class ExprFactor implements Factor {
    private ArrayList<Token> operators;
    private ArrayList<Term> terms;

    public ExprFactor() {
        operators = new ArrayList<>();
        terms = new ArrayList<>();
    }

    public void addOperator(Token token) {
        operators.add(token);
    }

    public void addTerm(Term term) {
        terms.add(term);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(terms.get(0));
        for (int i = 0;i < operators.size();i++) {
            sb.append(operators.get(i).getContent());
            sb.append(terms.get(i + 1));
        }
        sb.append(")");
        return sb.toString();
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        poly.addPoly(terms.get(0).toPoly());
        for (int i = 0;i < operators.size();i++) {
            Poly p = terms.get(i + 1).toPoly();
            for (Mono mono : p.getMonos()) {
                if (operators.get(i).getContent().equals("-")) {
                    mono.negateMono();
                }
            }
            poly.addPoly(p);
        }
        return poly;
    }
}
