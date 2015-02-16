package org.usfirst.frc.team435.robot.stateMachine;

import org.usfirst.frc.team435.robot.stateMachine.FiniteState.InState;


public class StateMachine {
	enum FiniteMode {
		IN(InState.getInstance()), //OUT, CLAMP_LIFT, UNCLAMP_DROP, OFF,
		
		public final FiniteState state;
		
		private FiniteMode(FiniteState state) {
			this.state = state;
		}
	}
}
