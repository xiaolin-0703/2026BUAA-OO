import com.oocourse.elevator3.MaintRequest;
import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;

public class Strategy {
    private static final double MOVE_TIME_NORMAL = 0.4;
    private static final double MOVE_TIME_TEST = 0.2;
    private static final double DOOR_TIME = 0.4;
    private static final double SPECIAL_WAIT = 1.0;

    private int intFloor(String strFloor) {
        if (strFloor.startsWith("F")) {
            return Integer.parseInt(strFloor.substring(1));
        } else if (strFloor.startsWith("B")) {
            return -Integer.parseInt(strFloor.substring(1));
        } else {
            return 0;
        }
    }

    private boolean isUp(int fromFloor, int toFloor) {
        return fromFloor < toFloor;
    }

    private int floorDist(int a, int b) {
        if (a * b > 0) {
            return Math.abs(a - b);
        }
        return Math.abs(a - b) - 1;
    }

    private boolean toOut(int eleId, int curFloor,
                          ArrayList<PersonRequest> persons, Stage stage) {
        for (PersonRequest person : persons) {
            int dest = intFloor(person.getToFloor());
            if (dest == curFloor) {
                return true;
            }
            if ((stage == Stage.DOUBLE || stage == Stage.REC_ACCEPT || stage == Stage.RECYCLE)
                    && curFloor == 2) {
                if (eleId > 6 && dest > 2) {
                    return true;
                }
                if (eleId <= 6 && dest < 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean toIn(int curFloor, int direction, int curWeight,
                         ArrayList<PersonRequest> waitingPersons) {
        for (PersonRequest person : waitingPersons) {
            int from = intFloor(person.getFromFloor());
            int to = intFloor(person.getToFloor());
            boolean sameToward = (isUp(from, to) == (direction == 1));
            if (sameToward && from == curFloor && curWeight + person.getWeight() <= 400) {
                return true;
            }
        }
        return false;
    }

    private boolean reqAhead(int curFloor, int direction,
                             ArrayList<PersonRequest> persons,
                             ArrayList<PersonRequest> waitingPersons) {
        for (PersonRequest person : persons) {
            int to = intFloor(person.getToFloor());
            if (direction == 1 && to > curFloor) {
                return true;
            }
            if (direction == -1 && to < curFloor) {
                return true;
            }
        }
        for (PersonRequest person : waitingPersons) {
            int from = intFloor(person.getFromFloor());
            if (direction == 1 && from > curFloor) {
                return true;
            }
            if (direction == -1 && from < curFloor) {
                return true;
            }
        }
        return false;
    }

    private int farthestAhead(int curFloor, int direction, ArrayList<PersonRequest> persons) {
        int ans = curFloor;
        boolean found = false;
        for (PersonRequest person : persons) {
            int to = intFloor(person.getToFloor());
            if (direction == 1 && to > curFloor) {
                if (!found || to > ans) {
                    ans = to;
                    found = true;
                }
            } else if (direction == -1 && to < curFloor) {
                if (!found || to < ans) {
                    ans = to;
                    found = true;
                }
            }
        }
        return found ? ans : curFloor;
    }

    private int farthestOpposite(int curFloor, int direction, ArrayList<PersonRequest> persons) {
        int ans = curFloor;
        boolean found = false;
        for (PersonRequest person : persons) {
            int to = intFloor(person.getToFloor());
            if (direction == 1 && to < curFloor) {
                if (!found || to < ans) {
                    ans = to;
                    found = true;
                }
            } else if (direction == -1 && to > curFloor) {
                if (!found || to > ans) {
                    ans = to;
                    found = true;
                }
            }
        }
        return found ? ans : curFloor;
    }

    private double acceptFixedTime(Stage stage, EleQueue myQueue) {
        if (stage == Stage.REP_ACCEPT) {
            MaintRequest maint = myQueue.getMaintRequest();
            int testTarget = 1;
            if (maint != null) {
                testTarget = intFloor(maint.getToFloor());
            }
            return DOOR_TIME + SPECIAL_WAIT
                    + floorDist(1, testTarget) * MOVE_TIME_TEST * 2
                    + DOOR_TIME;
        } else if (stage == Stage.UP_ACCEPT) {
            return DOOR_TIME + SPECIAL_WAIT;
        } else if (stage == Stage.REC_ACCEPT) {
            return DOOR_TIME + SPECIAL_WAIT;
        }
        return 0.0;
    }

    private int acceptTargetFloor(Stage stage) {
        if (stage == Stage.UP_ACCEPT) {
            return 3;
        }
        return 1;
    }

    private double acceptLimit(Stage stage) {
        if (stage == Stage.REP_ACCEPT) {
            return 6.1;
        }
        return 5.1;
    }

    private double minTime(int curFloor, Stage stage, EleQueue myQueue) {
        int target = acceptTargetFloor(stage);
        return floorDist(curFloor, target) * MOVE_TIME_NORMAL + acceptFixedTime(stage, myQueue);
    }

    private double optimisticTimeIfContinue(int curFloor, int direction,
                                            ArrayList<PersonRequest> persons,
                                            Stage stage, EleQueue myQueue) {
        if (persons.isEmpty()) {
            return minTime(curFloor, stage, myQueue);
        }

        final int target = acceptTargetFloor(stage);
        boolean hasAhead = false;
        boolean hasOpposite = false;

        for (PersonRequest p : persons) {
            int to = intFloor(p.getToFloor());
            if (direction == 1) {
                if (to > curFloor) {
                    hasAhead = true;
                } else if (to < curFloor) {
                    hasOpposite = true;
                }
            } else {
                if (to < curFloor) {
                    hasAhead = true;
                } else if (to > curFloor) {
                    hasOpposite = true;
                }
            }
        }
        double moveTime = 0.0;
        int pos = curFloor;
        if (hasAhead) {
            int ahead = farthestAhead(curFloor, direction, persons);
            moveTime += floorDist(pos, ahead) * MOVE_TIME_NORMAL;
            pos = ahead;
        }

        if (hasOpposite) {
            int opposite = farthestOpposite(curFloor, direction, persons);
            moveTime += floorDist(pos, opposite) * MOVE_TIME_NORMAL;
            pos = opposite;
        }

        moveTime += floorDist(pos, target) * MOVE_TIME_NORMAL;
        return moveTime + acceptFixedTime(stage, myQueue);
    }

    private boolean shouldKickForAccept(int curFloor, int direction,
                                        ArrayList<PersonRequest> persons,
                                        Stage stage, long startTime,
                                        EleQueue myQueue) {
        long now = System.currentTimeMillis();
        double used = (now - startTime) / 1000.0;
        double limit = acceptLimit(stage);
        if (used + minTime(curFloor, stage, myQueue) > limit) {
            return true;
        }
        return used +
                optimisticTimeIfContinue(curFloor, direction, persons, stage, myQueue) > limit;
    }

    private Advice adviceInAccept(int curFloor, int direction,
                                  ArrayList<PersonRequest> persons,
                                  Stage stage, long startTime,
                                  EleQueue myQueue) {
        int target = acceptTargetFloor(stage);

        if (stage == Stage.REC_ACCEPT && !persons.isEmpty()) { return Advice.KICK; }
        if (!persons.isEmpty() && shouldKickForAccept(curFloor, direction,
                persons, stage, startTime, myQueue)) {
            return Advice.KICK;
        }

        if (persons.isEmpty()) {
            if (curFloor == target) {
                return Advice.OPEN;
            }
            int targetDir = (target > curFloor) ? 1 : -1;
            return (direction == targetDir) ? Advice.MOVE : Advice.REVERSE;
        }

        if (reqAhead(curFloor, direction, persons, new ArrayList<>())) {
            return Advice.MOVE;
        } else {
            return Advice.REVERSE;
        }
    }

    public Advice getAdvice(int eleId, int curFloor, int direction, EleQueue myQueue,
                            ArrayList<PersonRequest> persons, int curWeight, Stage stage,
                            long startTime) {
        ArrayList<PersonRequest> waitingPersons = myQueue.getwaitPersons();

        if (toOut(eleId, curFloor, persons, stage)) {
            return Advice.OPEN;
        }

        if (stage == Stage.REP_ACCEPT || stage == Stage.UP_ACCEPT || stage == Stage.REC_ACCEPT) {
            return adviceInAccept(curFloor, direction, persons, stage, startTime, myQueue);
        }

        if (toIn(curFloor, direction, curWeight, waitingPersons)) {
            return Advice.OPEN;
        }

        if (reqAhead(curFloor, direction, persons, waitingPersons)) {
            return Advice.MOVE;
        }

        if (reqAhead(curFloor, -direction, persons, waitingPersons)) {
            return Advice.REVERSE;
        }

        if (persons.isEmpty() && waitingPersons.isEmpty()) {
            if ((stage == Stage.DOUBLE || stage == Stage.REC_ACCEPT) && curFloor == 2) {
                if (eleId <= 6) {
                    return direction == 1 ? Advice.MOVE : Advice.REVERSE;
                } else {
                    return direction == -1 ? Advice.MOVE : Advice.REVERSE;
                }
            }
            return myQueue.isEnd() ? Advice.OVER : Advice.WAIT;
        }

        return Advice.REVERSE;
    }
}