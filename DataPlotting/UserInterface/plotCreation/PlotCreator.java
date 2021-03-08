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
package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import dataSeries.DataSeries;
import genericPlot.BasicPlot;
import graphicalObjects_LayerTypes.GraphicLayer;
import undo.AbstractUndoableEdit2;
import undo.UndoAddItem;

/**items of this interface create a particular kind of plot*/
public interface PlotCreator<Type extends DataSeries>  {
	
	/**creates the plot*/
	public AbstractUndoableEdit2 createPlot(String name, ArrayList<Type> items, DisplayedImage diw);
	
	/**what the plot type is called*/
	public String getNameText();
	
	/** adds a plot to the figure. returns an undo for the addition
	 * that undo is also added to the undo manager
	 * @param diw
	 * @param plot
	 * @return
	 */
	public static UndoAddItem addPlotToWorksheet(DisplayedImage diw, BasicPlot plot) {
		GraphicLayer targetLayer = diw.getImageAsWrapper().getTopLevelLayer();
		targetLayer.add(plot);
		UndoAddItem undo = new UndoAddItem(targetLayer , plot);
		diw.getUndoManager().addEdit(undo);
		diw.updateDisplay();diw.updateDisplay();
		return undo;
	}
	
}
