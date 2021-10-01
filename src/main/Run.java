package main;

public class Run {


    public static void main(String[] args) {
//        InputStream s = xxnew Scanner();
        double[][] input = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[] target = new double[]{0, 1, 1, 0};
        NeuralNet neuralNet = new NeuralNet(2, 4,
                0.2, 0, -0.5, 0.5);
        neuralNet.train(input, target);
        System.out.println(neuralNet.outputFor(neuralNet.setUpBias(new double[]{0, 1})));
    }

    public static void partA(NeuralNetInterface neuralNetInterface) {

    }

    public static void partB(NeuralNetInterface neuralNetInterface) {

    }

    public static void partC(NeuralNetInterface neuralNetInterface) {

    }
}
