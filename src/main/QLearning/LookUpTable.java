package main.QLearning;

import lombok.Getter;
import main.interfaces.LUTInterface;
import main.robot.Action;
import main.robot.State;
import robocode.RobocodeFileOutputStream;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
public class LookUpTable implements LUTInterface, Serializable {

    private Map<State, double[]> table;
    private double[] stateQ;

    /**
     * Constructor.
     * The order must match the order as referred to in argVariableFloor.
     */
    public LookUpTable () {
        table = new HashMap<>();
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
        try(ObjectOutputStream writer = new ObjectOutputStream(new RobocodeFileOutputStream(argFile))){
            writer.writeObject(this.table);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(File file) {
        try(ObjectInputStream reader = new ObjectInputStream(new FileInputStream(file))) {
            Map<State, double[]> table = (Map<State, double[]>) reader.readObject();
            this.table = table;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
        if (Math.random() < randomRate) {

            return Action.getAction(new Random().nextInt(Action.NUM_ACTIONS));
        }
        double[] actionQVals = table.getOrDefault(state, stateQ.clone());
        double max = actionQVals[0];
        Action action = Action.AHEAD_LEFT;
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
//        count.put(state, count.getOrDefault(state, 0)+1);
    }
}
