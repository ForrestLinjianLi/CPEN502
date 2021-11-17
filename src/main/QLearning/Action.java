package main.QLearning;

public enum Action {
    FORWARD,
    BACKWARD,
    AHEAD_LEFT,
    AHEAD_RIGHT,
    FIRE,
    BACK_LEFT,
    BACK_RIGHT,
    MOVE_TO_CENTRE;

    public static final int LONG_DISTANCE = 100;
    public static final int SHORT_DISTANCE = 50;
    public static final int SHORT_ANGLE = 50;
    public static final int NUM_ACTIONS = Action.values().length;



    public static Action getAction(int i) {
        return Action.values()[i];
    }

    public static int getActionNum(Action action) {
        for (int i = 0; i < NUM_ACTIONS; i++) {
            if (getAction(i).equals(action)) {
                return i;
            }
        }
        return 0;
    }
}
