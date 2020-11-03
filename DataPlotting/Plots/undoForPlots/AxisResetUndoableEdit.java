package undoForPlots;

import plotParts.Core.AxesGraphic;
import undo.AbstractUndoableEdit2;

public class AxisResetUndoableEdit extends AbstractUndoableEdit2 {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AxesGraphic aI;
	private AxesGraphic item;
	private AxesGraphic aF;

	public AxisResetUndoableEdit(AxesGraphic ag) {
		this.item=ag;
		aI=ag.copy();
	}
	
	public void establishFinalState() {
		aF=item.copy();
	}
	public void redo() {
		item.copyEveryThingFrom(aF);
		item.updatePlotArea();
	}
	
	public void undo() {
		item.copyEveryThingFrom(aI);
		item.updatePlotArea();
	}
}
