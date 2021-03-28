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
import dataSeries.XYDataSeries;
import dataTableDialogs.SmartDataInputDialog;
import imageDisplayApp.ImageWindowAndDisplaySet;
import undo.AbstractUndoableEdit2;
import undo.UndoAddItem;
import xyPlots.XY_Plot;

/**implementation of a the plot creator for xy data*/
public class XYPlotCreator implements PlotCreator<XYDataSeries> {
	
	private xyPlotForm type=xyPlotForm.DefaultForm;

	public XYPlotCreator(xyPlotForm type) {
		this.type=type;
	}

	@Override
	public String getNameText() {
		if (type==xyPlotForm.ScatterForm) return "Scatter Plot ";
		if (type==xyPlotForm.DefaultForm) return "Default Plot ";
		if (type==xyPlotForm.LineForm) return "Line Plot ";
		return "Bar plot";
	}
	
	public UndoAddItem createPlot(String name, ArrayList<XYDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		XY_Plot plot=new XY_Plot(name, items);
		
		if (type==xyPlotForm.DefaultForm)  plot.defaultPlot();
		if (type==xyPlotForm.ScatterForm)  plot.scatterPlot();
		if (type==xyPlotForm.LineForm)  plot.linePlot();;
		
		return PlotCreator.addPlotToWorksheet(diw, plot);
	}
	
	public static enum xyPlotForm {
		DefaultForm,
		ScatterForm,
		LineForm
	}

	@Override
	public UndoAddItem createPlot(String name, SmartDataInputDialog items, DisplayedImage diw) {
		ArrayList<XYDataSeries> in = items.getXYDataSeriesUsingDefaultClassification();
		return this.createPlot(name, in, diw);
	}
}
