import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DealResult {
    private Poly poly;

    public DealResult(Poly midResult) {
        this.poly = midResult.standardPoly();
    }

    public String getResult() {
        if (poly.getMonos().isEmpty()) {
            return "0";
        }
        List<Mono> monos = poly.getMonos();
        for (int i = 0; i < monos.size(); i++) {
            if (monos.get(i).getCoefficient().compareTo(BigInteger.ZERO) > 0) {
                Mono temp = monos.get(0);
                monos.set(0, monos.get(i));
                monos.set(i, temp);
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Mono m : monos) {
            BigInteger co = m.getCoefficient();
            if (co.equals(BigInteger.ZERO)) {
                continue;
            }
            if (co.compareTo(BigInteger.ZERO) > 0) {
                if (!first) {
                    sb.append("+");
                }
            } else {
                sb.append("-");
            }
            first = false;
            List<String> factors = new ArrayList<>();
            BigInteger xexp = m.getExponent();
            if (xexp.compareTo(BigInteger.ZERO) > 0) {
                factors.add(xexp.equals(BigInteger.ONE) ? "x" : "x^" + xexp);
            }

            BigInteger yexp = m.getYexponent();
            if (yexp != null && yexp.compareTo(BigInteger.ZERO) > 0) {
                factors.add(yexp.equals(BigInteger.ONE) ? "y" : "y^" + yexp);
            }
            dealExpoly(m, factors);
            BigInteger absCo = co.abs();
            if (factors.isEmpty()) {
                sb.append(absCo);
            } else {
                if (!absCo.equals(BigInteger.ONE)) {
                    sb.append(absCo).append("*");
                }
                sb.append(String.join("*", factors));
            }
        }
        return sb.length() == 0 ? "0" : sb.toString();
    }

    private void dealExpoly(Mono m,List<String> factors) {
        Poly exPoly = m.getExpoly();
        if (exPoly != null) {
            String inExp1 = new DealResult(exPoly).getResult();
            if (!inExp1.equals("0")) {
                String opt1 = judge(exPoly) ? "exp((" + inExp1 + "))" : "exp(" + inExp1 + ")";
                BigInteger gcd = BigInteger.ONE;
                if (gcd.compareTo(BigInteger.ONE) > 0) {
                    Poly simplified = dividePoly(exPoly, gcd);
                    String inExp2 = new DealResult(simplified).getResult();
                    String opt2 = judge(simplified) ?
                            "exp((" + inExp2 + "))^" + gcd :
                            "exp(" + inExp2 + ")^" + gcd;

                    if (opt2.length() < opt1.length()) {
                        factors.add(opt2);
                    } else {
                        factors.add(opt1);
                    }
                } else {
                    factors.add(opt1);
                }
            }
        }
    }

    private boolean judge(Poly exPoly) {
        boolean needParens = true;
        if (exPoly.getMonos().size() == 1) {
            Mono innerMono = exPoly.getMonos().get(0);
            BigInteger c = innerMono.getCoefficient();
            BigInteger x = innerMono.getExponent();
            BigInteger y = innerMono.getYexponent();
            Poly e = innerMono.getExpoly();
            boolean noX = x.equals(BigInteger.ZERO);
            boolean noY = (y == null || y.equals(BigInteger.ZERO));
            boolean noE = (e == null);
            if (noX && noY && noE) {
                needParens = false;
            } else if (c.equals(BigInteger.ONE)) {
                int factorCount = 0;
                if (!noX) {
                    factorCount++;
                }
                if (!noY) {
                    factorCount++;
                }
                if (!noE) {
                    factorCount++;
                }
                if (factorCount == 1) {
                    needParens = false;
                }
            }
        }
        return needParens;
    }

    private BigInteger getGcd(BigInteger a, BigInteger b) {
        return a.gcd(b);
    }

    private BigInteger getPolyGcd(Poly poly) {
        if (poly.getMonos().isEmpty()) {
            return BigInteger.ONE;
        }
        BigInteger gcd = poly.getMonos().get(0).getCoefficient().abs();
        for (Mono m : poly.getMonos()) {
            gcd = getGcd(gcd, m.getCoefficient().abs());
            if (gcd.equals(BigInteger.ONE)) {
                break;
            }
        }
        return gcd;
    }

    private Poly dividePoly(Poly poly, BigInteger divisor) {
        Poly newPoly = new Poly();
        for (Mono m : poly.getMonos()) {
            newPoly.addMono(new Mono(
                    m.getCoefficient().divide(divisor),
                    m.getExponent(),
                    m.getYexponent(),
                    m.getExpoly()
            ));
        }
        return newPoly.standardPoly();
    }

}