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
 * Version: 2022.1
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import locatedObject.TakesAttachedItems;
import undo.UndoAddOrRemoveAttachedItem;

/**A menu for choosing to attach an item to another object. */
public class AddLockedMenu extends ReleaseLockedMenu implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddLockedMenu(TakesAttachedItems t) {
		this(t, "Attach Item");
	}
	public AddLockedMenu(TakesAttachedItems t,String st) {
		super(st);
		this.setName(st);
		this.setText(st);
		setLockbox(t);
		ArrayList<LocatedObject2D> arr = t.getNonLockedItems();
		if (arr==null) return;
		
		createMenuItemsForList(arr);
		addCompoundMenuItems();
	}



	@Override
	public void actionPerformed(ActionEvent arg0) {
		int index = oi.indexOf(arg0.getSource());
		LocatedObject2D itemtoadd = o.get(index);
		AbstractUndoableEdit undo = performAction(itemtoadd);
		addUndo(undo);
	}
	
	public AbstractUndoableEdit performAction(LocatedObject2D target) {
		UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(getLockbox(), target, false);
		Rectangle2D b = getLockbox().getContainerForBounds(target);
		Point2D location = RectangleEdges.getLocation(RectangleEdges.CENTER, target.getBounds());
		if (target.getAttachmentPosition()==null) {target.setAttachmentPosition(AttachmentPosition.defaultInternal());}
		target.getAttachmentPosition().setToNearestSnap(target.getBounds(), b, location);
		
		getLockbox().addLockedItem(target);
		undo.establishFinalState();
		return undo;
	}

	

	public TakesAttachedItems getLockbox() {
		return lockbox;
	}

	public void setLockbox(TakesAttachedItems lockbox) {
		this.lockbox = lockbox;
	}
	
	public int getNItems() {
		return oi.size();
	}
	
	
	
}
