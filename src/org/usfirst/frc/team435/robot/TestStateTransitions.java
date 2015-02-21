package org.usfirst.frc.team435.robot;

import junit.framework.Assert;

import org.junit.Test;
import org.usfirst.frc.team435.robot.stateMachine.FiniteState;
import org.usfirst.frc.team435.robot.stateMachine.FiniteState.OutState;
import org.usfirst.frc.team435.robot.stateMachine.LiftingToteState;
import org.usfirst.frc.team435.robot.stateMachine.LoadingInState;
import org.usfirst.frc.team435.robot.stateMachine.TransitionException;
import org.usfirst.frc.team435.robot.stateMachine.transitions.ToteFoundInBay;
import org.usfirst.frc.team435.robot.stateMachine.transitions.ToteFoundInFunnel;

public class TestStateTransitions {

	@Test
	public void testFindTote() throws TransitionException {
		ToteFoundInFunnel event = new ToteFoundInFunnel();
		FiniteState state = OutState.getInstance().transition(event);
		Assert.assertTrue(state == LoadingInState.getInstance());
	}

	@Test
	public void testFindTote2() throws TransitionException {
		ToteFoundInFunnel event = new ToteFoundInFunnel();
		FiniteState state = OutState.getInstance().transition(event);
		Assert.assertFalse(state == LiftingToteState.getInstance());
	}

	@Test(expected = TransitionException.class)
	public void testFindTote3() throws TransitionException {
		ToteFoundInBay event = new ToteFoundInBay();
		FiniteState state = OutState.getInstance().transition(event);
		Assert.assertTrue(state == LiftingToteState.getInstance());
	}
}
