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
import dataSeries.KaplenMeierDataSeries;
import imageDisplayApp.ImageWindowAndDisplaySet;
import kaplanMeierPlots.KM_Plot;
import logging.IssueLog;

public class KaplanMeierPlotCreator implements PlotCreator<KaplenMeierDataSeries> {
	
	private int type;

	public KaplanMeierPlotCreator(int type) {
		this.type=type;
	}

	@Override
	public String getNameText() {
		return "Kaplan-Meier Plot";
	}
	
	public void createPlot(String name, ArrayList<KaplenMeierDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		if(items.size()==0) {
			IssueLog.showMessage("Unable to extract data");
			return;
		}
		KM_Plot plot=new KM_Plot(name, items);
		
		if (type==0)  plot.defaultPlot();
		
		diw.getImageAsWrapper().getGraphicLayerSet().add(plot);
		diw.updateDisplay();diw.updateDisplay();
	}
}
