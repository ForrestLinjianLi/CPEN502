package main;

import exception.NumberMismatchException;

import java.util.*;

public class Neuron {
    private List<Double> weights;
    private List<Double> weightChanges;
    private double output;
    private double errorSignal;


    /***
     * Constructor of the Neuron
     * @param numWeights: number of weights that link to this neuron
     * @param argA: the lower bound of the weight
     * @param argB: the upper bound of the weight
     */
    public Neuron(int numWeights, double argA, double argB) {
        this.weights = Arrays.stream(new double[numWeights]).boxed().toList();
        Random random = new Random();
        double range = argB - argA;
        this.weights = this.weights.stream().map(x -> x = random.nextInt() % range + argA).toList();
        this.output = -1;
        this.weightChanges = Arrays.stream(new double[numWeights]).boxed().toList();
    }

    /***
     * The sum of this neuron based on the input X.
     * @param X: the inputs
     * @return the dot product of the X and weights
     */
    public double sum(double[] X) throws NumberMismatchException {
        if (X.length != this.weights.size()) throw new NumberMismatchException("");
        int res = 0;
        for (int i = 0; i < X.length; i++) {
            res += X[i] * this.weights.get(i);
        }
        return res;
    }


    /**
     * Zero out all the weights
     */
    public void zeroWeights() {
        this.weights = new ArrayList<>(Collections.nCopies(this.weights.size(), 0.0));
    }


    /**
     * @return the current output
     */
    public double getOutput() {
        return output;
    }


    /**
     * @return the error signal corresponding to the current weight
     */
    public double getErrorSignal() {
        return errorSignal;
    }


    /**
     *
     * @param errorSignals: the error signals of the layer above
     */
    public void setErrorSignal(double errorSignal, double weight) {
        this.errorSignal = this.output * (1 - this.output) * errorSignal * weight;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public void setErrorSignal(double error) {
        this.errorSignal = error * this.output * (1 - this.output);
    }


    public double getWeightByIndex(int i) {
        return this.weights.get(i);
    }

    public void updateWeights(double momentum, double stepSize) {
        for (int i = 0; i < this.weightChanges.size(); i++) {
            double curWeightChange = this.weightChanges.get(i);
            double curWeight = this.weights.get(i);
            double updatedWeightChange = momentum * curWeightChange + stepSize * this.errorSignal * curWeight;
            this.weightChanges.set(i, updatedWeightChange);
            this.weights.set(i, curWeight + updatedWeightChange);
        }
    }
}
