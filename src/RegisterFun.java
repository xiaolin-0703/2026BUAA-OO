import java.util.HashMap;
import java.util.Map;

public class RegisterFun {
    private static Poly normalFun = null;
    private static Poly f0Base = null;
    private static Poly f1Base = null;
    private static RecurDef recurDef = null;

    public static void registernorFun(Poly poly) {
        normalFun = poly.standardPoly();
    }

    public static void registerf0Base(Poly poly) {
        f0Base = poly.standardPoly();
    }

    public static void registerf1Base(Poly poly) {
        f1Base = poly.standardPoly();
    }

    public static void registerrecurDef(RecurDef def) {
        recurDef = def;
    }

    public static Poly evaluateNormal(Poly argPoly) {
        if (normalFun == null) {
            return new Poly();
        }
        return clonePoly(normalFun.substitute(argPoly));
    }

    private static final Map<String, Poly> memo = new HashMap<>();

    public static Poly evaluateRecur(int n, Poly argPoly) {
        if (n == 0) {
            return clonePoly(f0Base.substitute(argPoly));
        }
        if (n == 1) {
            return clonePoly(f1Base.substitute(argPoly));
        }

        String cacheKey = n + "_" + argPoly.toString();
        if (memo.containsKey(cacheKey)) {
            return clonePoly(memo.get(cacheKey));
        }

        Poly actualArg1 = recurDef.getArg1().substitute(argPoly);
        Poly actualArg2 = recurDef.getArg2().substitute(argPoly);

        Poly term1 = evaluateRecur(n - 1, actualArg1);
        Poly term2 = evaluateRecur(n - 2, actualArg2);

        term1 = term1.mulPoly(new NumFactor(recurDef.getC1()).toPoly());
        term2 = term2.mulPoly(new NumFactor(recurDef.getC2()).toPoly());

        Poly result = term1.add(term2);

        if (recurDef.getExpr() != null) {
            Poly actualExtra = recurDef.getExpr().substitute(argPoly);
            result = result.add(actualExtra);
        }
        result = result.standardPoly();
        memo.put(cacheKey, clonePoly(result));
        return result;
    }

    private static Poly clonePoly(Poly original) {
        Poly copy = new Poly();
        copy.addPoly(original);
        return copy.standardPoly();
    }
}
