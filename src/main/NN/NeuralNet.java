package main.NN;

import main.exception.NumberMismatchException;
import lombok.Getter;
import main.robot.Action;
import main.QLearning.LookUpTable;
import main.robot.State;
import main.interfaces.NeuralNetInterface;
import robocode.RobocodeFileOutputStream;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class NeuralNet implements NeuralNetInterface, Serializable {

    private int argNumInputs;
    private int argNumHidden;
    private int argNumOutput;
    private double argLearningRate;
    private double argMomentumTerm;
    private double argA;
    private double argB;
    private List<Neuron> hiddenLayer;
    private List<Neuron> outputLayer;
    private boolean isBipolar;

    private static final double THREASHOLD = 27.23;
    public static final String FILE_PATH = "./data/NN.ser";
//    private static final int NUM_HIDDEN_LAYERS = 1;
    private static final double DELTA = 0.1;
    private static final double GAMMA = 0.9;
    private static final double RANDOM_RATE = 0;
    private static NeuralNet neuralNet;


    public static NeuralNet getInstance(File file) {
        if (neuralNet == null) {
            neuralNet = new NeuralNet();
            neuralNet.load(file);
            return neuralNet;
        }
        return neuralNet;

    }
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
    public NeuralNet(int argNumInputs, int argNumHidden, int argNumOutput, double argLearningRate, double argMomentumTerm, double argA, double argB) {
        this.argNumInputs = argNumInputs;
        this.argNumHidden = argNumHidden;
        this.argNumOutput = argNumOutput;
        this.argLearningRate = argLearningRate;
        this.argMomentumTerm = argMomentumTerm;
        this.argA = argA;
        this.argB = argB;
        this.hiddenLayer = new ArrayList<>();
        this.isBipolar = argA + argB == 0;
        initializeWeights();
    }

    @Override
    public double outputFor(double[] X) {
        return forwardFeedWSigmoid(X)[0];
    }

    @Override
    public double train(double[] X, double argValue) {
        return forwardFeedWSigmoid(X)[0] - argValue;
    }

    public void save(File argFile, boolean isRobo)  {
        try {
            OutputStream outputStream = isRobo? new RobocodeFileOutputStream(argFile): new FileOutputStream(argFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            if (this.getHiddenLayer() != null && neuralNet.getHiddenLayer().size() != this.getHiddenLayer().size())
                return;
            this.hiddenLayer = neuralNet.hiddenLayer;
            this.outputLayer = neuralNet.outputLayer;
            this.argA = neuralNet.argA;
            this.argB = neuralNet.argB;
            this.argLearningRate = neuralNet.argLearningRate;
            this.argMomentumTerm = neuralNet.argMomentumTerm;
            this.argNumHidden = neuralNet.argNumHidden;
            this.argNumInputs = neuralNet.argNumInputs;
            this.argNumOutput = neuralNet.argNumOutput;
            this.isBipolar = neuralNet.isBipolar;
        } catch (ClassNotFoundException | IOException e) {
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
        this.hiddenLayer = new ArrayList<>();
        this.outputLayer = new ArrayList<>();
        int neuronCount = this.argNumHidden + 1;
        while (neuronCount -- > 0) {
            this.hiddenLayer.add(new Neuron(this.argNumInputs + 1, this.isBipolar));
        }
        neuronCount = this.argNumOutput;
        while (neuronCount -- > 0) {
            this.outputLayer.add(new Neuron(this.argNumHidden + 1, this.isBipolar));
        }
//        hiddenLayer.add(firstLayer);
//        for (int i = 1; i < NUM_HIDDEN_LAYERS; i++) {
//            neuronCount = this.argNumHidden + 1;
//            List<Neuron> curLayer = new ArrayList<>();
//            while (neuronCount -- > 0) {
//                curLayer.add(new Neuron(this.argNumHidden + 1, this.isBipolar));
//            }
//            hiddenLayer.add(curLayer);
//        }
    }

    @Override
    public void zeroWeights() {
        this.hiddenLayer.forEach(Neuron::zeroWeights);
        this.outputLayer.forEach(Neuron::zeroWeights);
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
                double[] input = getInputFromStateNAction(curState);
                double[] yi = forwardFeedWSigmoid(input);
                for (int i = 0; i < Action.NUM_ACTIONS; i++) {
                    totalError += Math.pow(Math.abs(actionQVals[i] - yi[i]), 2) / 2;
                    backProp(yi[i], actionQVals[i], i);
                }
            }
            epoch++;
            stringBuilder.append(totalError + "\n");
        } while (totalError > THREASHOLD);
        String fileName = String.format("./data/%s_m%f_%s_%d.txt", "NQ",  this.argMomentumTerm, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")), epoch);
        createDataFile(stringBuilder, fileName);
        save(new File(FILE_PATH), false);
        return epoch;
    }


    /**
     * train the model and save the data to an text file
     * @param X: training inputs
     * @param targets: training targets
     * @return number of epochs
     */
    public int train(double[][] X, double[] targets) {
        int epoch = 0;
        double totalError;
        StringBuilder stringBuilder = new StringBuilder();
        do {
            totalError = 0;
            for (int i = 0; i < X.length; i++) {
                double[] temp = setUpBias(X[i]);
                double[] yi = forwardFeedWSigmoid(temp);
                for (int j = 0; j < this.argNumOutput; j++) {
                    totalError += Math.pow(Math.abs(targets[i] - yi[j]), 2) / 2;
                    backProp(yi[j], targets[i], j);
                }
            }
            epoch++;
            stringBuilder.append(totalError + "\n");
        } while (totalError > THREASHOLD);
        String fileName = String.format("./data/%s_m%f_%s_%d.txt", isBipolar? "Bipolar" : "Binary",  this.argMomentumTerm, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")), epoch);
        createDataFile(stringBuilder, fileName);
        return epoch;
    }

    public double[] forwardFeedWSigmoid(double[] X) {
        double[] res = new double[this.argNumOutput];
        try {
            for (Neuron neuron : this.hiddenLayer) {
                double curOutput = this.customSigmoid(neuron.sum(X));
                neuron.setOutput(curOutput);
            }
//            for (int i = 1; i < NUM_HIDDEN_LAYERS; i++) {
//                double[] inputs = this.hiddenLayer.get(i - 1).stream().mapToDouble(Neuron::getOutput).toArray();
//                for (Neuron neuron : this.hiddenLayer.get(i)) {
//                    double curOutput = this.customSigmoid(neuron.sum(inputs));
//                    neuron.setOutput(curOutput);
//                }
//            }
            updateOutNeuron();
            for (int i = 0; i < this.argNumOutput; i++) {
                res[i] = this.outputLayer.get(i).getOutput();
            }
            return res;
        } catch (NumberMismatchException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void updateOutNeuron() {
//        double[] inputs = this.hiddenLayer.get(NUM_HIDDEN_LAYERS - 1).stream().mapToDouble(Neuron::getOutput).toArray();
        double[] inputs = this.hiddenLayer.stream().mapToDouble(Neuron::getOutput).toArray();
        try {
            for (Neuron neuron:this.outputLayer) {
                neuron.setOutput(customSigmoid(neuron.sum(inputs)));
            }
        } catch (NumberMismatchException e) {
            e.printStackTrace();
        }
    }

    public void qTrain(double reward, State prevState, Action prevAction, State curState) throws IllegalAccessException, NumberMismatchException {
        int prevActionIndex = Action.getActionNum(prevAction);
        double[] prevInput = getInputFromStateNAction(prevState);
        double prevQ = forwardFeedWSigmoid(prevInput)[prevActionIndex];
        double[] curInput = getInputFromStateNAction(curState);
        double curQ = Arrays.stream(forwardFeedWSigmoid(curInput)).max().orElse(0);
        double updatedQ = prevQ + DELTA * (reward + GAMMA * curQ - prevQ);
        backProp(prevQ, updatedQ, prevActionIndex);
    }

    public void sarsaTrain(double reward, State prevState, Action prevAction, State curState, Action curAction) throws IllegalAccessException, NumberMismatchException {
        int prevActionIndex = Action.getActionNum(prevAction);
        int curActionIndex = Action.getActionNum(curAction);
        double[] prevInput = getInputFromStateNAction(prevState);
        double[] prevQ = forwardFeedWSigmoid(prevInput);
        double[] curInput = getInputFromStateNAction(curState);
        double[] curQ = forwardFeedWSigmoid(curInput);
        double updatedQ = prevQ[prevActionIndex] + DELTA * (reward + GAMMA * curQ[curActionIndex] - prevQ[prevActionIndex]);
        backProp(prevQ[prevActionIndex], updatedQ, prevActionIndex);
    }

    public Action getNextAction(State state) throws IllegalAccessException {
        if (Math.random() < RANDOM_RATE) {
            return Action.getAction(new Random().nextInt(Action.NUM_ACTIONS));
        }
        double max = Double.NEGATIVE_INFINITY;
        Action action = Action.AHEAD_LEFT;
        double[] curInput = getInputFromStateNAction(state);
        double[] tempQ = forwardFeedWSigmoid(curInput);
        for (int i = 0; i < Action.NUM_ACTIONS; i++) {
            if (max<tempQ[i]) {
                action = Action.getAction(i);
                max = tempQ[i];
            }
        }
        return action;
    }

    public void backProp(double yi, double target, int outputNeuronIndex) {
        Neuron outpuNeuron = this.outputLayer.get(outputNeuronIndex);
        outpuNeuron.setErrorSignalForOutputNeuron(target - yi);
        outpuNeuron.updateWeights(this.argMomentumTerm, this.argLearningRate);
        for (int i = 0; i < this.argNumHidden; i++) {
//            Neuron curNeuron = this.hiddenLayer.get(NUM_HIDDEN_LAYERS-1).get(i);
            Neuron curNeuron = this.hiddenLayer.get(i);
            curNeuron.setErrorSignal(outpuNeuron.getErrorSignal(), outpuNeuron.getWeightByIndex(i));
            curNeuron.updateWeights(this.argMomentumTerm, this.argLearningRate);
        }
//        for (int i = NUM_HIDDEN_LAYERS-2; i >= 0; i--) {
//            for (int j = 0; j < this.argNumHidden; j++) {
//                Neuron curNeuron = this.hiddenLayer.get(i).get(j);
//                for (Neuron nextLayerNeuron: this.hiddenLayer.get(i+1)) {
//                    curNeuron.setErrorSignal(nextLayerNeuron.getErrorSignal(), nextLayerNeuron.getWeightByIndex(i));
//                    curNeuron.updateWeights(this.argMomentumTerm, this.argLearningRate);
//                }
//            }
//        }
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

    private double[] getInputFromStateNAction(State state) throws IllegalAccessException {
        double[] stateArray = state.toDoubleArray();
        double[] res = new double[stateArray.length];
        for (int i = 0; i < stateArray.length; i++) {
            res[i] = stateArray[i];
        }
        return setUpBias(res);
    }

    private double[] setUpBias(double[] X){
        double[] temp = new double[X.length+1];
        System.arraycopy(X, 0, temp, 0, X.length);
        temp[temp.length - 1] = bias;
        return temp;
    }
}
