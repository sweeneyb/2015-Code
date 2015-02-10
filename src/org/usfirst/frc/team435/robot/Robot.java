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
	Jaguar funnelLeft, funnelRight;
	// --Lift Components--
	Talon lift;
	DigitalInput upperLimit, lowerLimit, stepHeight;
	DoubleSolenoid leftClamp, rightClamp;
	// -- OI --
	Joystick driveStick, shmoStick;
	// --Compressor--
+	Compressor compressor;
	
	// Variables
	int counter; // for counting Automode cycles
	public boolean lastCompressorButtonState = false; // Compressor Button State Holding
+	public boolean compressorOn = true; // Compressor State
	boolean alreadyClicked; // for clamper state holding
	
	// Constants
	public static final double DEADBAND = .1;
	
	// Standard Methods
	public double calc(double value) { // DEADBAND function
+		if (Math.abs(value) < DEADBAND) {
+			return 0;
+		} else {
+			return (value - (Math.abs(value) / value * DEADBAND))
+					/ (1 - DEADBAND);
+		}
 	}
 	
	public void clamp(){
		leftClamp.set(Value.kForward);
		rightClamp.set(Value.kForward);	
	}
	
	public void unclamp(){
		leftClamp.set(Value.kReverse);
		rightClamp.set(Value.kReverse);	
	}

	public void clampClicking(){ // changes the state of the clamp on pressing the a button (a press and release)
		if(shmoStick.getRawButton(1) && !alreadyClicked){
			
			if(leftClamp.get().equals(DoubleSolenoid.Value.kReverse)){
				clamp();
			}
			else{
				unclamp();
			}
			alreadyClicked = true;
		}
		// reset the sate of the button
		else if(!shmoStick.getRawButton(1)){
			alreadyClicked = false;
		}
	}
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		// Compressor Init
		// Compressor Init
+		compressor = new Compressor();
+		compressor.start();
		//drive Init
		frontLeft = new CANTalon(0);
		frontRight = new CANTalon(1);
		backLeft = new VictorSP(0);
		backRight = new CANTalon(2);
		drive = new RobotDrive(frontLeft, backLeft, frontRight, backRight);
		//funnel Init
		funnelLeft = new Jaguar(1);
		funnelRight = new Jaguar(2);
		//lifter Init
		lift = new Talon(3);
		leftClamp = new DoubleSolenoid(0, 1);
		rightClamp = new DoubleSolenoid(2, 3);
		upperLimit = new DigitalInput(0);
		lowerLimit = new DigitalInput(1);
		stepHeight = new DigitalInput(2);
		//OI Init
		driveStick = new Joystick(0);
		shmoStick = new Joystick(1);
		// camera = new USBCamera();

		// camera.openCamera();
		
		// reset and equalize the clamp solenoids
		leftClamp.set(Value.kReverse);
		rightClamp.set(Value.kReverse);

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
		double xdrive = driveStick.getRawAxis(0);
		double ydrive = driveStick.getRawAxis(1);
		double twistdrive = driveStick.getRawAxis(2);
		double funnelLeftOp = shmoStick.getRawAxis(1);
		double funnelRightOp = shmoStick.getRawAxis(5);
		
		
		// drive Operation
		if(driveStick.getTrigger()){
			//half speed
			drive.mecanumDrive_Cartesian(
					calc(xdrive * 0.5), 
					calc(ydrive * 0.5), 
					calc(twistdrive * 0.5), 
					0);			
		}
		else{
			drive.mecanumDrive_Cartesian(
					xdrive, 
					ydrive, 
					twistdrive, 
					0);			
		}
		
		// Funnel Operation
		funnelLeft.set(funnelLeftOp); // left motor left joystick up/down
		funnelRight.set(funnelRightOp);// right motor right joystick up/down
		
		//Lifter Clamping
		clampClicking();
		
		// Lifter Lifting
		double up = shmoStick.getRawAxis(3);
		double down = shmoStick.getRawAxis(4);
		double threadedRodMult = 1; //multiplier so we don't go up too fast
		if(!upperLimit.get() && down == 0){
			lift.set(up*threadedRodMult);
		}
		if(!lowerLimit.get() && up == 0){
			lift.set(down*threadedRodMult);
		}
		
		// lift to step
		if(shmoStick.getRawButton(2) && !stepHeight.get()){
			lift.set(.3*threadedRodMult);
		}
		
		// Compressor Toggle
		if (shmoStick.getRawButton(7) && !lastCompressorButtonState ) {
+			if (compressorOn) {
+				compressor.stop();
+			} else {
+				compressor.start();
+			}
+			lastCompressorButtonState = true;
+		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}
}
