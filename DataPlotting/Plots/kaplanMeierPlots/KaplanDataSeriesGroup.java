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
 * Version: 2023.1
 */
package kaplanMeierPlots;


import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import dataSeries.KaplanMeierDataSeries;
import fLexibleUIKit.MenuItemMethod;
import genericPlot.BasicDataSeriesGroup;
import menuUtil.HasUniquePopupMenu;
import plotParts.DataShowingParts.DataLineShape;
import plotParts.DataShowingParts.DataShowingShape;
import plotParts.DataShowingParts.PlotComponent;
import plotParts.DataShowingParts.ScatterPoints;
import plotParts.DataShowingParts.SeriesLabel;
import plotParts.DataShowingParts.SeriesLabelPositionAnchor;
import undo.CombinedEdit;
import undo.UndoAbleEditForRemoveItem;
import undo.UndoAddItem;
/**A specialized layer that contains and organizes objects for displaying kaplan meier plots*/
public class KaplanDataSeriesGroup extends BasicDataSeriesGroup implements HasUniquePopupMenu, PlotComponent{

	
	private KaplanMeierDataSeries data;
	private KaplanMeierLineShape kaplanLine;
	private KaplanMeierCensorShower censorMark;
	
	
	
	private KaplanDataSeriesGroup(String name) {
		super(name);
	}
	
	public KaplanDataSeriesGroup(KaplanMeierDataSeries data2, BasicDataSeriesGroup template) {
		super(data2.getName());
		this.data=data2;
		
		addPartsBasedOn(template);
	}



	public KaplanDataSeriesGroup(KaplanMeierDataSeries data) {
		super(data.getName());
		this.data=data;
		addStandardParts();
	}
	
	
	/**called after a new data shape is added to the plot*/
	@Override
	protected void setFieldsToAdded(DataShowingShape z) {
		super.setFieldsToAdded(z);
		if (z instanceof KaplanMeierCensorShower ) {censorMark=(KaplanMeierCensorShower ) z;}
		if (z instanceof KaplanMeierLineShape) kaplanLine= (KaplanMeierLineShape) z;
	}

	/**Adds the line and censor marks for this plot*/
	@Override
	protected void addStandardParts() {
		 addKaplanLine() ;
		 addKaplanCensor();
	}

	
	

	@MenuItemMethod(menuActionCommand = "Edit data", menuText = "Input New Data", subMenuName="Data", orderRank=100)
	public void showDataEditDialog() {

		//XYDataSeries datanew = SeriesInoutForGroupPlots.getUserPointDataData(getDataSeries());

		//data.replaceData(datanew);
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
	
	/**adds plot parts to this data series such that it contains everything the argument contains
	 * @param template */
	@Override
	protected void addPartsBasedOn(BasicDataSeriesGroup template) {
		if (template==null)	{addStandardParts(); return;}//without template, no more is needed
		
		addKaplanLine() ;
		addKaplanCensor();
		if (template.getLegandShape()!=null&&template.getSeriesLabel()!=null ){
			this.addLegandPart(template.getLegandShape(), template.getSeriesLabel());
			//addLabel();
			//getSeriesLabel().copyAttributesFrom(template.getSeriesLabel());
		}
	
	
	}

	/**returns the data series*/
	public KaplanMeierDataSeries getDataSeries() {return data;}
	
	/**adds a line for the survival curve, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "New KaplanMeier Line", menuText = "New Kaplan-Meier Line", subMenuName="Add", orderRank=50)
	public CombinedEdit addKaplanLine() {
		AbstractUndoableEdit e1=null;
		if (kaplanLine!=null) e1=removeKaplanLine() ;
		kaplanLine=new KaplanMeierLineShape(this.getDataSeries());
		setupDataShape(kaplanLine);
		add(kaplanLine);
		kaplanLine.onAxisUpdate();
		this.updateDisplay();
		
		
		if (this.getStyle()!=null)this.getStyle().applyTo(kaplanLine);
		return new CombinedEdit(e1, new UndoAddItem(this, kaplanLine));
	}
	
	/**removes line for the survival curve, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Remove Line", menuText = "Line", subMenuName="Remove", orderRank=21, permissionMethod="getLine")
	public UndoAbleEditForRemoveItem removeLine() {
		if (kaplanLine!=null&&hasItem(kaplanLine)) {
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, kaplanLine);
			remove(kaplanLine);
			return undo;
		}
		return null;
	}
	
	/**adds a censor mark for the survival curve, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "New Censor Indicator", menuText = "Censor Marks for Kaplan-Meier", subMenuName="Add", orderRank=50)
	public CombinedEdit addKaplanCensor() {
		AbstractUndoableEdit e1=null;
		if (censorMark!=null) e1=removeKaplanLine() ;
		censorMark=new KaplanMeierCensorShower(this.getDataSeries());
		setupDataShape(censorMark);
		add(censorMark);
		censorMark.onAxisUpdate();
		this.updateDisplay();
		
		
		if (this.getStyle()!=null)this.getStyle().applyTo(censorMark);
		return new CombinedEdit(e1, new UndoAddItem(this, censorMark));
	}
	
	
	/**removes a line for the survival curve, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Remove Regression Line", menuText = "Line", subMenuName="Remove", orderRank=31, permissionMethod="getKaplanLine")
	public UndoAbleEditForRemoveItem removeKaplanLine() {
		if (kaplanLine!=null&&hasItem(kaplanLine)) {
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, kaplanLine);
			remove(kaplanLine);
			return undo;
		}
		setKaplanLine(null);
		return null;
	}
	
	/**returns the data shapes for this group*/
	@Override
	public ArrayList<DataShowingShape> getDataShapes() {
		ArrayList<DataShowingShape> out = super.getDataShapes();
		out.add(kaplanLine);
		out.add(censorMark);
		return out;
	}
	
	/**returns the line for the survival curve*/
	@Override
	public DataLineShape getFunctionLine() {
		return kaplanLine;
	}
	
	/**returns the line for the survival curve*/
	public KaplanMeierLineShape getKaplanLine() {
		return kaplanLine;
	}
	
	/**sets the line for the survival curve*/
	public void setKaplanLine(KaplanMeierLineShape lineReg) {
		this.kaplanLine = lineReg;
	}
	
	
	
	public KaplanMeierCensorShower getCensorMark() {return censorMark;}
	
	/**kaplan plot type does not contain data bars, scatter points or many other items from the */
	public AbstractUndoableEdit addDataBar() {return null;}
	public AbstractUndoableEdit addScatter() {return null;}
	public CombinedEdit addErrorBar() {return null;}
	public CombinedEdit addBoxPlot() {return null;}
	public CombinedEdit addLine() {return null;}
	protected ScatterPoints createScatter() {
		return null;
	}
	

	
	
	
}
