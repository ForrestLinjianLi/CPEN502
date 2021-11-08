package main.QLearning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class State {
    private double enemyHeading = 0;
    private double bearing = 0;
    private double enemyEnergy = 0;
    private double myEnergy = 0;
    private double distance = 0;
    private double vertical = 0;
    private double horizontal = 0;

    private static final int ANGLE_RANGE = 90;
    private static final int ENERGY_RANGE = 50;
    private static final int DISTANCE_RANGE = 250;
    private static final int VERTICAL_RANGE = 150;
    private static final int HORIZONTAL_RANGE = 200;

    public State(State state) {
        this.enemyHeading = state.getEnemyHeading();
        this.bearing = state.getBearing();
        this.enemyEnergy = state.getEnemyEnergy();
        this.myEnergy = state.getMyEnergy();
        this.distance = state.getDistance();
        this.vertical = state.getVertical();
        this.horizontal = state.getHorizontal();
    }

    public void setEnemyHeading(double enemyHeading) {
        this.enemyHeading = getLevel(ANGLE_RANGE, enemyHeading);
    }

    public void setBearing(double bearing) {
        this.bearing = getLevel(ANGLE_RANGE, bearing);
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
    public void setVertical(double y) {
        this.vertical = getLevel(VERTICAL_RANGE, y);
    }
    public void setHorizontal(double x) {
        this.horizontal = getLevel(HORIZONTAL_RANGE, x);
    }

    public double getLevel(int range, double target) {
        return Math.ceil(target / range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State state = (State) o;
        return Double.compare(state.enemyHeading, enemyHeading) == 0 && Double.compare(state.bearing, bearing) == 0 && Double.compare(state.enemyEnergy, enemyEnergy) == 0 && Double.compare(state.myEnergy, myEnergy) == 0 && Double.compare(state.distance, distance) == 0 && Double.compare(state.vertical, vertical) == 0 && Double.compare(state.horizontal, horizontal) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enemyHeading, bearing, enemyEnergy, myEnergy, distance, vertical, horizontal);
    }
}
