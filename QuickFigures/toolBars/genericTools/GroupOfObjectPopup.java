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
 * Date Modified: Jan 5, 2021
 * Version: 2022.0
 */
package genericTools;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import menuUtil.SmartPopupJMenu;
import selectedItemMenus.LayerSelectionSystem;
import selectedItemMenus.SelectionOperationsMenu;

/**A popup menu that appears when the user right clicks on a group of objects*/
public class GroupOfObjectPopup extends SmartPopupJMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LayerSelectionSystem selectedItems=null;
	private SelectionOperationsMenu allitem;

	public GroupOfObjectPopup(LayerSelectionSystem allSelectedRois) {
		selectedItems=allSelectedRois;
		allitem= SelectionOperationsMenu.getStandardMenu(selectedItems);
		allitem.setText("All Selected Items");
		this.add(allitem);
		}
	public  SelectionOperationsMenu getAllItemMenu() {
		allitem.setText("All clicked Items");
		return allitem;
	}
	
	/**Adds the items from the given j menu to this popup menu as a submenu*/
	public void addItemsFromJMenu(JPopupMenu j, String name) {
		if (j==null) return;
		MenuElement[] elis = j.getSubElements();
		JMenu j2 = new JMenu(name);
		for(MenuElement item: elis) {
			if (item instanceof JMenuItem) {
				j2.add((JMenuItem) item);
			}
			this.add(j2);
		};
	}


	

}
