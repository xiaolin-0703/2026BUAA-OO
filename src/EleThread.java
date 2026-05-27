//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator3.MaintRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;
import com.oocourse.elevator3.TimableOutput;
import java.util.ArrayList;
import java.util.Iterator;

public class EleThread extends Thread {
    private int id;
    private EleQueue myQueue;
    private int curFloor;
    private int direction;
    private int curWeight;
    private ArrayList<PersonRequest> persons;
    private Strategy strategy;
    private int moveSpeed = 400;
    private DispatcherThread dispatcher;
    private long startTime = -1L;
    private final Shaft shaft;

    public EleThread(int id, EleQueue myQueue, DispatcherThread dispatcher,Shaft shaft) {
        this.id = id;
        this.myQueue = myQueue;
        this.curFloor = 1;
        this.curWeight = 0;
        this.direction = 1;
        this.dispatcher = dispatcher;
        this.strategy = new Strategy();
        this.persons = new ArrayList();
        this.shaft = shaft;
    }

    public String strFloor(int floor) {
        return floor < 0 ? "B" + -floor : "F" + floor;
    }

    private int intFloor(String strFloor) {
        if (strFloor.startsWith("F")) {
            return Integer.parseInt(strFloor.substring(1));
        } else {
            return strFloor.startsWith("B") ? -Integer.parseInt(strFloor.substring(1)) : 0;
        }
    }

    public Shaft getShaft() {
        return shaft;
    }

    public void run() {
        while (true) {
            synchronized (this.shaft) {
                while (this.id > 6 && this.shaft.getStage() != Stage.DOUBLE &&
                        this.shaft.getStage() != Stage.REC_ACCEPT &&
                        this.shaft.getStage() != Stage.RECYCLE && !this.myQueue.isEnd()) {
                    try {
                        this.shaft.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (this.myQueue.isEnd()) {
                return;
            }
            Stage stage = shaft.getStage();
            if (stage == Stage.DOUBLE && this.myQueue.hasRecycleRequest() && this.id > 6) {
                shaft.setStage(Stage.REC_ACCEPT);
                this.startTime = this.myQueue.getSpecialStartTime();
                this.dispatcher.signal();
                continue;
            }

            if (stage == Stage.NORMAL && this.myQueue.hasMaintRequest() && this.id <= 6) {
                shaft.setStage(Stage.REP_ACCEPT);
                this.startTime = this.myQueue.getSpecialStartTime();
                this.dispatcher.signal();
            } else if (stage == Stage.REP_ACCEPT && this.id <= 6 && this.curFloor == 1 &&
                    this.persons.isEmpty()) {
                this.openwork();
                shaft.setStage(Stage.REPAIR);
            } else if (stage == Stage.REPAIR && this.id <= 6) {
                this.doRepair();
                shaft.setStage(Stage.TEST);
            } else if (stage == Stage.TEST && this.id <= 6) {
                this.doTest();
            } else if (stage == Stage.NORMAL && this.myQueue.hasUpdateRequest() && this.id <= 6) {
                shaft.setStage(Stage.UP_ACCEPT);
                this.startTime = this.myQueue.getSpecialStartTime();
            } else if (stage == Stage.UP_ACCEPT && this.id <= 6 && this.curFloor == 3
                    && this.persons.isEmpty()) {
                this.openForUpdate();
                shaft.setStage(Stage.UPDATE);
            } else if (stage == Stage.UPDATE && this.id <= 6) {
                this.doUpdate();
            }
            else if (stage == Stage.REC_ACCEPT && this.id > 6 && this.curFloor == 1
                    && this.persons.isEmpty()) {
                this.openForRecycle();
                shaft.setStage(Stage.RECYCLE);
            } else if (stage == Stage.RECYCLE && this.id > 6) {
                this.doRecycle();
            } else {
                if (this.id <= 6 && (stage == Stage.REC_ACCEPT)) {
                    stage = Stage.DOUBLE;
                }
                workNormal(stage);
            }
        }
    }

    public void workNormal(Stage stage) {
        Advice advice;
        synchronized (this.myQueue) {
            advice = this.strategy.getAdvice(this.id,this.curFloor, this.direction,
                    this.myQueue, this.persons, this.curWeight, stage, this.startTime);
            if (advice == Advice.WAIT) {
                try {
                    this.myQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        if (advice == Advice.OVER) {
            return;
        }
        if (advice == Advice.REVERSE) {
            this.direction = -this.direction;
        } else if (advice == Advice.MOVE) {
            this.move();
        } else if (advice == Advice.OPEN) {
            this.open(Advice.OPEN);
        } else if (advice == Advice.KICK) {
            this.open(Advice.KICK);
        }
    }

    private void move() {
        int nextFloor = this.curFloor + this.direction;
        if (this.curFloor == -1 && this.direction == 1) {
            nextFloor = 1;
        } else if (this.curFloor == 1 && this.direction == -1) {
            nextFloor = -1;
        }

        if (!shaft.canMove(this.id, nextFloor)) {
            try {
                Thread.sleep((long)this.moveSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.direction = -this.direction;
            return;
        }

        Stage stage = shaft.getStage();
        if (this.id <= 6 && (stage == Stage.REC_ACCEPT || stage == Stage.RECYCLE)) {
            stage = Stage.DOUBLE;
        }
        if ((stage == Stage.DOUBLE || stage == Stage.REC_ACCEPT) && nextFloor == 2) {
            if (!shaft.acquireF2(this.id)) {
                this.direction = -this.direction;
                return; }
        }
        try {
            Thread.sleep((long)this.moveSpeed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.curFloor == 2) {
            shaft.releaseF2(this.id);
        }
        this.curFloor = nextFloor;
        shaft.updateFloor(this.id, this.curFloor);
        TimableOutput.println(String.format("ARRIVE-%s-%d", this.strFloor(this.curFloor), this.id));
    }

    private boolean shouldKick(Advice advice1) {
        return advice1 == Advice.KICK;
    }

    private void open(Advice advice1) {
        String floorStr = this.strFloor(this.curFloor);
        TimableOutput.println(String.format("OPEN-%s-%d", floorStr, this.id));
        boolean shouldKick = this.shouldKick(advice1);
        Iterator<PersonRequest> itOut = this.persons.iterator();
        boolean down = false;
        ArrayList<Request> temp = new ArrayList();
        Stage stage = shaft.getStage();
        if (this.id <= 6 && (stage == Stage.REC_ACCEPT || stage == Stage.RECYCLE)) {
            stage = Stage.DOUBLE;
        }
        while (itOut.hasNext()) {
            PersonRequest p = (PersonRequest)itOut.next();
            int dest = this.intFloor(p.getToFloor());
            boolean forceTransfer = false;
            if (stage == Stage.DOUBLE || stage == Stage.REC_ACCEPT) {
                if (this.id > 6 && this.curFloor == 2 && dest > 2) { forceTransfer = true; }
                if (this.id <= 6 && this.curFloor == 2 && dest < 2) { forceTransfer = true; }
                if (stage == Stage.REC_ACCEPT && this.id > 6 && this.curFloor == 1) {
                    forceTransfer = true; }
            }
            if (dest == this.curFloor) {
                TimableOutput.println(String.format("OUT-S-%d-%s-%d", p.getPersonId(),
                        floorStr, this.id));
                itOut.remove();
                this.curWeight -= p.getWeight();
                down = true;
            } else if (shouldKick || forceTransfer) {
                TimableOutput.println(String.format("OUT-F-%d-%s-%d", p.getPersonId(),
                        floorStr, this.id));
                PersonRequest newRequest = new PersonRequest(floorStr, p.getToFloor(),
                        p.getPersonId(), p.getWeight());
                temp.add(newRequest);
                itOut.remove();
                this.curWeight -= p.getWeight();
                down = true;
            }
        }

        if (down) {
            if (!temp.isEmpty()) { this.dispatcher.kickPersons(temp); }
            this.dispatcher.signal();
        }

        if (stage == Stage.NORMAL || stage == Stage.DOUBLE) {
            ArrayList<PersonRequest> boards = this.myQueue.ablePersons(this.curFloor,
                    this.direction, 400 - this.curWeight);
            for (PersonRequest p : boards) {
                this.persons.add(p);
                this.curWeight += p.getWeight();
                TimableOutput.println(String.format("IN-%d-%s-%d", p.getPersonId(),
                        floorStr, this.id));
            }
            if (!boards.isEmpty()) { this.dispatcher.signal(); }
        }
        try {
            Thread.sleep(400L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TimableOutput.println(String.format("CLOSE-%s-%d", floorStr, this.id));
    }

    public synchronized EleState getEleState() {
        boolean idle = this.persons.isEmpty() && !this.myQueue.hasPerson();
        Stage currentStage = shaft.getStage();
        if (this.id > 6 && this.myQueue.hasRecycleRequest()) {
            currentStage = Stage.REC_ACCEPT;
        } else if (this.id <= 6 && this.myQueue.hasUpdateRequest()) {
            currentStage = Stage.UP_ACCEPT;
        } else if (this.id <= 6 && this.myQueue.hasMaintRequest()) {
            currentStage = Stage.REP_ACCEPT;
        }
        return new EleState(this.id,this.curFloor, this.direction, this.curWeight,
                this.myQueue.getwaitPersons().size(), this.persons.size(), currentStage, idle);
    }

    public void openwork() {
        String strFloor = this.strFloor(this.curFloor);
        TimableOutput.println(String.format("OPEN-%s-%d", strFloor, this.id));
        ArrayList<PersonRequest> kicks = this.myQueue.getwaitPersons();

        for (PersonRequest p : kicks) {
            this.myQueue.removePerson(p);
        }

        MaintRequest maint = this.myQueue.getMaintRequest();
        TimableOutput.println(String.format("IN-%d-%s-%d", maint.getWorkerId(), strFloor, this.id));

        try {
            Thread.sleep(400L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TimableOutput.println(String.format("CLOSE-%s-%d", strFloor, this.id));
        TimableOutput.println(String.format("MAINT1-BEGIN-%d", this.id));
        if (!kicks.isEmpty()) {
            this.dispatcher.kickPersons(new ArrayList(kicks));
        }

    }

    private void doRepair() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TimableOutput.println(String.format("MAINT2-BEGIN-%d", this.id));
    }

    private void doTest() {
        this.moveSpeed = 200;
        MaintRequest maint = this.myQueue.getMaintRequest();
        int targetFloor = this.intFloor(maint.getToFloor());

        for (int testDir = targetFloor > this.curFloor ? 1 : -1; this.curFloor != targetFloor;
            TimableOutput.println(String.format("ARRIVE-%s-%d",
                    this.strFloor(this.curFloor), this.id))) {
            try {
                Thread.sleep((long)this.moveSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (this.curFloor == -1 && testDir == 1) {
                this.curFloor = 1;
            } else if (this.curFloor == 1 && testDir == -1) {
                this.curFloor = -1;
            } else {
                this.curFloor += testDir;
            }
        }

        for (int var9 = 1 > this.curFloor ? 1 : -1; this.curFloor != 1;
            TimableOutput.println(String.format("ARRIVE-%s-%d",
                    this.strFloor(this.curFloor), this.id))) {
            try {
                Thread.sleep((long)this.moveSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (this.curFloor == -1 && var9 == 1) {
                this.curFloor = 1;
            } else if (this.curFloor == 1 && var9 == -1) {
                this.curFloor = -1;
            } else {
                this.curFloor += var9;
            }
        }

        this.moveSpeed = 400;
        String floorStr = this.strFloor(this.curFloor);
        TimableOutput.println(String.format("OPEN-%s-%d", floorStr, this.id));
        TimableOutput.println(String.format("OUT-S-%d-%s-%d", maint.getWorkerId(),
                floorStr, this.id));

        try {
            Thread.sleep(400L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TimableOutput.println(String.format("CLOSE-%s-%d", floorStr, this.id));
        TimableOutput.println(String.format("MAINT-END-%d", this.id));
        this.myQueue.clearMaintRequest();
        shaft.setStage(Stage.NORMAL);
        this.dispatcher.signal();
    }

    private void doUpdate() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("UPDATE-END-%d", this.id));
        this.myQueue.clearUpdateRequest();
        this.shaft.setStage(Stage.DOUBLE);
        this.dispatcher.signal();
    }

    public void openForUpdate() {
        String strFloor = this.strFloor(this.curFloor);
        ArrayList<PersonRequest> kicks = this.myQueue.getwaitPersons();
        for (PersonRequest p : kicks) {
            this.myQueue.removePerson(p);
        }
        TimableOutput.println(String.format("UPDATE-BEGIN-%d", this.id));
        if (!kicks.isEmpty()) {
            this.dispatcher.kickPersons(new ArrayList<>(kicks));
        }
    }

    public void openForRecycle() {
        String strFloor = this.strFloor(this.curFloor);
        ArrayList<PersonRequest> kicks = this.myQueue.getwaitPersons();
        for (PersonRequest p : kicks) {
            this.myQueue.removePerson(p);
        }
        TimableOutput.println(String.format("RECYCLE-BEGIN-%d", this.id));
        if (!kicks.isEmpty()) {
            this.dispatcher.kickPersons(new ArrayList<>(kicks));
        }
    }

    private void doRecycle() {
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("RECYCLE-END-%d", this.id));
        this.myQueue.clearRecycleRequest();
        this.shaft.setStage(Stage.NORMAL);
        this.dispatcher.signal();
    }
}
