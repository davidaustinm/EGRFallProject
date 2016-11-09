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
	public Sensors() {
		try {
			navx = new AHRS(SPI.Port.kMXP);
		} catch (RuntimeException ex ) {
			DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
    	}	
		left= new Encoder(0,1);
		right=new Encoder(2,3);
	}
    public int getLeftEncoder(){
    	return -left.get();
    }
    public int getRightEncoder(){
    	return right.get();
    }
    public double getYaw(){
    	return -navx.getYaw();
    }
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

