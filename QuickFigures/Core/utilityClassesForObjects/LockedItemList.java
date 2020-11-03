package utilityClassesForObjects;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import genericMontageKit.BasicOverlayHandler;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import logging.IssueLog;
import undo.CompoundEdit2;
import undo.UndoTakeLockedItem;
import utilityClasses1.ArraySorter;

public class LockedItemList extends ArrayList<LocatedObject2D> implements LocationChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TakesLockedItems taker;
	
	public LockedItemList(TakesLockedItems t) {
		this.setTaker(t);
	}

	public TakesLockedItems getTaker() {
		return taker;
	}

	public void setTaker(TakesLockedItems taker) {
		this.taker = taker;
	}
	
	@Override
	public boolean add(LocatedObject2D l) {
		
		/**in the event that both the taker and the item are lockable objects,
		  one cannot lock the other*/
		if (l instanceof TakesLockedItems&&taker instanceof LocatedObject2D) {
			TakesLockedItems t=(TakesLockedItems) l;
			t.removeLockedItem((LocatedObject2D) taker);
		}
		l. addLocationChangeListener(this);
		return super.add(l);
	}

	@Override
	public boolean remove(Object l) {
		if (l instanceof LocatedObject2D )((LocatedObject2D) l).removeLocationChangeListener(this);
		return super.remove(l);
	}
	
	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException {
		try {
			//ArraySorter.removeDeadItems(this);
			ArraySorter.removeNonserialiazble(this);
		} catch (Exception e) {
			IssueLog.log(e);
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
	
	
	/**returns a list of items that are not currently attached to the taker that might be eligible*/
	public ArrayList<LocatedObject2D> getEligibleNONLockedItems(TakesLockedItems taker, Rectangle r) {
		ObjectContainer container = taker.getTopLevelContainer();
		if (container !=null) {
			ArrayList<LocatedObject2D> o = new BasicOverlayHandler().getOverlapOverlaypingOrContainedItems(r, container);
			ensureThatNoOverlappingItemsStayOnThis(o);
			return LockedItemList.removeItemsNotAvailableForLock(container.getLocatedObjects(), o);
		}
		return new ArrayList<LocatedObject2D>();
	}
	/**given a list of items, removes any that are on this lock list*/
	private void ensureThatNoOverlappingItemsStayOnThis(ArrayList<LocatedObject2D> o) {
		/**Removes items that are locked onto this object*/
		for(LocatedObject2D t: taker.getLockedItems()) {
			o.remove(t);
		}
		if (taker instanceof LocatedObject2D) o.remove(taker);
	}
	/**considers the potential locked items in list o. Any that are already taken are removed*/
	private static ArrayList<LocatedObject2D> removeItemsNotAvailableForLock(ArrayList<?> allGraphics,
			ArrayList<LocatedObject2D> o) {
		ArrayList<LocatedObject2D> output = new ArrayList<LocatedObject2D>();
		output.addAll(o);
		
		/**Removes all the objects that are already locked onto another item*/
		
		for(Object t2: allGraphics) {
			if (t2 instanceof TakesLockedItems) {
				TakesLockedItems t=(TakesLockedItems) t2;
				for(LocatedObject2D item:o) {
					if (t.hasLockedItem(item)) output.remove(item);
					
				}
			}
		}
		
		/**Removes any layouts*/
		ArraySorter.removeThoseOfClass(output, MontageLayoutGraphic.class);
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
	public static void removeFromAlltakers(LocatedObject2D sel, ArrayList<?> allRoi, CompoundEdit2 undoer) {
	
		for(Object t: allRoi) try {
			if (t==null || !(t instanceof TakesLockedItems)) continue;
			
			TakesLockedItems taker = ((TakesLockedItems)t);
			if (taker.hasLockedItem(sel)) {
				UndoTakeLockedItem undo = new UndoTakeLockedItem(taker, sel, true);
				if (undoer!=null) {
					
					undoer.addEditToList(undo);
				}
				taker.removeLockedItem(sel);
				undo.establishFinalState();
				}
		} catch (Throwable t2) {
			IssueLog.log(t2);
		}
	}
	
	
}
