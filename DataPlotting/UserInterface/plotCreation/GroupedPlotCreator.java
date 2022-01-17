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
 * Date Modified: Mar 28, 2021
 * Version: 2022.0
 */
package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import dataSeries.GroupedDataSeries;
import dataTableDialogs.SmartDataInputDialog;
import groupedDataPlots.Grouped_Plot;
import imageDisplayApp.ImageWindowAndDisplaySet;
import undo.UndoAddItem;

public class GroupedPlotCreator implements PlotCreator<GroupedDataSeries> {
	int type=Grouped_Plot.STAGGERED_BARS;
	
	/**makes a group plot creator for a particular style of plot*/
	 public GroupedPlotCreator(int plotStyle) {
		type=plotStyle;
	}
	

	
	@Override
	public String getNameText() {
		if (type==Grouped_Plot.STAGGERED_BARS) return "  Staggered Barplot ";
		if (type==Grouped_Plot.STACKED_BARS) return "   Stacked Barplot ";
		if (type==Grouped_Plot.SEQUENTIAL_BARS) return "Sequential Barplot ";
		if (type==Grouped_Plot.JITTER_POINTS) return "Superimposed Scatter Plot ";
		return "Staggered";
	}
	public UndoAddItem createPlot(String name, ArrayList<GroupedDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		}

		Grouped_Plot plot=new Grouped_Plot(name, items);
		
		if (type==Grouped_Plot.STAGGERED_BARS)  plot.defaultPlot();
		if (type==Grouped_Plot.STACKED_BARS)  plot.stackedPlot();
		if (type==Grouped_Plot.SEQUENTIAL_BARS)  plot.sequentialBarPlot();;
		if (type==Grouped_Plot.JITTER_POINTS)  plot.scatterPlot();
		
		return PlotCreator.addPlotToWorksheet(diw, plot);
	}



	@Override
	public UndoAddItem createPlot(String name, SmartDataInputDialog items, DisplayedImage diw) {
		ArrayList<GroupedDataSeries> in = items.getCategoryDataSeriesUsingClassificationFolumn(0,1,2);
		return this.createPlot(name, in, diw);
	}
}
