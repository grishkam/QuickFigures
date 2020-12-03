/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package xyPlots;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import dataSeries.XYDataSeries;
import dataTableDialogs.SeriesInoutForGroupPlots;
import fLexibleUIKit.MenuItemMethod;
import genericPlot.BasicDataSeriesGroup;
import menuUtil.HasUniquePopupMenu;
import plotParts.DataShowingParts.DataLineShape;
import plotParts.DataShowingParts.DataShowingShape;
import plotParts.DataShowingParts.PlotComponent;
import plotParts.DataShowingParts.RegressionLineShape;
import plotParts.DataShowingParts.ScatterPoints;
import plotParts.DataShowingParts.SeriesLabel;
import plotParts.DataShowingParts.SeriesLabelPositionAnchor;
import undo.CombinedEdit;
import undo.UndoAbleEditForRemoveItem;
import undo.UndoAddItem;

public class XYPlotDataSeriesGroup extends BasicDataSeriesGroup implements HasUniquePopupMenu, PlotComponent{

	
	
	
	private XYDataSeries data;
	private RegressionLineShape lineReg;
	
	private XYPlotDataSeriesGroup(String name) {
		super(name);
	}
	
	public XYPlotDataSeriesGroup(XYDataSeries data2, BasicDataSeriesGroup template) {
		super(data2.getName());
		this.data=data2;
		
		addPartsBasedOn(template);
	}


	public XYPlotDataSeriesGroup(XYDataSeries data) {
		super(data.getName());
		this.data=data;
		addStandardParts();
	}
	
	protected void addStandardParts() {
		addScatter();
	}
	
	protected ScatterPoints createScatter() {
		return new ScatterPoints(getDataSeries(), false);
	}

	@MenuItemMethod(menuActionCommand = "Edit data", menuText = "Input New Data", subMenuName="Data", orderRank=100)
	public void showDataEditDialog() {

		XYDataSeries datanew = SeriesInoutForGroupPlots.getUserPointDataData(getDataSeries());

		data.replaceData(datanew);
		plotArea.autoCalculateAxisRanges();
		this.plotArea.fullPlotUpdate();
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	void setPositionOffset(int nthSeries) {
		position=nthSeries;
		if (this.getSeriesLabel()!=null) setFor(getSeriesLabel());
		data.setPositionOffset(position);
	}
	
	
	protected SeriesLabelPositionAnchor getSeriesLabelPositionAnchor() {
		return getLegandShape();
	}
	
	protected void createLabel() {
		this.seriesLabel=new SeriesLabel(this.getDataSeries().getName(), true);
	}
	
	protected void addPartsBasedOn(BasicDataSeriesGroup template) {
		if (template==null)	{addStandardParts(); return;}//without template, no more is needed

		addShapePartsBasedOn(template);
		
		if (template.getLegandShape()!=null&&template.getSeriesLabel()!=null ){
			this.addLegandPart(template.getLegandShape(), template.getSeriesLabel());
			//addLabel();
			//getSeriesLabel().copyAttributesFrom(template.getSeriesLabel());
		}
		
		if (template.getLine()!=null ){
			this.addLine();
			this.getLine().copyStrokeFrom(template.getLine());
		}
	}

	
	public XYDataSeries getDataSeries() {return data;}
	
	@MenuItemMethod(menuActionCommand = "New Regression Line", menuText = "New Regression Line", subMenuName="Add", orderRank=50)
	public CombinedEdit addRegressionLine() {
		AbstractUndoableEdit e1=null;
		if (lineReg!=null) e1=removeRegressionLine() ;
		lineReg=new RegressionLineShape(this.getDataSeries());
		setupDataShape(lineReg);
		add(lineReg);
		lineReg.onAxisUpdate();
		this.updateDisplay();
		
		add(lineReg.getInformationText());
		
		if (this.getStyle()!=null)this.getStyle().applyTo(line);
		return new CombinedEdit(e1, new UndoAddItem(this, line), new UndoAddItem(this, lineReg.getInformationText()));
	}
	
	@MenuItemMethod(menuActionCommand = "Remove Regression Line", menuText = "Regression Line", subMenuName="Remove", orderRank=31, permissionMethod="getRegressionLine")
	public CombinedEdit removeRegressionLine() {
		if (lineReg!=null&&hasItem(lineReg)) {
			UndoAbleEditForRemoveItem undo2 = new UndoAbleEditForRemoveItem(this, getRegressionLine().getInformationText());
			remove(getRegressionLine().getInformationText());
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, getRegressionLine());
			remove(lineReg);
			return new CombinedEdit(undo, undo2);
		}
		setLine(null);
		return null;
	}
	
	public RegressionLineShape getRegressionLine() {return lineReg;}
	
	protected void setFieldsToAdded(DataShowingShape z) {
		super.setFieldsToAdded(z);
		if (z instanceof RegressionLineShape) lineReg= (RegressionLineShape) z;
	}

	
	public ArrayList<DataShowingShape> getDataShapes() {
		ArrayList<DataShowingShape> out = super.getDataShapes();
		out.add(lineReg);
		return out;
	}

	
	public DataLineShape getFunctionLine() {
		return lineReg;
	}

}
