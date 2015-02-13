package org.usfirst.frc.team435.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
	static final int LIFTER_UP_AXIS = 3;
	static final int LIFTER_DOWN_AXIS = 2;
	static final int FUNNEL_LEFT_AXIS = 1;
	static final int FUNNEL_RIGHT_AXIS = 5;

	// USBCamera camera;
	// --Drive Motors--
	RobotDrive drive;
	SpeedController backLeft, frontLeft, frontRight, backRight;
	// --Funnel Components--
	Jaguar funnelLeft, funnelRight;
	// --Lift Components--
	Talon lift;
	DigitalInput upperLimit, lowerLimit, stepHeight, toteHeight;
	DoubleSolenoid leftClamp, rightClamp;
	// -- OI --
	Joystick driveStick, shmoStick;

	int stage;
	boolean taskdone;
	// --Compressor--
	Compressor compressor;
	// --Automode Chooser--
	SendableChooser autoChooser;
	// --Joystick Buttons --
	JoystickButton startCompressor, clampButton, stepLift;
	// Variables
	int counter; // for counting Automode cycles
	public boolean lastCompressorButtonState = false; // Compressor Button State
														// Holding
	public boolean compressorOn = true; // Compressor State
	boolean alreadyClicked = false; // for clamper state holding
	public AutoChoice autoMode = AutoChoice.DRIVE_FORWARD;

	// Constants
	public static final double DEADBAND = .1;
	public static final double AUTO_LIFT_SPEED = .5;
	public static final double AUTO_FUNNEL_SPEED =.5;

	// Standard Methods
	public double calc(double value) { // DEADBAND function
		if (Math.abs(value) < DEADBAND) {
			return 0;
		} else {
			return (value - (Math.abs(value) / value * DEADBAND))
					/ (1 - DEADBAND);
		}
	}

	public void clamp() {
		leftClamp.set(Value.kForward);
		rightClamp.set(Value.kForward);
	}

	public void unclamp() {
		leftClamp.set(Value.kReverse);
		rightClamp.set(Value.kReverse);
	}

	@Override
	public void autonomousInit() {
		try {
			autoMode = (AutoChoice) autoChooser.getSelected();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void clampClicking() { // changes the state of the clamp on pressing
									// the a button (a press and release)
		if (clampButton.get() && !alreadyClicked) {

			if (leftClamp.get().equals(DoubleSolenoid.Value.kReverse)) {
				clamp();
			} else {
				unclamp();
			}
			alreadyClicked = true;
		}
		// reset the sate of the button
		else if (!clampButton.get()) {
			alreadyClicked = false;
		}
	}

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		// Compressor Init
		compressor = new Compressor();
		compressor.start();
		// drive Init
		frontLeft = new CANTalon(0);
		frontRight = new CANTalon(1);
		backLeft = new CANTalon(3);
		backRight = new CANTalon(2);
		drive = new RobotDrive(frontLeft, backLeft, frontRight, backRight);
		// funnel Init
		funnelLeft = new Jaguar(1);
		funnelRight = new Jaguar(2);
		// lifter Init
		lift = new Talon(3);
		leftClamp = new DoubleSolenoid(0, 1);
		rightClamp = new DoubleSolenoid(2, 3);
		upperLimit = new DigitalInput(0);
		lowerLimit = new DigitalInput(1);
		stepHeight = new DigitalInput(2);
		// OI Init
		driveStick = new Joystick(0);
		shmoStick = new Joystick(1);
		// Compressor Init
		compressor = new Compressor();
		compressor.start();
		// camera = new USBCamera();

		// camera.openCamera();

		// reset and equalize the clamp solenoids
		leftClamp.set(Value.kReverse);
		rightClamp.set(Value.kReverse);
		autoChooser = new SendableChooser();
		// Auto Chooser
		autoChooser.addDefault("Drive forward", AutoChoice.DRIVE_FORWARD);
		autoChooser.addObject("Pick up a single tote", AutoChoice.PICK_UP_TOTE);
		autoChooser.addObject("Pick up a single tote and a recycle bin",
				AutoChoice.PICK_UP_TOTE_TRASH);
		autoChooser.addObject("Pick up all of the totes",
				AutoChoice.PICK_UP_TOTES);

		SmartDashboard.putData("Autonomous Choices", autoChooser);

		// Joystick Buttons
		clampButton = new JoystickButton(shmoStick, 1);
		startCompressor = new JoystickButton(shmoStick, 8);
		stepLift = new JoystickButton(shmoStick, 2);
		// camera = new USBCamera();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		switch (autoMode) {
		case DRIVE_FORWARD:
			if (counter < 25) {
				drive.mecanumDrive_Cartesian(0, -.5, 0, 0);
			} else {
				drive.mecanumDrive_Cartesian(0, 0, 0, 0);
			}
			break;

		case PICK_UP_TOTE:
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
			break;

		case PICK_UP_TOTE_TRASH:
			// Positioning: right in front of the recycle bin in line to get the tote
			// Drive Forward and get recycle
			if(counter < 25){
				drive.mecanumDrive_Cartesian(0, -0.5, 0, 0);
			} else if(counter < 50){
				drive.mecanumDrive_Cartesian(0,-0.5,0,0);
				funnelLeft.set(AUTO_FUNNEL_SPEED);
				funnelRight.set(AUTO_FUNNEL_SPEED);
			} 
			// Clamp and lift the bin
			else if(counter < 100){
				clamp();
			} else if(counter < 110 && toteHeight.get()){
				lift.set(AUTO_LIFT_SPEED);				
			} 
			// Drive forwards and funnel the tote
			else if(counter < 150){
				drive.mecanumDrive_Cartesian(0, -0.5, 0, 0);
			} else if(counter < 175){
				drive.mecanumDrive_Cartesian(0, -0.5, 0, 0);
				funnelLeft.set(AUTO_FUNNEL_SPEED);
				funnelRight.set(AUTO_FUNNEL_SPEED);
			} 
			// Let go of the bin and bring the lifter down
			else if(counter < 240){
				unclamp();
			} else if(counter < 250 && !lowerLimit.get()){
				lift.set(-AUTO_LIFT_SPEED);
				if(lowerLimit.get()){
					counter = 250;
				}
			}
			// Clamp and lift the tote just high enough to drive with
			else if(counter < 260){
				clamp();
			} else if(counter < 270 && toteHeight.get()){
				lift.set(AUTO_LIFT_SPEED);
			} 
			//Drive to autozone
			else if(counter < 300){
				drive.mecanumDrive_Cartesian(0, 0, 1, 0);
			} else if (counter < 400){
				drive.mecanumDrive_Cartesian(0, -1, 0, 0);
			}
			
			// setting everything to 0 before the end of the loop
			lift.set(0);
			funnelLeft.set(0);
			funnelRight.set(0);
			drive.mecanumDrive_Cartesian(0, 0, 0, 0);
			break;

		case PICK_UP_TOTES:
			break;

		case PICK_UP_RECYCLE_MIDDLE:
			break;

		case PICK_UP_TOTES_VISION:
			// camera.startCapture();
			break;
		case PICK_UP_ALL:
			if(counter < 50){
				drive.mecanumDrive_Cartesian(0, .1, 0, 0);
				funnelLeft.set(.5);
				funnelRight.set(.5);
			} else if(counter < 100){
				leftClamp.set(Value.kForward);
				rightClamp.set(Value.kForward);
				drive.mecanumDrive_Cartesian(0, 0, 0, 0);
				funnelLeft.set(0);
				funnelRight.set(0);
				lift.set(.5);
			} else if(counter < 150){
				lift.set(0);
				drive.mecanumDrive_Cartesian(0, .1, 0, 0);
				funnelLeft.set(.5);
				funnelRight.set(.5);
			} else if(counter < 200){
				
			}
			break;
		}
		counter++;
	}

	private void lift(double speed) {
		if((speed < 0 && lowerLimit.get()) || (speed > 0 && upperLimit.get())){
			return;
		}
		lift.set(speed);
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		double xdrive = driveStick.getX();
		double ydrive = driveStick.getY();
		double twistdrive = driveStick.getZ();
		double funnelLeftOp = shmoStick.getRawAxis(FUNNEL_LEFT_AXIS);
		double funnelRightOp = shmoStick.getRawAxis(FUNNEL_RIGHT_AXIS);

		// drive Operation
		if (driveStick.getTrigger()) {
			// Half speed
			// @formatter:off;
			drive.mecanumDrive_Cartesian(
					calc(xdrive / 2),
					calc(ydrive / 2),
					calc(twistdrive / 2),
					0);
		} else {
			drive.mecanumDrive_Cartesian(
					calc(xdrive),
					calc(ydrive), 
					calc(twistdrive), 
					0);
			// @formatter:on;
		}

		// Funnel Operation
		funnelLeft.set(funnelLeftOp); // left motor left joystick up/down
		funnelRight.set(funnelRightOp);// right motor right joystick up/down

		// Lifter Clamping
		clampClicking();

		// Lifter Lifting
		double up = shmoStick.getRawAxis(LIFTER_UP_AXIS);
		double down = shmoStick.getRawAxis(LIFTER_DOWN_AXIS);
		double threadedRodMult = 1; // multiplier so we don't go up too fast
		if (!upperLimit.get() && down == 0) {
			lift.set(up * threadedRodMult);
		} else if (!lowerLimit.get() && up == 0) {
			lift.set(down * threadedRodMult * -1.0);
		} else {
			lift.set(0);
		}

		// lift to step
		if (stepLift.get() && !stepHeight.get()) {
			lift.set(.3 * threadedRodMult);
		}

		// Compressor Toggle
		if (startCompressor.get() && !lastCompressorButtonState) {
			if (compressorOn) {
				compressor.stop();
			} else {
				compressor.start();
			}
			lastCompressorButtonState = true;
		}
		// updateDashboard();
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

	public void updateDashboard() {
		SmartDashboard.putBoolean("Upper Limit", upperLimit.get());
		SmartDashboard.putBoolean("Lower Limit", lowerLimit.get());
		SmartDashboard.putBoolean("Step Height", stepHeight.get());
		SmartDashboard.putBoolean("Compressor State", compressor.enabled());
		SmartDashboard.putNumber("Drive X", driveStick.getX());
		SmartDashboard.putNumber("Drive Y", driveStick.getY());
		SmartDashboard.putNumber("Drive Z", driveStick.getZ());
		SmartDashboard.putNumber("Front Left Drive", frontLeft.get());
		SmartDashboard.putNumber("Front Right Drive", frontRight.get());
		SmartDashboard.putNumber("Back Left", backLeft.get());
		SmartDashboard.putNumber("Back Right", backRight.get());
		SmartDashboard.putNumber("Funnel Left", funnelLeft.get());
		SmartDashboard.putNumber("Funnel Right", funnelRight.get());
		SmartDashboard.putNumber("Lift", lift.get());
		SmartDashboard.putNumber("Funnel Left Axis",
				shmoStick.getRawAxis(FUNNEL_LEFT_AXIS));
		SmartDashboard.putNumber("Funnel Right Axis",
				shmoStick.getRawAxis(FUNNEL_RIGHT_AXIS));
		SmartDashboard.putNumber("Lift Up",
				shmoStick.getRawAxis(LIFTER_UP_AXIS));
		SmartDashboard.putNumber("Lift Down",
				shmoStick.getRawAxis(LIFTER_DOWN_AXIS));
		SmartDashboard.putBoolean("Clamp Button", clampButton.get());
		SmartDashboard.putBoolean("Compressor Button", startCompressor.get());
		SmartDashboard.putBoolean("Lift to step button", stepLift.get());
	}
}
