import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Poly {
    private ArrayList<Mono> monos = new ArrayList<>();

    public Poly() {}

    public void addMono(Mono mono) {
        monos.add(mono);
    }

    public ArrayList<Mono> getMonos() {
        return monos;
    }

    public void addPoly(Poly poly1) {
        monos.addAll(poly1.getMonos());
        Poly newPoly = new Poly();
        newPoly = this.standardPoly();
        monos.clear();
        monos.addAll(newPoly.getMonos());
    }

    public Poly add(Poly op) {
        Poly result = new Poly();
        result.monos.addAll(this.monos);
        result.addPoly(op);
        return result.standardPoly();
    }

    public Poly mulPoly(Poly poly1) {
        Poly result = new Poly();
        for (Mono op1 : this.monos) {
            for (Mono op2 : poly1.getMonos()) {
                Mono newMono = op1.mulMono(op2);
                result.addMono(newMono);
            }
        }
        return result.standardPoly();
    }

    public Poly substitute(Poly xreplacement) {
        Poly result = new Poly();
        for (Mono m : this.monos) {
            result.addPoly(m.substitute(xreplacement));
        }
        return result.standardPoly();
    }

    public Poly power(BigInteger exp) {
        Poly res = new Poly();
        res.addMono(new Mono(BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO, null));
        Poly base = this.standardPoly();
        BigInteger e = exp;
        if (e.compareTo(BigInteger.ONE) == 0) { return base; }
        while (e.compareTo(BigInteger.ZERO) > 0) {
            if (e.mod(new BigInteger("2")).equals(BigInteger.ONE)) {
                res = res.mulPoly(base);
            }
            base = base.mulPoly(base);
            e = e.divide(new BigInteger("2"));
        }
        return res;
    }

    public Poly negatePoly() {
        for (Mono op : this.monos) {
            op.negateMono();
        }
        return this;
    }

    public Poly DerPoly(String var) {
        Poly result = new Poly();
        for (Mono m : this.monos) {
            result.addPoly(m.Dermono(var));
        }
        return result.standardPoly();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Mono mono : this.monos) {
            sb.append("+");
            sb.append(mono.toString());
        }
        return sb.toString();
    }

    public Poly standardPoly() {
        Map<MonoExp, BigInteger> map = new HashMap<>();
        for (Mono m : this.monos) {
            if (m.getCoefficient().equals(BigInteger.ZERO)) {
                continue;
            }
            Poly innerStandard = null;
            if (m.getExpoly() != null) {
                innerStandard = m.getExpoly().standardPoly();
                if (innerStandard.getMonos().isEmpty()) {
                    innerStandard = null;
                }
            }
            MonoExp key = new MonoExp(m.getExponent(), m.getYexponent(),innerStandard);
            map.put(key, map.getOrDefault(key, BigInteger.ZERO).add(m.getCoefficient()));
        }
        ArrayList<Mono> sortedList = new ArrayList<>();
        for (Map.Entry<MonoExp, BigInteger> entry : map.entrySet()) {
            if (!entry.getValue().equals(BigInteger.ZERO)) {
                sortedList.add(new Mono(entry.getValue(),
                        entry.getKey().getxExponent(),
                        entry.getKey().getyExponent(),entry.getKey().getExpoly()));
            }
        }
        sortedList.sort((a, b) -> {
            int xcomp = a.getExponent().compareTo(b.getExponent());
            if (xcomp != 0) {
                return xcomp;
            }
            int ycomp = a.getYexponent().compareTo(b.getYexponent());
            if (ycomp != 0) {
                return ycomp;
            }
            String s1 = (a.getExpoly() == null) ? "" : a.getExpoly().toString();
            String s2 = (b.getExpoly() == null) ? "" : b.getExpoly().toString();
            return s1.compareTo(s2);
        });
        Poly newPoly = new Poly();
        newPoly.monos.addAll(sortedList);
        return newPoly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Poly thisStd = this.standardPoly();
        Poly otherStd = ((Poly) o).standardPoly();
        return thisStd.monos.equals(otherStd.monos);
    }

    @Override
    public int hashCode() {
        return monos.hashCode();
    }
}
