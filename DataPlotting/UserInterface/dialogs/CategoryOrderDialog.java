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
 * Date Modified: Jan 7, 2021
 * Version: 2021.1
 */
package dialogs;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.HashMap;

import advancedChannelUseGUI.OrderSelectionJList;
import standardDialog.StandardDialog;

/**A dialog for chaning the order of objects in a list*/
public class CategoryOrderDialog extends StandardDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	OrderSelectionJList<String> usedList;
	
	public CategoryOrderDialog(HashMap<Double, String> locationMap, Iterable<String> otherItems) {
		super("Change order", true);
		ArrayList<String> items=new ArrayList<String>();
		
		for(Double d: locationMap.keySet()) {
			items.add(locationMap.get(d));
		}
		
		
		usedList=new OrderSelectionJList<String>(items, null, otherItems);
		this.getMainPanel().add(usedList, this.getCurrentConstraints());
		GridBagConstraints cons2 = getCurrentConstraints();
		cons2.gridy=8;
		getMainPanel().add(usedList.createButtonPanel(), cons2);
	}
	
	public ArrayList<String> getNewOrder() {
		return usedList.getNewOrder();
	}
	
	public ArrayList<String> getRemovedItems() {
		return usedList.getRemovedItems();
	}
	
	public ArrayList<String> getNewlyAddedItems() {
		return usedList.getNewlyAddedItems();
	}
	
}
