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
package fileread;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import dataTableDialogs.SmartDataInputDialog;

public class ShowTable extends BasicMenuItemForObj {

	int type=0;
	
	public ShowTable(int t) {
		type=t;
	}
	
	
	
	@Override
	public String getNameText() {
		if (type==1) return "Open text file as Data Table";
		return "New Data Table";
	}


	
	public static void main(String[] args) {
		new ShowTable(1).performActionDisplayedImageWrapper(null);;
	}



	@Override
	public String getMenuPath() {
		return "Plots";
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (type==1) {SmartDataInputDialog.showTableFromUserFile(false);}
		else
		SmartDataInputDialog.createDialog(0).showDialog();;
	}
}
