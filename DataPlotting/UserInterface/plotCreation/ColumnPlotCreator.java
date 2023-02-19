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
 * Version: 2023.1
 */
package plotCreation;

import java.util.ArrayList;

import columnPlots.ColumnPlot;
import applicationAdapters.DisplayedImage;
import dataSeries.ColumnDataSeries;
import dataTableDialogs.SmartDataInputDialog;
import fileread.PlotType;
import graphicActionToolbar.CurrentFigureSet;
import imageDisplayApp.ImageWindowAndDisplaySet;
import undo.UndoAddItem;

/**A Plot creator that can generate a few types of column plots*/
public class ColumnPlotCreator implements PlotCreator<ColumnDataSeries>{

public enum ColumnPlotStyle {STANDARD_BAR_PLOT ,TUKEY_BOX_PLOT , BOX_PLOT , SCATTER_POINTS,  BAR_AND_SCATTER }
	
ColumnPlotStyle type=ColumnPlotStyle.BAR_AND_SCATTER;

	
	 public ColumnPlotCreator(ColumnPlotStyle t) {
		type=t;
	}
	 
	// public static ArrayList<ColumnPlotCreator> getAlltypes
	
	
	
	public UndoAddItem createPlot(String name, ArrayList<ColumnDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		
		ColumnPlot plot=new ColumnPlot(name, items.toArray(new ColumnDataSeries[items.size()]));
		
		if (type==ColumnPlotStyle.STANDARD_BAR_PLOT)  plot.barPlot();
		if (type==ColumnPlotStyle.SCATTER_POINTS) plot.scatterPlot();;
		if (type==ColumnPlotStyle.BOX_PLOT) plot.normalBoxplotPlot();;
		if (type==ColumnPlotStyle.TUKEY_BOX_PLOT) plot.tukeyBoxplotPlot();;
		
		return PlotCreator.addPlotToWorksheet(diw, plot);
	}

	
	

	
	
	@Override
	public String getNameText() {
		if (type==ColumnPlotStyle.BAR_AND_SCATTER) return "Bar Plot With Dots";
		if (type==ColumnPlotStyle.STANDARD_BAR_PLOT) return "Normal Bar Plot";
		if (type==ColumnPlotStyle.SCATTER_POINTS) return "Scatter Point Plot";
		if (type==ColumnPlotStyle.BOX_PLOT) return "Boxplot";
		if (type==ColumnPlotStyle.TUKEY_BOX_PLOT) return "Tukey Boxplot";
		return "Bar plot";
	}

	@Override
	public UndoAddItem createPlot(String name, SmartDataInputDialog items, DisplayedImage diw) {
		ArrayList<ColumnDataSeries> in;
		if (items.getPlotForm()==PlotType.COLUMN_PLOT_TYPE ) 
			in= items.getDataSeriesUsingClassificationFolumn(0,1, items.getDataTable());
		else in=items.getAllColumns();
		
		return createPlot(name, in, CurrentFigureSet.getCurrentActiveDisplayGroup());
		
		
	}

}
