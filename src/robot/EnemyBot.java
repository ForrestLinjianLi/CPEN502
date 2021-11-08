package robot;

import lombok.Getter;
import robocode.Robot;
import robocode.ScannedRobotEvent;

@Getter
public class EnemyBot {
    private double x;
    private double y;
    private double distance;
    private double heading;
    private double velocity;


    public void update(ScannedRobotEvent e, Robot robot) {
        double absBearingDeg = (robot.getHeading() + e.getBearing());
        if (absBearingDeg < 0) absBearingDeg += 360;

        // yes, you use the _sine_ to get the X value because 0 deg is North
        x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();

        // yes, you use the _cosine_ to get the Y value because 0 deg is North
        y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();
        distance = e.getDistance();
        heading = e.getHeading();
        velocity = e.getVelocity();
    }

    public double getFutureX(long when){
        return x + Math.sin(Math.toRadians(getHeading())) * getVelocity() * when;
    }

    public double getFutureY(long when){
        return y + Math.cos(Math.toRadians(getHeading())) * getVelocity() * when;
    }
}
