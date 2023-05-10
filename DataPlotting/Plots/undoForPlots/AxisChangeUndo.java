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
package undoForPlots;

import genericPlot.BasicDataSeriesGroup;
import undo.AbstractUndoableEdit2;


/**an undo for switches of a data series between primary and secondary y axis*/
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
