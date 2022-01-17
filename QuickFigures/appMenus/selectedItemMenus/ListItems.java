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
 * Version: 2022.0
 */
package selectedItemMenus;

import graphicalObjects.ZoomableGraphic;
import logging.IssueLog;

/**prints a list of all selected items to the issue log
 * Used during testing but not helpful to users*/
public class ListItems extends BasicMultiSelectionOperator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
		for(ZoomableGraphic item: array) {
			if (item==null) continue;
			actioinOnSelected(item);
		}
		
	}
	
	@Override
	public String getMenuCommand() {
		return "List Selected Items";
	}
	
	/**Prints out the item selected. Used for debuging*/
	public void actioinOnSelected(ZoomableGraphic selectedItem) {
		IssueLog.log(selectedItem.toString());
		
	}
	
	@Override
	public String getMenuPath() {
		return "Item";
	}
	


}
