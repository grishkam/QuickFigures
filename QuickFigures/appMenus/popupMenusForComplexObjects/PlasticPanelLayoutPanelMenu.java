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
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;

import fLexibleUIKit.ObjectAction;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_LayoutObjects.PlasticPanelLayoutGraphic;
import layout.plasticPanels.AddPanelSizeDefiningItemMenu;
import menuUtil.IndexChoiceMenu;
import menuUtil.SmartJMenu;
import objectDialogs.SpacedPanelLayoutBorder;
import undo.AbstractUndoableEdit2;

public class PlasticPanelLayoutPanelMenu extends AttachedItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlasticPanelLayoutPanelMenu(PlasticPanelLayoutGraphic c) {

		add(addCreatePanelMenu(c));
		
		super.setLockedItem(c);
		super.addLockedItemMenus();
		
		//add(createPanelSizeDefSubMenu(c));
		
		
	}
	
	JMenu createPanelSizeDefSubMenu(PanelLayoutGraphic c) {
		JMenu psize = new JMenu("Panel Size Definers");
		AddPanelSizeDefiningItemMenu def = new AddPanelSizeDefiningItemMenu(c, c.getPanelSizeDefiningItems());
		def.setRemove(true);
		psize.add(new AddPanelSizeDefiningItemMenu(c, c.getLockedItems()));
		psize.add(def);
		return psize;
		
		
	}
	
	JMenu addCreatePanelMenu(PlasticPanelLayoutGraphic c) {
		JMenu panelsmen = new SmartJMenu("Panels");
		panelsmen.	add(new ObjectAction<PlasticPanelLayoutGraphic>(c) {
					@Override
					public AbstractUndoableEdit2 performAction() {item.repack();item.repack();item.repack(); item.updateDisplay();
					return null;}	
			}.createJMenuItem("Repack Panels"));
		
		panelsmen.add(new ObjectAction<PlasticPanelLayoutGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {SpacedPanelLayoutBorder dialog = new SpacedPanelLayoutBorder(item);  dialog.showDialog();
			return null; }	
	}.createJMenuItem("Set Borders"));
		panelsmen.add(new ObjectAction<PlasticPanelLayoutGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {item.getPanelLayout().addNewPanel(); item.updateDisplay();
			return null;}	
	}.createJMenuItem("Add Panel"));
		try {
			panelsmen.add(new RemovalPanelMenu(c));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		panelsmen.	add(new ObjectAction<PlasticPanelLayoutGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {item.getPanelLayout().sortLeftToRightAboveToBottom(); item.updateDisplay();
			return null;}	
	}.createJMenuItem("Resort Panels"));
		
		return panelsmen ;
	}
	
	/**a menu item to remove a panel*/
	public class RemovalPanelMenu extends IndexChoiceMenu<PlasticPanelLayoutGraphic> {

		public RemovalPanelMenu(PlasticPanelLayoutGraphic o) {
			super(o, "Remove Panel", 1, o.getPanelLayout().nPanels());

		}
		
		public void performAction(PlasticPanelLayoutGraphic t, int i) {
			t.getPanelLayout().removePanel(i);
			t.updateDisplay();
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
	}
	
}
