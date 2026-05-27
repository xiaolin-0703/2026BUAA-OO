//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.oocourse.elevator2.MaintRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.TimableOutput;
import java.util.ArrayList;

public class DispatcherThread extends Thread {
    private final InputQueue mainQueue;
    private final ArrayList<EleQueue> eleQueues;
    private final ArrayList<EleThread> elevators;

    public DispatcherThread(InputQueue mainQueue, ArrayList<EleQueue> eleQueue,
                            ArrayList<EleThread> elevators) {
        this.elevators = elevators;
        this.mainQueue = mainQueue;
        this.eleQueues = eleQueue;
    }

    public void kickPersons(ArrayList<Request> kickPersons) {
        if (kickPersons != null && !kickPersons.isEmpty()) {
            this.mainQueue.addRequests(kickPersons);
        }

    }

    public void signal() {
        synchronized (this.mainQueue) {
            this.mainQueue.notifyAll();
        }
    }

    public void run() {
        while (true) {
            Request request;
            try {
                request = this.nextRequest();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (request != null) {
                if (request instanceof PersonRequest) {
                    this.takePerson((PersonRequest)request);
                } else if (request instanceof MaintRequest) {
                    this.takeMaint((MaintRequest)request);
                }
            } else {
                if (this.isFinished()) {
                    this.setEnd();
                    return;
                }

                synchronized (this.mainQueue) {
                    try {
                        while (!this.mainQueue.hasRequest() && !this.isFinished()) {
                            this.mainQueue.wait();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void takePerson(PersonRequest pr) {
        int bestId = this.chooseBestElevator(pr);
        if (bestId != -1) {
            TimableOutput.println(String.format("RECEIVE-%d-%d", pr.getPersonId(), bestId));
            ((EleQueue)this.eleQueues.get(bestId - 1)).addPerson(pr);
        } else {
            this.mainQueue.addRequest(pr);
            synchronized (this.mainQueue) {
                try {
                    this.mainQueue.wait(20L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setEnd() {
        for (EleQueue eq : this.eleQueues) {
            eq.setEnd();
        }

    }

    private int chooseBestElevator(PersonRequest pr) {
        int bestId = -1;
        double minScore = Double.MAX_VALUE;

        for (int i = 0; i < this.elevators.size(); ++i) {
            EleState state = ((EleThread)this.elevators.get(i)).getEleState();
            double score = state.calScore(pr);
            if (score < minScore) {
                minScore = score;
                bestId = i + 1;
            }
        }

        return minScore == Double.MAX_VALUE ? -1 : bestId;
    }

    private Request nextRequest() throws InterruptedException {
        return this.mainQueue.getRequest();
    }

    private boolean isFinished() {
        for (int i = 0; i < this.elevators.size(); ++i) {
            EleState state = ((EleThread)this.elevators.get(i)).getEleState();
            if (state.getStage() != Stage.NORMAL) {
                return false;
            }

            if (state.getInSize() > 0 ||
                    !((EleQueue)this.eleQueues.get(i)).getwaitPersons().isEmpty() ||
                    ((EleQueue)this.eleQueues.get(i)).hasMaintRequest()) {
                return false;
            }
        }

        return !this.mainQueue.hasRequest();
    }

    private void takeMaint(MaintRequest mr) {
        int id = mr.getElevatorId();
        ((EleQueue)this.eleQueues.get(id - 1)).setMaintRequest(mr);
        this.signal();
    }
}
