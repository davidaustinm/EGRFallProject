package org.usfirst.frc.team5980.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team5980.robot.*;

/**
 *
 */
public class TankDriveCommand extends Command {

    public TankDriveCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.driveTrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }
    
    public double deadBand(double x){
    	if (Math.abs(x)<.2){
    		return 0;
    	}else{
    		return x;
    	}
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//double leftPower = -Robot.oi.driver.getRawAxis(1);
    	//double rightPower = -Robot.oi.driver.getRawAxis(5);
    	double leftPower = deadBand(-Robot.oi.driver.getLeftJoyY());
    	double rightPower = deadBand(-Robot.oi.driver.getRightJoyY());
    	Robot.driveTrain.setPower(leftPower,  rightPower);
    	SmartDashboard.putNumber("left:", leftPower);
    	SmartDashboard.putNumber("right:", rightPower);
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
