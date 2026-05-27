import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;

class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        RequestQueue reqQueue = new RequestQueue();

        ArrayList<RequestQueue> eleQueue = new ArrayList<RequestQueue>();

        for (int i = 0; i < 6; i++) {
            eleQueue.add(new RequestQueue());
        }

        for (int i = 0; i < 6; i++) {
            EleThread elevator = new EleThread(i + 1, eleQueue.get(i));
            elevator.start();
        }
        DispatcherThread dispatcher = new DispatcherThread(reqQueue, eleQueue);
        dispatcher.start();
        InputThread inputThread = new InputThread(reqQueue);
        inputThread.start();
    }
}
