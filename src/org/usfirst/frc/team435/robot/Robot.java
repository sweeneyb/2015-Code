package org.usfirst.frc.team435.robot;

import static java.lang.Math.pow;
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
	enum AutoChoice {
		DRIVE_FORWARD,
		PICK_UP_TOTE,
		PICK_UP_TOTE_TRASH,
		PICK_UP_TOTES,
		PICK_UP_RECYCLE_MIDDLE,
		PICK_UP_TOTES_VISION
	};
//	USBCamera camera;
	// --Drive Motors--
	RobotDrive drive;
	VictorSP backLeft;
	CANTalon frontLeft, frontRight, backRight;
	// --Funnel Components--
	Talon funnelLeft, funnelRight;
	// --Lift Components--
	Jaguar lift;
	DigitalInput upperLimit, lowerLimit;
	DoubleSolenoid leftClamp, rightClamp;
	// -- OI --
	Joystick driveStick, shmoStick;
	
	int counter;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		//drive Init
		frontLeft = new CANTalon(0);
		frontRight = new CANTalon(1);
		backLeft = new VictorSP(0);
		backRight = new CANTalon(2);
		drive = new RobotDrive(frontLeft, backLeft, frontRight, backRight);
		//funnel Init
		funnelLeft = new Talon(0);
		funnelRight = new Talon(1);
		//lifter Init
		lift = new Jaguar(2);
		leftClamp = new DoubleSolenoid(0, 1);
		rightClamp = new DoubleSolenoid(2, 3);
		upperLimit = new DigitalInput(0);
		lowerLimit = new DigitalInput(1);
		//OI Init
		driveStick = new Joystick(0);
		shmoStick = new Joystick(1);
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
//			camera.startCapture();
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		drive.mecanumDrive_Cartesian(calc(driveStick.getX()),
				calc(driveStick.getY()), calc(driveStick.getTwist()), 0); //Drive Mechanums

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

	public double calc(double value) {
		return pow(value, 3);
	}
}
