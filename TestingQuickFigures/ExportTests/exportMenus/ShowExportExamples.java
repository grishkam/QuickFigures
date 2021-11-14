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
 * Date Modified: Mar 28, 2021
 * Version: 2021.2
 */
package exportMenus;

import java.io.IOException;
import java.util.ArrayList;

import appContextforIJ1.ImageDisplayTester;
import basicMenusForApp.BasicMenuItemForObj;
import logging.IssueLog;
import testing.FigureTester;
import testing.TestExample;
import testing.TestProvider;

/**Shows the example figures that are used for the export tests*/
 class ShowExportExamples extends BasicMenuItemForObj {
	
	
	
	static TestExample testCase=TestExample._FIGURE;//which cases to test. set to null if all should be tested




	/**
	 * @throws Exception
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception, IOException {
		ImageDisplayTester.startToolbars(true);
		IssueLog.sytemprint=true;
		ArrayList<TestProvider> testsCases = TestProvider.getTestProviderListWithfigures();
		for(TestProvider ex: testsCases) {
			if (testCase==null||testCase==ex.getType())
			ex.createExample();
			
				}
		
		
		/**pauses long enough for the user to test out */
		IssueLog.waitSeconds(215);
		FigureTester.closeAllWindows();
	}


	@Override
	public String getNameText() {
		return "Test Illustrator Export";
	}

	@Override
	public String getMenuPath() {
		return "File<Test Export";
	}

	

	

}
