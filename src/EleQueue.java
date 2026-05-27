//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator2.MaintRequest;
import com.oocourse.elevator2.PersonRequest;
import java.util.ArrayList;
import java.util.Iterator;

public class EleQueue {
    private ArrayList<PersonRequest> waitPersons = new ArrayList();
    private MaintRequest maintRequest = null;
    private boolean isEnd = false;

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

    public synchronized void addPerson(PersonRequest personRequest) {
        this.waitPersons.add(personRequest);
        this.notifyAll();
    }

    public synchronized ArrayList<PersonRequest> getwaitPersons() {
        return new ArrayList(this.waitPersons);
    }

    public synchronized void removePerson(PersonRequest personRequest) {
        this.waitPersons.remove(personRequest);
    }

    public synchronized MaintRequest getMaintRequest() {
        return this.maintRequest;
    }

    public synchronized void setMaintRequest(MaintRequest maintRequest) {
        this.maintRequest = maintRequest;
        this.notifyAll();
    }

    public synchronized void setEnd() {
        this.isEnd = true;
        this.notifyAll();
    }

    public synchronized boolean isEnd() {
        return this.isEnd;
    }

    public synchronized boolean hasPerson() {
        return !this.waitPersons.isEmpty();
    }

    public synchronized boolean hasMaintRequest() {
        return this.maintRequest != null;
    }

    public synchronized void clearMaintRequest() {
        this.maintRequest = null;
    }

    public synchronized ArrayList<PersonRequest> ablePersons(int curFloor,
                                                             int direction, int remainWeight) {
        ArrayList<PersonRequest> boarded = new ArrayList();
        Iterator<PersonRequest> it = this.waitPersons.iterator();
        int used = 0;

        while (it.hasNext()) {
            PersonRequest p = (PersonRequest)it.next();
            int from = this.intFloor(p.getFromFloor());
            int to = this.intFloor(p.getToFloor());
            boolean sameDir = from < to == (direction == 1);
            if (from == curFloor && sameDir && used + p.getWeight() <= remainWeight) {
                boarded.add(p);
                used += p.getWeight();
                it.remove();
            }
        }

        return boarded;
    }
}
