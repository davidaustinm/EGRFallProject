package org.usfirst.frc.team5980.robot.subsystems;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team5980.robot.commands.*;

/**
 *
 */
public class DriveTrain extends Subsystem {
	Victor left1, left2, right1, right2;
	public DriveTrain() {
		left1 = new Victor(0);
		left2 = new Victor(1);
		right1 = new Victor(2);
		right2 = new Victor(3);
		right1.setInverted(true);
		right2.setInverted(true);
	}
	
	public void setPower(double left, double right) {
		left1.set(left);
		left2.set(left);
		right1.set(right);
		right2.set(right);
	}
    
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new TankDriveCommand());
    }
}

