package org.usfirst.frc.team5980.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Sensors extends Subsystem {
	AHRS navx;
	Encoder left,right;
	double x, y;
	double lastRightEncoder, lastLeftEncoder;
	public Sensors() {
		try {
			navx = new AHRS(SPI.Port.kMXP);
		} catch (RuntimeException ex ) {
			DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
    	}	
		left= new Encoder(0,1);
		right=new Encoder(2,3);
	}
	double gyroOffset = 0;
	public void resetPosition() {
		x = 0;
		y = 0;
		lastRightEncoder = getRightEncoder();
		lastLeftEncoder = getLeftEncoder();
		gyroOffset = navx.getYaw();
	}
	
	public void updatePosition() {
		double currentRightEncoder = getRightEncoder();
		double currentLeftEncoder = getLeftEncoder();
		double changeInRightEncoder = currentRightEncoder - lastRightEncoder;
		double changeInLeftEncoder = currentLeftEncoder - lastLeftEncoder;
		double distance = (changeInRightEncoder + changeInLeftEncoder) / 2.0;
		double angle = getYaw();
		x = x + distance*Math.cos(Math.toRadians(angle));
		y = y + distance*Math.sin(Math.toRadians(angle));
		lastRightEncoder = currentRightEncoder;
		lastLeftEncoder = currentLeftEncoder;
	}
	
	public double getX() {return x / 33.0;}
	public double getY() {return y / 33.0;}
	
    public int getLeftEncoder(){
    	return -left.get();
    }
    public int getRightEncoder(){
    	return right.get();
    }
    public double getYaw(){
    	return -(navx.getYaw() - gyroOffset);
    }
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

