package undo;

import graphicalObjects_BasicShapes.BarGraphic;

public class UndoScaleBarEdit extends AbstractUndoableEdit2 {

	private BarGraphic fBar;
	private BarGraphic iBar;
	private BarGraphic theBar;
	private UndoSnappingChange barTextUndo;
	
	public UndoScaleBarEdit(BarGraphic a) {
		theBar=a;
		if(theBar!=null)
			{iBar=theBar.copy();
			barTextUndo=new UndoSnappingChange(theBar.getBarText());
		 }
	}
	

	public void establishFinalState() {
		if(theBar!=null)
			fBar=theBar.copy();
		if( barTextUndo!=null)  barTextUndo.establishFinalState();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void redo() {
		if(theBar!=null)
			theBar.copyAttributesFrom(fBar);
		if( barTextUndo!=null)  barTextUndo.redo();
	}
	
	public void undo() {
		if(theBar!=null)
			theBar.copyAttributesFrom(iBar);
		if( barTextUndo!=null)  barTextUndo.undo();
	}

}
