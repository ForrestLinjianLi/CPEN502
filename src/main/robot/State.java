package main.robot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class State implements Serializable {
    private double energeDiff = 0;
    private double x = 0;
    private double y = 0;
    private double relativeX = 0;
    private double relativeY = 0;
    private boolean isNN;

    public State(boolean isNN) {
        this.isNN = isNN;
    }
//    private static final double[] ANGLE_RANGE = new double[]{-180, 180};
    private static final double[] X_RANGE = new double[]{0, 800, 0.2, 2};
    private static final double[] RX_RANGE = new double[]{-800, 800, 0.2, 2};
    private static final double[] Y_RANGE = new double[]{0, 600, 0.2, 2};
    private static final double[] RY_RANGE = new double[]{-600, 600, 0.2, 2};
    private static final double[] ENERGY_RANGE = new double[]{-200, 200, 0.2, 2};
//    private static final double[] DISTANCE_RANGE = new double[]{0, 1000};
    private static final int VERTICAL_RANGE = 150;
    private static final int HORIZONTAL_RANGE = 200;

    public State(State state, boolean isNN) {
        this.energeDiff = state.getEnergeDiff();
        this.x = state.getX();
        this.y = state.getY();
        this.relativeX = state.getRelativeX();
        this.relativeY = state.getRelativeY();
        this.isNN = isNN;
    }


    public void setEnergeDiff(double energeDiff) {
        this.energeDiff = scale(energeDiff, ENERGY_RANGE);
    }

    public void setX(double x) {
        this.x = scale(x, X_RANGE) ;
    }

    public void setY(double y) {
        this.y = scale(y, Y_RANGE);
    }

    public void setRelativeX(double relativeX) {
        this.relativeX = scale(relativeX, RX_RANGE);
    }

    public void setRelativeY(double relativeY) {
        this.relativeY = scale(relativeY, RY_RANGE);
    }

    public void setRelativeCoord(double distance, double heading, double bearing) {
        double absBearing = heading + bearing;
        absBearing += absBearing < 0? 360:0;
        this.setRelativeX(-distance*Math.sin(Math.toRadians(absBearing)));
        this.setRelativeY(-distance*Math.cos(Math.toRadians(absBearing)));
    }

//    public double getLevel(int range, double target) {
//        return Math.ceil(target / range);
//    }

    public double scale(double target, double[] params) {
        double ratio = Math.abs(target-params[0]) / (params[1] - params[0]);
        return isNN? (-params[2] + ratio*params[2]*2) : Math.round(-params[3] + ratio*params[3]*2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State state = (State) o;
        return Double.compare(state.energeDiff, energeDiff) == 0 && Double.compare(state.x, x) == 0 && Double.compare(state.y, y) == 0 && Double.compare(state.relativeX, relativeX) == 0 && Double.compare(state.relativeY, relativeY) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(energeDiff, x, y, relativeX, relativeY);
    }

    public double[] toDoubleArray() {
        return new double[]{this.energeDiff, this.x, this.y, this.relativeX, this.relativeY};
    }
}
