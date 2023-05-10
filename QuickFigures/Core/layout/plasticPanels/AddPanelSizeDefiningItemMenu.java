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
 * Version: 2023.2
 */
package layout.plasticPanels;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import locatedObject.LocatedObject2D;
import locatedObject.AttachedItemList;
import locatedObject.TakesAttachedItems;
import popupMenusForComplexObjects.ReleaseLockedMenu;
import undo.UndoAddOrRemoveAttachedItem;

public class AddPanelSizeDefiningItemMenu extends ReleaseLockedMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean remove=false;
	
	public AddPanelSizeDefiningItemMenu(TakesAttachedItems items, AttachedItemList list) {
		super(items, list);
	}
	
	public String getDefaultName() {
		if (remove) return "Remove Panel Size Definer";
		return "Make Panel Size Definer";
	}
	
public AbstractUndoableEdit performAction(LocatedObject2D target) {
		if (this.getLockbox() instanceof PanelLayoutGraphic) {
			
			PanelLayoutGraphic p=(PanelLayoutGraphic) getLockbox() ;
			UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(p, target, remove);
			if (remove) p.removeSizeDefiner(target); 
				else	p.addSizeDefiner(target);
			return undo;
		}
		return null;
	}

public boolean isRemove() {
	return remove;
}

public void setRemove(boolean remove) {
	this.remove = remove;
	this.setName(getDefaultName());
	this.setText(getDefaultName());
}
}
