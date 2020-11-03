package undoForPlots;

import groupedDataPlots.Grouped_Plot;
import undo.AbstractUndoableEdit2;

public class GroupedPlotTypeEdit extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Grouped_Plot plot;
	private int typeI;
	private int typeF;
	
	public GroupedPlotTypeEdit(Grouped_Plot p) {
		plot=p;
		typeI=plot.getGroupedPlotType();
	}
	public void establishFinalState() {
		typeF=plot.getGroupedPlotType();
	}
	public void redo() {
		plot.setGroupedPlotType(typeF);
		plot.updateOffsets();
		plot.fullPlotUpdate();
	}
	
	public void undo() {
		plot.setGroupedPlotType(typeI);
		plot.updateOffsets();
		plot.fullPlotUpdate();
	}

}
