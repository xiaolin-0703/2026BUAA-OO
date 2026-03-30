public class NorFunFactor implements Factor {
    private Factor arg;

    public NorFunFactor(Factor arg) {
        this.arg = arg;
    }

    @Override
    public Poly toPoly() {
        return RegisterFun.evaluateNormal(arg.toPoly().standardPoly());
    }
}
