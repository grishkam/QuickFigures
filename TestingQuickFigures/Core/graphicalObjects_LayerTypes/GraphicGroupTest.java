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
 * Version: 2023.1
 */
package graphicalObjects_LayerTypes;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.Test;

import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import appContextforIJ1.ImageDisplayTester;
import genericTools.ToolTester;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import handles.ReshapeHandleList;
import handles.SmartHandle;
import imageDisplayApp.ImageWindowAndDisplaySet;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import testing.TestExample;
import testing.TestShapes;

/**
Test to determine if groups are working properly
If so, objects should be resizable by clicking on the groups handles
 */
public class GraphicGroupTest extends ToolTester {

	@Test
	public void test() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		ImageDisplayTester.startToolbars(true);
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		
		testTool();
	
	}

	/**
	tests to make sure that the user can resize and move the objects in the group using the groups handles
	 */
	private void testTool() {
		ImageWindowAndDisplaySet image = TestShapes.createExample(TestExample.EMPTY);
		
		java.awt.geom.Rectangle2D.Double r = new Rectangle2D.Double(40, 80, 50, 70);
		java.awt.geom.Rectangle2D.Double rCircle = new Rectangle2D.Double(110, 80, 60, 90);
		RectangularGraphic testRect = new RectangularGraphic(r);
		RectangularGraphic testCircle = new CircularGraphic(rCircle);
		testRect.setFillColor(Color.blue);
		testRect.setStrokeColor(Color.green);
		testCircle.setFillColor(Color.orange);
		
		GraphicGroup gg = new GraphicGroup(true, testRect, testCircle);
		
		image.getImageAsWorksheet().addItemToImage(gg);
		
		/**test to determine if user can still click the objects to select them*/
		double px=50;//a point known to be within the rectangle
		double py=100;
		Point2D.Double pointInsideRectanglePress = new Point2D.Double(px, py);
		simulateMouseCordinateEvent(image, pointInsideRectanglePress,  MouseEvent.MOUSE_PRESSED);
		assert(testRect.isSelected());
		testRect.deselect();
		
		/**test to determine if user can still click near object to select everythin in group*/
		Point2D.Double pointInsideGroupHookPress = new Point2D.Double(39, 100);
		simulateMouseCordinateEvent(image, pointInsideGroupHookPress,  MouseEvent.MOUSE_PRESSED);
		assert(testRect.isSelected());
		assert(testCircle.isSelected());
		ReshapeHandleList startList = gg.getReshapeList();
		image.updateDisplay();
		
		assert(startList==gg.getSmartHandleList());//checks to make sure list is not changing
		
		/**simulates mouse drags on the group handles.
		 *  confirms that objects within the group change size*/
		for(int aHandle: RectangleEdges.internalLocations){
			testRect.setRectangle(r);
			testCircle.setRectangle(rCircle);
			gg.select();
			image.updateDisplay();
			SmartHandle sh = startList.getHandleOfType(aHandle);
				int n = sh.getHandleNumber();
				
				Point2D spot = super.simulateHandlePressCordinateEvent(image, gg, n);
				assert(gg.getSmartHandleList().getHandleNumber(n)==sh);
				int shift = 85;//suffidient distance for change to shapes to be certain
				this.simulateMouseCordinateEvent(image, new Point2D.Double(spot.getX()+shift, spot.getY()+shift), MouseEvent.MOUSE_DRAGGED);
				this.simulateMouseCordinateEvent(image, new Point2D.Double(spot.getX()+shift, spot.getY()+shift), MouseEvent.MOUSE_RELEASED);
				
				gg.select();
				image.updateDisplay();
				
				
				assert(!testRect.getRectangle().equals(r));//rectangle should have changed size or location
				assert(!testCircle.getRectangle().equals(rCircle));//circle should have changed size or location
	}
		
		image.closeWindowButKeepObjects();
	}

}
