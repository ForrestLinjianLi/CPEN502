import main.NeuralNet;

import java.util.Objects;
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
        String isDefault = s.nextLine();
        if (Objects.equals(isDefault, "N")) {
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
        int epochCount = 0;
        if (isBipolar) {
            epochCount = neuralNet.train(inputBipolar, targetBipolar);
        } else {
            epochCount = neuralNet.train(inputBinary, targetBinary);
        }
        System.out.printf("The number of epoch is: %d", epochCount);

    }
}
