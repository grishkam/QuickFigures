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
 * Date Modified: April 28, 2023
 * Date Modified: April 28, 2023
 * Version: 2023.1
 */
package exportMenus;


import java.io.File;
import java.io.IOException;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.SaveCurrentWorkSheet;
import ultilInputOutput.FileChoiceUtil;

/**this supports a menu item that exports a figure as PNG file*/
public class SerFileQuickExport extends QuickExport {
	public static boolean showDialogEverytime=true;

	/**
	 * @param openNow determines if the exported will will be opened right away
	 */
	public SerFileQuickExport(boolean openNow) {
		super(openNow);
	}

	protected String getExtension() {
		return "ser";
	}
	
	protected String getExtensionName() {
		return "Serialized Worksheets";
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		try{
		
		String newpath=getFileAndaddExtension().getAbsolutePath();
		FileChoiceUtil.overrideQuestion(new File(newpath));
		saveInPath(diw, newpath);
		
		} catch (Throwable t) {
			t.printStackTrace();
		}
	        
	}

	/** saves the image into a particular file path
	 * @param diw
	 * @param newpath
	 * @throws IOException
	 */
	public void saveInPath(DisplayedImage diw, String newpath) throws IOException {
		new SaveCurrentWorkSheet().saveWorksheetAs(diw, new File(newpath));
		
		
	}

	

	@Override
	public String getCommand() {
		return "Export as Ser";
	}

	@Override
	public String getNameText() {
		return "Serialized Worksheet (.ser)";
	}
	

	
	
	
}
