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
 * Version: 2022.2
 */
package exportMenus;

import java.io.IOException;

import appContextforIJ1.ImageDisplayTester;
import basicMenusForApp.BasicMenuItemForObj;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import imageDisplayApp.ImageWindowAndDisplaySet;
import layout.basicFigure.BasicLayout;
import logging.IssueLog;
import testing.TestExample;

/**a class that displays a layout with row labels on the right. for testing only*/
 class ShowManualLayoutTest extends BasicMenuItemForObj {
	
	
	
	static TestExample testCase=TestExample._FIGURE;//which cases to test. set to null if all should be tested




	/**
	 * @throws Exception
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception, IOException {
		ImageDisplayTester.startToolbars(true);
		IssueLog.sytemprint=true;
		
		ImageWindowAndDisplaySet diw = ImageWindowAndDisplaySet.createAndShowNew("New Image", 400, 300);
		BasicLayout layout = new BasicLayout(3, 2, 100, 80, 3, 2, true);
		
		DefaultLayoutGraphic roi = new DefaultLayoutGraphic(layout);
		
		roi.rowLabelsOnRight=true;
		diw.getTheSet().addItemToImage(roi);
		
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
