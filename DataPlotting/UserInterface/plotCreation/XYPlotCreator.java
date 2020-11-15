package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import dataSeries.XYDataSeries;
import imageDisplayApp.ImageAndDisplaySet;
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
			diw=ImageAndDisplaySet.createAndShowNew("Figure", 300,300);
		}
		XY_Plot plot=new XY_Plot(name, items);
		
		if (type==0)  plot.defaultPlot();
		if (type==1)  plot.scatterPlot();
		if (type==2)  plot.linePlot();;
		
		diw.getImageAsWrapper().getGraphicLayerSet().add(plot);
		diw.updateDisplay();diw.updateDisplay();
	}
}
