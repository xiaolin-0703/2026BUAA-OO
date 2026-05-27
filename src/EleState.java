//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator3.PersonRequest;

public class EleState {
    private int curFloor;
    private int curWeight;
    private int direction;
    private int waitSize;
    private int inSize;
    private Stage stage;
    private boolean isIdle;
    private int id;

    public EleState(int id,int curFloor, int direction, int curWeight, int waitSize,
                    int inSIze, Stage stage, boolean isIdle) {
        this.curFloor = curFloor;
        this.direction = direction;
        this.curWeight = curWeight;
        this.waitSize = waitSize;
        this.inSize = inSIze;
        this.stage = stage;
        this.isIdle = isIdle;
        this.id = id;
    }

    public int getInSize() {
        return this.inSize;
    }

    public Stage getStage() {
        return this.stage;
    }

    public double calScore(PersonRequest p) {
        final int from = this.intFloor(p.getFromFloor());
        final int to = this.intFloor(p.getToFloor());

        Stage evalStage = this.stage;
        if (this.id <= 6 && (evalStage == Stage.REC_ACCEPT || evalStage == Stage.RECYCLE)) {
            evalStage = Stage.DOUBLE;
        }
        if (this.stage == Stage.REC_ACCEPT && this.id > 6) {
            return Double.MAX_VALUE;
        }
        if (this.stage == Stage.REP_ACCEPT && this.id <= 6) {
            return Double.MAX_VALUE;
        }
        if (this.stage == Stage.UP_ACCEPT && this.id <= 6) {
            return Double.MAX_VALUE;
        }

        if (evalStage == Stage.DOUBLE || evalStage == Stage.REC_ACCEPT) {
            if (this.id <= 6 && (from < 2 || (from == 2 && to < 2))) { return Double.MAX_VALUE; }
            if (this.id > 6 && (from > 2 || (from == 2 && to > 2))) { return Double.MAX_VALUE; }
        }

        if (this.id > 6 && (this.stage == Stage.NORMAL || this.stage == Stage.UP_ACCEPT
                || this.stage == Stage.UPDATE)) {
            return Double.MAX_VALUE;
        }

        if ((evalStage != Stage.NORMAL && evalStage != Stage.DOUBLE) || this.waitSize > 5) {
            return Double.MAX_VALUE;
        } else if (this.curWeight + p.getWeight() > 400) {
            return 100000.0;
        } else {
            int reqDir = to > from ? 1 : -1;
            double score = (double)Math.abs(this.floorRank(this.curFloor) -
                    this.floorRank(from)) * (double)10.0F;
            score += (double)this.inSize * (double)4.0F;
            score += (double)this.waitSize * (double)8.0F;
            if (this.isIdle) {
                score -= (double)10.0F;
            } else {
                boolean sameDir = this.direction == reqDir;
                boolean ahead = this.direction == 1 ? this.curFloor <= from : this.curFloor >= from;
                if (sameDir && ahead) {
                    score -= (double)20.0F;
                } else if (!sameDir) {
                    score += (double)25.0F;
                } else {
                    score += (double)35.0F;
                }
            }

            return score;
        }
    }

    private int intFloor(String f) {
        if (f.startsWith("F")) {
            return Integer.parseInt(f.substring(1));
        } else {
            return f.startsWith("B") ? -Integer.parseInt(f.substring(1)) : 0;
        }
    }

    private int floorRank(int f) {
        return f < 0 ? f + 4 : f + 3;
    }
}
