package org.usfirst.frc.team5980.robot.commands;

import org.usfirst.frc.team5980.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ShootBall extends Command {
	long endTime;

    public ShootBall() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.ballIntake);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	endTime=System.currentTimeMillis()+2000;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.ballIntake.setPower(-1);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return System.currentTimeMillis()>=endTime;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.ballIntake.setPower(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
