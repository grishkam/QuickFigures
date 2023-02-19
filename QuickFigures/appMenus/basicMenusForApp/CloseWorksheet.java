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
 * Date Modified: Jan 6, 2021
 * Version: 2023.1
 */
package basicMenusForApp;

import applicationAdapters.DisplayedImage;
import imageDisplayApp.GraphicSetDisplayWindow;

/**this class implements a menu item for closing the figure. 
  the command will also close the layers window.*/
public class CloseWorksheet  extends BasicMenuItemForObj {

	/**whether the user should be asked to save the worksheet*/
	boolean save=false;
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (diw.getWindow() instanceof GraphicSetDisplayWindow) {
			GraphicSetDisplayWindow theSet=(GraphicSetDisplayWindow) diw.getWindow();
			theSet.closeGroupAndSupportingWindows(save);
				
		}
		
		
		}
			
		
	@Override
	public String getCommand() {
		return "closeDisplaySetAndSupporting";
	}

	@Override
	public String getNameText() {
		return "Close Group";
	}

	@Override
	public String getMenuPath() {
		return "File<";
	}

}
