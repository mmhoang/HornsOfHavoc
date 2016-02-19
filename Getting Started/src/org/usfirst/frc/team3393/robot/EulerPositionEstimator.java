package org.usfirst.frc.team3393.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

public class EulerPositionEstimator {
	
	private Accelerometer _accelSensor;
	
	private double _velocity;
	private double _displacement;
	private Timer _eTimer;
	
	public EulerPositionEstimator(Accelerometer accelerometer) {
		this._accelSensor = accelerometer;
		
		this._eTimer = new Timer();
		this._eTimer.start();
	}
	
	public Accelerometer getAccelerometer() {
		return this._accelSensor;
	}
	
	public void update() {
		this._velocity += this._accelSensor.getY() * this._eTimer.get();
		this._displacement += this._velocity * this._eTimer.get();
		this._eTimer.reset();
	}
	
	public void reset() {
		this._velocity = 0;
		this._displacement = 0;
		this._eTimer.reset();
	}
	
	public double getDisplacement() {
		return this._displacement * 10;
	}
	public double getDistance() {
		return Math.abs(this._displacement) * 10;
	}
	
	public double getVelocity() {
		return this._velocity * 10;
	}
	public double getSpeed() {
		return Math.abs(this._velocity) * 10;
	}
	
}
