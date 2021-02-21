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
 * Version: 2021.1
 */
package undoForPlots;

import genericPlot.BasicPlot;
import plotParts.Core.AxesGraphic;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;

/**An undoable edit for changes in a plot axis*/
public class AxisResetUndoableEdit extends AbstractUndoableEdit2 {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AxesGraphic item;
	private AxesGraphic aInitalForm;
	
	private AxesGraphic aFinalForm;

	public AxisResetUndoableEdit(AxesGraphic ag) {
		this.item=ag;
		aInitalForm=ag.copy();
	}
	
	public void establishFinalState() {
		aFinalForm=item.copy();
	}
	public void redo() {
		item.copyEveryThingFrom(aFinalForm);
		item.updatePlotArea();
	}
	
	public void undo() {
		item.copyEveryThingFrom(aInitalForm);
		item.updatePlotArea();
	}
	
	public static CombinedEdit createFor(BasicPlot b) {
		 CombinedEdit cc=new  CombinedEdit();
		
		 for(AxesGraphic axis: b.getAllAxes()) {
			 if(axis==null) continue;
			 cc.addEditToList(
					 new AxisResetUndoableEdit(axis));
		 }
		 return cc;
	}
}
