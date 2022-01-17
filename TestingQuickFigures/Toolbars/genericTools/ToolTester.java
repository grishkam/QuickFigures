/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Feb 24, 2021
 * Version: 2022.0
 */
package genericTools;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import handles.HasSmartHandles;
import handles.SmartHandle;
import imageDisplayApp.GraphicSetDisplayWindow;
import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;

/**
 A superclass for classes that test tools
 */
public abstract class ToolTester {

	
	/**simulates handle drags in several directions for every handle that is available
	 * @param image
	 * @param testRect the object being tested
	 * @param d1 the magnitude of one movement direction
	 * @param d2 the magnitude of the other movement direction
	 */
	void tryEveryHandle(ImageWindowAndDisplaySet image, HasSmartHandles testRect, double d1, double d2) {
		ArrayList<Integer> handles = testRect.getSmartHandleList().getAllHandleNumbers();
		IssueLog.log(handles.toString());
		for(Integer handleID: handles)
			simulateHandleMovements(image, testRect, handleID, d1, d2);
	}

	/**simulates a user mouse click
	 * @param image
	 * @param double1
	 */
	protected void simulateMouseCordinateEvent(ImageWindowAndDisplaySet image, Point2D p, int code) {
		simulateMouseCordinateEvent(image, p, code, 0, 1);
	}
	
	
	
	protected void simulateMouseCordinateEvent(ImageWindowAndDisplaySet image, Point2D p, int code, int modifiers, int clickCount) {
		JComponent canvas = image.getTheCanvas();
		/**will transform the cordinate system into mouse event cordinages*/
		double x = image.getConverter().transformX(p.getX());
		double y = image.getConverter().transformY(p.getY());
		MouseEvent event = new MouseEvent(canvas,code, System.currentTimeMillis(), modifiers, (int)x, (int)y, clickCount, false);
		canvas.dispatchEvent(event);
		IssueLog.waitMiliseconds(10);
	}
	
	/**simulates mouse clicks forevery coponent in the container*/
	public static void clickAllcomponents(Container jf) {
		
		for (Component canvas: jf.getComponents()) {
			clickComponent(canvas,0);
			if(canvas instanceof Container) {
				clickAllcomponents((Container) canvas);
			}
		}
	}
	
	/**simulates a mouse press, release and click on a component*/
	public static void clickComponent(Component canvas, int pausetime) {

		
		int x = 1;
		int y = 1;
		MouseEvent event = new MouseEvent(canvas,MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false);
		canvas.dispatchEvent(event);
		IssueLog.waitMiliseconds(1000+pausetime);
		
		event = new MouseEvent(canvas,MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false);
		canvas.dispatchEvent(event);
		
		event = new MouseEvent(canvas,MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false);
		canvas.dispatchEvent(event);
	}
	
	/**Assuming the handle includes its co-ordinate location, this simjulates an event
	 * @return */
	protected Point2D simulateHandlePressCordinateEvent(ImageWindowAndDisplaySet image, HasSmartHandles s, int handleID) {
		SmartHandle handle = s.getSmartHandleList().getHandleNumber(handleID);
		return simulateHandlePressEvent(image, handle);
	}

	/** presses the mouse within a given handle
	 * @param image
	 * @param handle
	 * @return
	 */
	Point2D simulateHandlePressEvent(ImageWindowAndDisplaySet image, SmartHandle handle) {
		assert(handle!=null);
		Point2D cordinateLocation = handle.getCordinateLocation();
		if (!handle.containsClickPoint(cordinateLocation))
		{
			double xc = handle.getClickableArea().getBounds().getCenterX();
			double yc = handle.getClickableArea().getBounds().getCenterY();
			xc=image.getTheCanvas().getConverter().unTransformX(xc);
			yc=image.getTheCanvas().getConverter().unTransformX(yc);
			cordinateLocation =new Point2D.Double(xc, yc);
		}
		
		simulateMouseCordinateEvent(image, cordinateLocation, MouseEvent.MOUSE_PRESSED);
		return cordinateLocation;
	}
	
	/**Assuming the handle includes its co-ordinate location, this simjulates an event
	 * @param dx how far from its original location the handle will be dragged to
	 * @param dy how far from its original location the handle will be dragged to*/
	private void simulateHandleMovement(ImageWindowAndDisplaySet image, HasSmartHandles s, int handleID, double dx, double dy) {
		s.makePrimarySelectedItem(true);
		SmartHandle handle = s.getSmartHandleList().getHandleNumber(handleID);
		if (handle==null) {IssueLog.log("Handle with id "+handleID+" is not longer available");return;}
		Point2D cordinateLocation = handle.getCordinateLocation();
		simulateMouseCordinateEvent(image, cordinateLocation, MouseEvent.MOUSE_PRESSED);
		Point2D dragTo = new Point2D.Double(cordinateLocation.getX()+dx, cordinateLocation.getY()+dy);
		simulateMouseCordinateEvent(image, dragTo , MouseEvent.MOUSE_DRAGGED);
		simulateMouseCordinateEvent(image, dragTo , MouseEvent.MOUSE_RELEASED);
		image.updateDisplay();
	}
	
	
	/**Assuming the handle includes its co-ordinate location, this simulates handle movements in 
	 * several possible directions.
	 * The object given must already be selected with its handles drawn onto the canvas for this to work*/
	private void simulateHandleMovements(ImageWindowAndDisplaySet image, HasSmartHandles s, int handleID, double d1, double d2) {
		
		this.simulateHandleMovement(image, s, handleID, d1, d2);
		this.simulateHandleMovement(image, s, handleID, -d1, -d2);
		
		this.simulateHandleMovement(image, s, handleID, -d1, d2);
		this.simulateHandleMovement(image, s, handleID, d1, -d2);
		
		this.simulateHandleMovement(image, s, handleID, d1, -d2);
		this.simulateHandleMovement(image, s, handleID, -d1, d2);
		
		this.simulateHandleMovement(image, s, handleID, -d1, -d2);
		this.simulateHandleMovement(image, s, handleID, d1, d2);
		
		
		this.simulateHandleMovement(image, s, handleID, 0, d2);
		this.simulateHandleMovement(image, s, handleID, 0, -d2);
		
		this.simulateHandleMovement(image, s, handleID, d1, 0);
		this.simulateHandleMovement(image, s, handleID, -d1, 0);
		
		this.simulateHandleMovement(image, s, handleID, 0, -d2);
		this.simulateHandleMovement(image, s, handleID, 0, d2);
		
		this.simulateHandleMovement(image, s, handleID, -d1, 0);
		this.simulateHandleMovement(image, s, handleID, d1, 0);
	}
	
	public static void simulateKeyEvents(ImageWindowAndDisplaySet image, CharSequence cs) {
		for(int i=0; i<cs.length(); i++) {
			simulateKeyStroke(image, cs.charAt(i), false);
		}
	}
	
	
	/**simulates a key stroke*/
	public static void simulateKeyStroke(ImageWindowAndDisplaySet image, char keyChar, boolean meta) {
		GraphicSetDisplayWindow c = image.getWindow();
		KeyEvent k = new KeyEvent(c, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), meta? KeyEvent.META_DOWN_MASK: 0, KeyEvent.getExtendedKeyCodeForChar(keyChar), keyChar);
		c.dispatchEvent(k);
		
	}
	
	/**
	 * @param image
	 * @param pointInsideRectanglePress
	 */
	public void simulatePress(ImageWindowAndDisplaySet image, Point2D.Double pointInsideRectanglePress) {
		simulateMouseCordinateEvent(image, pointInsideRectanglePress,  MouseEvent.MOUSE_PRESSED);
	}
	/**
	 * @param image
	 * @param dragPoint
	 */
	public void simulateRelease(ImageWindowAndDisplaySet image, Point2D.Double dragPoint) {
		simulateMouseCordinateEvent(image, dragPoint,  MouseEvent.MOUSE_RELEASED);
	}
	/**
	 * @param image
	 * @param dragPoint
	 */
	public void simulateDrag(ImageWindowAndDisplaySet image, Point2D.Double dragPoint) {
		simulateMouseCordinateEvent(image, dragPoint,  MouseEvent.MOUSE_DRAGGED);
	}
	
	public ToolSimulation getSimulation(ImageWindowAndDisplaySet image) {
		return new ToolSimulation(image);
	}
	
	public class ToolSimulation {
		ImageWindowAndDisplaySet image;
		
		public ToolSimulation(ImageWindowAndDisplaySet im) {
			this.image=im;
		}
		
		public void simulate(Point2D.Double p1, Point2D.Double p2) {
			simulatePress(image, p1);
			simulateDrag(image, p2);
			simulateRelease(image, p2);
		}
	}
}
