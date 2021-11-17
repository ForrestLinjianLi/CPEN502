package robot;

import main.QLearning.Action;
import main.QLearning.QLearning;
import main.QLearning.State;
import robocode.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;

public class MyFirstRobot extends AdvancedRobot {
    private QLearning q;
    private State prevState;
    private State curState;
    private Action curAction;
    private double reward;
    private EnemyBot enemyBot;
    private static final double REWARD = 0.5;

    private static int winCount = 0;
    private static int lastWinCount = 0;

    private Action tempAction;
    private double brearing;
    private static String LUT_FILE_NAME = "LUT.txt";
    private static String RESULT_FILE_NAME = "result.txt";

    public void run() {
        prevState = new State();
        curState = new State();
        enemyBot = new EnemyBot();
        q = QLearning.getInstance(getDataFile(LUT_FILE_NAME));
        tempAction  = q.move(curState);
        setAllColors(Color.red);
        setAdjustGunForRobotTurn(true); //Gun not Fix to body
        setAdjustRadarForGunTurn(true);
        while(true) {
            setTurnRadarLeftRadians(2*Math.PI);
            scan();
//            curAction = q.move(curState);
            curAction = tempAction;
            move(curAction);
            execute();
            updateState();
        }
    }

    public void updateState() {
        tempAction  = q.move(curState);
        q.qLearn(reward, curAction, prevState, curState, tempAction);
        reward = 0;
        prevState = new State(curState);
    }

    public void move(Action action) {
        switch (action) {
            case FORWARD:
                setAhead(Action.SHORT_DISTANCE);
            case BACKWARD:
                setBack(Action.SHORT_DISTANCE);
            case MOVE_TO_CENTRE:
                double b = absoluteBearing(getX(), getY(), 400, 300);
                setTurnRight(b);
                setBack(Action.LONG_DISTANCE);
            case AHEAD_LEFT:
                setTurnLeft(Action.SHORT_ANGLE);
                setAhead(Action.SHORT_DISTANCE);
            case AHEAD_RIGHT:
                setTurnRight(Action.SHORT_ANGLE);
                ahead(Action.SHORT_DISTANCE);
            case FIRE:
//                predictiveFire();
                turnGunRight(getHeading()-getGunHeading()+brearing);
                fire(3);
            case BACK_RIGHT:
                setTurnRight(Action.SHORT_ANGLE);
                setBack(Action.SHORT_DISTANCE);
            case BACK_LEFT:
                setTurnLeft(Action.SHORT_ANGLE);
                setBack(Action.SHORT_DISTANCE);
            curState.setHorizontal(getX());
            curState.setVertical(getY());
        }
    }

    private double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2-x1;
        double yo = y2-y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        double enemyBearing = e.getBearing();
        brearing = enemyBearing;
        double distance = e.getDistance();
        curState.setBearing(enemyBearing);
        curState.setDistance(distance);
        curState.setMyEnergy(getEnergy());
        enemyBot.update(e, this);
    }

    private double normalizeBearing(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    public void predictiveFire() {
        // calculate speed of bullet
        double bulletSpeed = 20;
        // distance = rate * time, solved for time
        long time = (long)(enemyBot.getDistance() / bulletSpeed);

        // calculate gun turn to predicted x,y location
        double futureX = enemyBot.getFutureX(time);
        double futureY = enemyBot.getFutureY(time);
        double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);

        // turn the gun to the predicted x,y location
        setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));

        // if the gun is cool and we're pointed in the right direction, shoot!
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
            setFire(2);
        }
    }

    /**
     * This method is called when the robot won.
     * @param event
     */
    @Override
    public void onWin(WinEvent event) {
        winCount++;
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
        reward += REWARD;
    }

    /**
     * This method is called when one of our bullets has missed.
     * @param event
     */
    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        reward -= 2/(curState.getMyEnergy()+1)*REWARD;
    }

    /**
     * This method is called when your robot is hit by a bullet.
     * @param event
     */
    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        reward -= REWARD;
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        reward -= REWARD;
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        reward -= REWARD;
    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        int roundNum = event.getRound()+1;
        int roundPeirod = 50;
        if (roundNum%roundPeirod == 0) {
            File file = getDataFile(RESULT_FILE_NAME);

            int trueWinCount = winCount - lastWinCount;

            double winrate = (double) trueWinCount/roundPeirod;
            try (RobocodeFileWriter fileWriter = new RobocodeFileWriter(file.getAbsolutePath(),true)){
                fileWriter.write(roundNum + ","+ trueWinCount + "," +  winrate +"\n");
                fileWriter.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
            lastWinCount = winCount;

        }

    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        q.save(getDataFile(LUT_FILE_NAME));
    }
}
