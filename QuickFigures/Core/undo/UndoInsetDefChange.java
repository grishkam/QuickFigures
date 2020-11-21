package undo;

import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner;

public class UndoInsetDefChange extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelGraphicInsetDefiner item;
	private double iScale;
	private double fScale;

	public UndoInsetDefChange(PanelGraphicInsetDefiner insetDefiner) {
		this.item=insetDefiner;
		iScale=item.getBilinearScale();
	}
	

	public void establishFinalState() {
		fScale=item.getBilinearScale();
	}
	
	public void redo() {
		item.setBilinearScale(fScale);
	}
	
	public void undo() {
		item.setBilinearScale(fScale);
	}

}
