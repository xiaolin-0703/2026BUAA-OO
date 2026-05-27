import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.Request;

import java.io.IOException;

public class InputThread extends Thread {
    private RequestQueue reqQueue;

    public InputThread(RequestQueue requestQueue) {
        this.reqQueue = requestQueue;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                reqQueue.setEnd();
                break;
            } else {
                if (request instanceof PersonRequest) {
                    PersonRequest personRequest = (PersonRequest) request;
                    reqQueue.addRequest(personRequest);
                }
            }
        }
        try {
            elevatorInput.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
}
