public class FunDef {
    private String inputFun;
    private String outputFun;

    public FunDef(String inputFun) {
        this.inputFun = inputFun;
        getfun();
    }

    public void getfun() {
        String [] str = inputFun.split("=");
        outputFun = str[1];
        Process process = new Process(outputFun);
        Lexer lexer = new Lexer(process.getOutput());
        Poly fun = new Prase(lexer).praseExpr().toPoly().standardPoly();
        RegisterFun.registernorFun(fun);
    }

    public String getOutputFun() {
        return '(' + outputFun + ')';
    }

}

