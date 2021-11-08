package main.QLearning;

public class Action {
    public enum ACTION {
        FORWARD,
        BACKWARD,
        AHEAD_LEFT,
        AHEAD_RIGHT,
        FIRE,
//        MOVE_TO_CENTRE,
        BACK_LEFT,
        BACK_RIGHT
    }

    public static final int LONG_DISTANCE = 100;
    public static final int SHORT_DISTANCE = 50;
    public static final int SHORT_ANGLE = 50;
    public static final int NUM_ACTIONS = ACTION.values().length;



    public static ACTION getAction(int i) {
        return ACTION.values()[i];
    }

    public static int getActionNum(ACTION action) {
        switch (action) {
            case FORWARD:
                return 0;
            case BACKWARD:
                return 1;
            case AHEAD_LEFT:
                return 2;
            case AHEAD_RIGHT:
                return 3;
            case FIRE:
                return 4;
            case BACK_LEFT:
                return 5;
            case BACK_RIGHT:
                return 6;
//            case MOVE_TO_CENTRE:
//                return 5;
            default:
                return -1;
        }
    }
}
