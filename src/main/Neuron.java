package main;

import exception.NumberMismatchException;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Neuron implements Serializable {
    private List<Double> weights;
    private List<Double> weightChanges;
    private double[] inputs;
    private double output;
    private double errorSignal;
    private boolean isBipolar;

    private static final double WEIGHT_UPPER_BOUND = 0.5;
    private static final double WEIGHT_LOWER_BOUND = -0.5;

    /***
     * Constructor of the Neuron
     * @param numWeights: number of weights that link to this neuron
     */
    public Neuron(int numWeights, boolean isBipolar) {
        this.weights = Arrays.stream(new double[numWeights]).boxed().collect(Collectors.toList());
        Random random = new Random();
        double range = this.WEIGHT_UPPER_BOUND - this.WEIGHT_LOWER_BOUND;
        this.weights = this.weights.stream().map(x -> x = random.nextDouble() * range + this.WEIGHT_LOWER_BOUND).collect(Collectors.toList());
        this.output = -1;
        this.weightChanges = Arrays.stream(new double[numWeights]).boxed().collect(Collectors.toList());
        this.isBipolar = isBipolar;
    }

    /***
     * The sum of this neuron based on the input X.
     * @param X: the inputs
     * @return the dot product of the X and weights
     */
    public double sum(double[] X) throws NumberMismatchException {
        this.inputs = X;
        if (X.length != this.weights.size()) throw new NumberMismatchException("");
        double res = 0;
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
     * Set the error signal for the hidden layer neurons. Equation depends on the is Bipolar
     * @param errorSignal: the error signals of the layer above
     */
    public void setErrorSignal(double errorSignal, double weight) {
        this.errorSignal = isBipolar ? (this.output + 1) * (1 - this.output) * errorSignal * weight * 0.5 :
                this.output * (1 - this.output) * errorSignal * weight;
    }

    public void setOutput(double output) {
        this.output = output;
    }


    /**
     * Set the error signal for output neuron
     * @param error: target - yi
     */
    public void setErrorSignalForOutputNeuron(double error) {
        this.errorSignal = isBipolar ? error * (this.output + 1) * (1 - this.output) * 0.5 :
                error * this.output * (1 - this.output);
    }


    public double getWeightByIndex(int i) {
        return this.weights.get(i);
    }


    /**
     * Updates the weights
     * @param momentum:
     * @param learningRate:
     */
    public void updateWeights(double momentum, double learningRate) {
        for (int i = 0; i < this.weightChanges.size(); i++) {
            double curWeightChange = this.weightChanges.get(i);
            double curWeight = this.weights.get(i);
            double updatedWeightChange = momentum * curWeightChange + learningRate * this.errorSignal * this.inputs[i];
            this.weightChanges.set(i, updatedWeightChange);
            this.weights.set(i, curWeight + updatedWeightChange);
        }
    }
}
