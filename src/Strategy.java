//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator2.MaintRequest;
import com.oocourse.elevator2.PersonRequest;
import java.util.ArrayList;

public class Strategy {
    private int intFloor(String strFloor) {
        if (strFloor.startsWith("F")) {
            return Integer.parseInt(strFloor.substring(1));
        } else {
            return strFloor.startsWith("B") ? -Integer.parseInt(strFloor.substring(1)) : 0;
        }
    }

    private boolean isUp(int fromFloor, int toFloor) {
        return fromFloor < toFloor;
    }

    private boolean toOut(int curFloor, ArrayList<PersonRequest> persons) {
        for (PersonRequest person : persons) {
            if (this.intFloor(person.getToFloor()) == curFloor) {
                return true;
            }
        }

        return false;
    }

    private boolean toIn(int curFloor, int direction, int curWeight, ArrayList<PersonRequest>
            waitingPersons) {
        for (PersonRequest person : waitingPersons) {
            int from = this.intFloor(person.getFromFloor());
            int to = this.intFloor(person.getToFloor());
            boolean sameToward = this.isUp(from, to) == (direction == 1);
            if (sameToward && from == curFloor && curWeight + person.getWeight() <= 400) {
                return true;
            }
        }

        return false;
    }

    private boolean reqAhead(int curFloor, int direction, ArrayList<PersonRequest> persons,
                             ArrayList<PersonRequest> waitingPersons) {
        for (PersonRequest person : persons) {
            int to = this.intFloor(person.getToFloor());
            if (direction == 1 && to > curFloor) {
                return true;
            }

            if (direction == -1 && to < curFloor) {
                return true;
            }
        }

        for (PersonRequest person : waitingPersons) {
            int from = this.intFloor(person.getFromFloor());
            if (direction == 1 && from > curFloor) {
                return true;
            }

            if (direction == -1 && from < curFloor) {
                return true;
            }
        }

        return false;
    }

    public Advice getAdvice(int curFloor, int direction, EleQueue myQueue,
                            ArrayList<PersonRequest> persons, int curWeight, Stage stage,
                            long startTime) {
        ArrayList<PersonRequest> waitingPersons = myQueue.getwaitPersons();
        if (stage == Stage.REP_ACCEPT && !persons.isEmpty()) {
            long usedTime = System.currentTimeMillis() - startTime;
            MaintRequest maint = myQueue.getMaintRequest();
            int targetF = this.intFloor(maint.getToFloor());
            double minMaintTime = (double)Math.abs(curFloor - 1) * 0.4 +
                    (double)Math.abs(targetF - 1) * 0.4 + 2.8;
            if ((double)usedTime / (double)1000.0F + minMaintTime > 5.0) {
                return Advice.KICK;
            }
        }

        if (this.toOut(curFloor, persons)) {
            return Advice.OPEN;
        } else if (stage == Stage.REP_ACCEPT) {
            if (persons.isEmpty()) {
                if (curFloor == 1) {
                    return Advice.OPEN;
                } else {
                    int targetDir = 1 > curFloor ? 1 : -1;
                    return direction == targetDir ? Advice.MOVE : Advice.REVERSE;
                }
            } else {
                return this.reqAhead(curFloor, direction, persons, new ArrayList()) ?
                        Advice.MOVE : Advice.REVERSE;
            }
        } else if (this.toIn(curFloor, direction, curWeight, waitingPersons)) {
            return Advice.OPEN;
        } else if (this.reqAhead(curFloor, direction, persons, waitingPersons)) {
            return Advice.MOVE;
        } else if (this.reqAhead(curFloor, -direction, persons, waitingPersons)) {
            return Advice.REVERSE;
        } else if (myQueue.isEnd() && persons.isEmpty() && waitingPersons.isEmpty()) {
            return Advice.OVER;
        } else {
            return persons.isEmpty() && waitingPersons.isEmpty() ? Advice.WAIT : Advice.REVERSE;
        }
    }
}
