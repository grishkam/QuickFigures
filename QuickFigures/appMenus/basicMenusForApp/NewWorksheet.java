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
 * Date Modified: Jan 4, 2021
 * Version: 2022.0
 */
package basicMenusForApp;

import appContext.ImageDPIHandler;
import applicationAdapters.DisplayedImage;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasDialogResize;

/**A menu item that creates a new worksheet for the new menu*/
public class NewWorksheet  extends BasicMenuItemForObj   {

	/**The width for 8 inches*/
	int width=8*ImageDPIHandler.getInchDefinition();
	
	/**The height for 10 inches*/
	int height=10*ImageDPIHandler.getInchDefinition();
	
	@Override
	public String getMenuPath() {
		return "File<New";
	}
	
	@Override
	public String getNameText() {
		return "Empty Worksheet"+typeString();
	}
	
	String typeString() {
		return "";
	}
	

	/**Creates a new worksheet*/
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		diw=ImageWindowAndDisplaySet.createAndShowNew("New Workseet", width, height);
		CanvasDialogResize cdr = new CanvasDialogResize(false);
		cdr.includePositionBox=false;
		diw.getImageAsWorksheet().setAllowAutoResize(false);
		cdr.performActionDisplayedImageWrapper(diw);
	}

	@Override
	public String getCommand() {
		return "newCanvas"+typeString();
	}

}
