package org.usfirst.frc.team3393.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

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
	int autoLoopCounter;
	Victor shooter = new Victor(4);

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		toggle = 1;

		frontDownLift = new Solenoid(1);
		frontUpLift = new Solenoid(3);
		myRobot = new RobotDrive(0, 1, 2, 3); // Added Motors 2 and 3 to make
												// all motors run.
		right = new Joystick(1);
		left = new Joystick(3);
		downLift = new Solenoid(6);
		upLift = new Solenoid(7);
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	public void autonomousInit() {
		autoLoopCounter = 0;
		// myRobot.drive(0.5, 0.0); // drive 50% fwd 0% turn
		// Timer.delay(2.0); // wait 2 seconds
		// myRobot.drive(0.0, 0.75); // drive 0% fwd, 75% turn
		// myRobot.drive(0.0, 0.0);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
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
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
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

	private void autonomous1() {
		if (autoLoopCounter < 160) { // going straight and crossing low bar, 150
			myRobot.drive(0.5, 0.0);
			autoLoopCounter++;
		} else if (autoLoopCounter < 220) { // Turning 135 degrees after
											// crossing low bar to shoot,220
			myRobot.drive(0.475, 1.1635);
			autoLoopCounter++;
		} else if (autoLoopCounter < 300) { // 290
			myRobot.drive(-0.5, 0.0); // From 6in to 8in wheel factor =
										// .766798419
			autoLoopCounter++;
		} else {
			myRobot.drive(0.0, 0.0); // If the robot has reached 100 packets,
			shooter.set(1.0); // this line tells the robot to stop

		}
	}

	private void autonomous2() {
		if (autoLoopCounter < 80) { // going straight and crossing low bar for
									// 16 feet
			myRobot.drive(0.5, 0.0);
			autoLoopCounter++;
		} else if (autoLoopCounter < 200) { // fix the odd right turn/angle.
											// Increased autoLoopCounter
			myRobot.drive(0.5, -0.32); // turn to fix odd right turn
			autoLoopCounter++; // THIS TURN IS WRONG!!!!!!
		} else if (autoLoopCounter < 270) { // Turning 135 degrees after
											// crossing low bar to shoot
			myRobot.drive(0.475, 1.2); // made the turn from 0.935 to whatever
										// else
			autoLoopCounter++;
		} else if (autoLoopCounter < 350) {// Robot is going to stop for 40
											// loops to stop and drop ball
			myRobot.drive(0.0, 0.0); //
			autoLoopCounter++;
		} else if (autoLoopCounter < 390) {
			myRobot.drive(0.475, 0.4); // made the turn from .49 to whatever
										// else
			autoLoopCounter++;
		} else if (autoLoopCounter < 500) { // should return to normal starting
											// position
			myRobot.drive(0.5, 0.0);
			autoLoopCounter++;
		} else {
			myRobot.drive(0.0, 0.0); // increased autoLoopCounter by 60
		}
	}

	// move one foot forword
	// pick up ball
	// move 5 feet reverse
	// turn 180
	// drive 6 ft reach barior
	private void autonomous3() {
		// move one foot forward
		if (autoLoopCounter < 75) {
			myRobot.drive(-0.25, 0.0);
			autoLoopCounter++;
		} else if (autoLoopCounter == 75) {
			// pick up ball

			Timer.delay(1);
			autoLoopCounter++;
		} else if (autoLoopCounter < 175) {
			myRobot.drive(0.4, 0.0);
			// move 5 feet reverse
			autoLoopCounter++;
		} else if (autoLoopCounter < 300) {
			// turn 180
			myRobot.drive(0.445, 1.2); // Turn is 1.2 Drive is 0.445

			autoLoopCounter++;
		} else {
			myRobot.drive(0.0, 0.0);
		}
	}
}
