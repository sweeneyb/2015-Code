package org.usfirst.frc.team435.robot.stateMachine;

import org.usfirst.frc.team435.robot.Robot;
import org.usfirst.frc.team435.robot.stateMachine.transitions.ToteFoundInBay;

public class LoadingInState extends FiniteState {

	protected static LoadingInState instance = new LoadingInState();

	public static LoadingInState getInstance() {
		return instance;
	}

	protected LoadingInState() {
		super("LoadingIn");
	}

	@Override
	public void runState(Robot robot) throws TransitionException {
		if (robot.inBay.get()) {
			transition(new ToteFoundInBay());
			return;
		}
		robot.runFunnel();
		// XXX should there be logic here to make sure the lifter is out of the
		// way?

	}

	public FiniteState transition(ToteFoundInBay mode) {
		return LiftingToteState.getInstance();
	}

}
