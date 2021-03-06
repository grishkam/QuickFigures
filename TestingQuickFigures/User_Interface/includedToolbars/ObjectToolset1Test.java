/**
 * Author: Greg Mazo
 * Date Modified: Dec 20, 2020
 * Version: 2021.1
 */
package includedToolbars;

import java.awt.Rectangle;

import appContextforIJ1.ImageDisplayTester;
import graphicalObjects_Shapes.RectangularGraphic;
import ij.IJ;
import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;
import testing.TestShapes;
import ultilInputOutput.FileChoiceUtil;

class ObjectToolset1Test {

	public static void main(String[] args) {
		new ObjectToolset1Test().test();
	}
	
	
	void test() {
		ImageDisplayTester.showInnitial();
		ImageWindowAndDisplaySet i = ImageWindowAndDisplaySet.createAndShowNew("Figure", 450,300);
		;
		
		/**Tests to make sure all of the rectangular object drawing tools work*/
		
		
	
		
		assert(FileChoiceUtil.yesOrNo("You should see the toolbar near the left of the screen,"
				+"For 5 seconds, try clicking on the tools"));
		
		IJ.wait(5000);
		
		i.getImageAsWorksheet().getTopLevelLayer().add(new RectangularGraphic(new Rectangle(0,0,40,40)));
		TestShapes.addAllRectangleShapeTools(i.getImageAsWorksheet().getTopLevelLayer());
		i.updateDisplay();
		IJ.wait(400);
		
		assert(FileChoiceUtil.yesOrNo("You should see all the shapes from the toolbar"+
				" Do they appear consistent with the text?"));
		
		IssueLog.showMessage("You can right click on the tools to switch to some of the more interesting shapes, try it");
		IJ.wait(5000);
		
		assert(FileChoiceUtil.yesOrNo("Were you able to switch tools?"));
		
		i.closeWindowButKeepObjects();
	}
	
	

}
