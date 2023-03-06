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

import java.io.File;
import java.io.IOException;

import applicationAdapters.DisplayedImage;
import exportMenus.FlatCreator;
import exportMenus.PNGQuickExport;
import ultilInputOutput.FileChoiceUtil;
import imageDisplayApp.StandardWorksheet;
import logging.IssueLog;
import messages.ShowMessage;
import imageDisplayApp.ImageDisplayIO;

/**a menu item used for testing purposes. */
public class SaveEachObjectTest  extends BasicMenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		
		if (diw.getImageAsWorksheet() instanceof StandardWorksheet) {
			StandardWorksheet theSet=(StandardWorksheet) diw.getImageAsWorksheet();
			File f=FileChoiceUtil.getSaveFile(theSet.getSavePath(), theSet.getSaveName());
		if (f==null) return;
		
		if (theSet.getTitle().equals(f.getName())) {} else {
			theSet.setTitle(f.getName());
				
		}
		
		/**performs update so new name can appear on window*/
		diw.updateWindowSize();
		
		/**does the actual saving*/
		ImageDisplayIO.writeToFile(f, theSet);
		
		
		
		ShowMessage.showOptionalMessage("You have saved", true, "You haved stored your work", "files saved with earlier versions of QuickFigures will not always be openable", "it is also helpful to export files to a stable format");
		
		savePreview(diw, f);
		}
			
		
	}

	/**saves a preview to accompany the saved worksheets
	 * @param diw
	 * @param f
	 * @throws IOException
	 */
	protected void savePreview(DisplayedImage diw, File f)  {
		try {
			String aPath = f.getAbsolutePath();
			aPath=aPath.replace(".ser", "");
			aPath = aPath + " preview.png";
			new PNGQuickExport(false).writePNGFile(diw, aPath, new FlatCreator());
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	}
	
	@Override
	public String getCommand() {
		return "saveDisplaySet";
	}

	@Override
	public String getNameText() {
		return "Save and reopen";
	}

	@Override
	public String getMenuPath() {
		return "Help<Save<";
	}

}
