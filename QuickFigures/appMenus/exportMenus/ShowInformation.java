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
 * Date Modified: April 10, 2021
 * Version: 2022.0
 */
package exportMenus;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import messages.ShowMessage;

/**Implements a menu item that only shows the user a message. 
 * An class that only shows information*/
public class ShowInformation  extends BasicMenuItemForObj {

	

	private String name;
	public String path = "File<Export";
	private String message=null;
	
	
	public ShowInformation(String name, String dialogMessage) {
		this.name=name;
		this.message=dialogMessage;
	}

	@Override
	public String getMenuPath() {
		
		return path;
	}
	
	
	/**name for the meny item*/
	public String getNameText() {
		return name;
	}
	
	/**Performs a task specific to the menu item. */
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (message!=null)
			ShowMessage.showMessages(message);
	}
	
	
	
}
