package org.usfirst.frc.team435.robot;

import static java.lang.Math.pow;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.ControlMode;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;

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
		PICK_UP_TOTES_VISION,
		PICK_UP_ALL
	};

	// USBCamera camera;
	RobotDrive drive;
	Jaguar frontLeft, frontRight;
	Talon backLeft, backRight, lift;
	CANTalon funnelLeft, funnelRight;
	DoubleSolenoid leftClamp, rightClamp;
	Joystick driveStick, shmoStick;
	DigitalInput upperLimit, lowerLimit;
	int counter;
	int stage;
	boolean taskdone;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		frontLeft = new Jaguar(0);
		frontRight = new Jaguar(1);
		backLeft = new Talon(2);
		backRight = new Talon(3);
		funnelLeft = new CANTalon(0);
		funnelRight = new CANTalon(1);
		funnelLeft.changeControlMode(ControlMode.PercentVbus);
		funnelRight.changeControlMode(ControlMode.PercentVbus);
		leftClamp = new DoubleSolenoid(0, 1);
		rightClamp = new DoubleSolenoid(2, 3);
		driveStick = new Joystick(0);
		shmoStick = new Joystick(1);
		upperLimit = new DigitalInput(0);
		lowerLimit = new DigitalInput(1);
		drive = new RobotDrive(frontLeft, backLeft, frontRight, backRight);

	}

	@Override
	public void autonomousInit() {
		counter = 0;
		stage = 0;
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
				counter++;
			}
			drive.mecanumDrive_Cartesian(0, 0, 0, 0);
			break;
		case PICK_UP_TOTE:
			if(counter < 25){
				drive.mecanumDrive_Cartesian(0, .3, 0, 0);
				funnelLeft.set
			}
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
		case PICK_UP_ALL:
			if (counter == 0) {
				// turn funnel on
			}
			if (stage == 0 && counter <= 1 * 50) {
				drive.mecanumDrive_Cartesian(0, -.5, 0, 0);
				counter++;
			} else {
				if (stage == 0 && counter == 1 * 50 + 1) {
					counter = 0;
					stage++;
				}
			}
			if (stage == 1 && taskdone == false) {
				// lift stuff here
				// taskdone = true;
			} else {
				if (stage == 1 && taskdone == true) {
					stage++;
					counter = 0;
				}
			}
			if (stage == 2 && counter <= 4.5 * 50) {
				drive.mecanumDrive_Cartesian(-.5, 0, 0, 0);
				counter++;
			} else {
				if (stage == 2 && counter == 2 * 50 + 1) {
					stage++;
					counter = 0;

				}
				if (stage == 3 && taskdone == false) {
					// drop stuff here
					// taskdone = true;

				} else {
					if (stage == 7 && taskdone == true) {
						taskdone = false;
						stage++;
					}
				}
				if (stage == 4 && counter <= 1.768 * 50) {
					drive.mecanumDrive_Cartesian(0, .5, 0, 0);
				}
				break;
			}
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
