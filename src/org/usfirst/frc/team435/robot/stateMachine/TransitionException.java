package org.usfirst.frc.team435.robot.stateMachine;

public class TransitionException extends Exception {
	public TransitionException(FiniteState state){
		super("Transition into \""+state+"\"not defined").
	}
}
