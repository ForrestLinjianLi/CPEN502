package main;

import exception.NumberMismatchException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NeuralNet implements NeuralNetInterface {

    public int argNumInputs;
    public int argNumHidden;
    public double argLearningRate;
    public double argMomentumTerm;
    public double argA;
    public double argB;
    public List<Neuron> layers;
    private Neuron outputNeuron;

    private static final double THREASHOLD = 0.2;




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
        this.layers = new ArrayList<>();
        initializeWeights();
    }


    @Override
    public double outputFor(double[] X) {
        return 0;
    }

    @Override
    public double train(double[] X, double argValue) {
        double[] temp = new double[X.length+1];
        System.arraycopy(X, 0, temp, 0, X.length);
        temp[temp.length - 1] = bias;
        initializeWeights();
        double curError = 1;
        while(curError > THREASHOLD) {
            double yi = sigmoid(forwardProp(temp));
            curError = Math.abs(argValue - yi);
            backProp(yi, argValue);
        }
        return 0;
    }

    private double forwardProp(double[] X) {
        List<Double> layer1Outputs = new ArrayList<>();
        try {
            for (Neuron neuron : this.layers) {
                double curOutput = this.sigmoid(neuron.sum(X));
                neuron.setOutput(curOutput);
                layer1Outputs.add(curOutput);
            }
            return this.outputNeuron.sum(layer1Outputs.stream().mapToDouble(i -> i).toArray());
        } catch (NumberMismatchException e) {
            System.exit(0);
        }
        return 0;
    }

    private void backProp(double yi, double argValue) {
        this.outputNeuron.setErrorSignal(argValue - yi);
        for (int i = 0; i < this.layers.size(); i++) {
            Neuron curNeuron = this.layers.get(i);
            curNeuron.setErrorSignal(this.outputNeuron.getErrorSignal(), this.outputNeuron.getWeightByIndex(i));
            curNeuron.updateWeights(this.argMomentumTerm, this.argLearningRate);
        }
    }

    @Override
    public void save(File argFile) {

    }

    @Override
    public void load(String argFileName) throws IOException {

    }

    @Override
    public double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    @Override
    public double customSigmoid(double x) {
        return (this.argB - this.argA) / (1 + Math.exp(-x)) - this.argA;
    }

    @Override
    public void initializeWeights() {
        int neuronCount = this.argNumHidden + 1;
        int numLayers =
        this.layers = new ArrayList<>();
        this.outputNeuron = new Neuron(this.argNumHidden + 1, this.argA, this.argB);
        while
        while (neuronCount -- > 0) {
            layers.add(new Neuron(this.argNumInputs + 1, this.argA, this.argB));
        }
    }

    @Override
    public void zeroWeights() {
        this.layers.forEach(Neuron::zeroWeights);
        this.outputNeuron.zeroWeights();
    }
}
