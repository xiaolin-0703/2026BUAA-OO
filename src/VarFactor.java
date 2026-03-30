import java.math.BigInteger;

public class VarFactor implements Factor {

    private String element;

    public VarFactor(String input) {
        element = input;
    }

    public Poly toPoly() {
        Mono newMono = new Mono(BigInteger.ONE,BigInteger.ONE,null);
        Poly poly = new Poly();
        poly.addMono(newMono);
        return poly;
    }
}
