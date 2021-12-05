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
 * Version: 2021.2
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import fLexibleUIKit.ObjectAction;
import genericMontageUIKitMenuItems.LayoutEditCommandMenu;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import menuUtil.SmartPopupJMenu;
import menuUtil.HasUniquePopupMenu;
import undo.AbstractUndoableEdit2;
import undo.UndoManagerPlus;

/**A menu for the default layouts */
public class LayoutMenu extends AttachedItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LayoutEditCommandMenu editmenu;

	public LayoutMenu(DefaultLayoutGraphic c) {

		
	
		c.generateCurrentImageWrapper();
		  editmenu = new LayoutEditCommandMenu(c);
	
		add(editmenu.getInclusiveList());
		
		super.setLockedItem(c);
		super.addLockedItemMenus();
		
		GraphicLayer par = c.getParentLayer();
		if (par instanceof HasUniquePopupMenu) try {
			JPopupMenu jp = ((HasUniquePopupMenu) par).getMenuSupplier().getJPopup();
			if (jp instanceof SmartPopupJMenu) {
				JMenu menuadded = ((SmartPopupJMenu) jp).extractToMenu("Figure");
				add(menuadded, 0);
				
			}
		} catch (Throwable t) {}
		
		add(new ObjectAction<DefaultLayoutGraphic>(c) {
			@Override
			public AbstractUndoableEdit2  performAction() {
				item.showOptionsDialog();
				return null;
			}	
	}.createJMenuItem("Other Options"));
		
	
	}
	
	

	
	

	
	
	public void setUndoManager(UndoManagerPlus u) {
		super.setUndoManager(u);
		if (editmenu!=null) editmenu.setUndoManager(u);
	}
	
	
}
