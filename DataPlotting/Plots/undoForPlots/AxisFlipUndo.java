package undoForPlots;

import genericPlot.BasicPlot;
import undo.AbstractUndoableEdit2;

public class AxisFlipUndo extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BasicPlot plot;
	
	public AxisFlipUndo(BasicPlot plot) {
		this.plot=plot;
	}
	
	public void redo() {
		plot.axisFlips();
	}
	
	public void undo() {
		plot.axisFlips();
	}

}
