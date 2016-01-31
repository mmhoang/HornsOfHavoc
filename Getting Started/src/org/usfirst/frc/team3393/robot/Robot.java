package org.usfirst.frc.team3393.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
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
	Joystick left, right;
	int toggle;
	int autoLoopCounter;
	Victor shooter = new Victor(4);
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	toggle=3;
    	myRobot = new RobotDrive(0,1,2,3); // Added Motors 2 and 3 to make all motors run.
    	right = new Joystick(1);
    	left = new Joystick(3);//hello//
//    	Relay exampleRelay = new Relay(13);
//    	exampleRelay.set(Relay.Value.kOn);
//    	exampleRelay.set(Relay.Value.kForward);
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
    	autoLoopCounter = 0;
//    	myRobot.drive(0.5, 0.0); // drive 50% fwd 0% turn  
//    	Timer.delay(2.0);   // wait 2 seconds  
//    	myRobot.drive(0.0, 0.75); // drive 0% fwd, 75% turn 
//    	myRobot.drive(0.0, 0.0); 
    }

    /**
     * This function is called periodically during autonomous
     */
	public void autonomousPeriodic() {
		if (toggle == 1){
			autonomous1();
		}else if (toggle == 2){
			autonomous2();}
		else{
			autonomous3();
		}
		
	}
    
    /**
     * This function is called once each time the robot enters tele-operated mode
     */
    public void teleopInit(){
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        myRobot.tankDrive(left,right);
        
        // This works!
        if(right.getRawButton(1)){
        	shooter.set(1.0);
        } else if(left.getRawButton(1)) {
        	shooter.set(-1.0);
        } else if (right.getRawButton(3) || left.getRawButton(3)) {
        	shooter.set(0.0);
        } else 
        while (right.getRawButton(1)) {
        	shooter.set(1.0);
        }
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
   private void autonomous1(){
	   if (autoLoopCounter < 150) { // going straight and crossing low bar			 
			myRobot.drive(0.5, 0.0); 							
			autoLoopCounter++;
		} else if (autoLoopCounter < 210) { // Turning 135 degrees after crossing low bar to shoot								
			myRobot.drive(0.475, 1.1635); 							
			autoLoopCounter++;
		} else if (autoLoopCounter < 290){ // Turning 135 degrees after crossing low bar to shoot								
			myRobot.drive(-0.5, 0.0); 	// From 6in to 8in wheel factor = .766798419						
			autoLoopCounter++;
		} else {
			myRobot.drive(0.0, 0.0); // If the robot has reached 100 packets,
									// this line tells the robot to stop
		}   
   }
   private void autonomous2() {
	   if (autoLoopCounter < 150) { // going straight and crossing low bar			 
			myRobot.drive(0.5, 0.0); 							
			autoLoopCounter++;
		} else if (autoLoopCounter < 210) { // Turning 135 degrees after crossing low bar to shoot								
			myRobot.drive(0.475, 0.935); 							
			autoLoopCounter++;
		} else if (autoLoopCounter < 290) {// Robot is going to stop for 40 loops to shoot
			myRobot.drive(0.0, 0.0); // If the robot has reached 100 packets,
			autoLoopCounter++;						// this line tells the robot to stop
		} else if (autoLoopCounter < 330) {
			myRobot.drive(0.475, 0.49);
			autoLoopCounter++;
		} else if (autoLoopCounter < 400) {					
			myRobot.drive(0.5, 0.0);
			autoLoopCounter++;
		} else {
			myRobot.drive(0.0,0.0);
		}    
   }
   //move one foot forword 
   //pick up ball
  //move 5 feet reverse
   //turn 180
   //drive 6 ft reach barior 
   private void autonomous3(){
	 //move one foot forword 
	   if (autoLoopCounter < 75) {
			myRobot.drive(-0.25, 0.0);
			autoLoopCounter++;
		} else if (autoLoopCounter == 75) {
			 //pick up ball
			Timer.delay(1);
			autoLoopCounter++;
		} else if (autoLoopCounter < 175) {
			myRobot.drive(0.4, 0.0);
			//move 5 feet reverse
			autoLoopCounter++;
		} else if (autoLoopCounter < 300) {
			//turn 180
			myRobot.drive(0.445, 1.2);
			
		 autoLoopCounter++;
		} else {
			myRobot.drive(0.0, 0.0);
		}   
   }
   }
