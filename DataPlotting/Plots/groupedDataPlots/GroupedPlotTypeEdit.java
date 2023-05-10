/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 7, 2021
 * Version: 2023.2
 */
package groupedDataPlots;

import undo.AbstractUndoableEdit2;

public class GroupedPlotTypeEdit extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Grouped_Plot plot;
	private int typeI;
	private int typeF;
	
	GroupedPlotTypeEdit(Grouped_Plot p) {
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
