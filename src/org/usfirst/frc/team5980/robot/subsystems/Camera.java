package org.usfirst.frc.team5980.robot.subsystems;

import org.usfirst.frc.team5980.robot.commands.CameraCommand;

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
    	//camera.setExposureManual(10);
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
    
    public void pushImage() {
    	camera.getImage(image);
    	SmartDashboard.putNumber("Time", System.currentTimeMillis());
    	if (processing == null || processing.isAlive() == false) {
    		setProcessingImage(image);
    		processing = new Thread(this);
    		processing.start();
    	}
    	ParticleReport[] reports = getParticleReports();
    	if (reports != null) SmartDashboard.putNumber("Reports Num", reports.length);
    	if (reports != null && reports.length > 0) {
    		SmartDashboard.putNumber("Number of reports", reports.length);
    		for (int count = 0; count < reports.length; count++) {
    			ParticleReport report = reports[count];
    			NIVision.Rect rectangle = new NIVision.Rect((int) report.top, (int) report.left, 
    					(int) report.height, (int)report.width);
    			NIVision.imaqDrawShapeOnImage(image, image, rectangle, DrawMode.DRAW_VALUE,
    					ShapeMode.SHAPE_RECT, 0.5f);
    		}
    	}
    	
    	CameraServer.getInstance().setImage(image);
    }
    
    class ParticleReport {
    	public double left, top, width, height;
    }
    
    NIVision.Range hue = new NIVision.Range(32, 57);
    NIVision.Range saturation = new NIVision.Range(112, 255);
    NIVision.Range value = new NIVision.Range(170, 255);
    
    
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

