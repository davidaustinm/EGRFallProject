package org.usfirst.frc.team5980.robot.commands;

import org.usfirst.frc.team5980.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class RotateToTarget extends Command {

    public RotateToTarget() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.driveTrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double angleToTarget = Robot.camera.getAngleToTarget();
    	if (Robot.camera.getTargetFound() == false) {
    		Robot.driveTrain.setPower(-0.25, 0.25);
    		return;
    	}
    	if (Math.abs(angleToTarget) < 3) {
    		Robot.driveTrain.setPower(0, 0);
    		return;
    	}
    	if (angleToTarget > 0) {
    		Robot.driveTrain.setPower(.2, -.2);
    	}
    	else {
    		Robot.driveTrain.setPower(-.2, .2);
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.camera.getTargetFound() && Math.abs(Robot.camera.getAngleToTarget())< 3;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
