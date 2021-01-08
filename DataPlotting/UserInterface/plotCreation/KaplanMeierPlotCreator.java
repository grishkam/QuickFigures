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
import dataSeries.KaplanMeierDataSeries;
import imageDisplayApp.ImageWindowAndDisplaySet;
import kaplanMeierPlots.KM_Plot;
import logging.IssueLog;

/**An implementation of plot creator for Kaplan Meier plots*/
public class KaplanMeierPlotCreator implements PlotCreator<KaplanMeierDataSeries> {
	
	

	public KaplanMeierPlotCreator() {
		
	}

	@Override
	public String getNameText() {
		return "Kaplan-Meier Plot";
	}
	
	public void createPlot(String name, ArrayList<KaplanMeierDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		if(items.size()==0) {
			IssueLog.showMessage("Unable to extract data");
			return;
		}
		KM_Plot plot=new KM_Plot(name, items);
		
		plot.defaultPlot();
		
		diw.getImageAsWrapper().getTopLevelLayer().add(plot);
		diw.updateDisplay();diw.updateDisplay();
	}
}
