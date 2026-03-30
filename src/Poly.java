import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

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
    }

    public void mulPoly(Poly poly1) {
        ArrayList<Mono> monos1 = new ArrayList<>();
        for (Mono op1 : this.monos) {
            for (Mono op2 : poly1.getMonos()) {
                Mono newMono = op1.mulMono(op2);
                monos1.add(newMono);
            }
        }
        monos.clear();
        monos.addAll(monos1);
        merge();
    }

    public void exPoly(int exp) {
        ArrayList<Mono> monos1 = new ArrayList<>();
        if (exp == 0) {
            Mono newMono = new Mono(BigInteger.ONE,0);
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Mono mono : this.monos) {
            if (mono.getSign() == 1) {
                sb.append("+");
            } else {
                sb.append("-");
            }
            sb.append(mono.toString());
        }

        return sb.toString();
    }

    public void merge() {
        HashMap<Integer,BigInteger> result = new HashMap<>();
        for (Mono mono : this.getMonos()) {
            int exponent =  mono.getExponent();
            BigInteger sign = new BigInteger(Integer.toString(mono.getSign()));
            if (result.containsKey(exponent)) {
                BigInteger coefficient = mono.getCoefficient();
                BigInteger newCoefficient = result.get(exponent).add(coefficient.multiply(sign));
                result.put(exponent,newCoefficient);
            } else {
                result.put(exponent,mono.getCoefficient().multiply(sign));
            }
        }
        monos.clear();
        for (Integer key : result.keySet()) {
            BigInteger coefficient = result.get(key);
            int exponent;
            exponent = key.intValue();
            Mono newMono = new Mono(coefficient,exponent);
            monos.add(newMono);
        }
    }

}
