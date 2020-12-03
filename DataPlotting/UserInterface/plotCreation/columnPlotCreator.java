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
import columnPlots.ColumnPlot;
import dataSeries.ColumnDataSeries;
import imageDisplayApp.ImageWindowAndDisplaySet;

public class columnPlotCreator implements PlotCreator<ColumnDataSeries>{

int type=0;
	
	public columnPlotCreator(int t) {
		type=t;
	}
	
	
	
	public void createPlot(String name, ArrayList<ColumnDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		
		ColumnPlot plot=new ColumnPlot(name, items.toArray(new ColumnDataSeries[items.size()]));
		
		if (type==1)  plot.barPlot();
		if (type==2) plot.scatterPlot();;
		if (type==3) plot.normalBoxplotPlot();;
		if (type==4) plot.tukeyBoxplotPlot();;
		
		diw.getImageAsWrapper().getGraphicLayerSet().add(plot);
		diw.updateDisplay();diw.updateDisplay();
	}
	
	@Override
	public String getNameText() {
		if (type==0) return "Bar Plot With Dots";
		if (type==1) return "Normal Bar Plot";
		if (type==2) return "Scatter Point Plot";
		if (type==3) return "Boxplot";
		if (type==4) return "Tukey Boxplot";
		return "Bar plot";
	}

}
