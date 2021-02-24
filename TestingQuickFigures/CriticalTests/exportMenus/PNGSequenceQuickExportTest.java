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

import org.junit.Test;

import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;
import testing.FigureTester;
import testing.TestShapes;

/**
 
 * 
 */
public class PNGSequenceQuickExportTest {

	@Test
	public void test() {
		FigureTester.setup();
		 ImageWindowAndDisplaySet i = TestShapes.createExample( TestShapes.DIVERSE_SHAPES);
		 i.getTheSet().getLocatedObjects().get(0);
		 i.setEndFrame(400);
		 new PNGSequenceQuickExport().performActionDisplayedImageWrapper(i);
		 
		 IssueLog.waitSeconds(40);
		 
	}

}
