package jrdp;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class InputHandler {
	
	Robot robot;
	
	public InputHandler() {
		try {robot = new Robot();}catch(Exception ex) {}
	}
	
	public void handle(String[] keystrokes, int mXi, int mYi, int mSi) throws Exception {
		for(int i = 0; i < keystrokes.length; i++) {
			int theKey = Integer.parseInt(keystrokes[i]);
			robot.keyPress(theKey);
		}
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