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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package plotCreation;

import java.util.ArrayList;

import columnPlots.ColumnPlot;
import applicationAdapters.DisplayedImage;
import dataSeries.ColumnDataSeries;
import imageDisplayApp.ImageWindowAndDisplaySet;
import undo.UndoAddItem;

/**A Plot creator that can generate a few types of column plots*/
public class ColumnPlotCreator implements PlotCreator<ColumnDataSeries>{

/**
	 * 
	 */
	public static final int TUKEY_BOX_PLOT = 4;
/**
	 * 
	 */
	public static final int BOX_PLOT = 3;
/**
	 * 
	 */
	public static final int SCATTER_POINTS = 2;
/**
	 * 
	 */
	public static final int STANDARD_BAR_PLOT = 1;
/**
	 * 
	 */
	public static final int BAR_AND_SCATTER = 0;
	
	int type=BAR_AND_SCATTER;
	
	 public ColumnPlotCreator(int t) {
		type=t;
	}
	
	
	
	public UndoAddItem createPlot(String name, ArrayList<ColumnDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		
		ColumnPlot plot=new ColumnPlot(name, items.toArray(new ColumnDataSeries[items.size()]));
		
		if (type==STANDARD_BAR_PLOT)  plot.barPlot();
		if (type==SCATTER_POINTS) plot.scatterPlot();;
		if (type==BOX_PLOT) plot.normalBoxplotPlot();;
		if (type==TUKEY_BOX_PLOT) plot.tukeyBoxplotPlot();;
		
		return PlotCreator.addPlotToWorksheet(diw, plot);
	}



	
	
	@Override
	public String getNameText() {
		if (type==BAR_AND_SCATTER) return "Bar Plot With Dots";
		if (type==STANDARD_BAR_PLOT) return "Normal Bar Plot";
		if (type==SCATTER_POINTS) return "Scatter Point Plot";
		if (type==BOX_PLOT) return "Boxplot";
		if (type==TUKEY_BOX_PLOT) return "Tukey Boxplot";
		return "Bar plot";
	}

}
