import com.oocourse.elevator1.PersonRequest;

import java.util.ArrayList;

public class RequestQueue {
    private ArrayList<PersonRequest> requests;
    private boolean isEnd;

    public RequestQueue() {
        this.requests = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void addRequest(PersonRequest personRequest) {
        this.requests.add(personRequest);
        notifyAll();
    }

    public synchronized PersonRequest getRequest() throws InterruptedException {
        while (requests.isEmpty() && !isEnd) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (requests.isEmpty() && isEnd) {
            return null;
        }
        return requests.remove(0);
    }

    public synchronized void setEnd() {
        isEnd = true;
        notifyAll();
    }

    public synchronized boolean isEnd() {
        return isEnd;
    }

    public synchronized ArrayList<PersonRequest> getRequests() {
        return requests;
    }
}
