package popupMenusForComplexObjects;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import undo.UndoTakeLockedItem;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.LockedItemList;
import utilityClassesForObjects.TakesLockedItems;

public class AddPanelSizeDefiningItemMenu extends ReleaseLockedMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean remove=false;
	
	public AddPanelSizeDefiningItemMenu(TakesLockedItems items, LockedItemList list) {
		super(items, list);
	}
	
	public String getDefaultName() {
		if (remove) return "Remove Panel Size Definer";
		return "Make Panel Size Definer";
	}
	
public AbstractUndoableEdit performAction(LocatedObject2D target) {
		if (this.getLockbox() instanceof PanelLayoutGraphic) {
			
			PanelLayoutGraphic p=(PanelLayoutGraphic) getLockbox() ;
			UndoTakeLockedItem undo = new UndoTakeLockedItem(p, target, remove);
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
