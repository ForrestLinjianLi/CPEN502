package robot;

import main.QLearning.Action;
import main.QLearning.QLearning;
import main.QLearning.State;
import robocode.*;

import java.awt.*;

public class MyFirstRobot extends AdvancedRobot {
    private QLearning q;
    private State prevState;
    private State curState;
    private Action.ACTION curAction;
    private int reward;
    private static final int REWARD = 1;

    public void run() {
        prevState = new State();
        curState = new State();
        q = QLearning.getInstance();
        setAllColors(Color.red);
        setAdjustGunForRobotTurn(true); //Gun not Fix to body
        setAdjustRadarForGunTurn(true);
        while(true) {
            curAction = q.move(curState);
            setTurnRadarLeftRadians(2*Math.PI);
            scan();
            move(curAction);
            updateState();
        }
    }

    public void updateState() {
        q.qLearn(reward, curAction, prevState, curState);
        reward = 0;
        prevState = new State(curState);
    }

    public void move(Action.ACTION action) {
        switch (action) {
            case FORWARD:
                ahead(Action.SHORT_DISTANCE);
            case BACKWARD:
                back(Action.SHORT_DISTANCE);
            case BIG_LEFT:
                turnLeft(Action.LARGE_ANGLE);
                back(Action.LONG_DISTANCE);
            case BIG_RIGHT:
                turnRight(Action.LARGE_ANGLE);
                back(Action.LONG_DISTANCE);
            case LEFT:
                turnLeft(Action.SHORT_ANGLE);
                ahead(Action.SHORT_DISTANCE);
            case RIGHT:
                turnRight(Action.SHORT_ANGLE);
                ahead(Action.SHORT_DISTANCE);
            case FIRE:
                fire();
            curState.setHorizontal(getX());
            curState.setVertical(getY());
        }
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        double enemyBearing = e.getBearing();
        double enemyEnergy = e.getEnergy();
        double distance = e.getDistance();
        double enemyHeading = e.getHeading();
        curState.setBearing(enemyBearing);
        curState.setDistance(distance);
        curState.setMyEnergy(getEnergy());
        curState.setEnemyEnergy(enemyEnergy);
        curState.setEnemyHeading(enemyHeading);
        turnGunRight(getHeading() - getGunHeading() + e.getBearing());
    }

    public void fire() {
        fireBullet(1);
    }

    /**
     * This method is called when the robot won.
     * @param event
     */
    @Override
    public void onWin(WinEvent event) {
        reward += 10*REWARD;
        updateState();
    }

    /**
     * This method is called when the robot died.
     * @param event
     */
    @Override
    public void onDeath(DeathEvent event) {
        reward -= 10*REWARD;
        updateState();
    }

    /**
     * This method is called when one of your bullets hits another robot
     * @param event
     */
    @Override
    public void onBulletHit(BulletHitEvent event) {
        reward += 2*REWARD;
    }

    /**
     * This method is called when one of our bullets has missed.
     * @param event
     */
    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        reward -= 4/curState.getMyEnergy()*REWARD;
    }

    /**
     * This method is called when your robot is hit by a bullet.
     * @param event
     */
    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        reward -= 4/curState.getMyEnergy()*REWARD;
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        reward -= 2/curState.getMyEnergy()*REWARD;
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        q.save();
    }
}
