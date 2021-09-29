package test;

import main.Neuron;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NeuronTest {

    private Neuron bipolarNeuron;

    @BeforeEach
    void setUp() {
        this.bipolarNeuron = new Neuron(20, 1,-1);
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
}