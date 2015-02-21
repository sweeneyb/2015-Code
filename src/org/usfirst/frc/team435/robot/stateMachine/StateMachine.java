package org.usfirst.frc.team435.robot.stateMachine;

import org.usfirst.frc.team435.robot.stateMachine.FiniteState.InState;
import org.usfirst.frc.team435.robot.stateMachine.FiniteState.OffState;
import org.usfirst.frc.team435.robot.stateMachine.FiniteState.OutState;


//XXX Remove this class?
public class StateMachine {
	enum FiniteMode {
		IN(InState.getInstance()), OUT(OutState.getInstance()), OFF(OffState.getInstance()); //CLAMP_LIFT, UNCLAMP_DROP, OFF,
		
		public final FiniteState state;
		
		private FiniteMode(FiniteState state) {
			this.state = state;
		}
	}
	
	private FiniteMode state = FiniteMode.OFF;
	
//	public void transition(FiniteMode from, FiniteMode to) throws TransitionException {
//		from.state.transition(to.state);
//	}
}
