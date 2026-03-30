import java.math.BigInteger;

public class DealRefun {
    private Poly fun0;
    private Poly fun1;
    private RecurDef refun;

    private String fun0str;
    private String fun1str;
    private String refunstr;

    public DealRefun(String fun0str, String fun1str, String refunstr) {
        this.fun0str = fun0str.contains("f{0}") ? fun0str : fun1str;
        this.fun1str = fun0str.contains("f{1}") ? fun0str : fun1str;
        this.refunstr = refunstr;
        creatPoly();
    }

    public void creatPoly() {
        String[] fun0 = fun0str.split("=");
        String[] fun1 = fun1str.split("=");
        this.fun0 = getPoly(fun0[1]);
        this.fun1 = getPoly(fun1[1]);
        String[] refun = refunstr.split("=");
        praseRule(refun[1]);
        RegisterFun.registerrecurDef(this.refun);
        if (this.fun0 != null) {
            RegisterFun.registerf0Base(this.fun0);
        }
        if (this.fun1 != null) {
            RegisterFun.registerf1Base(this.fun1);
        }
    }

    public void praseRule(String text3) {
        String text = text3.replaceAll("\\s+","");
        while (text.contains("++") || text.contains("--")
                || text.contains("+-") || text.contains("-+")) {
            text = text.replace("++", "+");
            text = text.replace("--", "+");
            text = text.replace("+-", "-");
            text = text.replace("-+", "-");
        }
        int pos1 = text.indexOf("*f{n-1}(");
        BigInteger c1 = new BigInteger(text.substring(0,pos1).replace("+",""));
        int pos1End = findClosingParen(text,pos1 + 7);
        String arg1 = text.substring(pos1 + 8,pos1End);

        String text2 = text.substring(pos1End + 1);
        int pos2 = text2.indexOf("*f{n-2}(");
        BigInteger c2 = new BigInteger(text2.substring(0,pos2).replace("+",""));

        int pos2End = findClosingParen(text2,pos2 + 7);
        String arg2 = text2.substring(pos2 + 8,pos2End);

        String extraStr = text2.substring(pos2End + 1);
        Poly expr = extraStr.isEmpty() ? null : getPoly(extraStr);

        refun = new RecurDef(c1, getPoly(arg1), c2, getPoly(arg2), expr);
    }

    private Poly getPoly(String fun) {
        Process dealedFun = new Process(fun);
        Lexer lexer = new Lexer(dealedFun.getOutput());
        Poly poly = new Prase(lexer).praseExpr().toPoly();
        return poly;
    }

    private int findClosingParen(String s, int openParenIdx) {
        int count = 1;
        for (int i = openParenIdx + 1; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                count++;
            }
            else if (s.charAt(i) == ')') {
                count--;
            }
            if (count == 0) {
                return i;
            }
        }
        return -1;
    }

}
