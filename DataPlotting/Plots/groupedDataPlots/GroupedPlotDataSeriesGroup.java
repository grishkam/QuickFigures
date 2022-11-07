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
 * Version: 2022.2
 */
package groupedDataPlots;


import dataSeries.GroupedDataSeries;
import dataTableDialogs.SeriesInoutForGroupPlots;
import fLexibleUIKit.MenuItemMethod;
import genericPlot.BasicDataSeriesGroup;
import menuUtil.HasUniquePopupMenu;
import plotParts.DataShowingParts.PlotComponent;
import plotParts.DataShowingParts.ScatterPoints;
import plotParts.DataShowingParts.SeriesLabel;
import plotParts.DataShowingParts.SeriesLabelPositionAnchor;
/**A specialized layer that contains and organizes objects for displaying parts of a grouped plot*/
public class GroupedPlotDataSeriesGroup extends BasicDataSeriesGroup implements HasUniquePopupMenu, PlotComponent{

	/**The data*/
	private GroupedDataSeries data;

	private GroupedPlotDataSeriesGroup(String name) {
		super(name);
	}
	
	public GroupedPlotDataSeriesGroup(GroupedDataSeries data2, BasicDataSeriesGroup template) {
		super(data2.getName());
		this.settheData(data2);
		addPartsBasedOn(template);
	}

	public GroupedPlotDataSeriesGroup(GroupedDataSeries data) {
		super(data.getName());
		this.settheData(data);
		addStandardParts();
	}
	
	/**Adds the standard plot parts that are components of any grouped plot*/
	protected void addStandardParts() {
		this.addDataBar();
		this.addErrorBar();
	}

	/**creates the scatter points for this group*/
	@Override
	protected ScatterPoints createScatter() {
		return new ScatterPoints(getDataSeries(), true);
	}

	/**shows a data input dialog to the user, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Edit data", menuText = "Input New Data", subMenuName="Data", orderRank=100)
	public void showDataEditDialog() {
		GroupedDataSeries newdata = SeriesInoutForGroupPlots.getUserInputSeries(getDataSeries(), false);
		if (newdata==null) return;
		
		this.getTheData().replaceData(newdata);
		this.plotArea.autoCalculateAxisRanges();
		this.plotArea.onAxisUpdate();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**sets the position for this data series group*/
	void setPositionOffset(int newOffset) {
		position=newOffset;
		if (this.getSeriesLabel()!=null) setFor(getSeriesLabel());
		getTheData().setPositionOffset(position);
	}
	
	public GroupedDataSeries getDataSeries() {return getTheData();}

/**
	@MenuItemMethod(menuActionCommand = "New Line", menuText = "New Line", subMenuName="Add", orderRank=0)
	public void addLine() {
		if (getLine()!=null) this.remove(getLine());
		setLine(new MeanLineShape(getDataSeries()));
		if (this.getStyle()!=null)this.getStyle().applyTo(getLine());
		setFor(getLine());
		this.add(getLine());
		getLine().demandShapeUpdate();
		getLine().setStrokeColor(groupColor.darker().darker());
		getLine().setFillColor(new Color(255,255,255, 0));
	}
	
	@MenuItemMethod(menuActionCommand = "Remove Line", menuText = "Line", subMenuName="Remove", orderRank=0)
	public void removeLine() {
		if (getLine()!=null) this.remove(getLine());
		setLine(null);
	}

	
	public MeanLineShape getLine() {
		return line;
	}

	public void setLine(MeanLineShape line) {
		this.line = line;
	}*/
	

	/**returns the anchor object for the data series label,
	 * In the case of grouped plots, it is the figure legend shape*/
	protected SeriesLabelPositionAnchor getSeriesLabelPositionAnchor() {
		return getLegandShape();
	}
	
	public GroupedDataSeries getTheData() {
		return data;
	}

	public void settheData(GroupedDataSeries data) {
		this.data = data;
	}
	
	protected void addPartsBasedOn(BasicDataSeriesGroup template) {
		if (template==null)	{addStandardParts(); return;}//without template, no more is needed
		
		super.addShapePartsBasedOn(template);
		
		if (template.getLegandShape()!=null&&template.getSeriesLabel()!=null ){
			this.addLegandPart(template.getLegandShape(), template.getSeriesLabel());
		}
		
	}
	
	/**creates a data series label for this data group*/
	@Override
	protected void createLabel() {
		this.seriesLabel=new SeriesLabel(this.getDataSeries().getName(), true);
	}
	
}
