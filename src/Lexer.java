import java.util.ArrayList;

public class Lexer {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int cur = 0;

    public Lexer(String input) {
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
                tokens.add(new Token("exp"));
                pos += 3;
            } else if (c == 'f') {
                pos++;
                char c1 = text.charAt(pos);
                if (c1 == '(') {
                    tokens.add(new Token("f"));
                } else if (c1 == '{') {
                    tokens.add(new Token("refun"));//将递推函数用refun表示
                }
            } else if (c == 'g') {
                tokens.add(new Token("grad"));
                pos += 4;
            } else if (c == 'd') {
                pos++;
                char c1 = text.charAt(pos);
                if (c1 == 'x') {
                    tokens.add(new Token("dx"));
                } else {
                    tokens.add(new Token("dy"));
                }
                pos++;
            }
            else {
                tokens.add(new Token(String.valueOf(c)));
                pos++;
            }
        }
    }

    public void next() { cur += 1; }

    public Token peek() { return tokens.get(cur); }

    public boolean notEmpty() { return cur < tokens.size(); }
}