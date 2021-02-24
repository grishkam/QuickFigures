/**
 * Author: Greg Mazo
 * Date Modified: Dec 19, 2020
 * Version: 2021
 */
package genericTools;


import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Test;

import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import appContextforIJ1.ImageDisplayTester;
import externalToolBar.InterfaceExternalTool;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.ImageWindowAndDisplaySet;
import includedToolbars.ObjectToolset1;
import locatedObject.ArrayObjectContainer;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import messages.ShowMessage;
import testing.TestShapes;
import testing.TestingOptions;

/**
 The tool used to select and move objects is critical.
 This test checks the basic functions of the tool
 After automated tests are done, paused for a little while to allow user to 
 attempt some manual tinkering
 */
public class Object_MoverTest extends ToolTester {

	@Test
	public void test() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		ImageDisplayTester.startToolbars(true);
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		
		testTool();
		
		if (TestingOptions.performManualTests) {
			ShowMessage.showOptionalMessage("Testing done", false,"Automated tests for object selector done. now user may perform manual edits for 30 seconds");
			IssueLog.waitSeconds(30);
		
		}
		
		
	}

	/**
	 tests several aspects of the object mover tool
	 */
	void testTool() {
		Object_Mover currentTool=null;
		
		
		InterfaceExternalTool<?> mover = ObjectToolset1.setCurrentTool(Object_Mover.selectorToolName);
		assert(mover instanceof GeneralTool) ;
		if (mover instanceof GeneralTool) {
			GeneralTool t=(GeneralTool) mover;
			ToolBit bit = t.getToolbit();
			assert(bit instanceof Object_Mover) ;
			currentTool=(Object_Mover) bit;
			assert(currentTool!=null);
			
		}
		ImageWindowAndDisplaySet image = TestShapes.createExample(TestShapes.DIVERSE_SHAPES);
		
		image.setZoomLevel(2);// must be whole number so that the cordinates match the canvas. otherwise, rounding of numbers will make the rest of this test fail ( since 44 does not equal 45 )
		
		
		java.awt.geom.Rectangle2D.Double r = new Rectangle2D.Double(40, 80, 50, 70);
		java.awt.geom.Rectangle2D.Double rCircle = new Rectangle2D.Double(110, 80, 60, 90);
		RectangularGraphic testRect = new RectangularGraphic(r);
		RectangularGraphic testCircle = new CircularGraphic(rCircle);
		testRect.setFillColor(Color.blue);
		testRect.setStrokeColor(Color.green);
		testCircle.setFillColor(Color.orange);
		
		image.getImageAsWrapper().addItemToImage(testRect);
		image.getImageAsWrapper().addItemToImage(testCircle);
		
		
		assert(testRect.getRectangle().equals(r));
		
		
		/**tests to determine if a mouse press selects the rectangle*/
		assert(!testRect.isSelected());
		assert(!testCircle.isSelected());
		double px=50;//a point known to be within the rectangle
		double py=100;
		Point2D.Double pointInsideRectanglePress = new Point2D.Double(px, py);
		simulatePress(image, pointInsideRectanglePress);
		assert(testRect.isSelected());
		assert(!testCircle.isSelected());//nothing should be done to objects outside of the click location
		Point2D.Double pointInsideCirclePress = new Point2D.Double(140,100);
		simulatePress(image, pointInsideCirclePress);
		assert(!testRect.isSelected());//should no longer be selected after the circle is clicked on
		assert(testCircle.isSelected());// now selected
		testRect.deselect();//deselect after test of selection is over
		testCircle.deselect();
		
		
		/**tests to determine if a mouse drag (without shift down) changes the location*/
		testRect.setRectangle(r);
		simulatePress(image, pointInsideRectanglePress);
		double dx=5;//displacement x
		double dy=20;//displacement y
		Point2D.Double dragPoint = new Point2D.Double(px+dx, py+dy);
		IssueLog.log(dragPoint);
		simulateDrag(image, dragPoint);
		assert(!testRect.getRectangle().equals(r));
		
		/**the new location for the rectangle*/
		java.awt.geom.Rectangle2D.Double r2 = new Rectangle2D.Double(r.x+dx, r.y+dy, 50, 70);
		IssueLog.log(r2);
		IssueLog.log(testRect.getRectangle());
		assert(testRect.getRectangle().equals(r2));
		assert(testCircle.getRectangle().equals(rCircle));//nothing should have been done to the circle as it was not selected
		
		
		/**tests to see if undo works*/
		//undo is added upon mouse release
		simulateRelease(image, dragPoint);
		image.getUndoManager().undo();
		assert(testRect.getRectangle().equals(r));//the original rectangle
		assert(!testRect.getRectangle().equals(r2));
		
		
		
		/**tests to determine if a mouse press outside of objects de-selects rectangle
		  and circle*/
		testRect.select();
		testCircle.select();
		Point2D.Double pointPress2 = new Point2D.Double(r.getMaxX()+80, r.getMaxY()+80);
		simulatePress(image, pointPress2);
		assert(!testRect.isSelected());
		assert(!testCircle.isSelected());
		
		
		/**tests to determine if a mouse press with shift down 
		 * keeps original object selected*/
		testRect.deselect();
		testCircle.select();
		simulateMouseCordinateEvent(image, pointInsideRectanglePress,  MouseEvent.MOUSE_PRESSED, MouseEvent.SHIFT_DOWN_MASK, 1);
		assert(testRect.isSelected());
		assert(testCircle.isSelected());
		
		
		/**tests to determine whether a mouse press in a handle resizes the rectangle*/
		simulatePress(image, pointInsideRectanglePress);//selects the rectangle so that the handles are visible
		simulateHandlePressCordinateEvent(image, testRect, RectangleEdges.LOWER_RIGHT);
		int finalWidth=100, finalHeight=120;
		Point2D.Double dragPointResize = new Point2D.Double(testRect.getRectangle().getX()+finalWidth, testRect.getRectangle().getY()+finalHeight);
		IssueLog.log(dragPointResize);
		simulateDrag(image, dragPointResize);
		assert(testRect.getObjectWidth()==finalWidth);
		assert(testRect.getObjectHeight()==finalHeight);
		
		
		
		
		/**tests to determine if a mouse drag with shift down 
		 changes locations of both selected objects. 
		 while the rectangle is being dragged, circle will also move*/
		testRect.setRectangle(r);
		testCircle.setRectangle(rCircle);
		testCircle.select();//so that circle starts selected
		testRect.deselect();
		simulateMouseCordinateEvent(image, pointInsideRectanglePress,  MouseEvent.MOUSE_PRESSED, MouseEvent.SHIFT_DOWN_MASK, 1);
		assert(testRect.isSelected());
		dx=50;//displacement x
		dy=70;//displacement y
		dragPoint = new Point2D.Double(px+dx, py+dy);
		IssueLog.log(dragPoint);
		simulateMouseCordinateEvent(image, dragPoint,  MouseEvent.MOUSE_DRAGGED, MouseEvent.SHIFT_DOWN_MASK, 1);
		assert(!testRect.getRectangle().equals(r));
		/**the new bounds expected for the circle*/
		r2 = new Rectangle2D.Double(rCircle.x+dx, rCircle.y+dy, rCircle.width, rCircle.height);
		IssueLog.log(r2);
		IssueLog.log(testCircle.getRectangle());
		assert(testCircle.getRectangle().equals(r2));
		assert(!testCircle.getRectangle().equals(rCircle));//circle should have changed
		
		
		/**tests to determine if a class set to be excluded is really not selected*/
		testRect.setRectangle(r);
		testCircle.setRectangle(rCircle);
		testRect.deselect();
		testCircle.deselect();		
		currentTool.setExcludedClass(RectangularGraphic.class);//exclude rectangles
		simulatePress(image, pointInsideRectanglePress);
		assert(!testRect.isSelected());//should not be selected
		
		currentTool.setExcludedClass(CircularGraphic.class);//exclude rectangles
		simulatePress(image, pointInsideCirclePress);
		assert(!testCircle.isSelected());//circle should not be selected
		simulatePress(image, pointInsideRectanglePress);
		assert(testRect.isSelected());//should be selected. Even though circle is a subclass of rectangle, rectangles are not circles
		
		testRect.deselect();
		currentTool.setExcludedClass(null);//no longer excludes rectangles
		simulatePress(image, pointInsideRectanglePress);
		assert(testRect.isSelected());//should be selected
		
		/**tests whether option for the tool to select only instances of a certain class is working */
		testRect.deselect();
		testCircle.deselect();
		currentTool.setSelectOnlyThoseOfClass(testCircle.getClass());
		simulatePress(image, pointInsideRectanglePress);
		assert(!testRect.isSelected());//should only select circles at this point
		simulatePress(image, pointInsideCirclePress);
		assert(testCircle.isSelected());//circle should be selected
		currentTool.setSelectOnlyThoseOfClass(null);
		
		/**tests to determine if a mouse drag over a large area (starting in a spot with no objects selects everything inside
		 */
		testRect.setRectangle(r);
		testCircle.setRectangle(rCircle);
		testRect.deselect();
		testCircle.deselect();
		simulateMouseCordinateEvent(image, new Point2D.Double(-250, -250),  MouseEvent.MOUSE_PRESSED);
		simulateMouseCordinateEvent(image, new Point2D.Double(900, 900),  MouseEvent.MOUSE_DRAGGED);
		simulateMouseCordinateEvent(image, new Point2D.Double(900, 900),  MouseEvent.MOUSE_RELEASED);
		assert(testRect.isSelected());
		assert(testCircle.isSelected());
		
		/**what about a selection region that containns one object (the rectangle) but not another (the circle)*/
		testRect.deselect();
		testCircle.deselect();
		simulateMouseCordinateEvent(image, new Point2D.Double(-250, -250),  MouseEvent.MOUSE_PRESSED);
		Point2D.Double notInCircle = new Point2D.Double(testCircle.getRectangle().x-2, 900);//dragpoint not within the circle chosen for test
		simulateDrag(image, notInCircle);
		simulateRelease(image, notInCircle);
		assert(testRect.isSelected());
		assert(!testCircle.isSelected());
		
		
		/**A special list of handles is used to resize multiple objects, tests to determine if a mouse drag in the reshape handle lists 
		 is detected as such. only tests one corner handle, others are tested elsewhere
		 */
		testRect.setRectangle(r);
		testCircle.setRectangle(rCircle);
		testRect.deselect();
		testCircle.deselect();
		/**Selects the rectangle with shift down so more than one object will be selected*/
		simulatePress(image, pointInsideCirclePress);
		simulateMouseCordinateEvent(image, pointInsideRectanglePress,  MouseEvent.MOUSE_PRESSED, MouseEvent.SHIFT_DOWN_MASK, 1);
		//since the circle extends farther down and right, its lower right corner should be the same as the reshape handles
		Point2D expectedHandleLocation = new Point2D.Double(rCircle.getMaxX(), rCircle.getMaxY());
		Rectangle2D combinedBounds = 	ArrayObjectContainer.combineOutLines( currentTool.getAllSelectedItems(false)).getBounds2D();
		Point2D spot = RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, combinedBounds);
		assert(spot.equals(expectedHandleLocation))	;	
		
		this.simulateMouseCordinateEvent(image, spot, MouseEvent.MOUSE_PRESSED);
		this.simulateMouseCordinateEvent(image, new Point2D.Double(spot.getX()+12, spot.getY()+12), MouseEvent.MOUSE_DRAGGED);
		this.simulateMouseCordinateEvent(image, new Point2D.Double(spot.getX()+12, spot.getY()+12), MouseEvent.MOUSE_RELEASED);
		assert(!testCircle.getRectangle().equals(r2));//rect should have changed
		assert(!testCircle.getRectangle().equals(rCircle));//circle should have changed
		
		
		/**simulate clicking on a text item*/
		TextGraphic text = new TextGraphic("text 1");
		image.getImageAsWrapper().addItemToImage(text);
		text.setLocationUpperLeft(50,50);
		Point2D.Double p = new Point2D.Double(50+5, 50+5);
		
		simulatePressReleaseAndClick(image, p, 1);
		simulatePressReleaseAndClick(image, p, 2);
		
		assert(text.isEditMode());//makes sure double clicking 
		
		super.simulateKeyEvents(image, "type m2");
		assert(text.getText().contains("type m2"));//tests to see if text can be typed in
		
		
		/**tries many mouse movements on many handles to determine if any exceptions are trigered*/
		tryEveryHandle(image, testRect, 5, 5);
		tryEveryHandle(image, testRect, 25, 25);
		tryEveryHandle(image, testRect, 75, 75);
		
		
		
		
		
		
	}

	

	

	

	/**
	 * @param image
	 * @param p
	 * @param clickCount
	 */
	public void simulatePressReleaseAndClick(ImageWindowAndDisplaySet image, Point2D.Double p, int clickCount) {
		this.simulateMouseCordinateEvent(image, p, MouseEvent.MOUSE_PRESSED,0,clickCount);
		this.simulateMouseCordinateEvent(image, p, MouseEvent.MOUSE_RELEASED,0,clickCount);
		this.simulateMouseCordinateEvent(image, p, MouseEvent.MOUSE_CLICKED,0,clickCount);
	}

	

}
