package org.usfirst.frc.team435.robot;

import static org.usfirst.frc.team435.robot.Robot.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * Class to assist in the organization of the autonomous modes
 */
public class Autonomous {

	/**
	 * This autonomous mode is designed to start out positioned directly in front of the auto zone.
	 * From there it will drive into the auto zone attempting to score a robot set.
	 */
	public static void driveForward(){
		if (counter < 25) {
			drive.mecanumDrive_Cartesian(0, .5, 0, 0);
		} else {
			drive.mecanumDrive_Cartesian(0, 0, 0, 0);
		}
		counter++;
	}
	
	/**
	 * This autonomous mode is designed to start out positioned so that the funnel wheels are grabbing a yellow
	 * tote. It will then pick up the tote and deposit it in the auto zone.
	 */
	public static void pickUpTote(){
		if(counter < 25){
			drive.mecanumDrive_Cartesian(0, .3, 0, 0);
			funnelLeft.set(.5);
			funnelRight.set(.5);
		} else if(counter < 40){
			leftClamp.set(Value.kForward);
			rightClamp.set(Value.kForward);
			drive.mecanumDrive_Cartesian(0, 0, 0, 0);
			funnelLeft.set(0);
			funnelRight.set(0);
			lift(.5);
			if(upperLimit.get()){
				counter = 39;
			}
		}else if (counter < 50){
			lift.set(0);
			drive.mecanumDrive_Cartesian(-.5, 0, 0, 0);
		}else if (counter < 65){
			lift.set(-.5);
			if(lowerLimit.get()){
				counter = 64;
			}
		}else if (counter < 100){
			lift.set(0);
			leftClamp.set(Value.kReverse);
			rightClamp.set(Value.kReverse);
			funnelLeft.set(-.1);
			funnelRight.set(-.1);
			drive.mecanumDrive_Cartesian(0, -.5, 0, 0);
		} else {
			funnelLeft.set(0);
			funnelRight.set(0);
			drive.mecanumDrive_Cartesian(0, 0, 0, 0);
		}
		counter++;
	}
	
}
