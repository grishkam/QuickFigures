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
package popupMenusForComplexObjects;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.undo.AbstractUndoableEdit;

import channelLabels.ChannelLabelTextGraphic;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import undo.Edit;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.LockedItemList;
import utilityClassesForObjects.TakesLockedItems;

/**A menu for choosing an attached item and removing it from the attached item list*/
public class ReleaseLockedMenu extends SelectItemJMenu implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TakesLockedItems lockbox;

	protected ReleaseLockedMenu(String st) {
		super(st);
	}
	
	public ReleaseLockedMenu(TakesLockedItems t, LockedItemList list) {
		this(t, list, "Release Attached Item");	
		
	}
	public ReleaseLockedMenu(TakesLockedItems t, LockedItemList list, String st) {
		this(st);
		this.setName(st);
		this.setText(st);
		setLockbox(t);
		ArrayList<LocatedObject2D> arr = list;
		if (arr==null) return;
		//ArraySorter.removeDeadItems(arr);
		
		createMenuItemsForList(arr);
		
		addCompoundMenuItems() ;
	}

	
	public AbstractUndoableEdit performAction(LocatedObject2D target) {
		return Edit.detachItem(getLockbox(), target);
	}

	

	public TakesLockedItems getLockbox() {
		return lockbox;
	}

	public void setLockbox(TakesLockedItems lockbox) {
		this.lockbox = lockbox;
	}
	
	public int getNItems() {
		return oi.size();
	}
	
	public void addCompoundMenuItems() {
		ArrayList<LocatedObject2D> arr;
		arr=new ArrayList<LocatedObject2D>(); arr.addAll(o);
		ArraySorter.removeThoseNotOfClass(arr, ImagePanelGraphic.class);
		if(arr.size()>1) 
			{
			add(new ComplexAdder("All Image Panels", arr),0);
			}
		
		arr=new ArrayList<LocatedObject2D>(); arr.addAll(o);
		ArraySorter.removeThoseNotOfClass(arr, TextGraphic.class);
		ArraySorter.removeThoseOfClass(arr, ChannelLabelTextGraphic.class);
		if(arr.size()>1) 
			{
			add(new ComplexAdder("All Text Items", arr),0);
			}
	}
	

	
	
}
