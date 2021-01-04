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
package kaplanMeierPlots;


import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import dataSeries.KaplenMeierDataSeries;
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

public class KaplanDataSeriesGroup extends BasicDataSeriesGroup implements HasUniquePopupMenu, PlotComponent{

	
	
	
	private KaplenMeierDataSeries data;
	//private int position;//The position on the plot,The data series on this plot are on a particular order
	private KaplanMeierLineShape kaplanLine;
	private KaplanMeierCensorShower censorMark;
	
	

	protected void setFieldsToAdded(DataShowingShape z) {
		super.setFieldsToAdded(z);
		if (z instanceof KaplanMeierCensorShower ) {censorMark=(KaplanMeierCensorShower ) z;}
		if (z instanceof KaplanMeierLineShape) kaplanLine= (KaplanMeierLineShape) z;
	}

	
	private KaplanDataSeriesGroup(String name) {
		super(name);
	}
	
	public KaplanDataSeriesGroup(KaplenMeierDataSeries data2, BasicDataSeriesGroup template) {
		super(data2.getName());
		this.data=data2;
		
		addPartsBasedOn(template);
	}



	public KaplanDataSeriesGroup(KaplenMeierDataSeries data) {
		super(data.getName());
		this.data=data;
		addStandardParts();
	}
	
	protected void addStandardParts() {
		 addKaplanLine() ;
		 addKaplanCensor();
	}

	
	protected ScatterPoints createScatter() {
		return null;
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

	
	public KaplenMeierDataSeries getDataSeries() {return data;}
	
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
	
	@MenuItemMethod(menuActionCommand = "Remove Line", menuText = "Line", subMenuName="Remove", orderRank=21, permissionMethod="getLine")
	public UndoAbleEditForRemoveItem removeLine() {
		if (kaplanLine!=null&&hasItem(kaplanLine)) {
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, kaplanLine);
			remove(kaplanLine);
			return undo;
		}
		return null;
	}
	
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
	
	public ArrayList<DataShowingShape> getDataShapes() {
		ArrayList<DataShowingShape> out = super.getDataShapes();
		out.add(kaplanLine);
		out.add(censorMark);
		return out;
	}
	
	@Override
	public DataLineShape getFunctionLine() {
		return kaplanLine;
	}
	public KaplanMeierLineShape getKaplanLine() {
		return kaplanLine;
	}
	public void setKaplanLine(KaplanMeierLineShape lineReg) {
		this.kaplanLine = lineReg;
	}
	
	
	
	public KaplanMeierCensorShower getCensorMark() {return censorMark;}
	
	public AbstractUndoableEdit addDataBar() {return null;}
	public AbstractUndoableEdit addScatter() {return null;}
	public CombinedEdit addErrorBar() {return null;}
	public CombinedEdit addBoxPlot() {return null;}
	public CombinedEdit addLine() {return null;}

	

	
	
	
}
