//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator2.PersonRequest;

public class EleState {
    private int curFloor;
    private int curWeight;
    private int direction;
    private int waitSize;
    private int inSize;
    private Stage stage;
    private boolean isIdle;

    public EleState(int curFloor, int direction, int curWeight, int waitSize,
                    int inSIze, Stage stage, boolean isIdle) {
        this.curFloor = curFloor;
        this.direction = direction;
        this.curWeight = curWeight;
        this.waitSize = waitSize;
        this.inSize = inSIze;
        this.stage = stage;
        this.isIdle = isIdle;
    }

    public int getInSize() {
        return this.inSize;
    }

    public Stage getStage() {
        return this.stage;
    }

    public double calScore(PersonRequest p) {
        if (this.stage != Stage.NORMAL || this.waitSize > 5) {
            return Double.MAX_VALUE;
        } else if (this.curWeight + p.getWeight() > 400) {
            return Double.MAX_VALUE - 100;
        } else {
            int from = this.intFloor(p.getFromFloor());
            int to = this.intFloor(p.getToFloor());
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
