package main.ReplayMemory;

import lombok.Getter;
import lombok.Setter;
import main.robot.Action;
import main.robot.State;

@Getter
@Setter
public class Experience {
    private double reward;
    private State curState;
    private State prevState;
    private Action prevAction;
}
