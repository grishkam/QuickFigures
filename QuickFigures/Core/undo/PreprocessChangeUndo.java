package undo;

import javax.swing.undo.AbstractUndoableEdit;

import channelMerging.PanelStackDisplay;
import channelMerging.PreProcessInformation;

public class PreprocessChangeUndo extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelStackDisplay display;
	private PreProcessInformation original;
	PreProcessInformation finalState;
	
	public PreprocessChangeUndo(PanelStackDisplay l) {
		this.display=l;
		this.original=l.getSlot().getModifications();
				
		
	}
	
	
	public void establishFinalLocations() {
		finalState = display.getPreProcess();
		
	}
	
	public void undo() {
		display.getSlot().applyCropAndScale(original);
	}
	
	public void redo() {
		display.getSlot().applyCropAndScale(finalState);
	}

}
