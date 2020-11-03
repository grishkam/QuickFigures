package undo;

import genericMontageKit.SelectionManager;

public class UndoSelectionSet extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SelectionManager selection;

	public UndoSelectionSet( SelectionManager selection) {
		this.selection=selection;
	}
	public void redo() {
		
	}
	
	public void undo() {
		selection.removeSelections();
	}

}
