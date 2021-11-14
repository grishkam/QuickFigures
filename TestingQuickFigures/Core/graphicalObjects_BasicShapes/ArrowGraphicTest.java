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
package graphicalObjects_BasicShapes;

import appContextforIJ1.ImageDisplayTester;
import logging.IssueLog;
import messages.ShowMessage;
import testing.TestExample;
import testing.TestShapes;
import testing.TestingOptions;
import ultilInputOutput.FileChoiceUtil;

/**
 This test simply helps a user to visually examine the appearance of the 
 arrows to determine if they appear as intended.
 Was used during editing of code for arrows
 */
class ArrowGraphicTest {

	//@Test//test does not need to be regularly performed. user will see the same arrows 
	void test() {
		ImageDisplayTester.showInnitial();
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		
		ShowMessage.showMessages("A window with examples of every arrow head and arrow tail style will appear. for manual testing. Tester can examine arrows");
		
		 TestShapes.createExample( TestExample.MANY_ARROWS);
		
		
		
		IssueLog.waitSeconds(TestingOptions.waitTimeAfterTests);
		assert(FileChoiceUtil.yesOrNo(
				"Please examine arrows manually to see if the arrow heads are drawn. Do they look ok? If not, click no and test will fail"));
		
		
		
	}

}
