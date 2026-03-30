import java.util.Scanner;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FunDef fun;
        int n =  sc.nextInt();
        sc.nextLine();
        if (n == 1) {
            String inputfun = sc.nextLine();
            fun = new FunDef(inputfun);
        } else {
            fun = new FunDef("f(x) = x");
        }

        String outputFun = fun.getOutputFun();

        String input =  sc.nextLine();

        Process process = new Process(input);

        Lexer lexer = new Lexer(process.getOutput(),outputFun);

        Prase prase = new Prase(lexer);

        ExprFactor expr = prase.praseExpr();

        Poly poly = expr.toPoly();

        DealResult dealResult = new DealResult(poly);

        String result = dealResult.getResult();

        if (result.charAt(0) == '+') {
            result = result.substring(1);
        }
        System.out.println(result);
    }
}