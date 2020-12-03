/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package undoForPlots;

import java.util.ArrayList;

import genericPlot.BasicPlot;
import groupedDataPlots.Grouped_Plot;
import undo.AbstractUndoableEdit2;
import undo.UndoScalingAndRotation;

public class PlotAreaChangeUndo extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BasicPlot plot;
	ArrayList<AbstractUndoableEdit2> acu=new ArrayList<AbstractUndoableEdit2>();

	public PlotAreaChangeUndo(BasicPlot plot) {
		this.plot=plot;
		acu.add(new AxisResetUndoableEdit(plot.getXAxis()));
		acu.add(new AxisResetUndoableEdit(plot.getYAxis()));
		if (plot.getSecondaryYaxis()!=null)acu.add(new AxisResetUndoableEdit(plot.getYAxisAlternate()));
		acu.add(new UndoScalingAndRotation(plot.plotAreaDefiningRectangle()));
	
	}
	
	public void establishFinalState() {
		for(AbstractUndoableEdit2 u: acu) {
			u.establishFinalState();
		}
	}
	public void redo() {
		for(AbstractUndoableEdit2 u: acu) {
			u.redo();;
		}
		if (plot instanceof Grouped_Plot) {
			((Grouped_Plot) plot).updateOffsets();
		}
		plot.fullPlotUpdate();
	}
	
	public void undo() {
		for(AbstractUndoableEdit2 u: acu) {
			u.undo();;
		}
		if (plot instanceof Grouped_Plot) {
			((Grouped_Plot) plot).updateOffsets();
		}
		plot.fullPlotUpdate();
	}
	
}
