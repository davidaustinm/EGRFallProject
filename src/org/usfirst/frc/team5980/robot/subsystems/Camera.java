package org.usfirst.frc.team5980.robot.subsystems;

import org.usfirst.frc.team5980.robot.commands.CameraCommand;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
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
    	camera.setExposureManual(10);
    	camera.openCamera();
    	camera.startCapture();
    	
    	CameraServer.getInstance().setQuality(20);
    	image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
    	processingImage = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
    	
    	criteria = new NIVision.ParticleFilterCriteria2[1];
        criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA, 100, 6000, 0,0);
    	filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);
    }
    
    public synchronized void setProcessingImage(Image image) {
    	NIVision.imaqDuplicate(processingImage, image);
    }
    
    public synchronized Image getProcessingImage() {
    	return processingImage;
    }
    
    public void pushImage() {
    	camera.getImage(image);
    	
    	if (processing != null && processing.isAlive() == false) {
    		setProcessingImage(image);
    		processing = new Thread(this);
    		processing.start();
    	}
    	
    	
    	CameraServer.getInstance().setImage(image);
    }
    
    NIVision.Range hue = new NIVision.Range(90, 130);
    NIVision.Range saturation = new NIVision.Range(175, 255);
    NIVision.Range value = new NIVision.Range(200, 255);
    
    
    public void run() {
    	Image image = getProcessingImage();
    	Image mask = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 0);
    	NIVision.imaqColorThreshold(mask, image, 128, NIVision.ColorMode.HSV, hue, saturation, value);
    	NIVision.imaqParticleFilter4(mask, mask, criteria, filterOptions, null);
    	
    	
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new CameraCommand());
    }
}

