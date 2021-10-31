package robot;

import main.Action;
import main.QLearning;
import main.State;
import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class MyFirstRobot extends AdvancedRobot {
    private QLearning q;
    public void run() {
        q = new QLearning();
        while(true) {
            getS
        }
    }
    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        double enemyBearing = e.getBearing();
        double enemyEnergy = e.getEnergy();
        double distance = e.getDistance();
        State state = new State();
        state.setBearing(enemyBearing);
        state.setDistance(distance);
        state.setMyEnergy(this.getEnergy());
        state.setEnemyEnergy(enemyEnergy);
        state.setEnemyHeading(enemyEnergy);
        Action.ACTION action = q.move(getState());
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

    }
}
