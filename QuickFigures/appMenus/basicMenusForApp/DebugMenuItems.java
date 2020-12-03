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
package basicMenusForApp;

import javax.swing.Icon;

import applicationAdapters.DisplayedImage;
import logging.IssueLog;

/**A menu item for turning on debugging mode*/
public  class DebugMenuItems implements MenuItemForObj{

	boolean on=true;
	public  DebugMenuItems() {
		this(true);
	}
	
	public  DebugMenuItems(boolean on) {
		this.on=on;
	}
	
	@Override
	public String getMenuPath() {
	
		return "Debug";
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (on) {
		IssueLog.reportAllFail(on);
		IssueLog.reportAllEvents();
		IssueLog.log("Testing Error Logging");
		}
		
		else {
			
		}
	}

	@Override
	public String getCommand() {
	if (!on)  return "Do NOT Log Errors to Window";
		return "Log Errors to Window";
	}

	@Override
	public String getNameText() {
		if (!on)  return "Do NOT Print Errors and Events to Windows";
		return "Print Errors and Events to Windows";
	}
	
	public static void main(String[] args) {
		new DebugMenuItems().performActionDisplayedImageWrapper(null);
		
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}
