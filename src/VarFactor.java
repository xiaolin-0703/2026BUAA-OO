import java.math.BigInteger;

public class VarFactor implements Factor {

    private String element;

    public VarFactor(String input) {
        element = input;
    }

    public String toString() {
        return element;
    }

    public Poly toPoly() {
        Mono newMono = new Mono(BigInteger.ONE,1);
        Poly poly = new Poly();
        poly.addMono(newMono);
        return poly;
    }
}
