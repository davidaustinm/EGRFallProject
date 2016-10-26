package org.usfirst.frc.team5980.robot.subsystems;

import org.usfirst.frc.team5980.robot.commands.BallIntakeCommand;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class BallIntake extends Subsystem {
	Victor frontMotor;
	public BallIntake() {
		frontMotor = new Victor(4);
	}
	public void setPower(double power) {
		frontMotor.set(power);
	}
    
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new BallIntakeCommand());
    }
}

