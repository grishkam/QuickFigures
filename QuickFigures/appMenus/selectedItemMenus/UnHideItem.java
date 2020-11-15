package selectedItemMenus;

import graphicalObjects.ZoomableGraphic;
import undo.UndoHideUnhide;
import utilityClassesForObjects.Hideable;

public class UnHideItem extends HideItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Unhide";
	}

	@Override
	public void run() {
		UndoHideUnhide undo = new UndoHideUnhide(this.array, false);
		for(ZoomableGraphic i: this.array) {
			if (i instanceof Hideable) {
				((Hideable) i).setHidden(false);
			}
		}
		this.getSelector().getGraphicDisplayContainer().getUndoManager().addEdit(undo);
	}

}
