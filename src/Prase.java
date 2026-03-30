import java.math.BigInteger;

public class Prase {
    private Lexer lexer;

    public Prase(Lexer lexer) {
        this.lexer = lexer;
    }

    public ExprFactor praseExpr() {
        ExprFactor expr = new ExprFactor();
        Term term = praseTerm();
        expr.addTerm(term);
        while (lexer.notEmpty() && (lexer.peek().getContent().equals("+")
            || lexer.notEmpty() && lexer.peek().getContent().equals("-"))) {
            expr.addOperator(lexer.peek());
            lexer.next();
            expr.addTerm(praseTerm());
        }
        return expr;
    }

    public Term praseTerm() {
        Term term = new Term();
        CellFactor factor = praseCell();
        term.addFactor(factor);
        while (lexer.notEmpty() && lexer.peek().getContent().equals("*")) {
            lexer.next();
            term.addFactor(praseCell());
        }
        return term;
    }

    public CellFactor praseCell() {
        CellFactor cell = new CellFactor();
        Factor factor = praseFactor();
        cell.addFactor(factor);
        boolean flag = false;
        while (lexer.notEmpty() && lexer.peek().getContent().equals("^")) {
            lexer.next();
            flag = true;
            cell.addFactor(praseFactor());
        }
        if (!flag) {
            cell.addFactor(new NumFactor(new BigInteger("1")));
        }
        return cell;
    }

    public Factor praseFactor() {
        if (lexer.peek().getContent().equals("x")) {
            VarFactor x = new VarFactor("x");
            lexer.next();
            return x;
        } else if (lexer.peek().getContent().equals("(")) {
            lexer.next();
            ExprFactor expr = praseExpr();
            lexer.next();
            return expr;
        } else if (lexer.peek().getContent().equals("e")) {
            lexer.next();
            lexer.next();//跳过'('
            ExprFactor factor = praseExpr();
            lexer.next();//跳过‘）’
            return new Exp(factor);
        } else if (lexer.peek().getContent().equals("[")) {
            lexer.next();
            lexer.next();//skip '('
            final ExprFactor factorA = praseExpr();
            lexer.next();
            lexer.next();//skip'=='
            final ExprFactor factorB = praseExpr();
            lexer.next();//skip ')'
            lexer.next();//skip'?'
            ExprFactor factorC = praseExpr();
            lexer.next();//skip ':'
            ExprFactor factorD = praseExpr();
            lexer.next();//skip ']'
            return new ChoiceFactor(factorA, factorB, factorC, factorD);
        }
        else {
            NumFactor num = new NumFactor(new BigInteger(lexer.peek().getContent()));
            lexer.next();
            return num;
        }
    }
}
