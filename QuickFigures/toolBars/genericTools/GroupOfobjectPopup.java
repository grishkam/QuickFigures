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
package genericTools;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import menuUtil.SmartPopupJMenu;
import selectedItemMenus.LayerSelector;
import selectedItemMenus.SelectionOperationsMenu;

public class GroupOfobjectPopup extends SmartPopupJMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LayerSelector groupd=null;
	private SelectionOperationsMenu allitem;

	public GroupOfobjectPopup(LayerSelector allSelectedRois) {
		groupd=allSelectedRois;
		allitem= SelectionOperationsMenu.getStandardMenu(groupd);
		allitem.setText("All Selected Items");
		this.add(allitem);
		}
	public  SelectionOperationsMenu getAllItemMenu() {
		allitem.setText("All clicked Items");
		return allitem;
	}
	
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