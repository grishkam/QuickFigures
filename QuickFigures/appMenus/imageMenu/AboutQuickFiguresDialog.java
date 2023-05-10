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
 * Date Created: May 14, 2022
 * Date Modified: May 14, 2022
 * Version: 2023.2
 * 
 */
package imageMenu;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import standardDialog.StandardDialog;
import standardDialog.strings.InfoDisplayPanel;

/**This class shows an information window for QuickFigures*/
public class AboutQuickFiguresDialog extends BasicMenuItemForObj {




/**
	 * 
	 */
	public static final String PUBLICATION_DOI = "https://doi.org/10.1371/journal.pone.0240280", 
			USER_GUIDE = "https://github.com/grishkam/QuickFigures/blob/master/UserGuide/User%20Guide.md";
public AboutQuickFiguresDialog() {
	
}


public void performActionDisplayedImageWrapper(DisplayedImage diw) {
	
		StandardDialog storedValueDilaog = new StandardDialog("QuickFigures");
		storedValueDilaog .add("Info", new InfoDisplayPanel("QuickFigures was created by ", " Gregory Mazo"));
		storedValueDilaog .add("Info", new InfoDisplayPanel("You are using ", " Version: 2023.2"));
		storedValueDilaog .add("Info", new InfoDisplayPanel("", " the code is open source and available on github "));
		storedValueDilaog .add("Info", new InfoDisplayPanel("User Guide", USER_GUIDE));
		storedValueDilaog .add("Info", new InfoDisplayPanel("Publication", PUBLICATION_DOI));
		storedValueDilaog.setWindowCentered(true);
		storedValueDilaog.showDialog();
		
}

/***/
public String getCommand() {return "version";}
public String getNameText() {return "About QuickFigures";}
public String getMenuPath() {return "Help";}





}