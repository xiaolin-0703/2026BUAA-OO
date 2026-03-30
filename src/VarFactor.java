import java.math.BigInteger;

public class VarFactor implements Factor {

    private String element;

    public VarFactor(String input) {
        element = input;
    }

    public Poly toPoly() {
        Mono newMono = null;
        if (element.equals("x")) {
            newMono = new Mono(BigInteger.ONE,BigInteger.ONE,BigInteger.ZERO,null);
        } else if (element.equals("y")) {
            newMono = new Mono(BigInteger.ONE,BigInteger.ZERO,BigInteger.ONE,null);
        }
        Poly poly = new Poly();
        poly.addMono(newMono);
        return poly;
    }
}
