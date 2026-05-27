//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator2.TimableOutput;
import java.util.ArrayList;

class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        InputQueue mainQueue = new InputQueue();
        ArrayList<EleQueue> eleQueue = new ArrayList();
        ArrayList<EleThread> elevators = new ArrayList();

        for (int i = 0; i < 6; ++i) {
            eleQueue.add(new EleQueue());
        }

        DispatcherThread dispatcher = new DispatcherThread(mainQueue, eleQueue, elevators);

        for (int i = 0; i < 6; ++i) {
            EleThread elevator = new EleThread(i + 1, (EleQueue)eleQueue.get(i), dispatcher);
            elevators.add(elevator);
            elevator.start();
        }

        dispatcher.start();
        InputThread inputThread = new InputThread(mainQueue);
        inputThread.start();
    }
}
