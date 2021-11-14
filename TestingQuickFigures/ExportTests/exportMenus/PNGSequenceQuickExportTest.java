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
