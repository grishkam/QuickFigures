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
package exportMenus;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import illustratorScripts.AdobeScriptMaker;
import illustratorScripts.ZIllustratorScriptGenerator;

public class ExportIllustrator extends BasicMenuItemForObj {
	AdobeScriptMaker sm=	new AdobeScriptMaker();

	public ExportIllustrator() {
	}

	
	
	
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (diw!=null) {
			
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
