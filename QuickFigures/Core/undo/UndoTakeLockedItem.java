package undo;

import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.TakesLockedItems;

public class UndoTakeLockedItem extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TakesLockedItems taker;
	private LocatedObject2D item;
	boolean remove=false;
	private UndoMoveItems undomove;
	private UndoSnappingChange snapundo;

	public UndoTakeLockedItem(TakesLockedItems taker, LocatedObject2D item, boolean remove ) {
		snapundo=new UndoSnappingChange(item);
		undomove=new UndoMoveItems(item);
		this.taker=taker;
		this.item=item;
		this.remove=remove;
	}
	

	public void establishFinalState() {
		undomove.establishFinalLocations();
		snapundo.establishFinalState();
	}
	

	public void redo() {
		undomove.redo();
		if (remove)taker.removeLockedItem(item); else 
		{
		taker.addLockedItem(item);
		}
		snapundo.redo();
	}
	
	public void undo() {
		undomove.undo();
		if (remove) {
			
			taker.addLockedItem(item);
			} else
		taker.removeLockedItem(item);
		snapundo.undo();
		taker.snapLockedItems();
		
	}

}
