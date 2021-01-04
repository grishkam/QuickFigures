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
package basicMenusForApp;

import java.io.File;
import applicationAdapters.DisplayedImage;
import ultilInputOutput.FileChoiceUtil;
import imageDisplayApp.StandardWorksheet;
import imageDisplayApp.ImageDisplayIO;

public class GraphicSetSaver  extends BasicMenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		
		if (diw.getImageAsWrapper() instanceof StandardWorksheet) {
			StandardWorksheet theSet=(StandardWorksheet) diw.getImageAsWrapper();
			File f=FileChoiceUtil.getSaveFile(theSet.getSavePath(), theSet.getSaveName());
		if (f==null) return;
		
		if (theSet.getTitle().equals(f.getName())) {} else {
			theSet.setTitle(f.getName());
				
		}
		
		/**performs update so new name can appear on window*/
		diw.updateWindowSize();
		
		/**does the actual saving*/
		ImageDisplayIO.writeToFile(f, theSet);
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

}
