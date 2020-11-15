package xyPlots;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import dataSeries.BasicDataPoint;
import dataSeries.XYDataSeries;
import dataTableDialogs.SeriesInoutForGroupPlots;
import dataTableDialogs.SmartDataInputDialog;
import fLexibleUIKit.MenuItemMethod;
import fileread.ExcelFileToXYPlot;
import genericPlot.BasicPlot;
import genericPlot.BasicDataSeriesGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import gridLayout.GridLayoutEditListener;
import gridLayout.MontageSpaces;
import menuUtil.HasUniquePopupMenu;
import plotParts.Core.PlotArea;
import plotParts.DataShowingParts.DataBarShape;
import plotParts.DataShowingParts.ErrorBarShowingShape;
import plotParts.DataShowingParts.ScatterPoints;
import undo.CombinedEdit;

/**A special layer for a plot with single dimensional data*/
public class XY_Plot extends BasicPlot implements PlotArea, HasUniquePopupMenu, MontageSpaces, GridLayoutEditListener {



	private ArrayList<XYPlotDataSeriesGroup> allData=new ArrayList<XYPlotDataSeriesGroup>();
	

	/**What to do when given the data for a scatter plot*/
	public XY_Plot(String name, XYDataSeries... numbers) {
		super(name);
		
		addManyNew( numbers);
		
		
		onConstruction();
			
	}


	private void onConstruction() {
		addTitleLabel();
		

		XYPlotDataSeriesGroup primarySeries = this.getAllDataSeries().get(0);
		 addYAxiLabel();
		 addXAxiLabel(4);
		 this.xLabel.getParagraph().get(0).get(0).setText(primarySeries.getDataSeries().getxName());
		 this.yLabel.getParagraph().get(0).get(0).setText(primarySeries.getDataSeries().getDependantVariableName());
		 titleLabel.getParagraph().get(0).get(0).setText(getName() );
		 
		//xAxis.setShowText(false);
		 //xAxis.setIntergerTics(true);
		 this.resetMinMax(true);
		
		 //xAxis.setIntergerTics(false);
		 
		 onPlotUpdate();
		 if (this.getAllDataSeries().size()>1) {createFigureLegends();}
	}


	public XY_Plot(String name, ArrayList<XYDataSeries> items) {
		super(name);
		
		addManyNew(items.toArray(new XYDataSeries[items.size()]));
		
		
		onConstruction();
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Called after a new data series is added to the layer*/
	protected void afterSeriesAdditionToLayer(BasicDataSeriesGroup g) {
		if (g instanceof XYPlotDataSeriesGroup) {
			XYPlotDataSeriesGroup d=(XYPlotDataSeriesGroup) g;
			nSeries++;
			getAllDataSeries().add(d);
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
			if (layer instanceof XYPlotDataSeriesGroup) {
				XYPlotDataSeriesGroup d=(XYPlotDataSeriesGroup) layer;
				//d.setPosition(nSeries);
				nSeries++;
				d.onPlotUpdate();
				d.onAxisUpdate();
			}
		}
	}
	




	

@MenuItemMethod(menuActionCommand = "Add Data", menuText = "New Data Series", subMenuName="Data", orderRank=19)
public void addDataSeriesFromUser() {
	XYDataSeries dat2 = SeriesInoutForGroupPlots.getUserPointDataData(new XYDataSeries(new ArrayList<BasicDataPoint> ()));
	
	addNew(dat2);
}

@MenuItemMethod(menuActionCommand = "Add Data File", menuText = "New Data Series From Excel File", subMenuName="Data", orderRank=18)
public void addDataSeriesFromFile() {
	ArrayList<XYDataSeries> newseries = new ExcelFileToXYPlot(0).readExcelDataXY();
	addManyNew( newseries.toArray(new XYDataSeries[newseries.size()] ) );
}

protected void addManyNew(XYDataSeries... numbers) {
	
	
	for(XYDataSeries data: numbers) {
		if (data==null||data.length()<1) continue;
		XYPlotDataSeriesGroup template=null;
		int sizeList=this.getAllDataSeries().size();
		if (sizeList>0) template=getAllDataSeries().get(sizeList-1);
		XYPlotDataSeriesGroup group = new XYPlotDataSeriesGroup(data, template);
		if (template!=null) group.addLegandPart(template.getLegandShape(), template.getSeriesLabel());
		this.add(group);
			//IssueLog.log("currently have "+this.getAllDataSeries().size());
			setStylesForNewData(group);
	}
	
	onSeriesPositionInLayerChanges();
	afterNumberOfDataSeriesChanges();
}

/**Adds one additional data series for the plot*/
protected void addNew(XYDataSeries data) {
	this.addManyNew(data);
	onPlotUpdate();
}


/***/
@Override
protected void afterNumberOfDataSeriesChanges() {
	this.resetMinMax(false);
}


@MenuItemMethod(menuActionCommand = "To default", menuText = "Make Default Plot", subMenuName="Change Format", orderRank=1)
public CombinedEdit defaultPlot() {
	CombinedEdit undo = new CombinedEdit();
	for(XYPlotDataSeriesGroup a: getAllDataSeries()) {
		if (a.getLine()==null) undo.addEditToList(
				a.addLine());

		undo.addEditToList(
				forceErrorBarToForm(a, ErrorBarShowingShape.SEM));
		undo.addEditToList(
				a.removeBoxplot());
		undo.addEditToList(
				a.removeScatter());
		undo.addEditToList(
				forceBarToForm(a, DataBarShape.SinglePoint));

	}
	fullPlotUpdate();
	return undo;
}

/**Edits the parts of each data series as to create a proper scatterplot*/
@MenuItemMethod(menuActionCommand = "To Scatter Plot", menuText = "Make Scatter", subMenuName="Change Format", orderRank=3)
public AbstractUndoableEdit scatterPlot() {
	CombinedEdit undo = new CombinedEdit();
	for(XYPlotDataSeriesGroup a: getAllDataSeries()) {
		
		undo.addEditToList(
				a.removeBoxplot());
		undo.addEditToList(
				a.removeErrorBar());
		undo.addEditToList(
				this.forceScatterBarToExclusion(a, ScatterPoints.NO_Exclusion));	
		undo.addEditToList(
				forceBarToForm(a, DataBarShape.Ghost));

	}
	fullPlotUpdate();
	return null;
}



@MenuItemMethod(menuActionCommand = "To Line", menuText = "Make Line Plot", subMenuName="Change Format", orderRank=5)
public void linePlot() {
	CombinedEdit undo = new CombinedEdit();
	for(XYPlotDataSeriesGroup a: getAllDataSeries()) {
	  if (a.getLine()==null)
		undo.addEditToList(
				a.addLine());
		undo.addEditToList(
				a.removeBoxplot());
		undo.addEditToList(
				a.removeScatter());
		undo.addEditToList(
				forceBarToForm(a, DataBarShape.SinglePoint));
		undo.addEditToList(
				forceErrorBarToForm(a, ErrorBarShowingShape.SEM));
	}
	fullPlotUpdate();
}

public void fullPlotUpdate() {
	this.onPlotUpdate();
	this.onAxisUpdate();
}




public ArrayList<XYPlotDataSeriesGroup> getAllDataSeries() {
	return allData;
}

public void setAllData(ArrayList<XYPlotDataSeriesGroup> allData) {
	this.allData = allData;
}



@MenuItemMethod(menuActionCommand = "Remove Lines", menuText = "Lines", subMenuName="Remove")
public CombinedEdit removeLines() {
	CombinedEdit undo = new CombinedEdit();
	for(XYPlotDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.removeLine());;;;}
	return undo;
}

@MenuItemMethod(menuActionCommand = "Add Lines", menuText = "New Lines", subMenuName="Add")
public CombinedEdit addLine() {
	CombinedEdit undo = new CombinedEdit();
	for(XYPlotDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.addLine());;}
	return undo;
}

@MenuItemMethod(menuActionCommand = "Add Legends", menuText = "New Figure Legends", subMenuName="Add")
public void createFigureLegends() {
	super.createFigureLegends();
}


/**Replaces the data with new input data*/
@MenuItemMethod(menuActionCommand = "Replace Data", menuText = "Replace data", subMenuName="Data", orderRank=23)
public void replaceDataWithSeriesFromUser() {

	ArrayList<XYPlotDataSeriesGroup> olderSeries = this.getAllDataSeries();
	
	ArrayList<XYDataSeries> cols = new ArrayList<XYDataSeries>();
	for(XYPlotDataSeriesGroup o: olderSeries) {cols.add(o.getDataSeries());}
	SmartDataInputDialog d2 = SmartDataInputDialog.createXYDataDialogFrom(cols);
	d2.setModal(true);d2.setWindowCentered(true);
	d2.showDialog();
	cols=d2.getXYDataSeriesUsingClassificationColumn(0, 1, 2);
	
	replaceData(olderSeries, cols);
	
}


private void replaceData(ArrayList<XYPlotDataSeriesGroup> olderSeries, ArrayList<XYDataSeries> cols) {
	for(int i=0; i<cols.size()||i<olderSeries.size(); i++) {
		
		XYDataSeries  novel = null;
		if (i<cols.size()) novel=cols.get(i);
		
		/**if Replacement need be done*/
		if (i<cols.size()&&i<olderSeries.size()) {
			XYDataSeries old = olderSeries.get(i).getDataSeries();
			
			boolean sameName = (old.getName().equals(novel.getName()));
			old.replaceData(novel);
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

}
