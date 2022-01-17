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
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.LocatedObject2D;
import locatedObject.AttachedItemList;
import menuUtil.SmartPopupJMenu;
import menuUtil.PopupMenuSupplier;
import undo.CombinedEdit;
import undo.UndoAddOrRemoveAttachedItem;
import utilityClasses1.ArraySorter;

/**A popup menu for a scale bar*/
public class BarGraphicMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier  {

	/**
	 * 
	 */
	
	static final String OPTIONS_DIALOG="Options", HIDE_TEXT="Hide Text", SHOW_TEXT="Show Text";//, backGroundShap="Outline Shape";
	
	BarGraphic barG;
	public BarGraphicMenu(BarGraphic textG) {
		super();
		this.barG = textG;
		add(createItem(OPTIONS_DIALOG));
		TextGraphicMenu textGraphicMenu = new TextGraphicMenu(textG.getBarText());		
		JMenu jMenu = textGraphicMenu.getJMenu("Bar Text");
		if (barG.isShowText())
			jMenu.add(createItem(HIDE_TEXT));
		else 
			jMenu.add(createItem(SHOW_TEXT));
		add(jMenu);
		add(new SwitchBarToOtherPanelMenu());
		
	}
	
	public JMenuItem createItem(String st) {
		JMenuItem o=new JMenuItem(st);
		o.addActionListener(this);
		o.setActionCommand(st);
		
		return o;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public JPopupMenu getJPopup() {
		return this;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String com=arg0.getActionCommand();
		
		if (com.equals(OPTIONS_DIALOG)) {
			barG.showOptionsDialog();
		}
		else {
		
			
				if (com.equals(HIDE_TEXT)) {
					barG.setShowText(false);
				}
				if (com.equals(SHOW_TEXT)) {
					barG.setShowText(true);
				}
		
		}
		
	}
	
	/**a menu that switches the bar location to another panel*/
	class SwitchBarToOtherPanelMenu extends SelectItemJMenu {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		SwitchBarToOtherPanelMenu() {
			super("Switch Panels");
			ArrayList<ZoomableGraphic> localItems = barG.getParentLayer().getAllGraphics();
			ArraySorter.removeThoseNotOfClass(localItems, ImagePanelGraphic.class);//a bar graphic can only be attached to an image panel
			createMenuItemsForList2(localItems);
		}
		
		/**Adds bar to selected ImagePanel
		 * @return */
		@Override
		public AbstractUndoableEdit performAction(LocatedObject2D target) {
			ImagePanelGraphic image=(ImagePanelGraphic) target;
			CombinedEdit output = new CombinedEdit();
			
			AttachedItemList.removeFromAlltakers( barG, barG.getParentLayer().getTopLevelParentLayer().getAllGraphics(), output);
			
			UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(image, barG, false);
	
			image.addLockedItem(barG);//does the deed
			
			undo.establishFinalState();
			output.addEditToList(undo);
			image.updateDisplay();
			return output;
		}
		}
	
}
