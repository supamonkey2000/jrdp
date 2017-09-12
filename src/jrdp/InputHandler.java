package jrdp;

import java.awt.Robot;
import java.awt.event.InputEvent;

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
					System.out.println("Info: A key was pressed!");
				}
				
				if(mClick!=-1) {
					robot.mousePress(InputEvent.getMaskForButton(mClick));
					robot.delay(50);
					robot.mouseRelease(InputEvent.getMaskForButton(mClick));
					System.out.println("Info: Mouse button "+InputEvent.getMaskForButton(mClick)+" was clicked");
				}
			}
		};
		pressThread.start();
		if(mXi!=0 || mYi!=0) {
			//Point ploc = MouseInfo.getPointerInfo().getLocation();getClass();
			int x = mXi;
			int y = mYi;
			robot.mouseMove(x, y);
			System.out.println("Info: Mouse moved to "+x+","+y);
		}
		if(mSi!=0) {
			robot.mouseWheel(mSi);
		}
	}	
}