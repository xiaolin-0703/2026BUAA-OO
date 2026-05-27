//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator3.Request;
import java.util.ArrayList;

public class InputQueue {
    private ArrayList<Request> requests = new ArrayList();
    private boolean isEnd = false;

    public synchronized void addRequest(Request requset1) {
        this.requests.add(requset1);
        this.notifyAll();
    }

    public synchronized Request getRequest() throws InterruptedException {
        while (this.requests.isEmpty() && !this.isEnd) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (this.requests.isEmpty() && this.isEnd) {
            return null;
        } else {
            return (Request)this.requests.remove(0);
        }
    }

    public synchronized void setEnd() {
        this.isEnd = true;
        this.notifyAll();
    }

    public synchronized boolean hasRequest() {
        return !this.requests.isEmpty();
    }

    public synchronized boolean isEnd() {
        return this.isEnd;
    }

    public synchronized void addRequests(ArrayList<Request> kickrequests) {
        for (int i = kickrequests.size() - 1; i >= 0; --i) {
            this.requests.add(0, (Request)kickrequests.get(i));
        }

        this.notifyAll();
    }
}
