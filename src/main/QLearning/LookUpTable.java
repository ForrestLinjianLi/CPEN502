package main.QLearning;

import main.interfaces.LUTInterface;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LookUpTable implements LUTInterface, Serializable {

    private int argNumInputs;
    private int[] argVariableFloor;
    private int[] argVariableCeiling;
    private double[][] table;
    private Map<Double, Integer> indexTable;

    private int enemyHeadingLevel = 4;
    private int bearingLevel = 4;
    private int enemyEnergyLevel = 2;
    private int myEnergyLevel = 2;
    private int distanceLevel = 2;

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
        this.indexTable = new HashMap<>();
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
            FileOutputStream fos = new FileOutputStream("LUT.txt");
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(String argFileName) throws IOException {
        try {
            FileInputStream fis = new FileInputStream("LUT.txt");
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
        table = new double[this.distanceLevel *this.enemyEnergyLevel *this.enemyHeadingLevel *this.myEnergyLevel *this.bearingLevel]
                [Action.NUM_ACTIONS];
        Arrays.fill(table, 0.0);
        int count = 0;
        for (double i = 0; i < this.distanceLevel; i++)
            for (int j = 0; j < this.enemyEnergyLevel; j++)
                for (int k = 0; k < this.enemyHeadingLevel; k++)
                    for (int l = 0; l < this.myEnergyLevel; l++)
                        for (int m = 0; m < this.distanceLevel; m++)
                            indexTable.put(hash(new double[]{i,j,k,l,m}), count++);
    }

    @Override
    public int indexFor(double[] X) {
        double[] convertedX = State.generateStateArray(X);
        double hashVal = hash(convertedX);
        return indexTable.getOrDefault(hashVal, -1);
    }

    private double hash(double[] X) {
        double[] primes = new double[]{3,5,7,11,13};
        double res = 0;
        for (int i = 0; i < X.length; i++) {
            res += X[i] * primes[i];
        }
        return res;
    }

    public double getMaxQ(State state) {
        double[] actionQVals = table[indexFor(State.getStateArray(state))];
        double max = 0;
        for (int i = 0; i < Action.NUM_ACTIONS; i++) {
            max = Math.max(actionQVals[i], max);
        }
        return max;
    }

    public Action.ACTION nextAction(double[] X) {
        double[] actionQVals = table[indexFor(X)];
        double max = 0;
        Action.ACTION action = null;
        for (int i = 0; i < Action.NUM_ACTIONS; i++) {
            if (max<actionQVals[i]) {
                action = Action.getAction(i);
            }
        }
        return action;
    }

    public void uodateQ(double q, State state, Action.ACTION action) {
        table[indexFor(State.getStateArray(state))][Action.getActionNum(action)] = q;
    }
}
