package main.QLearning;

import java.io.File;

public class QLearning {
    private LookUpTable lookUpTable;
    private static QLearning qLearning;
    private static final double DELTA = 0.8;
    private static final double GAMMA = 0.8;
    private static final int[] ARG_VARIABLE_FLOOR = new int[]{0, 0, 0, 0, 0};
    private static final int[] ARG_VARIABLE_CEILING = new int[]{3, 3, 1, 1, 1};
    //todo: add random rate
    private static final double RANDOM_RATE = 0.2;

    public static QLearning getInstance() {
        if (qLearning == null) {
            qLearning = new QLearning();
            return qLearning;
        }
        return qLearning;
    }


    public QLearning() {
        lookUpTable = new LookUpTable(5, ARG_VARIABLE_FLOOR, ARG_VARIABLE_CEILING);
    }

    public Action.ACTION move(State state) {
        return lookUpTable.nextAction(state, RANDOM_RATE);
    }

    public void qLearn(double reward, Action.ACTION action, State prevState, State curState) {
        double prevQ = lookUpTable.getQ(prevState, action);
        double curQ = lookUpTable.getMaxQ(curState);
        double updatedQ = prevQ + DELTA * (reward + GAMMA * curQ - prevQ);
        lookUpTable.uodateQ(updatedQ, prevState, action);
    }

    public void save() {
        lookUpTable.save();
    }
}
