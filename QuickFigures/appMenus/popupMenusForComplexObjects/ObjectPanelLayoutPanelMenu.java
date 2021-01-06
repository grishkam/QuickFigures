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
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import fLexibleUIKit.ObjectAction;
import graphicalObjects_LayoutObjects.SpacedPanelLayoutGraphic;
import objectDialogs.SpacedPanelLayoutBorder;

public class ObjectPanelLayoutPanelMenu extends AttachedItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObjectPanelLayoutPanelMenu(SpacedPanelLayoutGraphic c) {
		add(new ObjectAction<SpacedPanelLayoutGraphic>(c) {
					@Override
					public void actionPerformed(ActionEvent arg0) {item.repack();item.repack();item.repack(); item.updateDisplay();}	
			}.createJMenuItem("Repack Panels"));
		
		add(new ObjectAction<SpacedPanelLayoutGraphic>(c) {
			@Override
			public void actionPerformed(ActionEvent arg0) {SpacedPanelLayoutBorder dialog = new SpacedPanelLayoutBorder(item);  dialog.showDialog(); }	
	}.createJMenuItem("Set Borders"));
		
		
		super.setLockedItem(c);
		super.addLockedItemMenus();
		
		
		
	}
	
}
