//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public enum Stage {
    NORMAL,
    REP_ACCEPT,
    REPAIR,
    TEST;

    public Stage next() {
        Stage[] all = values();
        int nextIndex = (this.ordinal() + 1) % all.length;
        return all[nextIndex];
    }
}
