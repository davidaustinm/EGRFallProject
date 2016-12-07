package org.usfirst.frc.team5980.robot.commands;

import org.usfirst.frc.team5980.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveToTarget extends Command {
	EGRPID headingPID;

    public DriveToTarget() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.driveTrain);
        headingPID=new EGRPID(.005,0,0);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    protected void execute() {
    	if (Robot.camera.getTargetFound()==false){
    		Robot.driveTrain.setPower(-.25, .25);
    		return;
    	}
    	double changeInX=Robot.camera.getTargetX()-Robot.sensors.getX();
    	double changeInY=Robot.camera.getTargetY()-Robot.sensors.getY();
    	double heading=Math.toDegrees(Math.atan2(changeInY,changeInX));
    	headingPID.setTarget(heading);
    	double correction=headingPID.getCorrection(Robot.sensors.getYaw());
    	double leftPower=.4-correction;
    	double rightPower=.4+correction;
    	Robot.driveTrain.setPower(leftPower, rightPower);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return Robot.camera.getTargetFound() && Robot.camera.getDistanceToTarget()<24;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.driveTrain.setPower(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
