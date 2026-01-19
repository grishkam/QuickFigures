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
 * Version: 2023.2
 */
package basicMenusForApp;

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;

import applicationAdapters.DisplayedImage;
import exportMenus.FlatCreator;
import exportMenus.PNGQuickExport;
import iconGraphicalObjects.SaveIcon;
import ultilInputOutput.FileChoiceUtil;
import imageDisplayApp.StandardWorksheet;
import logging.IssueLog;
import messages.ShowMessage;
import imageDisplayApp.ImageDisplayIO;

/**implements a menu item for saving a worksheet*/
public class SaveCurrentWorkSheet  extends BasicMenuItemForObj {

	private boolean testingMode=IssueLog.sytemprint;//set to true during developer troubleshooting

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		File f=null;
		
		saveWorksheetAs(diw, f);
			
		
	}

	/**
	 * @param diw
	 * @param f
	 */
	public void saveWorksheetAs(DisplayedImage diw, File f) {
		if (diw.getImageAsWorksheet() instanceof StandardWorksheet) {
			StandardWorksheet theSet=(StandardWorksheet) diw.getImageAsWorksheet();
			
			if(f==null)
			f=FileChoiceUtil.getSaveFile(theSet.getSavePath(), theSet.getSaveName());
		
			if (f==null) return;
		
		if (theSet.getTitle().equals(f.getName())) {} else {
			theSet.setTitle(f.getName());
				
		}
		
		/**performs update so new name can appear on window*/
		diw.updateWindowSize();
		
		/**does the actual saving*/
		boolean outcome = ImageDisplayIO.writeToFile(f, theSet);
		if(!outcome) 
			{
			IssueLog.log("first save attempt failed: will try again");
			outcome = ImageDisplayIO.writeToFile(f, theSet);//if fails the first time, will try again. not sure exactly why some save attempts fail. It is always one non serializable object but that does not explain wh
			if (outcome)
				ShowMessage.showOptionalMessage("QuickFigures required two attempts before sucessful save ", true, "sorry for the wait: QuickFigures required two attempts before sucessful save ");
			
			}
		if(outcome) {
			ShowMessage.showOptionalMessage("You have saved", true, "You haved stored your work", "files saved with earlier versions of QuickFigures will not always be openable", "it is also helpful to export files to a stable format");
			savePreview(diw, f);
		}else {
			ShowMessage.showOptionalMessage("You have tried to save", false, "The worksheet failed to save", "you should try again", "if problem persists, feel free to report issue to developer" );
			String f2 = f.getAbsolutePath()+"test_save";
			if(testingMode)
				ImageDisplayIO.testWriteObjects(theSet.getTopLevelLayer().getAllGraphics(), new File(f2));
		}
		
		
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
		return "Current Worksheet";
	}

	@Override
	public String getMenuPath() {
		return "File<Save<";
	}
	
	public Icon getSuperMenuIcon() {
		return SaveIcon.createIcon();
	}

}
