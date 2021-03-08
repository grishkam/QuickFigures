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
 * Version: 2021.1
 */
package exportMenus;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import illustratorScripts.AdobeScriptMaker;
import illustratorScripts.ZIllustratorScriptGenerator;
import messages.ShowMessage;

/**A menu item for generating an a script that con run in Adobe Illustrator*/
public class ExportIllustrator extends BasicMenuItemForObj {
	AdobeScriptMaker sm=	new AdobeScriptMaker();

	public ExportIllustrator() {
	}

	
	
	
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (diw!=null) {
			diw.updateDisplay();
			ShowMessage.showOptionalMessage("Illustrator script creator ", false, "Illustrator script generator will create a .jsx file", "open that file with Adobe Illustrator" , "always wait for one .jsx script to finish before another",  "For alternative (with better results for text and shapes), export as SVG first and open with Illustrator");
		sm.sendWrapperToills(diw.getImageAsWrapper().getAsWrapper(), true);
		 ZIllustratorScriptGenerator.instance.execute();
			
		}
	}
	

	@Override
	public String getNameText() {
		
		return "Create and run Illustrator Script (.jsx)";
	}




	@Override
	public String getMenuPath() {
		return "File<Export";
	}
	
}
