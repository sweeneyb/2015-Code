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
import edu.wpi.first.wpilibj.Timer;
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
		PICK_UP_RECYCLE_MIDDLE,
		PICK_UP_TOTES_VISION,
		PICK_UP_ALL
	};

	enum Tote_State {
		EMPTY ("Empty"),
		IN_FUNNEL ("In Funnel"),
		IN_BAY ("In Bay"),
		CLAMPED ("Clamped"),
		PRECLAMPED ("Pre-Clamped"),
		LIFTED ("Lifted");
		
		private final String name;
		
		private Tote_State(String s){
			name = s;
		}
		
		public String toString(){
			return name;
		}
	};
	
	Timer clampTimer = new Timer();
	// USBCamera camera;
	// --Drive Motors--
	RobotDrive drive;
	SpeedController backLeft, frontLeft, frontRight, backRight;
	// --Funnel Components--
	Jaguar funnelLeft, funnelRight;
	// --Lift Components--
	Talon lift;
	DigitalInput upperLimit, lowerLimit, stepHeight, toteHeight, inFunnel, inBay;
	DoubleSolenoid leftClamp, rightClamp;
	// -- OI --
	Joystick driveStick, shmoStick;

	// --Compressor--
	Compressor compressor;
	// --Automode Chooser--
	SendableChooser autoChooser;
	// --Joystick Buttons --
	JoystickButton startCompressor, clampButton, stepLift;
	// Variables
	Tote_State state; // for finite state machine
	int counter; // for counting Automode cycles
	int totesPickedUp; // for counting totes in autonomous mode
	public boolean lastCompressorButtonState = false; // Compressor Button State
														// Holding
	public boolean compressorOn = true; // Compressor State
	boolean alreadyClicked = false; // for clamper state holding
	public AutoChoice autoMode = AutoChoice.DRIVE_FORWARD;

	// Constants
	public static final double DEADBAND = .1;
	public static final double AUTO_LIFT_SPEED = .5;
	public static final double AUTO_FUNNEL_SPEED = .5;
	static final int LIFTER_UP_AXIS = 3;
	static final int LIFTER_DOWN_AXIS = 2;
	static final int FUNNEL_LEFT_AXIS = 1;
	static final int FUNNEL_RIGHT_AXIS = 5;
	static final int THREADED_ROD_MULT = 1; // multiplier so we don't go up too
											// fast

	// Standard Methods
	public double calc(double value) { // DEADBAND function
		if (Math.abs(value) < DEADBAND) {
			return 0;
		} else {
			return (value - (Math.abs(value) / value * DEADBAND))
					/ (1 - DEADBAND);
		}
	}

	/**
	 * Engages clamp
	 */
	public void clamp() {
		leftClamp.set(Value.kForward);
		rightClamp.set(Value.kForward);
	}

	/**
	 * Disengages clamp
	 */
	public void unclamp() {
		leftClamp.set(Value.kReverse);
		rightClamp.set(Value.kReverse);
	}

	public void safeMotorSet(SpeedController speedController, double speed,
			DigitalInput hardBottom, DigitalInput hardTop, DigitalInput soft,
			boolean softIsTop) {
		boolean tops = false;
		boolean bottoms = false;
		if(softIsTop){
			tops = (hardTop.get() || soft.get());
			bottoms = hardBottom.get();
		} else{
			tops = hardTop.get();
			bottoms = (hardBottom.get() || soft.get());
		}
		if(speed > 0 && !tops){
			speedController.set(speed);
		} else if(speed <0 && !bottoms){
			speedController.set(speed);
		} else{
			speedController.set(0);
		}
	}

	@Override
	public void autonomousInit() {
		try {
			autoMode = (AutoChoice) autoChooser.getSelected();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		counter = 0;
		totesPickedUp = 1;
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
		// Finite State Init
		state = Tote_State.EMPTY;
		inBay = new DigitalInput(3);
		inFunnel = new DigitalInput(4);
		toteHeight = new DigitalInput(5);
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
				AutoChoice.PICK_UP_ALL);

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
			if (counter < 25) {
				drive.mecanumDrive_Cartesian(0, .3, 0, 0);
				funnelLeft.set(AUTO_FUNNEL_SPEED);
				funnelRight.set(AUTO_FUNNEL_SPEED);
			} else if (counter < 40) {
				clamp();
			} else if (counter < 50) {
				drive.mecanumDrive_Cartesian(0, 0, 0, 0);
				funnelLeft.set(0);
				funnelRight.set(0);
				lift.set(AUTO_LIFT_SPEED);
				;
				if (upperLimit.get()) {
					counter = 39;
				}
			} else if (counter < 60) {
				lift.set(0);
				drive.mecanumDrive_Cartesian(-.5, 0, 0, 0);
			} else if (counter < 70) {
				lift.set(-AUTO_LIFT_SPEED);
				if (lowerLimit.get()) {
					counter = 64;
				}
			} else if (counter < 110) {
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
			// Positioning: right in front of the recycle bin in line to get the
			// tote
			// Drive Forward and get recycle
			if (counter < 25) {
				drive.mecanumDrive_Cartesian(0, -0.5, 0, 0);
			} else if (counter < 50) {
				drive.mecanumDrive_Cartesian(0, -0.5, 0, 0);
				funnelLeft.set(AUTO_FUNNEL_SPEED);
				funnelRight.set(AUTO_FUNNEL_SPEED);
			}
			// Clamp and lift the bin
			else if (counter < 100) {
				clamp();
			} else if (counter < 110 && toteHeight.get()) {
				lift.set(AUTO_LIFT_SPEED);
			}
			// Drive forwards and funnel the tote
			else if (counter < 150) {
				drive.mecanumDrive_Cartesian(0, -0.5, 0, 0);
			} else if (counter < 175) {
				drive.mecanumDrive_Cartesian(0, -0.5, 0, 0);
				funnelLeft.set(AUTO_FUNNEL_SPEED);
				funnelRight.set(AUTO_FUNNEL_SPEED);
			}
			// Let go of the bin and bring the lifter down
			else if (counter < 240) {
				unclamp();
			} else if (counter < 250 && !lowerLimit.get()) {
				lift.set(-AUTO_LIFT_SPEED);
				if (lowerLimit.get()) {
					counter = 250;
				}
			}
			// Clamp and lift the tote just high enough to drive with
			else if (counter < 260) {
				clamp();
			} else if (counter < 270 && toteHeight.get()) {
				lift.set(AUTO_LIFT_SPEED);
			}
			// Drive to autozone
			else if (counter < 300) {
				drive.mecanumDrive_Cartesian(0, 0, 1, 0);
			} else if (counter < 400) {
				drive.mecanumDrive_Cartesian(0, -1, 0, 0);
			}

			// setting everything to 0 before the end of the loop
			lift.set(0);
			funnelLeft.set(0);
			funnelRight.set(0);
			drive.mecanumDrive_Cartesian(0, 0, 0, 0);
			break;

		case PICK_UP_RECYCLE_MIDDLE:
			break;

		case PICK_UP_TOTES_VISION:
			// camera.startCapture();
			break;
		case PICK_UP_ALL:
			if (counter < 50) {
				// Funnel the tote
				drive.mecanumDrive_Cartesian(0, -.1, 0, 0);
				funnelLeft.set(AUTO_FUNNEL_SPEED);
				funnelRight.set(AUTO_FUNNEL_SPEED);
			} else if (counter < 60) {
				clamp();
			} else if (counter < 110) {
				// Lift
				drive.mecanumDrive_Cartesian(0, 0, 0, 0);
				funnelLeft.set(0);
				funnelRight.set(0);
				lift.set(AUTO_LIFT_SPEED);
				if (upperLimit.get()) {
					counter = 99;
				}
			} else if (counter < 125) {
				// Drive around Recycle bin
				drive.mecanumDrive_Cartesian(.5, 0, 0, 0);
				lift.set(0);
			} else if (counter < 150) {
				drive.mecanumDrive_Cartesian(0, -.5, 0, 0);
			} else if (counter < 175) {
				drive.mecanumDrive_Cartesian(-.5, 0, 0, 0);
			} else if (counter < 225) {
				// Funnel in another tote
				drive.mecanumDrive_Cartesian(0, -.5, 0, 0);
				funnelLeft.set(AUTO_FUNNEL_SPEED);
				funnelRight.set(AUTO_FUNNEL_SPEED);
			} else if (counter < 275) {
				// Make Pick up other tote
				drive.mecanumDrive_Cartesian(0, 0, 0, 0);
				funnelLeft.set(0);
				funnelRight.set(0);
				lift.set(-AUTO_LIFT_SPEED);
				if (lowerLimit.get()) {
					unclamp();
					totesPickedUp++;
					if (totesPickedUp >= 3) {
						counter = 59;
					}
				}
			} else if (counter < 315) {
				drive.mecanumDrive_Cartesian(.5, 0, 0, 0);
			} else {
				drive.stopMotor();
			}
			break;
		}
		counter++;
	}

	private void lift(double speed) {
		if ((speed < 0 && lowerLimit.get()) || (speed > 0 && upperLimit.get())) {
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
		boolean finiteEnabled = false;

		// drive Operation
		if (driveStick.getTrigger()) {
			// Half speed
			// @formatter:off;
			drive.mecanumDrive_Cartesian(calc(xdrive / 2), calc(ydrive / 2),
					calc(twistdrive / 2), 0);
		} else {
			drive.mecanumDrive_Cartesian(calc(xdrive), calc(ydrive),
					calc(twistdrive), 0);
			// @formatter:on;
		}
		//Finite State (mapped to POV up and stopped at a shmo action)
		
		if(shmoStick.getPOV() == 0 && finiteEnabled == false){
			finiteEnabled = true;
		} else if(shmoStick.getPOV() == 0 && finiteEnabled == false){
			finiteEnabled = false;
		}
			

		// Funnel Operation
		funnelLeft.set(funnelLeftOp); // left motor left joystick up/down
		funnelRight.set(funnelRightOp);// right motor right joystick up/down

		// Lifter Clamping
		clampClicking();

		// Lifter Lifting
		double up = shmoStick.getRawAxis(LIFTER_UP_AXIS);
		double down = shmoStick.getRawAxis(LIFTER_DOWN_AXIS);
		if (!upperLimit.get() && down == 0) {
			lift.set(up * THREADED_ROD_MULT);
		} else if (!lowerLimit.get() && up == 0) {
			lift.set(down * -THREADED_ROD_MULT);
		} else {
			lift.set(0);
		}

		// lift to step
		if (stepLift.get() && !stepHeight.get()) {
			lift.set(.3 * THREADED_ROD_MULT);
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

	public void finiteStateMachine() {
				switch(state){
				case EMPTY:
					break;
				case IN_FUNNEL:
					if(!inBay.get()){
						funnelLeft.set(AUTO_FUNNEL_SPEED);
						funnelRight.set(AUTO_FUNNEL_SPEED);
					} else{
						funnelLeft.set(0);
						funnelRight.set(0);
						state = Tote_State.IN_BAY;
					}
					break;
				case IN_BAY:
					if(!lowerLimit.get()){
						safeMotorSet(lift, -1, lowerLimit, upperLimit, toteHeight, true);
						if(toteHeight.get()){
							unclamp();							
						}
					} else{
						lift.set(0);
						clamp();
						clampTimer.start();
						state = Tote_State.PRECLAMPED;
					}					
					break;
				case PRECLAMPED:
					if(clampTimer.get() > .5){
						state = Tote_State.CLAMPED;
						clampTimer.stop();
						clampTimer.reset();
					}
					break;
				case CLAMPED:
					if(toteHeight.get()){
						state = Tote_State.LIFTED;
						lift.set(0);
					}
					safeMotorSet(lift, AUTO_LIFT_SPEED, lowerLimit, upperLimit, toteHeight, true);
					break;
				case LIFTED:
					break;
				}
	}

	public void updateDashboard() {
		SmartDashboard.putBoolean("Tote Cleared Bay Height", toteHeight.get());
		SmartDashboard.putBoolean("Tote in the Funnel", inFunnel.get());
		SmartDashboard.putBoolean("In Bay", inBay.get());
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
		SmartDashboard.putString("Finite State", state.toString());
	}
}
