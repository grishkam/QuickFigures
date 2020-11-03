package undoForPlots;

import genericPlot.BasicDataSeriesGroup;
import undo.AbstractUndoableEdit2;


/**an undo for switching between primary and secondary y axis*/
public class AxisChangeUndo extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int AxisI;
	private BasicDataSeriesGroup seriesGroup;
	private int AxisF;
	
	public AxisChangeUndo(BasicDataSeriesGroup g, int orginal) {
		this.seriesGroup=g;
		this.AxisI=orginal;
	}
	
	public void setFinalAxis(int axf) {
		this.AxisF=axf;
	}
	
	public void redo() {
		seriesGroup.setAxis(AxisF);
	}
	
	public void undo() {
		seriesGroup.setAxis(AxisI);
	}

}
