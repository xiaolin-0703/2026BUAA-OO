import java.util.concurrent.locks.ReentrantLock;

public class Shaft {
    private int shattId;
    private Stage stage;
    private final ReentrantLock f2Lock = new ReentrantLock();

    private int mainEleFloor = 1;
    private int spareEleFloor = 1;

    public Shaft(int shattId) {
        this.shattId = shattId;
        stage = Stage.NORMAL;
    }

    public synchronized void setStage(Stage shaftStage) {
        this.stage = shaftStage;
        this.notifyAll();
    }

    public synchronized Stage getStage() {
        return stage;
    }

    public boolean acquireF2(int eleId) {
        return f2Lock.tryLock();
    }

    public void releaseF2(int eleId) {
        if (f2Lock.isHeldByCurrentThread()) {
            f2Lock.unlock();
        }
    }

    public void updateFloor(int eleId,int floor) {
        if (eleId <= 6) {
            this.mainEleFloor = floor;
        } else {
            this.spareEleFloor = floor;
        }
    }

    public boolean canMove(int eleId,int floor) {
        Stage evalStage = stage;
        if (eleId <= 6 && (stage == Stage.REC_ACCEPT || stage == Stage.RECYCLE)) {
            evalStage = Stage.DOUBLE;
        }
        if (evalStage != Stage.DOUBLE && evalStage != Stage.REC_ACCEPT) {
            return true;
        }
        if (eleId <= 6) {
            return floor >= 2 && floor <= 7;
        } else {
            return floor >= -4 && floor <= 2;
        }
    }

    public boolean needF2(int eleId) {
        if (stage != Stage.DOUBLE) {
            return false;
        }
        if (eleId <= 6 && mainEleFloor == 2 && spareEleFloor == 1) {
            return false;
        }

        return false;
    }
}
