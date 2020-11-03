package undo;

import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;

public class UndoLayoutEdit extends UndoMoveItems {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	BasicMontageLayout layout;
	BasicMontageLayout oldlayout;
	BasicMontageLayout newlayout;
	
	public UndoLayoutEdit(MontageLayoutGraphic layoutGraphic) {
		super(layoutGraphic.generateCurrentImageWrapper().getLocatedObjects());
		 layout= layoutGraphic.getPanelLayout();
		 oldlayout=layout.duplicate();
	}
	
	public UndoLayoutEdit(BasicMontageLayout panelLayout) {
		super(panelLayout.getWrapper().getLocatedObjects());
		 layout= panelLayout;
		 oldlayout=layout.duplicate();
		 
	}
	
	public void establishFinalLocations() {
		super.establishFinalLocations();
		newlayout=layout.duplicate();
		
	}
	
	public void undo() {
		super.undo();
		layout.setToMatch(oldlayout);
	}
	
	public void redo() {
		super.redo();
		layout.setToMatch(newlayout);
	}

}
