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
 * Version: 2023.1
 * 
 */
package imageMenu;

import java.awt.Desktop;
import java.net.URI;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import logging.IssueLog;

/**This class shows an information window for QuickFigures*/
public class HelpfulLink extends BasicMenuItemForObj {


private URI link;
private String menuText;
public HelpfulLink(String link1, String menuText) {
	this.menuText=menuText;
	try {
	this.link=new URI(link1);
} catch (Exception e) {
	IssueLog.logT(e);
}
}


public void performActionDisplayedImageWrapper(DisplayedImage diw) {
	
	
		
		try {
			
			Desktop.getDesktop().browse(link);
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		
}

/***/
public String getCommand() {return menuText;}
public String getNameText() {return menuText;}
public String getMenuPath() {return "Help";}





}