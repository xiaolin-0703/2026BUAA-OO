public class DerFactor implements Factor {
    private String var;
    private ExprFactor expr;

    public DerFactor(String var, ExprFactor expr) {
        this.var = var;
        this.expr = expr;
    }

    @Override
    public Poly toPoly() {
        Poly poly = new Poly();
        if (var.equals("grad")) {
            poly.addPoly(expr.toPoly().DerPoly("x"));
            poly.addPoly(expr.toPoly().DerPoly("y"));
        } else {
            poly.addPoly(expr.toPoly().DerPoly(String.valueOf(var.charAt(1))));
        }
        return poly;
    }

}
