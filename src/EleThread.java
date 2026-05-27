//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator2.MaintRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.TimableOutput;
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
    private Stage stage;
    private int moveSpeed = 400;
    private DispatcherThread dispatcher;
    private long startTime = -1L;

    public EleThread(int id, EleQueue myQueue, DispatcherThread dispatcher) {
        this.id = id;
        this.myQueue = myQueue;
        this.curFloor = 1;
        this.curWeight = 0;
        this.direction = 1;
        this.stage = Stage.NORMAL;
        this.dispatcher = dispatcher;
        this.strategy = new Strategy();
        this.persons = new ArrayList();
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

    public void run() {
        while (true) {
            if (this.stage == Stage.NORMAL && this.myQueue.hasMaintRequest()) {
                this.stage = Stage.REP_ACCEPT;
                this.startTime = System.currentTimeMillis();
                this.dispatcher.signal();
            }

            if (this.stage == Stage.REP_ACCEPT && this.curFloor == 1 && this.persons.isEmpty()) {
                this.openwork();
                this.stage = Stage.REPAIR;
            } else if (this.stage == Stage.REPAIR) {
                this.doRepair();
                this.stage = Stage.TEST;
            } else if (this.stage == Stage.TEST) {
                this.doTest();
            } else {
                Advice advice;
                synchronized (this.myQueue) {
                    advice = this.strategy.getAdvice(this.curFloor, this.direction,
                            this.myQueue, this.persons, this.curWeight, this.stage, this.startTime);
                    if (advice == Advice.WAIT) {
                        try {
                            this.myQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
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
        }
    }

    private void move() {
        try {
            Thread.sleep((long)this.moveSpeed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (this.curFloor == -1 && this.direction == 1) {
            this.curFloor = 1;
        } else if (this.curFloor == 1 && this.direction == -1) {
            this.curFloor = -1;
        } else {
            this.curFloor += this.direction;
        }

        TimableOutput.println(String.format("ARRIVE-%s-%d", this.strFloor(this.curFloor), this.id));
    }

    private boolean shouldKick(Advice advice1) {
        boolean isTimePanic = false;
        if (this.stage == Stage.REP_ACCEPT) {
            long usedTime = System.currentTimeMillis() - this.startTime;
            MaintRequest maint = this.myQueue.getMaintRequest();
            if (maint != null) {
                int targetF = this.intFloor(maint.getToFloor());
                double minMaintTime = (double)Math.abs(this.curFloor - 1) * 0.4 +
                        (double)Math.abs(targetF - 1) * 0.4 + 2.8;
                if ((double)usedTime / (double)1000.0F + minMaintTime > (double)6.0F) {
                    isTimePanic = true;
                }
            }
        }

        boolean shouldKick = advice1 == Advice.KICK || isTimePanic;
        return shouldKick;
    }

    private void open(Advice advice1) {
        String floorStr = this.strFloor(this.curFloor);
        TimableOutput.println(String.format("OPEN-%s-%d", floorStr, this.id));
        boolean shouldKick = this.shouldKick(advice1);
        Iterator<PersonRequest> itOut = this.persons.iterator();
        boolean down = false;
        ArrayList<Request> temp = new ArrayList();

        while (itOut.hasNext()) {
            PersonRequest p = (PersonRequest)itOut.next();
            int dest = this.intFloor(p.getToFloor());
            if (dest == this.curFloor) {
                TimableOutput.println(String.format("OUT-S-%d-%s-%d", p.getPersonId(),
                        floorStr, this.id));
                itOut.remove();
                this.curWeight -= p.getWeight();
                down = true;
            } else if (shouldKick) {
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
            if (!temp.isEmpty()) {
                this.dispatcher.kickPersons(temp);
            }

            this.dispatcher.signal();
        }

        if (this.stage == Stage.NORMAL) {
            ArrayList<PersonRequest> boards = this.myQueue.ablePersons(this.curFloor,
                    this.direction, 400 - this.curWeight);

            for (PersonRequest p : boards) {
                this.persons.add(p);
                this.curWeight += p.getWeight();
                TimableOutput.println(String.format("IN-%d-%s-%d", p.getPersonId(),
                        floorStr, this.id));
            }

            if (!boards.isEmpty()) {
                this.dispatcher.signal();
            }
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
        return new EleState(this.curFloor, this.direction, this.curWeight,
                this.myQueue.getwaitPersons().size(), this.persons.size(), this.stage, idle);
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
        this.stage = Stage.NORMAL;
        this.dispatcher.signal();
    }
}
