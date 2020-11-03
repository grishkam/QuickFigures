package undo;

import channelMerging.PanelStackDisplay;
import genericMontageKit.PanelList;

public class UndoStackEdit extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelList stack;
	private double iScale;
	private double fScale;
	private PanelStackDisplay display;

	public UndoStackEdit(PanelList stack) {
		this.stack=stack;
		
		iScale=stack.getScaleBilinear();
				
	}
	
	public void establishFinalState() {
		fScale=stack.getScaleBilinear();
	}
	
	public void redo() {
		stack.setScaleBilinear(fScale);
		if (this.display!=null) display.updatePanels();
	}
	
	public void undo() {
		stack.setScaleBilinear(iScale);
		if (this.display!=null) display.updatePanels();
	}

	public void setDisplayLayer(PanelStackDisplay layer) {
		this.display=layer;
		
	}

}
