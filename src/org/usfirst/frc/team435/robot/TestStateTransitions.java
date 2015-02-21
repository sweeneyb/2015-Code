package org.usfirst.frc.team435.robot;

import org.junit.Test;
import org.usfirst.frc.team435.robot.stateMachine.FiniteState;
import org.usfirst.frc.team435.robot.stateMachine.FiniteState.InState;
import org.usfirst.frc.team435.robot.stateMachine.FiniteState.OutState;
import org.usfirst.frc.team435.robot.stateMachine.TransitionException;
import org.usfirst.frc.team435.robot.stateMachine.transitions.ToteFoundInFunnel;

public class TestStateTransitions {

	@Test
	public void testFindTote() throws TransitionException {
		ToteFoundInFunnel event = new ToteFoundInFunnel();
		FiniteState state = OutState.getInstance().transition(event);
		assert state == InState.getInstance();
	}
}
