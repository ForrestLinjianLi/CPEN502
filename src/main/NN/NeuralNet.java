package main.NN;

import exception.NumberMismatchException;
import lombok.Getter;
import robot.Action;
import main.QLearning.LookUpTable;
import robot.State;
import main.interfaces.NeuralNetInterface;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
public class NeuralNet implements NeuralNetInterface, Serializable {

    private int argNumInputs;
    private int argNumHidden;
    private double argLearningRate;
    private double argMomentumTerm;
    private double argA;
    private double argB;
    private List<List<Neuron>> hiddenLayers;
    private Neuron outputNeuron;
    private boolean isBipolar;

    private static final double THREASHOLD = 100;
    public static final String FILE_PATH = "./data/NN.txt";
    private static final int NUM_HIDDEN_LAYERS = 1;
    private static final double DELTA = 0.9;
    private static final double GAMMA = 0.9;
    private static final double RANDOM_RATE = 0.2;

    public NeuralNet() {
    }

    /**
     * Constructor.
     * @param argNumInputs The number of inputs in your input vector
     * @param argNumHidden The number of hidden neurons in your hidden layer. Only a single hidden layer is supported
     * @param argLearningRate The learning rate coefficient
     * @param argMomentumTerm The momentum coefficient
     * @param argA Integer lower bound of sigmoid used by the output neuron only.
     * @param argB Integer upper bound of sigmoid used by the output neuron only.
     */
    public NeuralNet(int argNumInputs, int argNumHidden, double argLearningRate, double argMomentumTerm, double argA, double argB) {
        this.argNumInputs = argNumInputs;
        this.argNumHidden = argNumHidden;
        this.argLearningRate = argLearningRate;
        this.argMomentumTerm = argMomentumTerm;
        this.argA = argA;
        this.argB = argB;
        this.hiddenLayers = new ArrayList<>();
        this.isBipolar = argA + argB == 0;
        initializeWeights();
    }

    @Override
    public double outputFor(double[] X) {
        return forwardFeedWSigmoid(X);
    }

    @Override
    public double train(double[] X, double argValue) {
        return forwardFeedWSigmoid(X) - argValue;
    }

    @Override
    public void save(File argFile)  {
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(argFile))) {
            objectOutputStream.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(File file) {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            NeuralNet neuralNet = (NeuralNet) objectInputStream.readObject();
            if (this.getHiddenLayers() != null && neuralNet.getHiddenLayers().size() != this.getHiddenLayers().size())
                return;
            this.hiddenLayers = neuralNet.hiddenLayers;
            this.outputNeuron = neuralNet.outputNeuron;
            this.argA = neuralNet.argA;
            this.argB = neuralNet.argB;
            this.argLearningRate = neuralNet.argLearningRate;
            this.argMomentumTerm = neuralNet.argMomentumTerm;
            this.argNumHidden = neuralNet.argNumHidden;
            this.argNumInputs = neuralNet.argNumInputs;
            this.isBipolar = neuralNet.isBipolar;
        } catch (ClassNotFoundException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    @Override
    public double customSigmoid(double x) {
        return (this.argB - this.argA) / (1 + Math.exp(-x)) + this.argA;
    }

    @Override
    public void initializeWeights() {
        this.hiddenLayers = new ArrayList<>();
        this.outputNeuron = new Neuron(this.argNumHidden + 1, this.isBipolar);
        List<Neuron> firstLayer = new ArrayList<>();
        int neuronCount = this.argNumHidden + 1;
        while (neuronCount -- > 0) {
            firstLayer.add(new Neuron(this.argNumInputs + 1, this.isBipolar));
        }
        hiddenLayers.add(firstLayer);
        for (int i = 1; i < NUM_HIDDEN_LAYERS; i++) {
            neuronCount = this.argNumHidden + 1;
            List<Neuron> curLayer = new ArrayList<>();
            while (neuronCount -- > 0) {
                curLayer.add(new Neuron(this.argNumHidden + 1, this.isBipolar));
            }
            hiddenLayers.add(curLayer);
        }
    }

    @Override
    public void zeroWeights() {
        this.hiddenLayers.forEach((e) -> e.forEach(Neuron::zeroWeights));
        this.outputNeuron.zeroWeights();
    }

    private void createDataFile(StringBuilder stringBuilder, String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(stringBuilder.toString());
            System.out.printf("The data is saved to file: %s \n", fileName);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * train the model and save the data to an text file
     * @param lookUpTable: training data from Part2 lookuptable. Note it is alway bipolar.
     * @return number of epochs
     */
    public int trainByLUT(LookUpTable lookUpTable) throws Exception {
        int epoch = 0;
        double totalError;
        StringBuilder stringBuilder = new StringBuilder();
        Map<State, double[]> dataTable = lookUpTable.getTable();
        double QMax = 0;
        double QMin = 0;
        for(double[] qVals: dataTable.values()){
            double[] temp = qVals.clone();
            Arrays.sort(temp);
            QMax = Math.max(QMax, temp[temp.length-1]);
            QMin = Math.min(QMin, temp[0]);
        }
        double QValRange = QMax - QMin;
        do {
            totalError = 0;
            for (Map.Entry<State, double[]> entry: lookUpTable.getTable().entrySet()) {
                State curState = entry.getKey();
                double[] actionQVals = entry.getValue().clone();
                //Transform all Q vals within [-1,1]
                for(int i=0; i<actionQVals.length; i++){
                    actionQVals[i] = -1 + 2*(actionQVals[i]-QMin)/QValRange;
                }

                for (int i = 0; i < Action.NUM_ACTIONS; i++) {
                    double[] input = getInputFromStateNAction(curState, i);
                    double yi = forwardFeedWSigmoid(input);
                    totalError += Math.pow(Math.abs(actionQVals[i] - yi), 2) / 2;
                    backProp(yi, actionQVals[i]);
                }
            }
            epoch++;
            stringBuilder.append(totalError + "\n");
        } while (totalError > THREASHOLD);
        String fileName = String.format("./data/%s_m%f_%s_%d.txt", "NQ",  this.argMomentumTerm, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")), epoch);
        createDataFile(stringBuilder, fileName);
        save(new File(FILE_PATH));
        return epoch;
    }


    /**
     * train the model and save the data to an text file
     * @param X: training inputs
     * @param targets: training targets
     * @return number of epochs
     */
    public int trainByLUT(double[][] X, double[] targets) {
        int epoch = 0;
        double totalError;
        StringBuilder stringBuilder = new StringBuilder();
        do {
            totalError = 0;
            for (int i = 0; i < X.length; i++) {
                double[] temp = setUpBias(X[i]);
                double yi = forwardFeedWSigmoid(temp);
                totalError += Math.pow(Math.abs(targets[i] - yi), 2) / 2;
                backProp(yi, targets[i]);
            }
            epoch++;
            stringBuilder.append(totalError + "\n");
        } while (totalError > THREASHOLD);
        String fileName = String.format("./data/%s_m%f_%s_%d.txt", isBipolar? "Bipolar" : "Binary",  this.argMomentumTerm, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")), epoch);
        createDataFile(stringBuilder, fileName);
        return epoch;
    }

    public double forwardFeedWSigmoid(double[] X) {
        try {
            for (Neuron neuron : this.hiddenLayers.get(0)) {
                double curOutput = this.customSigmoid(neuron.sum(X));
                neuron.setOutput(curOutput);
            }
            for (int i = 1; i < NUM_HIDDEN_LAYERS; i++) {
                double[] inputs = this.hiddenLayers.get(i - 1).stream().mapToDouble(Neuron::getOutput).toArray();
                for (Neuron neuron : this.hiddenLayers.get(i)) {
                    double curOutput = this.customSigmoid(neuron.sum(inputs));
                    neuron.setOutput(curOutput);
                }
            }
            updateOutNeuron();
            return this.outputNeuron.getOutput();
        } catch (NumberMismatchException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateOutNeuron() {
        double[] inputs = this.hiddenLayers.get(NUM_HIDDEN_LAYERS - 1).stream().mapToDouble(Neuron::getOutput).toArray();
        try {
            this.outputNeuron.setOutput(customSigmoid(this.outputNeuron.sum(inputs)));
        } catch (NumberMismatchException e) {
            e.printStackTrace();
        }
    }

    public void sarsaTrain(double reward, State prevState, Action prevAction, State curState) throws IllegalAccessException, NumberMismatchException {
        double[] prevInput = getInputFromStateNAction(prevState, Action.getActionNum(prevAction));
        double prevQ = forwardFeedWSigmoid(prevInput);
        double curQ = Double.MIN_VALUE;
        for (int i = 0; i < Action.NUM_ACTIONS; i++) {
            double[] curInput = getInputFromStateNAction(curState, i);
            double tempQ = forwardFeedWSigmoid(curInput);
            curQ = Math.max(tempQ, curQ);
        }
        double updatedQ = prevQ + DELTA * (reward + GAMMA * curQ - prevQ);
        backProp(prevQ, updatedQ);
    }

    public Action getNextAction(State state) throws IllegalAccessException {
        Random random = new Random();
        if ((random.nextInt(9) < (10 * RANDOM_RATE))) {
            return Action.getAction(random.nextInt(Action.NUM_ACTIONS));
        }
        double max = Double.MIN_VALUE;
        Action action = Action.FORWARD;
        for (int i = 0; i < Action.NUM_ACTIONS; i++) {
            double[] curInput = getInputFromStateNAction(state, i);
            double tempQ = forwardFeedWSigmoid(curInput);
            if (max<tempQ) {
                action = Action.getAction(i);
                max = tempQ;
            }
        }
        return action;
    }

    public void backProp(double yi, double target) {
        this.outputNeuron.setErrorSignalForOutputNeuron(target - yi);
        this.outputNeuron.updateWeights(this.argMomentumTerm, this.argLearningRate);
        for (int i = 0; i < this.argNumHidden; i++) {
            Neuron curNeuron = this.hiddenLayers.get(NUM_HIDDEN_LAYERS-1).get(i);
            curNeuron.setErrorSignal(this.outputNeuron.getErrorSignal(), this.outputNeuron.getWeightByIndex(i));
            curNeuron.updateWeights(this.argMomentumTerm, this.argLearningRate);
        }

        for (int i = NUM_HIDDEN_LAYERS-2; i >= 0; i--) {
            for (int j = 0; j < this.argNumHidden; j++) {
                Neuron curNeuron = this.hiddenLayers.get(i).get(j);
                for (Neuron nextLayerNeuron: this.hiddenLayers.get(i+1)) {
                    curNeuron.setErrorSignal(nextLayerNeuron.getErrorSignal(), nextLayerNeuron.getWeightByIndex(i));
                    curNeuron.updateWeights(this.argMomentumTerm, this.argLearningRate);
                }
            }
        }
    }

//    public void backPropLinear(double yi, double target) {
//        this.outputNeuron.setErrorSignalForOutputNeuronLinear(target - yi);
//        this.outputNeuron.updateWeights(this.argMomentumTerm, this.argLearningRate);
//        for (int i = 0; i < this.hiddenLayer.size(); i++) {
//            Neuron curNeuron = this.hiddenLayer.get(i);
//            curNeuron.setErrorSignalLinear(this.outputNeuron.getErrorSignal(), this.outputNeuron.getWeightByIndex(i));
//            curNeuron.updateWeights(this.argMomentumTerm, this.argLearningRate);
//        }
//    }

    private double[] getInputFromStateNAction(State state, int action) throws IllegalAccessException {
        double[] stateArray = state.toDoubleArray();
        double[] res = new double[stateArray.length+1];
        for (int i = 0; i < stateArray.length; i++) {
            res[i] = stateArray[i];
        }
        res[stateArray.length] = action;
        return setUpBias(res);
    }

    private double[] setUpBias(double[] X){
        double[] temp = new double[X.length+1];
        System.arraycopy(X, 0, temp, 0, X.length);
        temp[temp.length - 1] = bias;
        return temp;
    }
}
