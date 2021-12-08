package main.QLearning;

import lombok.Getter;
import main.interfaces.LUTInterface;
import robocode.RobocodeFileOutputStream;
import robot.Action;
import robot.State;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
public class LookUpTable implements LUTInterface, Serializable {

    private Map<State, double[]> table;
    private Map<State, Integer> count;
    private double[] stateQ;

    /**
     * Constructor.
     * The order must match the order as referred to in argVariableFloor.
     */
    public LookUpTable () {
        table = new HashMap<State, double[]>();
        count = new HashMap<>();
        stateQ = new double[Action.NUM_ACTIONS];
        Arrays.fill(stateQ, 0d);
        initialiseLUT();
    }

    @Override
    public double outputFor(double[] X) {
        return 0;
    }

    @Override
    public double train(double[] X, double argValue) {
        return 0;
    }

    @Override
    public void save(File argFile) {
        try {
            RobocodeFileOutputStream fos = new RobocodeFileOutputStream(argFile);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fis);
            LookUpTable lookUpTable = (LookUpTable) in.readObject();
            this.table = lookUpTable.getTable();
            this.count = lookUpTable.getCount();
            this.stateQ = lookUpTable.getStateQ();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void initialiseLUT() {
        return;
    }

    @Override
    public int indexFor(double[] X) {
        return 0;
    }

    public double getQ(State state, Action action) {
        return table.getOrDefault(state, stateQ.clone())[Action.getActionNum(action)];
    }

    public double getMaxQ(State state) {
        double[]  actionQVals = table.getOrDefault(state, stateQ.clone());
        double max = 0;
        for (int i = 0; i < Action.NUM_ACTIONS; i++) {
            max = Math.max(actionQVals[i], max);
        }
        return max;
    }

    public Action nextAction(State state, double randomRate) {
        Random random = new Random();
        if ((count.getOrDefault(state, 0) < 1000) && (random.nextInt(9) < (10 * randomRate))) {
            return Action.getAction(random.nextInt(Action.NUM_ACTIONS));
        }
        double[] actionQVals = table.getOrDefault(state, stateQ.clone());
        double max = actionQVals[0];
        Action action = Action.FORWARD;
        for (int i = 0; i < Action.NUM_ACTIONS; i++) {
            if (max<actionQVals[i]) {
                action = Action.getAction(i);
                max = actionQVals[i];
            }
        }
        return action;
    }

    public void updateQ(double q, State state, Action action) {
        table.put(state, table.getOrDefault(state, stateQ.clone()));
        table.get(state)[Action.getActionNum(action)] = q;
        count.put(state, count.getOrDefault(state, 0)+1);
    }
}
