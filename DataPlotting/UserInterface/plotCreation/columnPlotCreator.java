package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import columnPlots.ColumnPlot;
import dataSeries.ColumnDataSeries;
import imageDisplayApp.ImageAndDisplaySet;

public class columnPlotCreator implements PlotCreator<ColumnDataSeries>{

int type=0;
	
	public columnPlotCreator(int t) {
		type=t;
	}
	
	
	
	public void createPlot(String name, ArrayList<ColumnDataSeries> items, DisplayedImage diw) {
		if (diw==null|| (diw.getWindow().isVisible()==false)) {
			diw=ImageAndDisplaySet.createAndShowNew("Figure", 300,300);
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
