import java.util.ArrayList;

public class Lexer {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int cur = 0;
    private String fun;

    public Lexer(String input, String fun) {
        this.fun = fun;
        lexString(input);
    }

    private void lexString(String text) {
        int pos = 0;
        while (pos < text.length()) {
            char c = text.charAt(pos);
            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                    sb.append(text.charAt(pos));
                    pos++;
                }
                tokens.add(new Token(sb.toString()));
            } else if (c == 'e') {
                tokens.add(new Token("e"));
                pos += 3;
            } else if (c == 'f') {
                pos += 2;
                int count = 1;
                int start = pos;
                while (pos < text.length() && count > 0) {
                    if (text.charAt(pos) == '(') {
                        count++;
                    }
                    else if (text.charAt(pos) == ')') {
                        count--;
                    }
                    pos++;
                }
                String arg = text.substring(start, pos - 1);
                String expanded = substitute(fun, arg);

                lexString("(" + expanded + ")");
            } else {
                tokens.add(new Token(String.valueOf(c)));
                pos++;
            }
        }
    }

    private String substitute(String funDef, String arg) {
        StringBuilder sb = new StringBuilder();
        int k = 0;
        while (k < funDef.length()) {
            char c = funDef.charAt(k);
            if (c == 'e' && k + 2 < funDef.length() && funDef.charAt(k + 1) == 'x'
                    && funDef.charAt(k + 2) == 'p') {
                sb.append("exp");
                k += 3;
            } else if (c == 'x') {
                sb.append("(").append(arg).append(")");
                k++;
            } else {
                sb.append(c);
                k++;
            }
        }
        return sb.toString();
    }

    public void next() { cur += 1; }

    public Token peek() { return tokens.get(cur); }

    public boolean notEmpty() { return cur < tokens.size(); }
}