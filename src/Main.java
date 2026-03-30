import java.util.Scanner;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();

        Process process = new Process(input);
        Lexer lexer = new Lexer(process.getOutput());

        Prase prase = new Prase(lexer);

        ExprFactor expr = prase.praseExpr();

        Poly midResult = expr.toPoly();

        DealResult dealResult = new DealResult(midResult);

        String result = dealResult.getResult();

        System.out.println(result);
    }
}