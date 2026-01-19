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

import java.awt.Color;
import java.io.File;

import javax.swing.Icon;

import ultilInputOutput.FileChoiceUtil;
import applicationAdapters.DisplayedImage;
import iconGraphicalObjects.FolderIconGraphic;
import imageDisplayApp.ImageDisplayIO;

/**Opens a saved file with figures*/
public class OpenerSavedWorkSheet  extends BasicMenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		File f=FileChoiceUtil.getOpenFile();
	
		ImageDisplayIO.showFile(f);
			
		
	}
	


	@Override
	public String getCommand() {
		return "openDisplaySet";
	}

	@Override
	public String getNameText() {
		return "Saved Worksheet";
	}

	@Override
	public String getMenuPath() {
		return "File<Open<";
	}
	
	/**The icon for the menu that contains this item*/
	public Icon getSuperMenuIcon() {
		return FolderIconGraphic.createAnIcon( new Color(120,120,180), true);
	}
	

}
