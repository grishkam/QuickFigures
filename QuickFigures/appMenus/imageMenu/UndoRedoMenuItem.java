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
 * Version: 2023.1
 */
package imageMenu;


import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import logging.IssueLog;

/**The Undo/Redo menu item in the Edit meny*/
public class UndoRedoMenuItem  extends BasicMenuItemForObj {
	boolean undo=true;
	
	public UndoRedoMenuItem(boolean un) {
		undo=un;
		
		}
	


	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		
		try {
			if (undo) {diw.getUndoManager().undo();} 
			else diw.getUndoManager().redo();
		} catch (Exception e) {
			IssueLog.log("Cannot undo/redo "+diw.getUndoManager().getLastEditFromList());
			
		} 
		
		diw.updateDisplay();
	}

	@Override
	public String getCommand() {
		if (IssueLog.isWindows())  {
			if (!undo) return "Redo				 (ctrl+Y)";
			return "Undo				 (ctrl+Z)";
			
		}
		if (!undo) return "Redo				 \u2318Y";
		return "Undo				 \u2318Z";
	}

	@Override
	public String getNameText() {
		return getCommand();
	}

	@Override
	public String getMenuPath() {
		return "Edit";
	}

}
