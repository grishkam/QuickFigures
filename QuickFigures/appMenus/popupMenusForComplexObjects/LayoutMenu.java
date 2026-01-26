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
 * Date Modified: Jan 25, 2026
 * Version: 2026.1
 */
package popupMenusForComplexObjects;

import java.awt.Color;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import fLexibleUIKit.ObjectAction;
import genericMontageUIKitMenuItems.LayoutEditCommandMenu;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import locatedObject.AttachmentPosition;
import menuUtil.SmartPopupJMenu;
import menuUtil.HasUniquePopupMenu;
import undo.AbstractUndoableEdit2;
import undo.UndoAddOrRemoveAttachedItem;
import undo.UndoManagerPlus;

/**A menu for the default layouts */
public class LayoutMenu extends AttachedItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LayoutEditCommandMenu editmenu;
	private DefaultLayoutGraphic the_layout;

	public LayoutMenu(DefaultLayoutGraphic c) {

		the_layout=c;
	
		c.generateCurrentImageWrapper();
		  editmenu = new LayoutEditCommandMenu(c);
	
		add(editmenu.getInclusiveList());
		
		super.setLockedItem(c);
		JMenu lock_menu = super.addLockedItemMenus();
		
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
		
		
		/***/
		lock_menu.add(new AddTextToClickedLayer("New Title Label", c) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void afterAddition() {
				addition.setAttachmentPosition(AttachmentPosition.defaultMontageTitle());
				the_layout.addLockedItem(addition);
				the_undo.addEditToList(new UndoAddOrRemoveAttachedItem(the_layout, addition, false));
				addition.setFontSize(20);
				addition.setTextColor(Color.black);
			}
		});
		
		/***/
		add(new AddTextToClickedLayer("Add Text", c) );
		
		
		
	
	}
	
	

	
	

	
	
	public void setUndoManager(UndoManagerPlus u) {
		super.setUndoManager(u);
		if (editmenu!=null) editmenu.setUndoManager(u);
	}
	
	
}
