package main.QLearning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class State {
    private double enemyHeading;
    private double bearing;
    private double enemyEnergy;
    private double myEnergy;
    private double distance;

    private static final int[] BEARING_RANGE = new int[]{-180, -90, 0, 90, 180};
    private static final int[] HEADING_RANGE = new int[]{0, 90, 180, 270, 360};
    private static final int ENERGY_RANGE = 50;
    private static final int[] DISTANCE_RANGE = new int[]{0, 250, 500, 750, 1000};

    public void setEnemyHeading(double enemyHeading) {
        this.enemyHeading = getLevel(HEADING_RANGE, enemyHeading);
    }

    public void setBearing(double bearing) {
        this.bearing = getLevel(BEARING_RANGE, bearing);
    }

    public void setEnemyEnergy(double enemyEnergy) {
        this.enemyEnergy = enemyEnergy > ENERGY_RANGE ? 1 : 0;
    }

    public void setMyEnergy(double myEnergy) {
        this.myEnergy = myEnergy > ENERGY_RANGE ? 1 : 0;
    }

    public void setDistance(double distance) {
        this.distance = getLevel(DISTANCE_RANGE, distance);
    }

    public double getLevel(int[] range, double target) {
        for (int i = 0; i < range.length - 1; i++) {
            if (target >= range[i] && target < range[i+1]) {
                return i;
            }
        }
        return -1;
    }

    public static double[] generateStateArray(double[] X) {
        State state = generateState(X);
        return new double[]{state.getEnemyHeading(), state.getBearing(), state.getEnemyEnergy(), state.getMyEnergy(), state.getDistance()};
    }

    public static double[] getStateArray(State state) {
        return new double[]{state.getEnemyHeading(), state.getBearing(), state.getEnemyEnergy(), state.getMyEnergy(), state.getDistance()};
    }

    public static State generateState(double[] X) {
        State state = new State();
        state.setEnemyHeading(X[0]);
        state.setBearing(X[1]);
        state.setEnemyEnergy(X[2]);
        state.setMyEnergy(X[3]);
        state.setDistance(X[4]);
        return state;
    }
}
