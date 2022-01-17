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
 * Date Modified: Feb 21, 2021
 * Version: 2022.0
 */
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
import layout.basicFigure.GridLayoutEditListener;
import layout.basicFigure.LayoutSpaces;
import menuUtil.HasUniquePopupMenu;
import plotParts.Core.PlotArea;
import plotParts.DataShowingParts.DataBarShape;
import plotParts.DataShowingParts.ErrorBarShowingShape;
import plotParts.DataShowingParts.ScatterPoints;
import undo.CombinedEdit;

/**A special layer for a plot with two dimensional data*/
public class XY_Plot extends BasicPlot implements PlotArea, HasUniquePopupMenu, LayoutSpaces, GridLayoutEditListener {



	private ArrayList<XYPlotDataSeriesGroup> allData=new ArrayList<XYPlotDataSeriesGroup>();
	

	/**What to do when given the data for a scatter plot*/
	public XY_Plot(String name, XYDataSeries... newData) {
		super(name);
		
		addManyNew( newData);
		
		
		onConstruction();
			
	}

	/**setups the starting labels and axes for the plot*/
	private void onConstruction() {
		addTitleLabel();
		

		XYPlotDataSeriesGroup primarySeries = this.getAllDataSeries().get(0);
		 addYAxiLabel();
		 addXAxiLabel(4);
		 this.xLabel.getParagraph().get(0).get(0).setText(primarySeries.getDataSeries().getxName());
		 this.yLabel.getParagraph().get(0).get(0).setText(primarySeries.getDataSeries().getDependantVariableName());
		 getTitleLabel().getParagraph().get(0).get(0).setText(getName() );
		 
		
		 this.resetMinMax(true);
		
		 
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
	ArrayList<XYDataSeries> newseries = new ExcelFileToXYPlot().readExcelDataXY();
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


/**called after data series are added to or removed from the plot*/
@Override
protected void afterNumberOfDataSeriesChanges() {
	this.resetMinMax(false);
}


/**changes the plot objects such that the plot format matches a line with error bars and points, annotation indicates that it should be called by a popup menu*/
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
				forceBarToForm(a, DataBarShape.SINGLE_POINT));

	}
	fullPlotUpdate();
	return undo;
}

/**Edits the parts of each data series as to create a proper scatterplot, annotation indicates that it should be called by a popup menu*/
@MenuItemMethod(menuActionCommand = "To Scatter Plot", menuText = "Make Scatter", subMenuName="Change Format", orderRank=3)
public AbstractUndoableEdit scatterPlot() {
	CombinedEdit undo = new CombinedEdit();
	for(XYPlotDataSeriesGroup a: getAllDataSeries()) {
		
		undo.addEditToList(
				a.removeBoxplot());
		undo.addEditToList(
				a.removeErrorBar());
		undo.addEditToList(
				this.forceScatterBarToExclusion(a, ScatterPoints.NO_EXCLUSION));	
		undo.addEditToList(
				forceBarToForm(a, DataBarShape.GHOST));

	}
	fullPlotUpdate();
	return null;
}


/**Edits the parts of each data series as to create a line plot, annotation indicates that it should be called by a popup menu*/
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
				forceBarToForm(a, DataBarShape.SINGLE_POINT));
		undo.addEditToList(
				forceErrorBarToForm(a, ErrorBarShowingShape.SEM));
	}
	fullPlotUpdate();
}

/**updates the shapes and axes to replect chanes in the settings for the plot*/
@Override
public void fullPlotUpdate() {
	this.onPlotUpdate();
	this.onAxisUpdate();
}



/**returns all the data series in the plot*/
public ArrayList<XYPlotDataSeriesGroup> getAllDataSeries() {
	return allData;
}

/**sets all the data series in the plot*/
public void setAllData(ArrayList<XYPlotDataSeriesGroup> allData) {
	this.allData = allData;
}



/**removes the lines for every data series, annotation indicates that it should be called by a popup menu*/
@MenuItemMethod(menuActionCommand = "Remove Lines", menuText = "Lines", subMenuName="Remove")
public CombinedEdit removeLines() {
	CombinedEdit undo = new CombinedEdit();
	for(XYPlotDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.removeLine());;;;}
	return undo;
}

/**adds lines for the xy data of every data series, annotation indicates that it should be called by a popup menu*/
@MenuItemMethod(menuActionCommand = "Add Lines", menuText = "New Lines", subMenuName="Add")
public CombinedEdit addLine() {
	CombinedEdit undo = new CombinedEdit();
	for(XYPlotDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.addLine());;}
	return undo;
}

/**creates the figure legend, annotation indicates that it should be called by a popup menu
 * @return */
@MenuItemMethod(menuActionCommand = "Add Legends", menuText = "New Figure Legends", subMenuName="Add")
public CombinedEdit createFigureLegends() {
	return super.createFigureLegends();
}


/**Replaces the data with new input data that is provided by the user via dialog, annotation indicates that it should be called by a popup menu*/
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

/**replaces the old data series with new data*/
private void replaceData(ArrayList<XYPlotDataSeriesGroup> olderSeries, ArrayList<XYDataSeries> newAddedData) {
	for(int i=0; i<newAddedData.size()||i<olderSeries.size(); i++) {
		
		XYDataSeries  novel = null;
		if (i<newAddedData.size()) novel=newAddedData.get(i);
		
		/**if Replacement need be done*/
		if (i<newAddedData.size()&&i<olderSeries.size()) {
			XYDataSeries old = olderSeries.get(i).getDataSeries();
			
			boolean sameName = (old.getName().equals(novel.getName()));
			old.replaceData(novel);
			old.setName(novel.getName());
			if (!sameName) {
				olderSeries.get(i).getSeriesLabel().getParagraph().get(0).get(0).setText(novel.getName());
			}
			
			
		}
		
		if (i<newAddedData.size()&&!(i<olderSeries.size())) {
			addNew(novel);
		}
		
		if (!(i<newAddedData.size())&&(i<olderSeries.size())) {
			this.remove(olderSeries.get(i));
		}
		
		
		
	}
	
	this.autoCalculateAxisRanges();
	this.fullPlotUpdate();
}

}
