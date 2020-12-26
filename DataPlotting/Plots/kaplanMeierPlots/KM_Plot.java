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
package kaplanMeierPlots;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import dataSeries.KaplenMeierDataSeries;
import dataTableDialogs.SmartDataInputDialog;
import dialogs.CensorMarkDialog;
import fLexibleUIKit.MenuItemMethod;
import genericPlot.BasicPlot;
import genericPlot.BasicDataSeriesGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import layout.basicFigure.GridLayoutEditListener;
import layout.basicFigure.LayoutSpaces;
import menuUtil.HasUniquePopupMenu;
import plotParts.Core.PlotArea;
import undo.CombinedEdit;

/**A special layer for a plot with single dimensional data*/
public class KM_Plot extends BasicPlot implements PlotArea, HasUniquePopupMenu, LayoutSpaces, GridLayoutEditListener {



	private ArrayList<KaplanDataSeriesGroup> allData=new ArrayList<KaplanDataSeriesGroup>();
	

	/**What to do when given the data for a scatter plot*/
	public KM_Plot(String name, KaplenMeierDataSeries... numbers) {
		super(name);
		
		addManyNew( numbers);
		
		onConstruction();
			
	}


	private void onConstruction() {
		addTitleLabel();
		

		 addYAxiLabel();
		 addXAxiLabel(4);
		 titleLabel.getParagraph().get(0).get(0).setText(getName() );
		 
		
		 this.resetMinMax(true);
		
		 
		 onPlotUpdate();
		 if (this.getAllDataSeries().size()>1) {createFigureLegends();}
	}


	public KM_Plot(String name, ArrayList<KaplenMeierDataSeries> items) {
		super(name);
		if (items!=null)
		addManyNew(items.toArray(new KaplenMeierDataSeries[items.size()]));
		
		
		onConstruction();
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Called after a new data series is added to the layer*/
	protected void afterSeriesAdditionToLayer(BasicDataSeriesGroup g) {
		if (g instanceof KaplanDataSeriesGroup) {
			KaplanDataSeriesGroup d=(KaplanDataSeriesGroup) g;
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
			if (layer instanceof KaplanDataSeriesGroup) {
				KaplanDataSeriesGroup d=(KaplanDataSeriesGroup) layer;
				//d.setPosition(nSeries);
				nSeries++;
				d.onPlotUpdate();
				d.onAxisUpdate();
			}
		}
	}
	

@MenuItemMethod(menuActionCommand = "Edit Censor Marks", menuText = "Censor Marks", subMenuName="Edit")
public void editCensorMarkBar() {
	ArrayList<KaplanMeierCensorShower> bars = getCensorMarks();
	if (bars.size()==0) return;
	CensorMarkDialog d = new CensorMarkDialog(bars.get(0), true);
	d.addAdditionalBars(bars);
	d.showDialog();
}

private ArrayList<KaplanMeierCensorShower> getCensorMarks() {
	ArrayList<KaplanMeierCensorShower> marks=new ArrayList<KaplanMeierCensorShower>();
	for(KaplanDataSeriesGroup d: this.getAllDataSeries()) marks.add(d.getCensorMark());
	return marks;
}


//@MenuItemMethod(menuActionCommand = "Add Data", menuText = "New Data Series", subMenuName="Data", orderRank=19)
public void addDataSeriesFromUser() {
	//KaplenMeierDataSeries dat2 = SeriesInoutForGroupPlots.getUserPointDataData(new KaplenMeierDataSeries(new ArrayList<Point2D> ()));
	
	//addNew(dat2);
}

//@MenuItemMethod(menuActionCommand = "Add Data File", menuText = "New Data Series From Excel File", subMenuName="Data", orderRank=18)
public void addDataSeriesFromFile() {
	//ArrayList<KaplenMeierDataSeries> newseries = new ExcelFileToXYPlot(0).readExcelDataXY();
	//addManyNew( newseries.toArray(new KaplenMeierDataSeries[newseries.size()] ) );
}

protected void addManyNew(KaplenMeierDataSeries... numbers) {
	
	
	for(KaplenMeierDataSeries data: numbers) {
		if (data==null||data.length()<1) continue;
		
		KaplanDataSeriesGroup template=null;
		int sizeList=this.getAllDataSeries().size();
		if (sizeList>0) template=getAllDataSeries().get(sizeList-1);
		KaplanDataSeriesGroup group = new KaplanDataSeriesGroup(data, template);
		if (template!=null) group.addLegandPart(template.getLegandShape(), template.getSeriesLabel());
		this.add(group);
			//IssueLog.log("currently have "+this.getAllDataSeries().size());
			setStylesForNewData(group);
	}
	
	onSeriesPositionInLayerChanges();
	afterNumberOfDataSeriesChanges();
}

/**Adds one additional data series for the plot*/
protected void addNew(KaplenMeierDataSeries data) {
	this.addManyNew(data);
	onPlotUpdate();
}


/***/
@Override
protected void afterNumberOfDataSeriesChanges() {
	this.resetMinMax(false);
}


//@MenuItemMethod(menuActionCommand = "To default", menuText = "Make Default Plot", subMenuName="Change Format", orderRank=1)
public CombinedEdit defaultPlot() {
	CombinedEdit undo = new CombinedEdit();
	for(KaplanDataSeriesGroup a: getAllDataSeries()) {
	//not yet implemented
		a.getKaplanLine().setStrokeWidth(1);
		a.getCensorMark().setStrokeWidth(1);
	}
	fullPlotUpdate();
	return undo;
}


public void fullPlotUpdate() {
	this.onPlotUpdate();
	this.onAxisUpdate();
}




public ArrayList<KaplanDataSeriesGroup> getAllDataSeries() {
	return allData;
}

public void setAllData(ArrayList<KaplanDataSeriesGroup> allData) {
	this.allData = allData;
}



@MenuItemMethod(menuActionCommand = "Remove Lines", menuText = "Lines", subMenuName="Remove")
public CombinedEdit removeLines() {
	CombinedEdit undo = new CombinedEdit();
	for(KaplanDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.removeKaplanLine() );;;;}
	return undo;
}

@MenuItemMethod(menuActionCommand = "Add Lines", menuText = "New Lines", subMenuName="Add")
public CombinedEdit addLine() {
	CombinedEdit undo = new CombinedEdit();
	for(KaplanDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.addKaplanLine());;}
	return undo;
}

@MenuItemMethod(menuActionCommand = "Add Censors ", menuText = "New Censor Marks", subMenuName="Add")
public CombinedEdit addCensorMarke() {
	CombinedEdit undo = new CombinedEdit();
	for(KaplanDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.addKaplanCensor());;}
	return undo;
}

@MenuItemMethod(menuActionCommand = "Add Legends", menuText = "New Figure Legends", subMenuName="Add")
public void createFigureLegends() {
	super.createFigureLegends();
}


/**Replaces the data with new input data*/
@MenuItemMethod(menuActionCommand = "Replace Data", menuText = "Replace data", subMenuName="Data", orderRank=23)
public void replaceDataWithSeriesFromUser() {

	ArrayList<KaplanDataSeriesGroup> olderSeries = this.getAllDataSeries();
	
	ArrayList<KaplenMeierDataSeries> cols = new ArrayList<KaplenMeierDataSeries>();
	for(KaplanDataSeriesGroup o: olderSeries) {cols.add(o.getDataSeries());}
	SmartDataInputDialog d2 = SmartDataInputDialog.createKaplanDataDialogFrom(cols);
	d2.setModal(true);d2.setWindowCentered(true);
	d2.showDialog();
	cols=d2.getKaplanDataSeriesUsingDefaultClassification();
	
	replaceData(olderSeries, cols);
	this.resetMinMax(false);
	this.fullPlotUpdate();
}


private void replaceData(ArrayList<KaplanDataSeriesGroup> olderSeries, ArrayList<KaplenMeierDataSeries> cols) {
	for(int i=0; i<cols.size()||i<olderSeries.size(); i++) {
		
		KaplenMeierDataSeries  novel = null;
		if (i<cols.size()) novel=cols.get(i);
		
		/**if Replacement need be done*/
		if (i<cols.size()&&i<olderSeries.size()) {
			KaplenMeierDataSeries old = olderSeries.get(i).getDataSeries();
			
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

/**Overrides superclass methods*/
	public AbstractUndoableEdit addScatter() {return null;}
	public AbstractUndoableEdit addDataBar() {return null;}
	public AbstractUndoableEdit addErrorBar() {return null;}
	public AbstractUndoableEdit addBoxplotBar() {return null;}
	public void editErrorBar() {}
	public void editMeanBar() {}
	public void editScatterPoints() {}
	public void editBoxPlots() {}
	public void editBoxplots() {}
	public CombinedEdit barPlot() {return null;}
	public AbstractUndoableEdit scatterPlot() {return null;}
	public CombinedEdit normalBoxplotPlot() {return null;}
	public CombinedEdit tukeyBoxplotPlot() {return null;}
	public CombinedEdit removeDataBar() {return null;}
	public CombinedEdit removeErrorBar() {return null;}
	public CombinedEdit removeBoxplots() {return null;}
	public AbstractUndoableEdit removeScatter() {return null;}

}
