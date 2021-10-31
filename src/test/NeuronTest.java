package test;

import exception.NumberMismatchException;
import main.NN.Neuron;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NeuronTest {

    private Neuron bipolarNeuron;

    @BeforeEach
    void setUp() {
        this.bipolarNeuron = new Neuron(3, true);
    }

    @Test
    void getOutput() {

    }

    @Test
    void getErrorSignal() {

    }

    @Test
    void setErrorSignal() {
    }

    @Test
    void setOutput() {
    }

    @Test
    void testSetErrorSignal() {
    }

    @Test
    void getWeightByIndex() {
    }

    @Test
    void updateWeights() {

    }

    @Test
    void sumTest() {
        double[] testInputs = new double[]{1, 1, 1};
        try {
            Assertions.assertEquals(this.bipolarNeuron.sum(testInputs), 1+1+1);
        } catch (NumberMismatchException e) {
            Assertions.fail();
        }
    }
}