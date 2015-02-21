package org.usfirst.frc.team435.robot.stateMachine;

import org.usfirst.frc.team435.robot.stateMachine.StateMachine.FiniteMode;

public class TransitionException extends Exception {
	public TransitionException(TransitionEvent state){
		super("Transition into \""+state+"\"not defined");
	}

//	public TransitionException(FiniteMode mode) {
//		// TODO Auto-generated constructor stub
//	}
}
