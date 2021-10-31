package robot;

import main.QLearning.Action;
import main.QLearning.QLearning;
import main.QLearning.State;
import robocode.*;

public class MyFirstRobot extends AdvancedRobot {
    private QLearning q;
    private State state;
    private int reward;


    public MyFirstRobot() {
        super();
        state = new State();
    }

    public void run() {
        q = new QLearning();
        while(true) {
            scan();
            Action.ACTION action = q.move(getState());
        }
    }
    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        double enemyBearing = e.getBearing();
        double enemyEnergy = e.getEnergy();
        double distance = e.getDistance();
        state.setBearing(enemyBearing);
        state.setDistance(distance);
        state.setMyEnergy(this.getEnergy());
        state.setEnemyEnergy(enemyEnergy);
        state.setEnemyHeading(enemyEnergy);
    }
    /**
     * onHitByBullet: What to do when you're hit by a bullet
     */
    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }

    public void fire() {
        scan();
    }

    public State getState() {
        return State.generateState(State.getStateArray(state));
    }

    @Override
    public void onDeath(DeathEvent event) {

    }


//    /**
//     * This method is called when the robot collides with the opponent.
//     * @param event
//     */
//    @Override
//    public void onHitRobot(HitRobotEvent event) {
//
//    }
//
//    /**
//     * This method is called when the robot collides with the opponent.
//     * @param event
//     */
//    @Override
//    public void onHitRobot(HitRobotEvent event) {
//
//    }
//
//    /**
//     * This method is called when another robot dies.
//     * @param event
//     */
//    @Override
//    public void onRobotDeath(RobotDeathEvent event) {
//
//    }
}
