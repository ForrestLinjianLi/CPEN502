package main.QLearning;

import main.interfaces.LUTInterface;
import net.sf.robocode.host.io.RobotFileOutputStream;
import robocode.RobocodeFileOutputStream;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LookUpTable implements LUTInterface, Serializable {

    private int argNumInputs;
    private int[] argVariableFloor;
    private int[] argVariableCeiling;
    private Map<Object, double[]> table;
    private Map<State, Integer> count;
    private double[] stateQ;
    private static final String FILE_PATH = "data/LUT";


    /**
     * Constructor.
     * @param argNumInputs The number of inputs in your input vector
     * @param argVariableFloor An array specifying the lowest value of each variable in the input vector.
     * @param argVariableCeiling An array specifying the highest value of each of the variables in the input vector.
     * The order must match the order as referred to in argVariableFloor.
     */
    public LookUpTable (int argNumInputs, int [] argVariableFloor, int [] argVariableCeiling) {
        this.argNumInputs = argNumInputs;
        this.argVariableFloor = argVariableFloor;
        this.argVariableCeiling = argVariableCeiling;
        table = new HashMap<Object, double[]>();
        count = new HashMap<>();
        stateQ = new double[Action.NUM_ACTIONS];
        Arrays.fill(stateQ, 0);
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
    public void save() {
        try {
            RobocodeFileOutputStream fos = new RobocodeFileOutputStream(FILE_PATH);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() throws IOException {
        try {
            FileInputStream fis = new FileInputStream(FILE_PATH);
            ObjectInputStream in = new ObjectInputStream(fis);
            LookUpTable lookUpTable = (LookUpTable) in.readObject();
            for (Field field:this.getClass().getDeclaredFields()) {
                field.set(this, lookUpTable.getClass().getDeclaredField(field.getName()));
            }
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

    public double getQ(State state, Action.ACTION action) {
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

    public Action.ACTION nextAction(State state, double randomRate) {
        Random random = new Random();
        if (count.getOrDefault(state, 0) < 200 && random.nextInt(9) < 10 * randomRate) {
            return Action.getAction(random.nextInt(3));
        }
        double[] actionQVals = table.getOrDefault(state, stateQ.clone());
        double max = actionQVals[0];
        Action.ACTION action = Action.ACTION.FORWARD;
        for (int i = 0; i < Action.NUM_ACTIONS; i++) {
            if (max<actionQVals[i]) {
                action = Action.getAction(i);
            }
        }
        return action;
    }

    public void uodateQ(double q, State state, Action.ACTION action) {
        table.put(state, table.getOrDefault(state, stateQ.clone()));
        table.get(state)[Action.getActionNum(action)] = q;
        count.put(state, count.getOrDefault(state, 0)+1);
    }
}
