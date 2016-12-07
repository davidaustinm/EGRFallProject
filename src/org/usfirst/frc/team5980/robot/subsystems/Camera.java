package org.usfirst.frc.team5980.robot.subsystems;



import org.usfirst.frc.team5980.robot.Robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

/**
 *
 */
public class Camera extends Subsystem implements Runnable {
    USBCamera camera;
    double targetWidth=16;
    double targetHeight=18;
    double targetAspectRatio=targetWidth/targetHeight;
    Image image, processingImage;
    Thread processing = null;
    NIVision.ParticleFilterCriteria2[] criteria;
    NIVision.ParticleFilterOptions2 filterOptions;
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public Camera() {
    	camera = new USBCamera("cam1");
    	camera.setFPS(10);
    	camera.setSize(320, 240);
    	camera.setWhiteBalanceHoldCurrent();
    	camera.setExposureManual(15);
    	camera.openCamera();
    	camera.startCapture();
    	
    	CameraServer.getInstance().setQuality(20);
    	image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
    	processingImage = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
    	
    	criteria = new NIVision.ParticleFilterCriteria2[1];
        criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA, 100, 100000, 0,0);
    	filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);
    }
    
    public synchronized void setProcessingImage(Image image) {
    	NIVision.imaqDuplicate(processingImage, image);
    }
    
    public synchronized Image getProcessingImage() {
    	return processingImage;
    }
    
    ParticleReport[] particleReports = null;
    public synchronized void setParticleReports(ParticleReport[] reports) {
    	particleReports = reports;
    }
    
    public synchronized ParticleReport[] getParticleReports() {
    	return particleReports;
    }
    ParticleReport target = null;
    double x = 0;
    double distanceToTarget = 0;
    double angleToTarget = 0;
    boolean targetFound = false;
    double imagePoseX, imagePoseY, imagePoseHeading;
    double targetX, targetY;
    public void pushImage() {
    	//camera.setExposureManual(5);
    	camera.getImage(image);
    	SmartDashboard.putNumber("Time", System.currentTimeMillis());
    	if (processing == null || processing.isAlive() == false) {
    		imagePoseX = Robot.sensors.getX();
    		imagePoseY = Robot.sensors.getY();
    		imagePoseHeading = Robot.sensors.getYaw();
    		setProcessingImage(image);
    		processing = new Thread(this);
    		processing.start();
    	}
    	ParticleReport[] reports = getParticleReports();
    	if (reports != null && reports.length > 0) {
    		SmartDashboard.putNumber("Number of reports", reports.length);
    		target = reports[0];
    		
    		for (int count = 0; count < reports.length; count++) {
    			ParticleReport report = reports[count];
    			if(Math.abs(report.getAspectRatio()-targetAspectRatio) < Math.abs(target.getAspectRatio()-targetAspectRatio)) {
    				target = report;
    			}
    			NIVision.Rect rectangle = new NIVision.Rect((int) report.top, (int) report.left, 
    					(int) report.height, (int)report.width);
    			NIVision.imaqDrawShapeOnImage(image, image, rectangle, DrawMode.DRAW_VALUE,
    					ShapeMode.SHAPE_RECT, 0.5f);
    			
    		}
    		double pixelsToInches = target.width/targetWidth;
    		double d = 200/pixelsToInches;
    		distanceToTarget = d/Math.tan(Math.toRadians(39));
    		x = target.left+target.width/2;
    		angleToTarget = Math.toDegrees(Math.atan((x-160)/pixelsToInches/distanceToTarget));
    		targetFound = true;
    		double headingToTarget = Math.toRadians(imagePoseHeading - angleToTarget);
    		targetX = imagePoseX + distanceToTarget * Math.cos(headingToTarget);
    		targetY = imagePoseY + distanceToTarget * Math.sin(headingToTarget);
    		SmartDashboard.putNumber("Distance to Target: ", distanceToTarget);
    		SmartDashboard.putNumber("X: ", x);
    		SmartDashboard.putNumber("Angle to Target: ", angleToTarget);
    	}
    	else {
    		target = null;
    		x = 0;
    		distanceToTarget = 0;
    		angleToTarget = 0;
    		targetFound = false;
    	}
    	
    	CameraServer.getInstance().setImage(image);
    }
    
    public double getTargetX() {
    	return targetX;
    }
    public double getTargetY() {
    	return targetY;
    }
    
    public boolean getTargetFound() {
    	return targetFound;
    }
    public double getDistanceToTarget() {
    	return distanceToTarget;
    }
    public double getAngleToTarget() {
    	return angleToTarget;
    }
    
    class ParticleReport {
    	public double left, top, width, height;
    	public double getAspectRatio(){
    		return width/height;
    	}
    }
    /*
    NIVision.Range hue = new NIVision.Range(32, 57);
    NIVision.Range saturation = new NIVision.Range(112, 255);
    NIVision.Range value = new NIVision.Range(100, 255);
    */
    
    NIVision.Range hue = new NIVision.Range(90, 130); // 90, 130 at worlds
	NIVision.Range saturation = new NIVision.Range(150, 255); // 175, 255 at worlds
	NIVision.Range value = new NIVision.Range(150, 255); // 200, 255 at worlds
    
    public void run() {
    	Image image = getProcessingImage();
    	Image mask = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 0);
    	NIVision.imaqColorThreshold(mask, image, 128, NIVision.ColorMode.HSV, hue, saturation, value);
    	NIVision.imaqParticleFilter4(mask, mask, criteria, filterOptions, null);
    	int numParticles = NIVision.imaqCountParticles(mask, 1);
    	SmartDashboard.putNumber("Number of particles", numParticles);
    	
    	ParticleReport[] reports = new ParticleReport[numParticles];
    	for (int count = 0; count < numParticles; count++) {
    		reports[count] = new ParticleReport();
    		reports[count].left = NIVision.imaqMeasureParticle(mask, count, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
    		reports[count].top = NIVision.imaqMeasureParticle(mask, count, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
    		reports[count].width = NIVision.imaqMeasureParticle(mask, count, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_WIDTH);
    		reports[count].height = NIVision.imaqMeasureParticle(mask, count, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_HEIGHT);
    	}
    	setParticleReports(reports);
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new CameraCommand());
    }
}

