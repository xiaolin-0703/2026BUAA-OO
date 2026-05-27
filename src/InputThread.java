//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.Request;
import java.io.IOException;

public class InputThread extends Thread {
    private InputQueue mainQueue;

    public InputThread(InputQueue requestQueue) {
        this.mainQueue = requestQueue;
    }

    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);

        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                this.mainQueue.setEnd();
                try {
                    elevatorInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }

            this.mainQueue.addRequest(request);
        }
    }
}
