package org.usfirst.frc.team3393.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
// ******* After initializing the SendableChooser, configs will show up in the
// SmartDashboard (with the driver station) ********
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	RobotDrive myRobot; 
	Joystick left, right, control;
	Solenoid downLift, upLift;
	Solenoid frontDownLift, frontUpLift; 
	int toggle;
	Victor shooter;
	Victor climber1;
	Victor climber2;
	Timer _aTimer;
	Accelerometer _accelerometer;
	
	AnalogGyro _gyro;
	
	double _velocity;
	double _displacement; 
	Timer _eTimer;
 
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */ 
	public void robotInit() {
		toggle = 1;
		shooter = new Victor(6);
		climber1 = new Victor(4);
		climber2 = new Victor(5);
		frontDownLift = new Solenoid(2); //4 and 5
		frontUpLift = new Solenoid(3);
		myRobot = new RobotDrive(0, 1, 2, 3); // Added Motors 2 and 3 to make all motors run
		right = new Joystick(1);
		left = new Joystick(3);
		downLift = new Solenoid(0); //6 and 7
		upLift = new Solenoid(1);
		
		this._aTimer = new Timer();
		this._accelerometer = new BuiltInAccelerometer();
		
		this._gyro = new AnalogGyro(0);
		
		this._velocity = 0;
		this._displacement = 0;
		this._eTimer = new Timer();
		
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	public void autonomousInit() {
		this._aTimer.start();
		
		this._gyro.reset();
		
		this.resetDistance();
		
		this._eTimer.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		this.updateDistance();
		
		if (toggle == 1) {
			autonomous1();
		} else if (toggle == 2) {
			autonomous2();
		} else if (toggle == 3) {
			autonomous3();
		} else {
			// This is an error condition
			System.out.print("No autonomous mode here.");
		}
	}

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 * 
	 * \
	 */
	public void teleopInit() { 
			 
	}
	
	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		this.updateDistance();
		
		myRobot.tankDrive(left, right);

		// This works!
		// press and release for joystick number 5 motor
		// Using IF - ELSE IF - ELSE allows teleopPeriodic to continue looping
		// (a WHILE loop would be stuck until the button was released)
		if (right.getRawButton(3)) {
			shooter.set(1.0);
		}
		else if (right.getRawButton(2)) {
			shooter.set(-1.0);
		}
		else shooter.set(0.0);

		if (left.getRawButton(4)) { //fork pneumatics 
			downLift.set(true);
			upLift.set(false);
		} else if (left.getRawButton(5)) { 
			downLift.set(false);
			upLift.set(true);
		}
		if (right.getRawButton(4)) { //ball lifting 
			frontDownLift.set(true);
			frontUpLift.set(false);
		} else if (right.getRawButton(5)) {
			frontDownLift.set(false);
			frontUpLift.set(true);
		}
		if (right.getRawButton(7)){
			climber1.set(1.0);
			climber2.set(1.0);
	
		} else if (right.getRawButton(8)){
			climber1.set(-1.0);
			climber2.set(-1.0);	
		} else {
			climber1.set(0);
			climber2.set(0);
		}
		
		// //if(left.getRawbutton(x)){
		// Relay.set(Relay.Value.bForward);
		// }
		// else if (left.getRawButton(x));{
		// Relay.set(Relay.Value.bReverse);
		// }
		// else{
		// Relay.setDefaultSolenoidModule(Relay.Value.koff);
		// }
		// if(left.getRawButton(x));{
		// dsolenoid.set(Relay.Value.bReverse);
		// }
		// else if(left.getRawButton(x)){
		// dsolenoid.set(DoubleSolenoid.Value.kForward);
		// }
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}

	enum Auto1State {
		DRIVE_FORWARD,
		CASTLE_TURN,
		DRIVE_TO_CASTLE,
		RELEASE_BALL,
		FINISH
	}
	Auto1State auto1State = Auto1State.DRIVE_FORWARD;
	private void autonomous1() {
		if(auto1State == Auto1State.DRIVE_FORWARD) { 
			myRobot.tankDrive(0.6, 0.6);
			downLift.set(true);//Splatalorx
			upLift.set(false);
			frontDownLift.set(false);//front
			frontUpLift.set(true);
			System.out.println("Distance" + this.getDistance());
			
			// Stop after 8 feet in meters
			if(this.getDistance() >= 3.7) { //2.4384, 2,, 3.6576
				// Reset distance because we're turning in the next state
				//this.resetDistance();
				this._aTimer.reset();
				auto1State = Auto1State.CASTLE_TURN;
			}
		}else if(auto1State == Auto1State.CASTLE_TURN) {
			myRobot.tankDrive(0.1,0.85);		//0.1,0.4
			System.out.println("castle"+this._aTimer.get());
			if(this._aTimer.get() >=1.5){
				auto1State = Auto1State.DRIVE_TO_CASTLE;
				this.resetDistance();
			}
		
		}else if (auto1State == Auto1State.DRIVE_TO_CASTLE) {
			myRobot.tankDrive(-0.6, -0.6);
			System.out.println("drivetocastle"+this.getDistance());
			if(this.getDistance() >= 1.0){
				this.resetDistance();
				this._aTimer.reset();
				auto1State = Auto1State.RELEASE_BALL;
				
				
			}
		} else if(auto1State == Auto1State.RELEASE_BALL) {
			myRobot.tankDrive(0.0, 0.0);
			shooter.set(1.0);
			if (this._aTimer.get() >= 2.0) {
				shooter.set(0.0);
				auto1State = Auto1State.FINISH;
			}
		} else if (auto1State == Auto1State.FINISH) {
			System.out.print("IT'S OVER!");
		}
		

	}

	
	private void autonomous2() {
		if (this._aTimer.get() <= 4.0) { // going straight and crossing low bar for
									// 16 feet
			myRobot.drive(0.5, 0.0);
		} else if (this._aTimer.get() <= 6.0) { // fix the odd right turn/angle.
											// Increased autoLoopCounter
			myRobot.drive(0.5, -0.32); // turn to fix odd right turn
		} else if (this._aTimer.get() <= 8.0) { // Turning 135 degrees after
											// crossing low bar to shoot
			myRobot.drive(0.475, 1.2); // made the turn from 0.935 to whatever
										// else
		} else if (this._aTimer.get() <= 10.0) {// Robot is going to stop for 40
											// loops to stop and drop ball
			myRobot.drive(0.0, 0.0); //
		} else if (this._aTimer.get() <= 12.0) {
			myRobot.drive(0.475, 0.4); // made the turn from .49 to whatever
										// else
		} else if (this._aTimer.get() <= 14.0) { // should return to normal starting
											// position
			myRobot.drive(0.5, 0.0);
		} else {
			myRobot.drive(0.0, 0.0); // increased autoLoopCounter by 60
		}
	}

	// move one foot forword
	// pick up ball
	// move 5 feet reverse
	// turn 180
	// drive 6 ft reach barior
	enum Auto3State {
		DRIVE_REVERSE,  
		FINISH
	}
	Auto3State auto3State = Auto3State.DRIVE_REVERSE;
	private void autonomous3() {
		if(auto3State == Auto3State.DRIVE_REVERSE) {
			myRobot.tankDrive(-0.25, -0.25);
			if(this.getDistance() >= 2.4384) {
				
				auto3State = Auto3State.FINISH;
			}
		}else if(auto3State == Auto3State.FINISH) {
			myRobot.tankDrive(0,0);
		}
	}
	
	private void updateDistance() {
		this._velocity += this._accelerometer.getY() * this._eTimer.get();
		this._displacement += this._velocity * this._eTimer.get();
		this._eTimer.reset();
	}
	private double getDistance() {
		return Math.abs(this._displacement) * 10;
	}
	private void resetDistance() {
		this._velocity = 0.0;
		this._displacement = 0.0;
	}
}
