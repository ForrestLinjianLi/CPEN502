package test;

import main.NeuralNet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.NNUtil;

import static org.junit.jupiter.api.Assertions.*;

class NeuralNetTest {

    private NeuralNet neuralNet;

    @BeforeEach
    void setUp() {
        this.neuralNet = new NeuralNet(2, 4,
                0.2, 0, -0.5, 0.5);
    }

    @Test
    void outputFor() {
    }

    @Test
    void trainWithTrainingSet() {

    }

    @Test
    void save() {
    }

    @Test
    void load() {
    }

    @Test
    void initializeWeightsTest() {
        assertEquals(this.neuralNet.getArgNumHidden() + 1,
                this.neuralNet.getHiddenLayer().size());
        this.neuralNet.getHiddenLayer()
                .forEach(x -> x.getWeights()
                        .forEach(y -> assertTrue(y >= this.neuralNet.getArgA()
                                && y <= this.neuralNet.getArgB())));
        this.neuralNet.getOutputNeuron().getWeights()
                .forEach(x -> assertTrue(x >= this.neuralNet.getArgA() && x <= this.neuralNet.getArgB()));
    }

    @Test
    void zeroWeightsTest() {
        this.neuralNet.zeroWeights();
        this.neuralNet.getHiddenLayer().forEach(x -> x.getWeights().forEach(y -> assertEquals(0, y)));
        this.neuralNet.getOutputNeuron().getWeights().forEach(x -> assertEquals(0, x));
    }

    @Test
    void forwardFeedTest() {
        NeuralNet nn = new NeuralNet(2, 4, 0.2, 0, 1,1);
        assertEquals(nn.forwardFeed(NNUtil.INPUT[0]), 0);
//        assertEquals(nn.forwardFeed(NNUtil.INPUT[1]), );
    }
}