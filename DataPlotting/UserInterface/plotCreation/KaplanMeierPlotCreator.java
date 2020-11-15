package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import dataSeries.KaplenMeierDataSeries;
import imageDisplayApp.ImageAndDisplaySet;
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
			diw=ImageAndDisplaySet.createAndShowNew("Figure", 300,300);
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
