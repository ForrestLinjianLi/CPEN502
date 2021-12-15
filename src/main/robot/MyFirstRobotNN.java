package main.robot;

import main.ReplayMemory.Experience;
import main.ReplayMemory.ReplayMemory;
import main.exception.NumberMismatchException;
import main.NN.NeuralNet;
import robocode.*;

import java.awt.*;

public class MyFirstRobotNN extends MyFirstRobot {
    private NeuralNet nn;
    private static String NN_FILE_NAME = "NN.ser";
    private static final int MINI_BATCH_SIZE = 20;
    private static final int MEMORY_SIZE = 100;
    private static ReplayMemory<Experience> replayMemory = new ReplayMemory<>(MEMORY_SIZE);

    @Override
    public void run() {
        prevState = new State(true);
        curState = new State(true);
        enemyBot = new EnemyBot();

        //Load NN after the offline training.
        nn = NeuralNet.getInstance(getDataFile(NN_FILE_NAME));
        try {
            setAllColors(Color.red);
            setAdjustGunForRobotTurn(true); //Gun not Fix to body
            setAdjustRadarForGunTurn(true);
            while(true) {
                setTurnRadarLeftRadians(2*Math.PI);
                scan();
                curAction = nn.getNextAction(curState);
                move(curAction);
                execute();
                updateState();
                reward = 0;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateState() {
        try {
            Experience experience = new Experience();
            experience.setPrevState(prevState);
            experience.setPrevAction(curAction);
            experience.setCurState(curState);
            experience.setReward(reward);
            replayMemory.add(experience);
            if (replayMemory.sizeOf() >= MINI_BATCH_SIZE) {
                for (Object object: replayMemory.randomSample(MINI_BATCH_SIZE)) {
                    Experience experienceBatch = (Experience) object;
                    nn.qTrain(experienceBatch.getReward(),
                            experienceBatch.getPrevState(),
                            experienceBatch.getPrevAction(),
                            experienceBatch.getCurState());
                }
            }
        } catch (IllegalAccessException | NumberMismatchException e) {
            e.printStackTrace();
        }
    }

    /**
     * onScannedRobot: What to do when you see another main.robot
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double enemyBearing = e.getBearing();
        prevState = new State(curState, true);
        this.brearing = enemyBearing;
        this.curState.setX(getX());
        this.curState.setY(getY());
        this.curState.setRelativeCoord(e.getDistance(), getHeading(), enemyBearing);
        this.curState.setEnergeDiff(getEnergy() - e.getEnergy());
//        this.enemyBot.update(e, this);
    }


    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        nn.save(getDataFile(NN_FILE_NAME), true);
    }
}
