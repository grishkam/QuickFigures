package columnPlots;

import java.util.ArrayList;

import dataSeries.ColumnDataSeries;
import dataTableDialogs.DataTable;
import dataTableDialogs.SmartDataInputDialog;
import fLexibleUIKit.MenuItemMethod;
import fileread.ExcelFileToBarPlot;
import genericPlot.BasicPlot;
import genericPlot.BasicDataSeriesGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import gridLayout.GridLayoutEditListener;
import gridLayout.MontageSpaces;
import menuUtil.HasUniquePopupMenu;
import plotParts.Core.AxesGraphic;
import plotParts.Core.PlotArea;
import plotParts.DataShowingParts.PlotUtil;
import undoForPlots.AxisFlipUndo;

/**A special layer for a plot with single dimensional data*/
public class ColumnPlot extends BasicPlot implements PlotArea, HasUniquePopupMenu, MontageSpaces, GridLayoutEditListener {



	private ArrayList<ColumnPlotDataSeriesGroup> allData=new ArrayList<ColumnPlotDataSeriesGroup>();
	

	/**What to do when given the data for a scatter plot*/
	public ColumnPlot(String name, ColumnDataSeries... numbers) {
		super(name);
		
		addManyNew( numbers);
		
		
		addTitleLabel();
		
		 addYAxiLabel();
		 addXAxiLabel(55);
		 
		 xAxis.setShowText(false);
		 xAxis.setIntergerTics(true);
		 moxAxisLabelOutOfWay();
		 onPlotUpdate();
			
	}





	/**Assuming the plot consistes of bars/boxes/points for 1D categories of 
	 * data, each category can be positioned as 1,2,3 est. This method sets
	 * the axis to be slightly larger than the number of categories.*/
	protected void setInDependantVariableAxisFor1DData(int length, AxesGraphic xAxis) {
		xAxis.getAxisData().setMaxValue(length+0.5);
		xAxis.getAxisData().setMinValue(0.5);
		xAxis.getAxisData().setMinorTic(1);
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Called after a new data series is added to the layer*/
	protected void afterSeriesAdditionToLayer(BasicDataSeriesGroup g) {
		if (g instanceof ColumnPlotDataSeriesGroup) {
			ColumnPlotDataSeriesGroup d=(ColumnPlotDataSeriesGroup) g;
			nSeries++;
			getAllDataSeries().add(d);
			d.setPosition(nSeries);
			onPlotUpdate();
			
		}
	}
	
	
	/**Sets the positions of the data series. This determines
	  the order that the data series are displayed in.
	  Since it is called whenever they are added, removed
	  or swapped within the layer, they are in practice, constantly updated*/
	public void onSeriesPositionInLayerChanges() {
		nSeries=1;
		for(GraphicLayer layer: this.getSubLayers()) {
			if (layer instanceof ColumnPlotDataSeriesGroup) {
				ColumnPlotDataSeriesGroup d=(ColumnPlotDataSeriesGroup) layer;
				d.setPosition(nSeries);
				nSeries++;
				d.onPlotUpdate();
				d.onAxisUpdate();
			}
		}
	}
	



	

@MenuItemMethod(menuActionCommand = "Add Data", menuText = "New Data Series", subMenuName="Data", orderRank=19)
public void addDataSeriesFromUser() {
	ArrayList<ColumnDataSeries> cols = getNewDataSeriesFromUser();
		for(ColumnDataSeries data: cols)addNew(data);
}


/**Replaces the data with new input data*/
@MenuItemMethod(menuActionCommand = "Replace Data", menuText = "Replace data", subMenuName="Data", orderRank=23)
public void replaceDataWithSeriesFromUser() {

	ArrayList<ColumnPlotDataSeriesGroup> olderSeries = this.getAllDataSeries();
	
	ArrayList<ColumnDataSeries> cols = new ArrayList<ColumnDataSeries>();
	for(ColumnPlotDataSeriesGroup o: olderSeries) {cols.add(o.getDataSeries());}
	SmartDataInputDialog d2 = SmartDataInputDialog.createFrom(cols);
	d2.setModal(true);d2.setWindowCentered(true);
	d2.showDialog();
	cols=d2.getAllColumns();
	
	replaceData(olderSeries, cols);
	
}




/**replaces data with new data series*/
private void replaceData(ArrayList<ColumnPlotDataSeriesGroup> olderSeries, ArrayList<ColumnDataSeries> cols) {
	for(int i=0; i<cols.size()||i<olderSeries.size(); i++) {
		ColumnDataSeries   novel = null;
		if (i<cols.size()) novel=cols.get(i);
		
		/**if Replacement need be done*/
		if (i<cols.size()&&i<olderSeries.size()) {
			ColumnDataSeries old = olderSeries.get(i).getDataSeries();
			
			boolean sameName = (old.getName().equals(novel.getName()));
			old.replaceData(novel.getDataPointList());
			old.setName(novel.getName());
			if (!sameName) {
				olderSeries.get(i).getSeriesLabel().getParagraph().get(0).get(0).setText(novel.getName());
			}
			
			
		}
		
		if (i<cols.size()&&!(i<olderSeries.size())) {
			addNew(novel);
		}
		
		if (!(i<cols.size())&&(i<olderSeries.size())) {
			this.remove(olderSeries.get(i));
		}
		
		
		
	}
	
	this.autoCalculateAxisRanges();
	this.fullPlotUpdate();
}


protected ArrayList<ColumnDataSeries> getNewDataSeriesFromUser() {
	DataTable tab = new DataTable(100, 6);
	tab.setValueAt("New Data Series", 0,0);
	SmartDataInputDialog s = new SmartDataInputDialog(tab, 1);
	s.setModal(true);s.setWindowCentered(true);
	s.showDialog();
	ArrayList<ColumnDataSeries> cols = s.getAllColumns();
	return cols;
}

@MenuItemMethod(menuActionCommand = "Add Data File", menuText = "New Data Series From Excel File", subMenuName="Data", orderRank=18)
public void addDataSeriesFromFile() {
	ColumnDataSeries[] datas = new ExcelFileToBarPlot(0).getDataFromFile();
	for(ColumnDataSeries data: datas)
		addNew(data);
	
}

protected void addManyNew(ColumnDataSeries... numbers) {
	
	
	for(ColumnDataSeries data: numbers) {
		if (data==null||data.length()<1) continue;
		
		ColumnPlotDataSeriesGroup template=null;
		if (this.getAllDataSeries().size()>0) template=getAllDataSeries().get(0);
		ColumnPlotDataSeriesGroup group = new ColumnPlotDataSeriesGroup(data, template);
			this.add(group);
			setStylesForNewData(group);
	}
	onSeriesPositionInLayerChanges();
	afterNumberOfDataSeriesChanges();
}

/**Adds one additional data series for the plot*/
protected void addNew(ColumnDataSeries data) {
	this.addManyNew(data);
	onPlotUpdate();
}


/***/
@Override
protected void afterNumberOfDataSeriesChanges() {
	setInDependantVariableAxisFor1DData(this.getAllDataSeries().size(), this.getInDependantVariableAxis());
	double max = PlotUtil.findMaxNeededValueIn(this.getAllDataShapes());
	this.setDependantVariableAxisBasedOnMax(max, false, getDependantVariableAxis());
}




@MenuItemMethod(menuActionCommand = "Flip axes", menuText = "Flip Axes", subMenuName="Edit")
public AxisFlipUndo axisFlips() {
	super.flipPlotOrientation();
		
	for(ColumnPlotDataSeriesGroup a: getAllDataSeries()) {
		a.setOrientation(orientation);
	}
	
	fullPlotUpdate();
	return new AxisFlipUndo(this);
}


public ArrayList<ColumnPlotDataSeriesGroup> getAllDataSeries() {
	return allData;
}

public void setAllData(ArrayList<ColumnPlotDataSeriesGroup> allData) {
	this.allData = allData;
}
















}