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

    public void fire() {
        scan();
    }

    public State getState() {
        return State.generateState(State.getStateArray(state));
    }

    /**
     * This method is called when the robot won.
     * @param event
     */
    @Override
    public void onWin(WinEvent event) {
        reward += 10;
    }

    /**
     * This method is called when the robot died.
     * @param event
     */
    @Override
    public void onDeath(DeathEvent event) {
        reward -= 10;
    }

    /**
     * This method is called when one of your bullets hits another robot
     * @param event
     */
    @Override
    public void onBulletHit(BulletHitEvent event) {
        double bulletPower = event.getBullet().getPower();
        reward += 3*(int)bulletPower;
    }

    /**
     * This method is called when one of our bullets has missed.
     * @param event
     */
    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        double bulletPower = event.getBullet().getPower();
        reward -= (int)bulletPower;
    }

    /**
     * This method is called when your robot is hit by a bullet.
     * @param event
     */
    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        double bulletPower = event.getBullet().getPower();
        reward -= 3*(int)bulletPower;
    }
}
