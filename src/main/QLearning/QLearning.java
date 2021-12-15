package main.QLearning;

import main.robot.Action;
import main.robot.State;

import java.io.File;

public class QLearning {
    private LookUpTable lookUpTable;
    private static QLearning qLearning;
    private static final double ALPHA = 0.1;
    private static final double GAMMA = 0.9;
    private static final double RANDOM_RATE = 0;

    private static boolean IS_ON_POLICY = false;

    public static QLearning getInstance(File file) {
        if (qLearning == null) {
            qLearning = new QLearning(file);
            return qLearning;
        }
        return qLearning;
    }


    public QLearning(File file) {
        lookUpTable = new LookUpTable();
        load(file);
    }

    public Action getNextAction(State state) {
        return lookUpTable.nextAction(state, RANDOM_RATE);
    }

    public void qLearn(double reward, Action action, State prevState, State curState) {
        double prevQ = lookUpTable.getQ(prevState, action);
        double curQ;
        Action nextAction = getNextAction(curState);
        if(IS_ON_POLICY){
            curQ = lookUpTable.getQ(curState, nextAction);
        }else{
            curQ = lookUpTable.getMaxQ(curState);
        }

        double updatedQ = prevQ + ALPHA * (reward + GAMMA * curQ - prevQ);
        lookUpTable.updateQ(updatedQ, prevState, action);
    }

    public void save(File argFile) {
        lookUpTable.save(argFile);
    }


    public void load(File file) {
        lookUpTable.load(file);
    }

    public LookUpTable getLookUpTable() {
        return lookUpTable;
    }
}
