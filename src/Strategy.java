import com.oocourse.elevator1.PersonRequest;

import java.util.ArrayList;

public class Strategy {

    private int intFloor(String strFloor) {
        if (strFloor.startsWith("F")) {
            return Integer.parseInt(strFloor.substring(1));
        } else if (strFloor.startsWith("B")) {
            return -Integer.parseInt(strFloor.substring(1));
        } else {
            return 0;
        }
    }

    private boolean isUp(int fromFloor,int toFloor) {
        return fromFloor < toFloor;
    }

    private boolean toOut(int curFloor,ArrayList<PersonRequest> persons) {
        for (PersonRequest person : persons) {
            if (intFloor(person.getToFloor()) == curFloor) {
                return true;
            }
        }
        return false;
    }

    private boolean toIn(int curFloor,int direction,int curWeight,
                         ArrayList<PersonRequest> waitingPersons) {
        for (PersonRequest person : waitingPersons) {
            int from = intFloor(person.getFromFloor());
            int to = intFloor(person.getToFloor());
            boolean sameToward = (isUp(from,to) == (direction == 1));
            if (sameToward && from == curFloor && curWeight + person.getWeight() <= 400) {
                return true;
            }
        }
        return false;
    }

    private boolean reqAhead(int curFloor,int direction, ArrayList<PersonRequest> persons,
                             ArrayList<PersonRequest> waitingPersons) {
        for (PersonRequest person : persons) {
            int to = intFloor(person.getToFloor());
            if (direction == 1 && to > curFloor) {
                return true;
            }
            if (direction == -1 && to < curFloor) {
                return true;
            }
        }
        for (PersonRequest person : waitingPersons) {
            int from = intFloor(person.getFromFloor());
            if (direction == 1 && from > curFloor) {
                return true;
            }
            if (direction == -1 && from < curFloor) {
                return true;
            }
        }
        return false;
    }

    public Advice getAdvice(int curFloor,int direction,RequestQueue myQueue,
                            ArrayList<PersonRequest> persons,int curWeight) {
        ArrayList<PersonRequest> waitingPersons = myQueue.getRequests();
        if (toOut(curFloor,persons)) {
            return Advice.OPEN;
        }
        if (toIn(curFloor,direction,curWeight,waitingPersons)) {
            return Advice.OPEN;
        }
        if (reqAhead(curFloor,+direction,persons,waitingPersons)) {
            return Advice.MOVE;
        }
        if (reqAhead(curFloor,-direction,persons,waitingPersons)) {
            return Advice.REVERSE;
        }
        if (myQueue.isEnd() && persons.isEmpty() && waitingPersons.isEmpty())  {
            return Advice.OVER;
        }
        if (!persons.isEmpty() || !waitingPersons.isEmpty()) {
            return Advice.REVERSE;
        }
        return Advice.WAIT;
    }

}
