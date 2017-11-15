package jrdp;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.*;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

class ImageHandler {
	private double compression;
	private DatagramSocket socket;
	private Robot robot;
	
	ImageHandler(double compression, DatagramSocket socket) {
		this.socket = socket;
		this.compression = compression;
		try {
			robot = new Robot();
		}catch(Exception ex) {ex.printStackTrace();}
	}
	
	BufferedImage getScreenshot(int x, int y, int width, int height) {
		//BufferedImage fromRobot = getFromRobot(x,y,width,height);
		//BufferedImage compressed = compress(getFromRobot(x,y,width,height));
		return compress(getFromRobot(x,y,width,height));
		//return getFromRobot(x, y, width, height);
	}
	
	private BufferedImage getFromRobot(int x,int y,int width,int height) {
		Rectangle rect;
		if(x == -1) {
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			width = gd.getDisplayMode().getWidth();
			height = gd.getDisplayMode().getHeight();
			rect = new Rectangle(0,0,width,height);
		}else {
			rect = new Rectangle(x, y, width, height);
		}
		//BufferedImage robotImage = robot.createScreenCapture(rect);
		return robot.createScreenCapture(rect);
	}
	
	private BufferedImage compress(BufferedImage compressThis) {
		BufferedImage returnThis = null;
		try {
			ByteArrayOutputStream compressed = new ByteArrayOutputStream();
			ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed);
			ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
			ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
			jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jpgWriteParam.setCompressionQuality((float)compression);
			jpgWriter.setOutput(outputStream);
			jpgWriter.write(null, new IIOImage(compressThis, null, null), jpgWriteParam);
			jpgWriter.dispose();
			ByteArrayInputStream bais = new ByteArrayInputStream(compressed.toByteArray());
			returnThis = ImageIO.read(bais);
		}catch(Exception ex) {}
		return returnThis;
	}
}