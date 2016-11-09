package org.usfirst.frc.team5980.robot.commands;

import org.usfirst.frc.team5980.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveForwardForDistance extends Command {
	
	int distance; 
	double speed;
	int encoderTarget;
	
    public DriveForwardForDistance(int distance,double speed) {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.driveTrain);
        this.distance = distance;
        this.speed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	encoderTarget = Robot.sensors.getRightEncoder() + distance;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.driveTrain.setPower(speed, speed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.sensors.getRightEncoder() >= encoderTarget;
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
