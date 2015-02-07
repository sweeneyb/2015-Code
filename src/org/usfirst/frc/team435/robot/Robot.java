package org.usfirst.frc.team435.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.VictorSP;

//import edu.wpi.first.wpilibj.vision.USBCamera;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	public static final double DEADBAND = .1;
	enum AutoChoice {
		DRIVE_FORWARD,
		PICK_UP_TOTE,
		PICK_UP_TOTE_TRASH,
		PICK_UP_TOTES,
		PICK_UP_RECYCLE_MIDDLE,
		PICK_UP_TOTES_VISION
	};
//	USBCamera camera;
	RobotDrive drive;
	VictorSP backLeft;
	CANTalon frontLeft, frontRight, backRight;
	Talon funnelLeft, funnelRight, lift;
	DoubleSolenoid leftClamp, rightClamp;
	Joystick driveStick, shmoStick;
	DigitalInput upperLimit, lowerLimit;
	int counter;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		frontLeft = new CANTalon(0);
		frontRight = new CANTalon(1);
		backLeft = new VictorSP(0);
		backRight = new CANTalon(2);
		funnelLeft = new Talon(0);
		funnelRight = new Talon(1);
		leftClamp = new DoubleSolenoid(0, 1);
		rightClamp = new DoubleSolenoid(2, 3);
		driveStick = new Joystick(0);
		shmoStick = new Joystick(1);
		upperLimit = new DigitalInput(0);
		lowerLimit = new DigitalInput(1);
		drive = new RobotDrive(frontLeft, backLeft, frontRight, backRight);
		// camera = new USBCamera();

		// camera.openCamera();

	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		AutoChoice test = AutoChoice.PICK_UP_TOTE;
		switch (test) {
		case DRIVE_FORWARD:
			if (counter < 25) {
				drive.mecanumDrive_Cartesian(0, .5, 0, 0);
			}
			drive.mecanumDrive_Cartesian(0, 0, 0, 0);
			break;

		case PICK_UP_TOTE:
			break;

		case PICK_UP_TOTE_TRASH:
			break;

		case PICK_UP_TOTES:
			break;

		case PICK_UP_RECYCLE_MIDDLE:
			break;

		case PICK_UP_TOTES_VISION:
			// camera.startCapture();
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		if (driveStick.getTrigger()) {
			drive.mecanumDrive_Cartesian(calc(driveStick.getX() / 2),
					calc(driveStick.getY() / 2),
					calc(driveStick.getTwist() / 2), 0);
		} else {
			drive.mecanumDrive_Cartesian(calc(driveStick.getX()),
					calc(driveStick.getY()), calc(driveStick.getTwist()), 0);
		}
		funnelLeft.set(shmoStick.getRawAxis(2));
		funnelRight.set(shmoStick.getRawAxis(5));
		if(shmoStick.getRawAxis(3) > 0){
			lift.set(-shmoStick.getRawAxis(3));
		} else {
			lift.set(shmoStick.getRawAxis(4)); //THIS NEEDS TO BE FIXED -----------------------------------------------------------------------------------
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

	public double calc(double value) {
		if (Math.abs(value) < DEADBAND) {
			return 0;
		} else {
			return (value - (Math.abs(value) / value * DEADBAND))
					/ (1 - DEADBAND);
		}
	}
}
