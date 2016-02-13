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
		frontDownLift = new Solenoid(6);
		frontUpLift = new Solenoid(7);
		myRobot = new RobotDrive(0, 1, 2, 3); // Added Motors 2 and 3 to make all motors run
		right = new Joystick(1);
		left = new Joystick(3);
		downLift = new Solenoid(6);
		upLift = new Solenoid(7);
		
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
		// myRobot.drive(0.5, 0.0); // drive 50% fwd 0% turn
		// Timer.delay(2.0); // wait 2 seconds
		// myRobot.drive(0.0, 0.75); // drive 0% fwd, 75% turn
		// myRobot.drive(0.0, 0.0);
		
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
	 */
	public void teleopInit() { 
		myRobot = new RobotDrive(4, 5, 6, 7);	
	}
	
	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		this.updateDistance();
		
		myRobot.tankDrive(left, right);

		// This works!
		// press and release for joystick trigger number 5 motor
		while (right.getRawButton(1)) {
			shooter.set(1.0);
		}
		while (left.getRawButton(1)) {
			shooter.set(-1.0);
		}
		shooter.set(0.0);

		if (left.getRawButton(10)) {
			downLift.set(true);
			upLift.set(false);
		} else if (left.getRawButton(7)) {
			downLift.set(false);
			upLift.set(true);
		}
		if (left.getRawButton(11)) {
			frontDownLift.set(true);
			frontUpLift.set(false);
		} else if (left.getRawButton(6)) {
			frontDownLift.set(false);
			frontUpLift.set(true);
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
		FINISH
	}
	Auto1State auto1State = Auto1State.DRIVE_FORWARD;
	private void autonomous1() {
		if(auto1State == Auto1State.DRIVE_FORWARD) {
			myRobot.tankDrive(0.4, 0.4);
			
			// Stop after 3 meters
			if(this.getDistance() >= 3.0) {
				this._aTimer.stop();
				this._aTimer.reset();
				this._aTimer.start();
				
				// Reset distance because we're turning in the next state
				this.resetDistance();
				
				auto1State = Auto1State.FINISH;
			}
		}else if(auto1State == Auto1State.FINISH) {
			myRobot.tankDrive(0, 0);
		}
//		
//		if (this._aTimer.get() >= 4.0) { // going straight and crossing low bar, 150,160(recent)
//			myRobot.drive(0.4, 0.0);
//			this._aTimer.reset();
//    //		} else if (autoLoopCounter < 220) { // Turning 135 degrees after
//	//										// crossing low bar to shoot,220
//	//	myRobot.drive(0.475, 1.1635);
////			autoLoopCounter++;
////		} else if (autoLoopCounter < 300) { // 290
////			myRobot.drive(-0.5, 0.0); // From 6in to 8in wheel factor =
////										// .766798419
////			autoLoopCounter++;
////		} else {
////			myRobot.drive(0.0, 0.0); // If the robot has reached 100 packets,
////			shooter.set(1.0); // this line tells the robot to stop
//
//		}
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
		DRIVE_FORWARD_3,
		BALL_PICKUP,
		DRIVE_REVERSE,
		TURN_AROUND,
		FINISH
	}
	Auto3State auto3State = Auto3State.DRIVE_FORWARD_3;
	private void autonomous3() {
		if(auto3State == Auto3State.DRIVE_FORWARD_3) {
			myRobot.tankDrive(-0.25, -0.25);
			
			if(this._aTimer.get() >= 1.0) {
				this._aTimer.stop();
				this._aTimer.reset();
				this._aTimer.start();
				
				auto3State = Auto3State.BALL_PICKUP;
			}
		}else if(auto3State == Auto3State.BALL_PICKUP) {
			// Pick up ball
			
			if(this._aTimer.get() >= 1.0) {
				this._aTimer.stop();
				this._aTimer.reset();
				this._aTimer.start();
				
				auto3State = Auto3State.DRIVE_REVERSE;
			}
		}else if(auto3State == Auto3State.DRIVE_REVERSE) {
			myRobot.tankDrive(0.4, 0.4);
			
			if(this._aTimer.get() >= 2.0) {
				this._aTimer.stop();
				this._aTimer.reset();
				this._aTimer.start();
				
				auto3State = Auto3State.TURN_AROUND;
			}
		}else if(auto3State == Auto3State.TURN_AROUND) {
			myRobot.tankDrive(0.5, -0.5);
			
			if(this._gyro.getAngle() >= 180) {
				this._aTimer.stop();
				this._aTimer.reset();
				this._aTimer.start();
				
				this._gyro.reset();
				
				auto3State = Auto3State.FINISH;
			}
		}else if(auto3State == Auto3State.FINISH) {
			myRobot.tankDrive(0, 0);
		}
		
//		// move one foot forward
//		if (this._aTimer.get() <= 4.0) {
//			myRobot.drive(-0.25, 0.0);
//		} else if (this._aTimer.get() <= 6.0) {
//			// pick up ball
//
//			Timer.delay(1);
//		} else if (this._aTimer.get() <= 8.0) {
//			myRobot.drive(0.4, 0.0);
//			// move 5 feet reverse
//		} else if (this._aTimer.get() <= 10.0) {
//			// turn 180
//			myRobot.drive(0.445, 1.2); // Turn is 1.2 Drive is 0.445
//
//		} else {
//			myRobot.drive(0.0, 0.0);
//		}
	}
	
	private void updateDistance() {
		this._velocity = this._accelerometer.getY() * this._eTimer.get();
		this._displacement = this._velocity * this._eTimer.get();
	}
	private double getDistance() {
		return this._displacement;
	}
	private void resetDistance() {
		this._velocity = 0.0;
		this._displacement = 0.0;
	}
}
