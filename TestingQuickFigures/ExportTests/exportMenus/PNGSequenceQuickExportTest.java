/**
 * Author: Greg Mazo
 * Date Modified: Jan 1, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package exportMenus;

import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;
import testing.FigureTester;
import testing.TestExample;
import testing.TestShapes;

/**
 
 * 
 */
public class PNGSequenceQuickExportTest {

	//commented out so that this test is nto run every time that I choose to run all tests
	//
	//@Test
	public void test() {
		FigureTester.setup();
		 ImageWindowAndDisplaySet i = TestShapes.createExample( TestExample.DIVERSE_SHAPES);
		 i.getTheSet().getLocatedObjects().get(0);
		 i.setEndFrame(400);
		 new PNGSequenceQuickExport().performActionDisplayedImageWrapper(i);
		 
		 IssueLog.waitSeconds(40);
		 
	}

}
