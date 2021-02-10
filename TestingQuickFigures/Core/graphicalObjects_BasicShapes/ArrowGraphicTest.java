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
import ij.IJ;
import logging.IssueLog;
import messages.ShowMessage;
import testing.TestShapes;
import ultilInputOutput.FileChoiceUtil;

/**
 
 * 
 */
class ArrowGraphicTest {

	@Test
	void test() {
		ImageDisplayTester.showInnitial();
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		 TestShapes.createExample( TestShapes.MANY_ARROWS);
		
		
		ShowMessage.showMessages("A window with examples of every arrow head and arrow tail style will appear. for manual testing. Tester can examine arrows");
		
		IJ.wait(10000);
		assert(FileChoiceUtil.yesOrNo(
				"Please examine arrows manually to see if the arrow heads are drawn. Do they look ok? If not, click no and test will fail"));
		
		
		
	}

}
