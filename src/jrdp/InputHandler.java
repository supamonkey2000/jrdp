package jrdp;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class InputHandler {
	
	Robot robot;
	
	public InputHandler() {
		try {robot = new Robot();}catch(Exception ex) {}
	}
	
	public void handle(String keystrokes, int mXi, int mYi, int mSi, int mClick) throws Exception { //mouse click: 0=left; 1=right; 2=middle;
		int key = Integer.parseInt(keystrokes);
		Thread pressThread = new Thread() {
			public void run() {
				if(key!=-1) {
					robot.keyPress(key);
					try{
						Thread.sleep(200);//Might need to change this value
					}catch(Exception ex) {};
					robot.keyRelease(key);
				}
				
				if(mClick!=-1) {
					robot.mousePress(mClick);
					try{
						Thread.sleep(50);//Might need to change this value
					}catch(Exception ex) {};
					robot.mouseRelease(mClick);
				}
			}
		};
		pressThread.start();
		if(mXi!=0) {
			Point ploc = MouseInfo.getPointerInfo().getLocation();getClass();
			int x = ((int)ploc.getX()) + (mXi);
			int y = ((int)ploc.getY());
			robot.mouseMove(x, y);
		}
		if(mXi!=0) {
			Point ploc = MouseInfo.getPointerInfo().getLocation();getClass();
			int x = ((int)ploc.getX());
			int y = ((int)ploc.getY()) + (mYi);
			robot.mouseMove(x, y);
		}
		if(mSi!=0) {
			robot.mouseWheel(mSi);
		}
	}	
}