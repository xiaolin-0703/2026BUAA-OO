public class ReFunFactor implements Factor {
    private Factor arg;
    private int n1;

    public ReFunFactor(int n,Factor arg) {
        this.n1 = n;
        this.arg = arg;
    }

    @Override
    public Poly toPoly() {
        return RegisterFun.evaluateRecur(n1,arg.toPoly().standardPoly());
    }
}