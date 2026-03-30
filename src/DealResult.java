import java.math.BigInteger;
import java.util.HashMap;

public class DealResult {
    private Poly poly = new Poly();
    private HashMap<Integer,BigInteger> result =  new HashMap<>();

    public DealResult(Poly midResult) {
        this.poly = midResult;
        merge();
    }

    public void merge() {
        for (Mono mono : this.poly.getMonos()) {
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
    }

    public String getResult() {
        StringBuilder sb = new StringBuilder();
        for (Integer exponent : result.keySet()) {
            BigInteger coefficient = result.get(exponent);
            if (coefficient.compareTo(BigInteger.ZERO) == 0) {
                continue;
            }
            if (exponent.equals(0)) {
                sb.append(coefficient);
                sb.append("+");
            } else if (exponent.equals(1)) {
                if (coefficient.compareTo(BigInteger.ONE) != 0) {
                    if (coefficient.equals(BigInteger.valueOf(-1))) {
                        sb.append("-");
                    } else {
                        sb.append(coefficient);
                        sb.append("*");
                    }
                }
                sb.append("x");
                sb.append("+");
            } else {
                if (coefficient.compareTo(BigInteger.ONE) != 0) {
                    if (coefficient.equals(BigInteger.valueOf(-1))) {
                        sb.append("-");
                    } else {
                        sb.append(coefficient);
                        sb.append("*");
                    }
                }
                sb.append("x^");
                sb.append(exponent);
                sb.append("+");
            }
        }

        String result = sb.toString();
        result = result.replace("+-","-");
        if (result.isEmpty()) {
            return "0";
        }
        return result.substring(0,result.length() - 1);
    }
}
