//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public enum Stage {
    NORMAL,
    REP_ACCEPT,
    REPAIR,
    TEST,

    UP_ACCEPT,
    UPDATE,
    DOUBLE,
    REC_ACCEPT,
    RECYCLE;

    public Stage next() {
        Stage[] all = values();
        int nextIndex = (this.ordinal() + 1) % all.length;
        return all[nextIndex];
    }
}
