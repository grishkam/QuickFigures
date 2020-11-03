package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImageWrapper;
import dataSeries.GroupedDataSeries;
import groupedDataPlots.Grouped_Plot;
import imageDisplayApp.ImageAndDisplaySet;

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
	public void createPlot(String name, ArrayList<GroupedDataSeries> items, DisplayedImageWrapper diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		
		
		
		Grouped_Plot plot=new Grouped_Plot(name, items);
		
		if (type==0)  plot.defaultPlot();
		if (type==1)  plot.stackedPlot();
		if (type==2)  plot.sequentialBarPlot();;
		if (type==3)  plot.scatterPlot();
		
		diw.getImageAsWrapper().getGraphicLayerSet().add(plot);
		diw.updateDisplay();diw.updateDisplay();
	}
}
