package test;

import main.NeuralNet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NeuralNetTest {

    private NeuralNet neuralNet;

    @BeforeEach
    void setUp() {
        neuralNet = new NeuralNet(2, 4,
                0.2, 0, -0.5, 0.5);
    }

    @Test
    void outputFor() {
    }

    @Test
    void train() {
    }

    @Test
    void save() {
    }

    @Test
    void load() {
    }

    @Test
    void initializeWeightsTest() {
        assertEquals( 2, this.neuralNet.weights.size());
        assertEquals(this.neuralNet.argNumHidden * this.neuralNet.argNumInputs,
                this.neuralNet.weights.get(0).size());
        assertEquals(this.neuralNet.argNumHidden,
                this.neuralNet.weights.get(1).size());
        this.neuralNet.weights.get(0)
                .forEach(x -> assertTrue(x >= this.neuralNet.argA && x <= this.neuralNet.argB ));
        this.neuralNet.weights.get(1)
                .forEach(x -> assertTrue(x >= this.neuralNet.argA && x <= this.neuralNet.argB));
    }

    @Test
    void zeroWeightsTest() {
        this.neuralNet.zeroWeights();
        assertEquals( 2, this.neuralNet.weights.size());
        assertEquals(this.neuralNet.argNumHidden * this.neuralNet.argNumInputs,
                this.neuralNet.weights.get(0).size());
        assertEquals(this.neuralNet.argNumHidden,
                this.neuralNet.weights.get(1).size());
        this.neuralNet.weights.get(0).forEach(x -> assertEquals(0, x));
        this.neuralNet.weights.get(1).forEach(x -> assertEquals(0, x));
    }
}