package undo;

import genericMontageKit.OverlayObjectManager;

public class UndoSelectionSet extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OverlayObjectManager selection;

	public UndoSelectionSet( OverlayObjectManager selection) {
		this.selection=selection;
	}
	public void redo() {
		
	}
	
	public void undo() {
		selection.removeSelections();
	}

}
