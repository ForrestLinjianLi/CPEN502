package main;

import main.NN.NeuralNet;
import main.QLearning.QLearning;
import main.robot.Action;

import java.io.File;
import java.util.Scanner;

public class Run {


    public static void main(String[] args) throws Exception {
        double[][] inputBinary = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[] targetBinary = new double[]{0, 1, 1, 0};
        double[][] inputBipolar = new double[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        double[] targetBipolar = new double[]{-1, 1, 1, -1};

        Scanner s = new Scanner(System.in);
        int epochCount = 0;


        while (true) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++");
            NeuralNet neuralNet;
            System.out.println("Robocode? (Y/N)");
            String isRobocode = s.nextLine();
            if (!"YN".contains(isRobocode)) {
                System.out.println("Illegal command");
                continue;
            }if (isRobocode.equals("Y")){
                // First use LUT data to do the offline training
                // Ask for different hyperparameters:
                QLearning q = QLearning.getInstance(new File("./data/LUT.ser"));
                System.out.println("do load? Y/N");
                String onLoad = s.nextLine();
                if (onLoad.equals("Y")) {
                    neuralNet = new NeuralNet();
                    neuralNet.load(new File(NeuralNet.FILE_PATH));
                } else {
                    System.out.println("argMomentumTerm: ");
                    double argMomentumTerm = Double.parseDouble(s.nextLine());
                    System.out.println("argNumHidden: ");
                    int argNumHidden = Integer.parseInt(s.nextLine());
                    System.out.println("argLearningRate: ");
                    double argLearningRate = Double.parseDouble(s.nextLine());

                    int argNumInputs = 5;
                    neuralNet = new NeuralNet(argNumInputs, argNumHidden, Action.NUM_ACTIONS, argLearningRate, argMomentumTerm,-1,1);
                }
                System.out.println("Robocode offline training starts running.");
                try {
                    epochCount = neuralNet.trainByLUT(q.getLookUpTable());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }else{
                System.out.println("argMomentumTerm: ");
                double argMomentumTerm = Double.parseDouble(s.nextLine());
                System.out.println("Bipolar? (Y/N)");
                String isBipolar = s.nextLine();
                if (!"YN".contains(isBipolar)) {
                    System.out.println("Illegal command");
                    continue;
                }
                if (isBipolar.equals("Y")) {
                    neuralNet = new NeuralNet(2, 4, 1, 0.2, argMomentumTerm, -1, 1);
                    System.out.println("Bipolar starts running.");
                    epochCount = neuralNet.train(inputBipolar, targetBipolar);
                } else {
                    neuralNet = new NeuralNet(2, 4, 1, 0.2, argMomentumTerm, 0, 1);
                    System.out.println("Binary starts running.");
                    epochCount = neuralNet.train(inputBinary, targetBinary);
                }
            }

            System.out.printf("The number of epoch is: %d \n", epochCount);
            neuralNet.save(new File("./data/NN.ser"));
        }
    }
}
