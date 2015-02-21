package org.usfirst.frc.team435.robot.stateMachine;

import org.usfirst.frc.team435.robot.Robot;
import org.usfirst.frc.team435.robot.stateMachine.transitions.ToteFoundInFunnel;

public class FiniteState {

	final String name;

	public FiniteState(String name) {
		this.name = name;
	}

	public FiniteState transition(TransitionEvent mode)
			throws TransitionException {
		throw new TransitionException(mode);
	}
	
	public void runState(Robot robot) {
		// does nothing by default
	}

	public static class InState extends FiniteState {
		protected InState(String name) {
			super(name);
		}

		private static InState instance = new InState("in");

		public static InState getInstance() {
			return instance;
		}
	}

	public static class OutState extends FiniteState {
		protected OutState(String name) {
			super(name);
		}

		private static OutState instance = new OutState("out");

		public static OutState getInstance() {
			return instance;
		}
		
		public void runState(Robot robot) {
			// logic probably backwards
			if(robot.inFunnel.get()) {
				try {
					robot.robotState = transition(new ToteFoundInFunnel());
				} catch (TransitionException e) {
					// send a message to the dashboard?
				}
			}
			// if no tote, stay in this state
		}
		
		public FiniteState transition(ToteFoundInFunnel event)
				throws TransitionException {
			return InState.instance;
			
		}
	}

	public static class OffState extends FiniteState {
		protected OffState(String name) {
			super(name);
		}

		private static OffState instance = new OffState("off");

		public static OffState getInstance() {
			return instance;
		}
	}

}
