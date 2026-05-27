//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator3.TimableOutput;
import java.util.ArrayList;

class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        InputQueue mainQueue = new InputQueue();
        ArrayList<EleQueue> eleQueue = new ArrayList();
        ArrayList<EleThread> elevators = new ArrayList();

        for (int i = 0; i < 12; ++i) {
            eleQueue.add(new EleQueue());
        }

        DispatcherThread dispatcher = new DispatcherThread(mainQueue, eleQueue, elevators);
        Shaft[] shafts = new Shaft[6];

        for (int i = 0; i < 6; ++i) {
            shafts[i] = new Shaft(i + 1);
            EleThread elevator = new EleThread(i + 1, eleQueue.get(i), dispatcher, shafts[i]);
            elevators.add(elevator);
        }

        for (int i = 0; i < 6; ++i) {
            EleThread spareEle = new EleThread(i + 7, eleQueue.get(i + 6), dispatcher, shafts[i]);
            elevators.add(spareEle);
            elevators.get(i).start();
            spareEle.start();
        }

        dispatcher.start();
        InputThread inputThread = new InputThread(mainQueue);
        inputThread.start();
    }
}
