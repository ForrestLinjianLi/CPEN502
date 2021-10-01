package main;

import java.io.InputStream;
import java.util.Scanner;

public class Run {


    public static void main(String[] args) {
        double[][] inputBinary = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[] targetBinary = new double[]{0, 1, 1, 0};
        double[][] inputBipolar = new double[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        double[] targetBipolar = new double[]{-1, 1, 1, -1};
        NeuralNet neuralNet;

        Scanner s = new Scanner(System.in);
        System.out.println("Please input the argNumInputs, argNumHidden, argLearningRate, argMomentumTerm, argA, argB to train: \n");
        System.out.println("Default? Y/N: ");
        boolean isDefault = Boolean.parseBoolean(s.nextLine());
        if (isDefault) {
            System.out.println("argNumInputs: ");
            int argNumInputs = Integer.parseInt(s.nextLine());
            System.out.println("argNumHidden: ");
            int argNumHidden = Integer.parseInt(s.nextLine());
            System.out.println("argLearningRate: ");
            double  argLearningRate = Double.parseDouble(s.nextLine());
            System.out.println("argMomentumTerm: ");
            double argMomentumTerm = Double.parseDouble(s.nextLine());
            System.out.println("argA: ");
            int argA = Integer.parseInt(s.nextLine());
            System.out.println("argB: ");
            int argB = Integer.parseInt(s.nextLine());
            neuralNet = new NeuralNet(argNumInputs, argNumHidden, argLearningRate, argMomentumTerm, argA, argB);
        } else {
            neuralNet = new NeuralNet(2, 4, 0.2, 0, 0, 1);
        }

        System.out.println("Bipolar? (Y/N)");
        boolean isBipolar = Boolean.parseBoolean(s.nextLine());
        if (isBipolar) {
            neuralNet.train(inputBipolar, targetBipolar);
        } else {
            neuralNet.train(inputBinary, targetBinary);
        }

        neuralNet.train(inputBinary, targetBinary);
        System.out.println(neuralNet.outputFor(neuralNet.setUpBias(new double[]{0, 1})));
    }
}
