package org.usfirst.frc.team435.robot.stateMachine;

import org.usfirst.frc.team435.robot.Robot;

public class LiftingToteState extends FiniteState {

	protected static LiftingToteState instance = new LiftingToteState();

	public static LiftingToteState getInstance() {
		return instance;
	}

	protected LiftingToteState() {
		super("LiftingTote");
	}

	@Override
	public FiniteState transition(TransitionEvent mode)
			throws TransitionException {
		// TODO Auto-generated method stub
		return super.transition(mode);
	}

	@Override
	public void runState(Robot robot) throws TransitionException {
		// TODO Auto-generated method stub
		super.runState(robot);
	}

}
