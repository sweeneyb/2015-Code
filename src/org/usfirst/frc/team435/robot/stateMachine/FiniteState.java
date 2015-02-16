package org.usfirst.frc.team435.robot.stateMachine;

public class FiniteState {
	

	
	final String name;
	
	public FiniteState(String name) {
		this.name = name;  
	}
	
	public void transition(FiniteState toState) throws TransitionException {
		throw new TransitionException(toState); 
	}
	
	public static class InState extends FiniteState {
		protected InState(String name) {
			super(name);
		}

		private InState instance = new InState("in");
		
		public static InState getInstance() {
			return instance;
		}
	}
	
	
}
