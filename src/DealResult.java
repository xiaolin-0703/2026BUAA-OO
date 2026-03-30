import java.math.BigInteger;

public class DealResult {
    private Poly poly = new Poly();

    public DealResult(Poly midResult) {
        this.poly = midResult.standardPoly();
    }

    public String getResult() {
        if (poly.getMonos().isEmpty()) {
            return "0";
        }
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Mono m : poly.getMonos()) {
            BigInteger co = m.getCoefficient();
            Poly exPoly = m.getExpoly();
            if (co.equals(BigInteger.ZERO)) {
                continue;
            }
            if (co.compareTo(BigInteger.ZERO) > 0) {
                if (!first) {
                    sb.append("+");
                }
            } else {
                sb.append("-");
                co = co.abs();
            }
            String innerExpStr = null;
            if (exPoly != null) {
                innerExpStr = new DealResult(exPoly).getResult();
                innerExpStr = (innerExpStr.equals("0")) ? null : innerExpStr;
            }
            boolean hasX = (!m.getExponent().equals(BigInteger.ZERO));
            boolean hasExp = (innerExpStr != null);
            if (co.equals(BigInteger.ONE)) {
                if (!hasX && !hasExp) {
                    sb.append("1");
                }
            } else {
                sb.append(co);
                if (hasX || hasExp) {
                    sb.append("*");
                }
            }
            if (hasX) {
                sb.append("x");
                if (!m.getExponent().equals(BigInteger.ONE)) {
                    sb.append("^").append(m.getExponent());
                }
                if (hasExp) {
                    sb.append("*");
                }
            }
            if (hasExp) {
                sb.append("exp(");
                if (innerExpStr.contains("+") || innerExpStr.contains("-") ||
                        innerExpStr.contains("*")) {
                    sb.append("(").append(innerExpStr).append(")");
                } else {
                    sb.append(innerExpStr);
                }
                sb.append(")");
            }
            first = false;
        }
        return (sb.length() == 0) ? "0" : sb.toString();
    }
}
