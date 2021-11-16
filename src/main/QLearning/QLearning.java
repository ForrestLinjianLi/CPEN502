package main.QLearning;

import java.io.File;

public class QLearning {
    private LookUpTable lookUpTable;
    private static QLearning qLearning;
    private static final double DELTA = 0.9;
    private static final double GAMMA = 0.9;
    //todo: add random rate
    private static final double RANDOM_RATE = 0.3;

    private static boolean is_On_Policy = false;

    public static QLearning getInstance() {
        if (qLearning == null) {
            qLearning = new QLearning();
            return qLearning;
        }
        return qLearning;
    }


    public QLearning() {
        lookUpTable = new LookUpTable();
        load("out/production/CPEN502/robot/MyFirstRobot.data/LUT.txt");
    }

    public Action.ACTION move(State state) {
        return lookUpTable.nextAction(state, RANDOM_RATE);
    }

    public void qLearn(double reward, Action.ACTION action, State prevState, State curState, Action.ACTION nextAction) {
        double prevQ = lookUpTable.getQ(prevState, action);
        double curQ;

        if(is_On_Policy){
            curQ = lookUpTable.getQ(curState, nextAction);
        }else{
            curQ = lookUpTable.getMaxQ(curState);
        }

        double updatedQ = prevQ + DELTA * (reward + GAMMA * curQ - prevQ);
        lookUpTable.updateQ(updatedQ, prevState, action);
    }

    public void save(File argFile) {
        lookUpTable.save(argFile);
    }


    public void load(String fileName) {
        lookUpTable.load(fileName);
    }
}
