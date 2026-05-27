import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;
import java.util.Iterator;

public class EleThread extends Thread {
    private int id;
    private RequestQueue myQueue;
    private int curFloor;
    private int direction;
    private int curWeight;
    private ArrayList<PersonRequest> persons;
    private Strategy strategy;

    public EleThread(int id, RequestQueue myQueue) {
        this.id = id;
        this.myQueue = myQueue;
        this.curFloor = 1;
        this.curWeight = 0;
        this.direction = 1;
        this.strategy = new Strategy();
        this.persons = new ArrayList<>();
    }

    public String strFloor(int floor) {
        if (floor < 0) {
            return "B" + (-floor);
        } else {
            return "F" + floor;
        }
    }

    private int intFloor(String strFloor) {
        if (strFloor.startsWith("F")) {
            return Integer.parseInt(strFloor.substring(1));
        } else if (strFloor.startsWith("B")) {
            return -Integer.parseInt(strFloor.substring(1));
        } else {
            return 0;
        }
    }

    @Override
    public void run() {
        while (true) {
            Advice advice;
            synchronized (myQueue) {
                advice = strategy.getAdvice(curFloor,direction, myQueue, persons, curWeight);
                if (advice == Advice.WAIT) {
                    try {
                        myQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
            }
            if (advice == Advice.OVER) {
                break;
            } else if (advice == Advice.REVERSE) {
                direction = -direction;
            } else if (advice == Advice.MOVE) {
                move();
            } else if (advice == Advice.OPEN) {
                open();
            }
        }
    }

    private void move() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (curFloor == -1 && direction == 1) {
            curFloor = 1;
        } else if (curFloor == 1 && direction == -1) {
            curFloor = -1;
        } else {
            curFloor += direction;
        }
        TimableOutput.println(String.format("ARRIVE-%s-%d",strFloor(curFloor), id));
    }

    private void open() {
        String floorStr = strFloor(curFloor);
        TimableOutput.println(String.format("OPEN-%s-%d", floorStr, id));
        // 先下客
        Iterator<PersonRequest> itOut = persons.iterator();
        while (itOut.hasNext()) {
            PersonRequest p = itOut.next();
            if (intFloor(p.getToFloor()) == curFloor) {
                itOut.remove();
                curWeight -= p.getWeight();
                TimableOutput.println(String.format("OUT-S-%d-%s-%d", p.getPersonId(),floorStr,id));
            }
        }
        //后上客
        synchronized (myQueue) {
            Iterator<PersonRequest> itIn = myQueue.getRequests().iterator();
            while (itIn.hasNext()) {
                PersonRequest p = itIn.next();
                int from = intFloor(p.getFromFloor());
                int to = intFloor(p.getToFloor());
                boolean isSameDir = (from < to) == (direction == 1);
                if (from == curFloor && isSameDir) {
                    if (curWeight + p.getWeight() <= 400) {
                        itIn.remove();
                        persons.add(p);
                        curWeight += p.getWeight();
                        TimableOutput.println(String.format("IN-%d-%s-%d",
                                p.getPersonId(), floorStr, id));
                    }
                }
            }
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TimableOutput.println(String.format("CLOSE-%s-%d", floorStr, id));
    }
}
