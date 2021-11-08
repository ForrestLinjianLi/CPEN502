package main.QLearning;

public class Action {
    public enum ACTION {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT,
        FIRE,
        BIG_LEFT,
        BIG_RIGHT
    }

    public static final int LONG_DISTANCE = 100;
    public static final int SHORT_DISTANCE = 50;
    public static final int SHORT_ANGLE = 50;
    public static final int LARGE_ANGLE = 100;
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
            case LEFT:
                return 2;
            case RIGHT:
                return 3;
            case FIRE:
                return 4;
            case BIG_LEFT:
                return 5;
            case BIG_RIGHT:
                return 6;
            default:
                return -1;
        }
    }
}
