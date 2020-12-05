/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package basicMenusForApp;

import appContext.ImageDPIHandler;
import applicationAdapters.DisplayedImage;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasDialogResize;

public class NewCanvasDialog  extends BasicMenuItemForObj   {

	int width=8*ImageDPIHandler.getStandardDPI();
	int height=10*ImageDPIHandler.getStandardDPI();
	
	@Override
	public String getMenuPath() {
		return "File<New";
	}
	
	@Override
	public String getNameText() {
		return "Empty Figure Display"+typeString();
	}
	
	String typeString() {
		
		return "";
	}
	

	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		diw=ImageWindowAndDisplaySet.createAndShowNew("New Image", width, height);
		CanvasDialogResize cdr = new CanvasDialogResize();
		cdr.fancy=false;
		new CanvasDialogResize().performActionDisplayedImageWrapper(diw);
	}

	@Override
	public String getCommand() {
		return "newCanvas"+typeString();
	}

}
