import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;

public class DispatcherThread extends Thread {
    private RequestQueue reqQueue;
    private ArrayList<RequestQueue> eleQueue;

    public DispatcherThread(RequestQueue reqQueue,ArrayList<RequestQueue> eleQueue) {
        this.reqQueue = reqQueue;
        this.eleQueue = eleQueue;
    }

    @Override
    public void run() {
        PersonRequest personRequest = null;
        while (true) {
            try {
                personRequest =  reqQueue.getRequest();
                if (personRequest == null) {
                    for (RequestQueue eleQueue : eleQueue) {
                        eleQueue.setEnd();
                    }
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int eleId = personRequest.getElevatorId();
            int personId = personRequest.getPersonId();

            TimableOutput.println(String.format("RECEIVE-%d-%d", personId, eleId));
            eleQueue.get(eleId - 1).addRequest(personRequest);
        }
    }
}
