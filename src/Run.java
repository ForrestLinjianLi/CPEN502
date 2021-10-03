import main.NeuralNet;

import java.util.Objects;
import java.util.Scanner;

public class Run {


    public static void main(String[] args) {
        double[][] inputBinary = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[] targetBinary = new double[]{0, 1, 1, 0};
        double[][] inputBipolar = new double[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        double[] targetBipolar = new double[]{-1, 1, 1, -1};

        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
//            System.out.println("Please input the argNumInputs, argNumHidden, argLearningRate, argMomentumTerm, argA, argB to train: \n");
//            System.out.println("Default? Y/N: ");
//            String isDefault = s.nextLine();
//            if (!"YN".contains(isDefault)) {
//                System.out.println("Illegal command");
//                continue;
//            }
//            if (Objects.equals(isDefault, "N")) {
//                System.out.println("argNumInputs: ");
//                int argNumInputs = Integer.parseInt(s.nextLine());
//                System.out.println("argNumHidden: ");
//                int argNumHidden = Integer.parseInt(s.nextLine());
//                System.out.println("argLearningRate: ");
//                double argLearningRate = Double.parseDouble(s.nextLine());
//                System.out.println("argMomentumTerm: ");
//                double argMomentumTerm = Double.parseDouble(s.nextLine());
//                System.out.println("argA: ");
//                int argA = Integer.parseInt(s.nextLine());
//                System.out.println("argB: ");
//                int argB = Integer.parseInt(s.nextLine());
//                neuralNet = new NeuralNet(argNumInputs, argNumHidden, argLearningRate, argMomentumTerm, argA, argB);
//            } else {
//
//            }

            System.out.println("argMomentumTerm: ");
            double argMomentumTerm = Double.parseDouble(s.nextLine());
            NeuralNet neuralNet;
            System.out.println("Bipolar? (Y/N)");
            String isBipolar = s.nextLine();
            if (!"YN".contains(isBipolar)) {
                System.out.println("Illegal command");
                continue;
            }
            int epochCount = 0;
            if (isBipolar.equals("Y")) {
                neuralNet = new NeuralNet(2, 4, 0.2, argMomentumTerm, -1, 1);
                System.out.println("Bipolar starts running.");
                epochCount = neuralNet.train(inputBipolar, targetBipolar);
            } else {
                neuralNet = new NeuralNet(2, 4, 0.2, argMomentumTerm, 0, 1);
                System.out.println("Binary starts running.");
                epochCount = neuralNet.train(inputBinary, targetBinary);
            }
            System.out.printf("The number of epoch is: %d \n", epochCount);
        }
    }
}
