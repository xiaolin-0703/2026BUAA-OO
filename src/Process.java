public class Process {
    private String input;
    private String output;

    Process(String input) {
        this.input = input;
        this.output = process(this.input);
    }

    private String process(String input) {
        String dealString = input.replaceAll("\\s+","");
        String dealString1;
        if (dealString.charAt(0) == '+' || dealString.charAt(0) == '-') {
            dealString1 = '0' + dealString;
        } else {
            dealString1 = dealString;
        }

        while (dealString1.contains("++") || dealString1.contains("--")
            || dealString1.contains("+-") || dealString1.contains("-+")) {
            dealString1 = dealString1.replace("++", "+");
            dealString1 = dealString1.replace("--", "+");
            dealString1 = dealString1.replace("+-", "-");
            dealString1 = dealString1.replace("-+", "-");
        }

        dealString1 = dealString1.replace("**", "*");
        dealString1 = dealString1.replace("^+", "^");
        dealString1 = dealString1.replace("*+", "*");
        dealString1 = dealString1.replace("*-", "*(0-1)*");
        dealString1 = dealString1.replace("(+", "(0+");
        dealString1 = dealString1.replace("(-", "(0-");
        dealString1 = dealString1.replace("=-", "=0-");
        dealString1 = dealString1.replace("=+", "=0+");
        dealString1 = dealString1.replace(":+", ":0+");
        dealString1 = dealString1.replace(":-", ":0-");
        dealString1 = dealString1.replace("?-", "?0-");
        dealString1 = dealString1.replace("?+", "?0+");
        return dealString1;
    }

    public String getOutput() {
        return output;
    }
}
