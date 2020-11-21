package undo;

import channelMerging.ImageDisplayLayer;
import channelMerging.PreProcessInformation;

public class PreprocessChangeUndo extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImageDisplayLayer display;
	private PreProcessInformation original;
	PreProcessInformation finalState;
	
	public PreprocessChangeUndo(ImageDisplayLayer l) {
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
