import java.math.BigInteger;

public class RecurDef {
    private final BigInteger c1;
    private final Poly arg1;
    private final BigInteger c2;
    private final Poly arg2;
    private final Poly expr;

    public RecurDef(BigInteger c1, Poly arg1, BigInteger c2,Poly arg2, Poly expr) {
        this.c1 = c1;
        this.arg1 = arg1;
        this.c2 = c2;
        this.arg2 = arg2;
        this.expr = expr;
    }

    public BigInteger getC1() {
        return c1;
    }

    public Poly getArg1() {
        return arg1;
    }

    public BigInteger getC2() {
        return c2;
    }

    public Poly getArg2() {
        return arg2;
    }

    public Poly getExpr() {
        return expr;
    }

}
