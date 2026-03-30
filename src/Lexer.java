import java.util.ArrayList;

public class Lexer {
    private final ArrayList<Token> tokens =  new ArrayList<>();
    private String input;
    private int pos = 0;
    private int cur = 0;

    public Lexer(String input) {
        this.input = input;
        dealInput(this.input);
    }

    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            ++pos;
        }
        return sb.toString();
    }

    private void dealInput(String input) {
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isDigit(c)) {
                tokens.add(new Token(getNumber()));
            } else {
                tokens.add(new Token(String.valueOf(c)));
                pos++;
            }
        }
    }

    public void next() {
        cur += 1;
    }

    public Token peek() {
        return tokens.get(cur);
    }

    public boolean notEmpty() {
        return cur < tokens.size();
    }

}
