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
 * Version: 2022.1
 */
package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import dataSeries.KaplanMeierDataSeries;
import dataTableDialogs.SmartDataInputDialog;
import imageDisplayApp.ImageWindowAndDisplaySet;
import kaplanMeierPlots.KM_Plot;
import logging.IssueLog;
import undo.UndoAddItem;

/**An implementation of plot creator for Kaplan Meier plots*/
public class KaplanMeierPlotCreator implements PlotCreator<KaplanMeierDataSeries> {
	
	

	public KaplanMeierPlotCreator() {
		
	}

	@Override
	public String getNameText() {
		return "Kaplan-Meier Plot";
	}
	
	public UndoAddItem createPlot(String name, ArrayList<KaplanMeierDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		if(items.size()==0) {
			IssueLog.showMessage("Unable to extract data");
			return null;
		}
		KM_Plot plot=new KM_Plot(name, items);
		
		plot.defaultPlot();
		
		return PlotCreator.addPlotToWorksheet(diw, plot);
	}

	@Override
	public UndoAddItem createPlot(String name, SmartDataInputDialog items, DisplayedImage diw) {
		ArrayList<KaplanMeierDataSeries> in = items.getKaplanDataSeriesUsingDefaultClassification();
		return this.createPlot(name, in, diw);
	}
}
