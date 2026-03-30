
public class ChoiceFactor implements Factor {
    private Factor factorA;
    private Factor factorB;
    private Factor factorC;
    private Factor factorD;

    public ChoiceFactor(Factor a, Factor b, Factor c, Factor d) {
        factorA = a;
        factorB = b;
        factorC = c;
        factorD = d;
    }

    @Override
    public Poly toPoly() {
        if (checkEquality(factorA, factorB)) {
            return factorC.toPoly();
        } else {
            return factorD.toPoly();
        }
    }

    private boolean checkEquality(Factor a, Factor b) {
        Poly polyA = factorA.toPoly().standardPoly();
        Poly polyB = factorB.toPoly().standardPoly();
        if (polyA.equals(polyB)) {
            return true;
        }
        return false;
    }
}

