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
        ArrayList<Mono> monos1 = new ArrayList<>();
        for (Mono op1 : this.monos) {
            for (Mono op2 : poly1.getMonos()) {
                Mono newMono = op1.mulMono(op2);
                monos1.add(newMono);
            }
        }
        monos.clear();
        monos.addAll(monos1);
        Poly poly2 = this.standardPoly();
        monos.clear();
        monos.addAll(poly2.getMonos());
        return poly2;
    }

    public void exPoly(int exp) {
        ArrayList<Mono> monos1 = new ArrayList<>();
        if (exp == 0) {
            Mono newMono = new Mono(BigInteger.ONE,BigInteger.ZERO,null);
            monos.clear();
            monos.add(newMono);
            return;
        }
        Poly poly1 = new Poly();
        poly1.monos.addAll(this.monos);
        for (int i = 1;i < exp;i++) {
            this.mulPoly(poly1);
        }

    }

    public Poly negatePoly() {
        for (Mono op : this.monos) {
            op.negateMono();
        }
        return this;
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
            MonoExp key = new MonoExp(m.getExponent(), innerStandard);
            map.put(key, map.getOrDefault(key, BigInteger.ZERO).add(m.getCoefficient()));
        }
        ArrayList<Mono> sortedList = new ArrayList<>();
        for (Map.Entry<MonoExp, BigInteger> entry : map.entrySet()) {
            if (!entry.getValue().equals(BigInteger.ZERO)) {
                sortedList.add(new Mono(entry.getValue(),
                        entry.getKey().getxExponent(), entry.getKey().getExpoly()));
            }
        }
        sortedList.sort((a, b) -> {
            int xcomp = a.getExponent().compareTo(b.getExponent());
            if (xcomp != 0) {
                return xcomp;
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
