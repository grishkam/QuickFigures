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
package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import dataSeries.XYDataSeries;
import imageDisplayApp.ImageWindowAndDisplaySet;
import xyPlots.XY_Plot;

public class XYPlotCreator implements PlotCreator<XYDataSeries> {
	
	private int type;

	public XYPlotCreator(int type) {
		this.type=type;
	}

	@Override
	public String getNameText() {
		if (type==1) return "Scatter Plot ";
		if (type==0) return "Default Plot ";
		if (type==2) return "Line Plot ";
		return "Bar plot";
	}
	
	public void createPlot(String name, ArrayList<XYDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		XY_Plot plot=new XY_Plot(name, items);
		
		if (type==0)  plot.defaultPlot();
		if (type==1)  plot.scatterPlot();
		if (type==2)  plot.linePlot();;
		
		diw.getImageAsWrapper().getTopLevelLayer().add(plot);
		diw.updateDisplay();diw.updateDisplay();
	}
}
