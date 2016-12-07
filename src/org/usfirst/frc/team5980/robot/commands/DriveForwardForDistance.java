package org.usfirst.frc.team5980.robot.commands;

import org.usfirst.frc.team5980.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveForwardForDistance extends Command {
	
	int distance; 
	double speed;
	double inches;
	EGRPID headingPID, coordinatePID;
	
    public DriveForwardForDistance(double inches,double speed) {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.driveTrain);
        headingPID = new EGRPID(0.02,0.002,0);
        headingPID.setTarget(0);
        coordinatePID = new EGRPID(0.04,0,0);
        coordinatePID.setTarget(0);
        this.inches = inches;
        this.speed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	//encoderTarget = Robot.sensors.getRightEncoder() + distance;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double correction = headingPID.getCorrection(Robot.sensors.getYaw()) + 
    			coordinatePID.getCorrection(Robot.sensors.getY());
    	Robot.driveTrain.setPower(speed - correction, speed + correction);
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.sensors.getX() >= inches;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.driveTrain.setPower(0,0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
