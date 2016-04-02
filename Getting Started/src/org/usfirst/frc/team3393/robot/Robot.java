package org.usfirst.frc.team3393.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
// ******* After initializing the SendableChooser, configs will show up in the
// SmartDashboard (with the driver station) ********
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	Command autonomousCommand;
	SendableChooser autoChooser;

	RobotDrive myRobot;
	Joystick left, right, control;
	Solenoid downLift, upLift;

	Victor shooter;
	Victor winch1;
	Victor winch2;
	Solenoid climber1;
	Solenoid climber2;
	Timer _aTimer;
	Accelerometer _accelerometer;

	AnalogGyro _gyro;
	double Kp = 0.03;
	double _velocity;
	double _displacement;
	Timer _eTimer;
	boolean SPATULORX_DOWN = false;
	boolean SPATULORX_UP = true;
	boolean ARM_UP = true; 
	boolean ARM_DOWN = false; 
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		CameraServer server = CameraServer.getInstance();
		server.setQuality(50); //50
		server.startAutomaticCapture("cam0");
		
		autoChooser = new SendableChooser();
		autoChooser.addDefault("Autonomous 1: Drive REVERSE over low bar, turn, drive forward, shoot", "autonomous1");
		autoChooser.addObject("Autonomous 2: Drive REVERSE over low bar, stop", "autonomous2");
		autoChooser.addObject("Autonomous 3: Driver FORWARD over rough terrain, stop", "autonomous3");
		SmartDashboard.putData("Autonomous Modes", autoChooser);
		
		// Robot drive connected to motor ports 0-3
		myRobot = new RobotDrive(0, 1, 2, 3); 
		right = new Joystick(1);
		left = new Joystick(3);
		
		// Spatulorx connected to pneumatics 0, 1
		downLift = new Solenoid(0); 
		upLift = new Solenoid(1);
		
		// Low ball shooter connected to motor port 6
		shooter = new Victor(6);
		
		// Climbing pistons connected to pneumatics 4, 5
		climber1 = new Solenoid(4);
		climber2 = new Solenoid(5);
		
		// Climbing motors connected to motor ports 4, 5
		winch1 = new Victor(4);
		winch2 = new Victor(5);

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
		/*
		 * autonomousCommand = (Command)autoChooser.getSelected();
		 * autonomousCommand.start();
		 */
		this._aTimer.stop();
		this._aTimer.reset();
		this._aTimer.start();

		this._gyro.reset();

		this.resetDistance();
		this._eTimer.stop();
		this._eTimer.reset();
		this._eTimer.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		this.updateDistance();
		String autoOption = autoChooser.getSelected().toString();

		if (autoOption.equals("autonomous1")) {
			autonomous1();
		} else if (autoOption.equals("autonomous2")) {
			autonomous2();
		} else if (autoOption.equals("autonomous3")) {
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

		// Spatulorx (LEFT joystick)
		if (left.getRawButton(4)) { // fork pneumatics
			setSpatulorx(SPATULORX_DOWN);
		} else if (left.getRawButton(5)) {
			setSpatulorx(SPATULORX_UP);
		}
		
		// Ball intake (RIGHT joystick)
		if (right.getRawButton(2)) {	
			shooter.set(1.0);
		} else if (right.getRawButton(3)) {
			shooter.set(-1.0);
		} else {
			shooter.set(0.0);
		}
			
		// Climbing bimbas (LEFT joystick)
		if (left.getRawButton(6)) {
			setClimbing(ARM_UP);
		} else if (left.getRawButton(7)) {
			setClimbing(ARM_DOWN);
		}
		  
		// Climbing winch (LEFT joystick)
		if (left.getRawButton(8)) {
			winch1.set(1.0);
			winch2.set(1.0);
		} else if (left.getRawButton(9)) {	
			winch1.set(-1.0);
			winch2.set(-1.0);
		} else  {
			winch1.set(0.0);
			winch2.set(0.0);	
		} 

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}

	enum Auto1State {
		DRIVE_REVERSE, CASTLE_TURN, DRIVE_TO_CASTLE, RELEASE_BALL, FINISH
	}

	Auto1State auto1State = Auto1State.DRIVE_REVERSE;

	private void autonomous1() {
		if (auto1State == Auto1State.DRIVE_REVERSE) {
			setClimbing(ARM_DOWN);
			setSpatulorx(SPATULORX_DOWN);
			myRobot.tankDrive(0.7, 0.7);
			// Stop after 8 feet in meters
			if (this.getDistance() >= 4.2) { // 3.7 2.4384, 2,, 3.6576, 4.877=16
												// feet
				// Reset distance because we're turning in the next state
				// this.resetDistance();
				this._aTimer.reset();

				auto1State = Auto1State.CASTLE_TURN;
			}
		} else if (auto1State == Auto1State.CASTLE_TURN){
			setClimbing(ARM_DOWN);
			myRobot.tankDrive(0.6, -0.6); // (0.6, -0.6) 0.1, 0.85
			System.out.println("castle" + this._aTimer.get());
			if (this._aTimer.get() >= 1.6) {
				auto1State = Auto1State.DRIVE_TO_CASTLE;
				this.resetDistance();
			}

		} else if (auto1State == Auto1State.DRIVE_TO_CASTLE) {
			myRobot.tankDrive(-0.6, -0.6);
			System.out.println("drivetocastle" + this.getDistance());
			if (this.getDistance() >= 3.3) {
				this.resetDistance();
				this._aTimer.reset();
				auto1State = Auto1State.RELEASE_BALL;

			}
		} else if (auto1State == Auto1State.RELEASE_BALL) {
			myRobot.tankDrive(0.0, 0.0);
			shooter.set(-1.0);
			if (this._aTimer.get() >= 2.0) {
				shooter.set(0.0);
				auto1State = Auto1State.FINISH;
			}
		} else if (auto1State == Auto1State.FINISH) {
			System.out.print("IT'S OVER!");
		}

	}

	enum Auto2State {
		DRIVE_REVERSE, FINISH
	}

	Auto2State auto2State = Auto2State.DRIVE_REVERSE;

	private void autonomous2() {
		if (auto2State == Auto2State.DRIVE_REVERSE) {
			setClimbing(ARM_DOWN);
			setSpatulorx(SPATULORX_DOWN);
			myRobot.tankDrive(0.7, 0.7);
			if (this.getDistance() >= 4.2) {

				auto2State = Auto2State.FINISH;
			}
		} else if (auto2State == Auto2State.FINISH) {
			myRobot.tankDrive(0, 0);
		}
	}

	enum Auto3State {
		DRIVE_FORWARD, FINISH
	}

	Auto3State auto3State = Auto3State.DRIVE_FORWARD;

	private void autonomous3() {
		if (auto3State == Auto3State.DRIVE_FORWARD) {
			setClimbing(ARM_DOWN);
			myRobot.tankDrive(-0.9, -0.9);
			if (this.getDistance() >= 5.1) {

				auto3State = Auto3State.FINISH;
			}
		} else if (auto3State == Auto3State.FINISH) {
			myRobot.tankDrive(0, 0);
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
	
	private void setSpatulorx(boolean liftSpatulorx) {
		if (liftSpatulorx == true){
			downLift.set(false);// Spatulorx
			upLift.set(true);
		}
		else {
			downLift.set(true);// Spatulorx down 
			upLift.set(false);	
		}		

	}
	
	private void setClimbing(boolean winchUp) {
		if (winchUp == true) {
			climber1.set(false);
			climber2.set(true);
		} else {
			climber1.set(true);
			climber2.set(false);
		}
	}
}
