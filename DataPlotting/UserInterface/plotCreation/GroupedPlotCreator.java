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
package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import dataSeries.GroupedDataSeries;
import groupedDataPlots.Grouped_Plot;
import imageDisplayApp.ImageWindowAndDisplaySet;

public class GroupedPlotCreator implements PlotCreator<GroupedDataSeries> {
	int type=0;
	
	public GroupedPlotCreator(int t) {
		type=t;
	}
	

	
	@Override
	public String getNameText() {
		if (type==0) return "  Stagered Barplot ";
		if (type==1) return "   Stacked Barplot ";
		if (type==2) return "Sequential Barplot ";
		if (type==3) return "Superimposed Scatter Plot ";
		return "Stagered";
	}
	public void createPlot(String name, ArrayList<GroupedDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		
		
		
		Grouped_Plot plot=new Grouped_Plot(name, items);
		
		if (type==0)  plot.defaultPlot();
		if (type==1)  plot.stackedPlot();
		if (type==2)  plot.sequentialBarPlot();;
		if (type==3)  plot.scatterPlot();
		
		diw.getImageAsWrapper().getTopLevelLayer().add(plot);
		diw.updateDisplay();diw.updateDisplay();
	}
}
