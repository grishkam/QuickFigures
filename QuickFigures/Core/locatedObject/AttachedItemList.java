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
 * Date Modified: Jan 4, 2021
 * Version: 2023.1
 */
package locatedObject;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.BasicObjectListHandler;
import logging.IssueLog;
import undo.CombinedEdit;
import undo.UndoAddOrRemoveAttachedItem;
import utilityClasses1.ArraySorter;

/**Maintains a list of attached items*/
public class AttachedItemList extends ArrayList<LocatedObject2D> implements LocationChangeListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TakesAttachedItems taker;
	
	public AttachedItemList(TakesAttachedItems t) {
		this.setTaker(t);
	}

	public TakesAttachedItems getTaker() {
		return taker;
	}

	public void setTaker(TakesAttachedItems taker) {
		this.taker = taker;
	}
	
	/**Adds an item to this list*/
	@Override
	public boolean add(LocatedObject2D l) {
		
		/**in the event that both the taker and the item are lockable objects,
		  one cannot lock the other*/
		if (l instanceof TakesAttachedItems&&taker instanceof LocatedObject2D) {
			TakesAttachedItems t=(TakesAttachedItems) l;
			t.removeLockedItem((LocatedObject2D) taker);
		}
		l. addLocationChangeListener(this);
		return super.add(l);
	}

	/**Removes an item from this list*/
	@Override
	public boolean remove(Object l) {
		if (l instanceof LocatedObject2D )((LocatedObject2D) l).removeLocationChangeListener(this);
		return super.remove(l);
	}
	
	/**Removes any non Serializable items before writing the object*/
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException {
		try {
			ArraySorter.removeNonSerialiazble(this);
		} catch (Exception e) {
			IssueLog.logT(e);
			IssueLog.log("Problem attempting to purge problematic items");
		}
		out.defaultWriteObject();
	}
	
	public void purgeDeadIfNeeded() {
		ArraySorter.removeDeadItems(this);
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		return taker.isDead();
	}

	@Override
	public void objectMoved(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectSizeChanged(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectEliminated(LocatedObject2D object) {
		if (object==taker) {
			for(LocatedObject2D l:this) {
				l.removeLocationChangeListener(this);
			}
		} 
		
	}

	@Override
	public void userMoved(LocatedObject2D object) {
		if (object==taker) {
			taker.snapLockedItems();
		} 
	}

	@Override
	public void userSizeChanged(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}
	
	
	/**returns a list of items that are not currently attached to the taker that might be eligible for attachment
	 * @param r where to look*/
	public ArrayList<LocatedObject2D> getEligibleNONLockedItems(TakesAttachedItems taker, Rectangle r) {
		ObjectContainer container = taker.getTopLevelContainer();
		if (container !=null) {
			ArrayList<LocatedObject2D> o = new BasicObjectListHandler().getOverlapOverlaypingOrContainedItems(r, container);
			ensureThatNoOverlappingItemsStayOnThis(o);
			return AttachedItemList.removeItemsNotAvailableForLock(container.getLocatedObjects(), o);
		}
		return new ArrayList<LocatedObject2D>();
	}
	/**given a list of items, removes any that are on this lock list*/
	@SuppressWarnings("unlikely-arg-type")
	private void ensureThatNoOverlappingItemsStayOnThis(ArrayList<LocatedObject2D> o) {
		/**Removes items that are locked onto this object*/
		for(LocatedObject2D t: taker.getLockedItems()) {
			o.remove(t);
		}
		if (taker instanceof LocatedObject2D) o.remove(taker);
	}
	/**considers the potential locked items in list o. Any that are already taken are removed
	 * Any that are not eligible for attachment are also removed*/
	private static ArrayList<LocatedObject2D> removeItemsNotAvailableForLock(ArrayList<?> allGraphics,
			ArrayList<LocatedObject2D> o) {
		ArrayList<LocatedObject2D> output = new ArrayList<LocatedObject2D>();
		output.addAll(o);
		
		/**Removes all the objects that are already locked onto another item*/
		
		for(Object t2: allGraphics) {
			if (t2 instanceof TakesAttachedItems) {
				TakesAttachedItems t=(TakesAttachedItems) t2;
				for(LocatedObject2D item:o) {
					if (t.hasLockedItem(item)) output.remove(item);
					
				}
			}
		}
		
		/**Removes any layouts*/
		ArraySorter.removeThoseOfClass(output, DefaultLayoutGraphic.class);
		ArraySorter.removeThoseOfClass(output, PanelLayoutGraphic.class);
		
		/**Only allows the locking of certain items*/
		for(LocatedObject2D object: o) {
			if (object instanceof TextGraphic) {
				TextGraphic t=(TextGraphic) object;
				if(!t.isUserEditable())output.remove(t);
			}
		}
		
		return output;
	}
	
	/**goes through a list of lock taking items and removes the selected item*/
	public static void removeFromAlltakers(LocatedObject2D sel, ArrayList<?> allRoi, CombinedEdit undoer) {
	
		for(Object t: allRoi) try {
			if(t instanceof CarriesLockTaker) {
				t= (( CarriesLockTaker)t).getLockTaker();
			}
			if (t==null || !(t instanceof TakesAttachedItems)) continue;
			
			TakesAttachedItems taker = ((TakesAttachedItems)t);
			if (taker.hasLockedItem(sel)) {
				UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(taker, sel, true);
				if (undoer!=null) {
					
					undoer.addEditToList(undo);
				}
				taker.removeLockedItem(sel);
				undo.establishFinalState();
				}
		} catch (Throwable t2) {
			IssueLog.logT(t2);
		}
	}
	
	
}
