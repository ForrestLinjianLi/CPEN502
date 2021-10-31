package main.QLearning;

import robocode.AdvancedRobot;

public class Action {
    public enum ACTION {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        FIRE
    }

    private static final int DISTANCE = 200;
    public static final int NUM_ACTIONS = 5;

    public static void move(AdvancedRobot robot, ACTION actions) {
        switch (actions) {
            case UP:
                robot.setAhead(DISTANCE);
            case DOWN:
                robot.setBack(DISTANCE);
            case LEFT:
                robot.setTurnLeft(90);
                robot.setAhead(DISTANCE);
            case RIGHT:
                robot.setTurnRight(90);
                robot.setAhead(DISTANCE);
//            case FIRE:
//
        }
    }

    public static ACTION getAction(int i) {
        switch (i) {
            case 0:
                return ACTION.UP;
            case 1:
                return ACTION.DOWN;
            case 2:
                return ACTION.LEFT;
            case 3:
                return ACTION.RIGHT;
            default:
                return ACTION.FIRE;
        }
    }

    public static int getActionNum(ACTION action) {
        switch (action) {
            case UP:
                return 0;
            case DOWN:
                return 1;
            case LEFT:
                return 2;
            case RIGHT:
                return 3;
            case FIRE:
                return 4;
            default:
                return -1;
        }
    }
}
