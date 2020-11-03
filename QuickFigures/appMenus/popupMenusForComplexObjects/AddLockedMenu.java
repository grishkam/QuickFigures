package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import undo.UndoTakeLockedItem;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.SnappingPosition;
import utilityClassesForObjects.TakesLockedItems;

/**A menu for choosing to attach an item to another object. */
public class AddLockedMenu extends ReleaseLockedMenu implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddLockedMenu(TakesLockedItems t) {
		this(t, "AttacehItem");
	}
	public AddLockedMenu(TakesLockedItems t,String st) {
		super(st);
		this.setName(st);
		this.setText(st);
		setLockbox(t);
		ArrayList<LocatedObject2D> arr = t.getNonLockedItems();
		if (arr==null) return;
		//ArraySorter.removeDeadItems(arr);
		
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
		UndoTakeLockedItem undo = new UndoTakeLockedItem(getLockbox(), target, false);
		Rectangle2D b = getLockbox().getContainerForBounds(target);
		Point2D location = RectangleEdges.getLocation(RectangleEdges.CENTER, target.getBounds());
		if (target.getSnappingBehaviour()==null) {target.setSnappingBehaviour(SnappingPosition.defaultInternal());}
		target.getSnappingBehaviour().setToNearestSnap(target.getBounds(), b, location);
		
		getLockbox().addLockedItem(target);
		undo.establishFinalState();
		return undo;
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
	
	
	
}
