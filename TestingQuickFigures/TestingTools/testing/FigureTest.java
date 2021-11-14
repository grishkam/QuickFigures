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
 * Date Modified: Feb 20, 2021
 * Version: 2021.2
 */
package testing;

import applicationAdapters.DisplayedImage;
import figureOrganizer.FigureOrganizingLayerPane;
import graphicActionToolbar.CurrentFigureSet;
import logging.IssueLog;

/**
 superclass for tests to figue
 */
public class FigureTest {
	public DisplayedImage gg;
	
	/**
	 * @return
	 */
	public FigureOrganizingLayerPane createFirstExample() {
		FigureTester.setup();
		FigureTester figureTester = new FigureTester();
		FigureTester.ignoreTemplate=true;
		FigureOrganizingLayerPane f = figureTester. createFigureFromExample1AImages();
		chooseCurrentWindow() ;
		return f;
	}
	
	/**
	 * @return
	 */
	public FigureOrganizingLayerPane createMockExample() {
		FigureTester.setup();
		FigureTester figureTester = new FigureTester();
		FigureTester.ignoreTemplate=true;
		FigureOrganizingLayerPane f = figureTester.createFigureFromMockImages();
		chooseCurrentWindow() ;
		return f;
	}
	
	/**
	 stores the current window
	 */
	public void chooseCurrentWindow() {
		gg=CurrentFigureSet.getCurrentActiveDisplayGroup();
		gg.getWindow().setLocation(10, 400);
	}
	
	/**
	 stores the current window
	 */
	public void closeCurrentWindow() {
		gg.closeWindowButKeepObjects();
	}
	
	/**
	 * 
	 */
	public void showUser() {
		gg.updateDisplay();
		IssueLog.waitSeconds(4);
	}
}
