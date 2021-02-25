/**
 * Author: Greg Mazo
 * Date Modified: Dec 5, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package graphicalObjects_BasicShapes;

import org.junit.jupiter.api.Test;

import appContextforIJ1.ImageDisplayTester;
import logging.IssueLog;
import messages.ShowMessage;
import testing.TestShapes;
import testing.TestingOptions;
import ultilInputOutput.FileChoiceUtil;

/**
 This test simply helps a user to visually examine the appearance of the 
 arrows to determine if they appear as intended.
 Was used during editing of code for arrows
 */
class ArrowGraphicTest {

	@Test//test does not need to be regularly performed
	void test() {
		ImageDisplayTester.showInnitial();
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		
		ShowMessage.showMessages("A window with examples of every arrow head and arrow tail style will appear. for manual testing. Tester can examine arrows");
		
		 TestShapes.createExample( TestShapes.MANY_ARROWS);
		
		
		
		IssueLog.waitSeconds(TestingOptions.waitTimeAfterTests);
		assert(FileChoiceUtil.yesOrNo(
				"Please examine arrows manually to see if the arrow heads are drawn. Do they look ok? If not, click no and test will fail"));
		
		
		
	}

}
