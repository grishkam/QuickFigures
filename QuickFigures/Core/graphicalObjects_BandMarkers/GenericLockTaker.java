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
 * Date Created: April 17, 2022
 * Date Modified: April 17, 2022
 * Version: 2023.2
 */
package graphicalObjects_BandMarkers;

import java.awt.Rectangle;
import java.util.ArrayList;

import fLexibleUIKit.MenuItemExecuter;
import handles.AttachmentPositionHandle;
import handles.SmartHandleList;
import locatedObject.ArrayObjectContainer;
import locatedObject.AttachedItemList;
import locatedObject.LocatedObject2D;
import locatedObject.ObjectContainer;
import locatedObject.TakesAttachedItems;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import storedValueDialog.StoredValueDilaog;

/**A special list of */
public class GenericLockTaker implements HasUniquePopupMenu, TakesAttachedItems {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	AttachedItemList list=new AttachedItemList(this);
	public SmartHandleList theList=new SmartHandleList();


	private Object parent;
	
	public GenericLockTaker(Object parentObject) {
		this.parent=parentObject;
	}
	

	
	/**returns a menu for the plot area*/
	public PopupMenuSupplier getMenuSupplier(){
		return new MenuItemExecuter(this);
		}
	
	
	public void showOptionsDialog() {
		new StoredValueDilaog(this).showDialog();
		
	}
	
	


	
	

	@Override
	public void addLockedItem(LocatedObject2D l) {
		list.add(l);
		getSmartHandleList().add(new AttachmentPositionHandle(this, l, 400+list.size()));
	}

	@Override
	public void removeLockedItem(LocatedObject2D l) {
		list.remove(l);
		getSmartHandleList().removeLockedItemHandle(l);
	}

	@Override
	public void snapLockedItems() {
		for(LocatedObject2D l: list) {
			snapLockedItem(l);
		}
		
	}

	@Override
	public void snapLockedItem(LocatedObject2D l) {
		 {
			//l.getAttachmentPosition().snapObjectToRectangle(l, this.getRectangle().getBounds());
		}
	}

	@Override
	public boolean hasLockedItem(LocatedObject2D l) {
		return list.contains(l);
	}

	@Override
	public AttachedItemList getLockedItems() {
		return list;
	}
	
	

	
	@Override
	public Rectangle getContainerForBounds(LocatedObject2D l) {
		return getBounds();
	}

	
	@Override
	public ArrayList<LocatedObject2D> getNonLockedItems() {
		return new ArrayList<LocatedObject2D>();
	}


	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void select() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deselect() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean makePrimarySelectedItem(boolean isFirst) {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**creates a handle for adjusting the location of an attached item
	 * @param l
	 */
	public void addHandleForAttachedItem(LocatedObject2D l) {
		
		theList.add(new AttachmentPositionHandle(this, l, list.size()));
	}

	@Override
	public SmartHandleList getSmartHandleList() {
		return theList;
	}


	@Override
	public Rectangle getBounds() {
		return new Rectangle();
	}


	@Override
	public ObjectContainer getTopLevelContainer() {
		
		return new ArrayObjectContainer(new ArrayList<LocatedObject2D> ());
	}
	


}
