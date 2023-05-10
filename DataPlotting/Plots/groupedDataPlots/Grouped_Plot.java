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
 * Version: 2023.2
 */
package groupedDataPlots;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import dataSeries.Basic1DDataSeries;
import dataSeries.GroupedDataSeries;
import dataTableDialogs.SeriesInoutForGroupPlots;
import dataTableDialogs.SmartDataInputDialog;
import dialogs.CategoryOrderDialog;
import dialogs.MeanBarDialog;
import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import genericPlot.BasicDataSeriesGroup;
import genericPlot.BasicPlot;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ShapeGraphic;
import logging.IssueLog;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import plotParts.Core.AxesGraphic;
import plotParts.Core.PlotAreaRectangle;
import plotParts.DataShowingParts.DataBarShape;
import plotParts.DataShowingParts.DataShowingShape;
import plotParts.DataShowingParts.ErrorBarShowingShape;
import plotParts.DataShowingParts.PlotLabel;
import plotParts.DataShowingParts.PlotUtil;
import plotParts.DataShowingParts.ScatterPoints;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import standardDialog.numbers.NumberInputPanel;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undoForPlots.AxisFlipUndo;
import undoForPlots.PlotAreaChangeUndo;
import utilityClasses1.ArraySorter;

/**A class for organizing parts of a plot with data divided into multiple groups
Has methods to produce a layout, and a pair of axes*/
public class Grouped_Plot extends BasicPlot{

	/**
	 * 
	 */
	public static final int STAGGERED_BARS=0, STACKED_BARS=1, JITTER_POINTS=2, SEQUENTIAL_BARS=3;
	
	private int type=STACKED_BARS;
	
	
	
	
	ArrayList<GroupedPlotDataSeriesGroup> allData=new ArrayList<GroupedPlotDataSeriesGroup> ();
	HashMap<String, PlotLabel> categoryLabels=new HashMap<String, PlotLabel>();

	/**The bar spacing for stggered bars*/
	private int spacing=3;
	
	public Grouped_Plot(String name, ArrayList<GroupedDataSeries> items) {
		super(name);
		addManyNew(items.toArray(new GroupedDataSeries[items.size()] ));
		 onConstruction();
	}
	
	/**peforms the setup process for plot labels*/
	public void setUpPlotLabels() {
		if (allData.size()>0) {
			setUpPlotLabels(allData.get(0).getTheData().getCategoryToLocationMap());
		}
	}
	/**peforms the setup process for plot labels*/
	public void setUpPlotLabels(HashMap<Double, String> map) {
		PlotLabel lastLabel=null;
		for(Double d: map.keySet()) {
			String v=map.get(d);
			PlotLabel label1 = categoryLabels.get(v);
			if (label1==null) {
				label1=new PlotLabel(v);
				 categoryLabels.put(v, label1);
				 super.areaRect.addLockedItem(label1);
				 label1.setPlotOrientation(orientation);
				 label1.setPlotArea(this);
				 if (lastLabel==null)lastLabel=label1; else label1.setAttachmentPosition(lastLabel.getAttachmentPosition());
				 this.add(label1);
			
			}
			label1.setPosition(d);
			label1.putIntoAnchorPosition();
			
		}
	}
	
	/**removes any plot labels that are not stored within the map*/
	public void clearUnusedPlotLabels(HashMap<Double, String> map) {
		map=allData.get(0).getTheData().getCategoryToLocationMap();
	
		ArrayList<String> deadLabelList=new ArrayList<String>();
		
		for(String label: categoryLabels.keySet()) {
			if (map.values().contains(label)) continue;
			this.remove(categoryLabels.get(label));
			deadLabelList.add(label);
		}
		
		for(String label: deadLabelList) {
			categoryLabels.remove(label);
		}
		
	}
	
	
	private static final long serialVersionUID = 1L;

	
	/**Adds new layers and objects to dipict the data series given*/
	protected void addManyNew(GroupedDataSeries... numbers) {
		
		for(GroupedDataSeries data: numbers) {
			if (data==null||data.length()<1) continue;
			GroupedPlotDataSeriesGroup template=null;
			if (this.getAllDataSeries().size()>0) template=getAllDataSeries().get(getAllDataSeries().size()-1);
			GroupedPlotDataSeriesGroup group = new GroupedPlotDataSeriesGroup(data, template);
				this.add(group);
			setStylesForNewData(group);
		}
		onSeriesPositionInLayerChanges();
		afterNumberOfDataSeriesChanges();
	}
	
	/**setups the starting labels and axes for the plot*/
	private void onConstruction() {
		addTitleLabel();
		

		GroupedPlotDataSeriesGroup primarySeries = this.getAllDataSeries().get(0);
		 addYAxiLabel();
		 addXAxiLabel(54);
		 this.xLabel.getParagraph().get(0).get(0).setText(primarySeries.getDataSeries().getxName());
		 this.yLabel.getParagraph().get(0).get(0).setText(primarySeries.getDataSeries().getyName());
		 getTitleLabel().getParagraph().get(0).get(0).setText(getName() );
		  expandPlotToFitMeanBar();
		//xAxis.setShowText(false);
		 //xAxis.setIntergerTics(true);
		 this.resetMinMax(true);
		 this.updateOffsets();
		 this.setUpPlotLabels();
		 moveAxisLabelsOutOfWay();
		
		 onPlotUpdate();
		 if (this.getAllDataSeries().size()>1) {createFigureLegends();}
	}
	
	/**Returns the labels under the bars of the plot*/
	public ArrayList<PlotLabel> getBarLabels() {
		ArrayList<PlotLabel> out = new  ArrayList<PlotLabel> ();
		out.addAll(categoryLabels.values());
		return out;
	} 

	/**calculates how wide the plot will need to be to fit all of its bars*/
	public int getNeededWidthOfPlot() {
		if (this.type==STACKED_BARS) {
			return super.getNeededWidthOfPlot()/this.getAllDataSeries().size()+
					(this.getMeanBars().size()-1)*2;
		}
		return super.getNeededWidthOfPlot();
	}
	
	/**Called to create new figure legends, annotation indicates that it should be called by a popup menu
	 * @return */
	@MenuItemMethod(menuActionCommand = "Add Legends", menuText = "New Figure Legends", subMenuName="Add")
	public CombinedEdit createFigureLegends() {
		return super.createFigureLegends();
	}

	

	/**Sets the positions of the data series. This determines
	  the order that the data series are displayed in.
	  Since it is called whenever they are added, removed
	  or swapped within the layer, they are in practice, constantly updated*/
	public void onSeriesPositionInLayerChanges() {
		nSeries=1;
		for(GraphicLayer layer: this.getSubLayers()) {
			if (layer instanceof GroupedPlotDataSeriesGroup) {
				//ComplexPlotDataSeriesGroup d=(ComplexPlotDataSeriesGroup) layer;
				
				nSeries++;
				
			}
		}
		updateOffsets() ;
		onPlotUpdate();
		onAxisUpdate();
	}
	
	/**called after a data series is added or removed from the plot,
	   might update a figure legend or axis */
	protected void afterNumberOfDataSeriesChanges() {
		updateOffsets();
		setInDependantVariableAxisFor1DData(this.getInDependantVariableAxis());
		super.afterNumberOfDataSeriesChanges();
		}

	/**calculates the locations for of the different groupd on the plot
	 * staggers the bars of the data sets so they dont overlap*/
	public void updateOffsets() {
		ArrayList<GroupedPlotDataSeriesGroup> orderedList = seriesInOrder();
		double totalWidth=0;
		
		for(GroupedPlotDataSeriesGroup o: orderedList) {
			if (o.getDataBar()!=null&&o.getDataBar().getBarType()!=DataBarShape.GHOST) {
				totalWidth+=o.getDataBar().getBarWidth()*2;
			}
			else if (o.getBoxPlot()!=null) {
				totalWidth+=o.getBoxPlot().getBarWidth()*2;
				
				}
		}
		
		
		if (getGroupedPlotType()==STAGGERED_BARS) {
			double minOffset=-(totalWidth+spacing*orderedList.size())/2+totalWidth*0.5/orderedList.size();
			for(GroupedPlotDataSeriesGroup o: orderedList) {
				o.getDataSeries().setPositionOffset(minOffset);
				o.getDataSeries().setValueOffsetMap(null);
				if (o.getDataBar()!=null&&o.getDataBar().getBarType()!=DataBarShape.GHOST) {
					minOffset+=o.getDataBar().getBarWidth()*2+spacing;
					
				} else
					if (o.getBoxPlot()!=null) 		{
						minOffset+=o.getBoxPlot().getBarWidth()*2+spacing;
						}
			}
		}
		
		if (getGroupedPlotType()==JITTER_POINTS) {
			for(GroupedPlotDataSeriesGroup o: orderedList) {
				o.getDataSeries().setPositionOffset(0);
				o.getDataSeries().setValueOffsetMap(null);
			}
		}
				if (getGroupedPlotType()==SEQUENTIAL_BARS) {
					//HashMap<Double, Double> vOffsets = new HashMap<Double, Double> ();
					for(int i=orderedList.size()-1; i>=0; i--) {
						//GroupedPlotDataSeriesGroup groupSeries = orderedList.get(i);
						GroupedDataSeries thedata = orderedList.get(i).getTheData();
						 thedata.setPositionOffset(0);
						 thedata.setValueOffsetMap(null);
						
						// double[] p =thedata.getAllPositions();
							double dist = super.getCordinateHandler().translate(2, 0, 0,0).getX()-getCordinateHandler().translate(1,0,0,0).getX();
								
							thedata.setPositionOffset((orderedList.size())*dist*(i));
						 
					}
					}
				
				if (getGroupedPlotType()==STACKED_BARS) {
					HashMap<Double, Double> vOffsets = new HashMap<Double, Double> ();
					for(int i=orderedList.size()-1; i>=0; i--) {
						GroupedDataSeries thedata = orderedList.get(i).getTheData();
						thedata.setValueOffsetMap( new HashMap<Double, Double> (vOffsets));
						thedata.setPositionOffset(0);
						
						double[] p =thedata.getAllPositions();
						
						for(double pos: p) {
							Double current = vOffsets.get(pos);
							if (current==null) current=0.0;
							/*adds the mean to this offset**/
							Basic1DDataSeries posval = thedata.getValuesForPosition(pos);
							if (posval!=null&&posval.length()>0) {current+=posval.getMean();}
											   vOffsets.put(pos, current);
						}
					}
					}
				if (orderedList.size()>0)
				setUpPlotLabels(orderedList.get(0).getTheData().getCategoryToLocationMap());
		}
		
	
	
	/**Returns the order of the data series as they exist currently in the paret layer*/
	public ArrayList<GroupedPlotDataSeriesGroup> seriesInOrder() {
		ArrayList<GroupedPlotDataSeriesGroup> output = new ArrayList<GroupedPlotDataSeriesGroup> ();
		for(GraphicLayer layer: this.getSubLayers()) {
			if (layer instanceof GroupedPlotDataSeriesGroup) {
				output.add((GroupedPlotDataSeriesGroup) layer);
				
				
			}
		}
		return output;
	
	}
	
	/**Assuming the plot consistes of bars/boxes/points for 1D categories of 
	 * data, each category can be positioned as 1,2,3 est. This method sets
	 * the axis to be slightly larger than the number of categories.*/
	protected void setInDependantVariableAxisFor1DData(AxesGraphic xAxis) {
		int length=this.getAllDataSeries().size();
		xAxis.getAxisData().setMaxValue(length+1);
		xAxis.getAxisData().setMinValue(0.25);
		xAxis.getAxisData().setMinorTic(1);
		xAxis.setIntergerTics(true);
		if (length>0) 
			{
			GroupedPlotDataSeriesGroup ser0 = this.getAllDataSeries().get(0);
			this.setUpPlotLabels(ser0.getTheData().getCategoryToLocationMap());
			xAxis.setShowText(false);
			//xAxis.setAlternateNames();
			//xAxis.getLabelText().setAngle(Math.PI/4);
			}
	}
	
	/**called after a data series is added to this plot layer*/
	@Override
	protected void afterSeriesAdditionToLayer(BasicDataSeriesGroup g) {
	
		if (g instanceof GroupedPlotDataSeriesGroup) {
			GroupedPlotDataSeriesGroup d=(GroupedPlotDataSeriesGroup) g;
			nSeries++;
			getAllDataSeries().add(d);
			this.resetMinMax(false);
			updateOffsets() ;
			onPlotUpdate();
			
		}
		
	}
	
	/**Automatically determines the axes based on the data series
	 * if the boolean is true, will also reset teh tic spacing*/
	public void resetMinMax(boolean ticDistance) {
		ArrayList<DataShowingShape> shapes = getAllDataShapes();
		double max = PlotUtil.findMaxNeededValueIn(shapes);
		setDependantVariableAxisBasedOnMax(max, ticDistance, this.getDependantVariableAxis());
		max = PlotUtil.findMaxNeededPositionFrom(shapes);
		this.setInDependantVariableAxisBasedOnMax(max+0.25, ticDistance, this.getInDependantVariableAxis());
	}

	@Override
	public ArrayList<GroupedPlotDataSeriesGroup> getAllDataSeries() {

		return allData;
	}

	/**changes the plot type to a taggered barplot, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "To Staggered Bars", menuText = "Make Staggered Bar Plot", subMenuName="Change Format", orderRank=2)
	public CombinedEdit defaultPlot() {
		CombinedEdit undo =barPlot();
		undo.addEditToList(
				barPlot());
		undo.addEditToList(
				forcePlotTypeTo(STAGGERED_BARS));
		undo.addEditToList( 
				setRangeToFitBars());
		
		this.fullPlotUpdate();
		
		/**this often results in more bars than can fit*/
		undo.addEditToList( 
				expandPlotToFitMeanBar());
		
		this.updateOffsets();
		this.onAxisUpdate();
		this.fullPlotUpdate();
		return undo;
	}

	/**changes the plot type to a plot with bars of different colors stacked atop each other, 
	 * annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "To Stacked Bars", menuText = "Make Stacked Bar Plot", subMenuName="Change Format", orderRank=2)
	public CombinedEdit stackedPlot() {
		CombinedEdit undo =barPlot();
		
		undo.addEditToList(
				forcePlotTypeTo(STACKED_BARS));
		undo.addEditToList(
				setRangeToFitBars());
		
		this.onAxisUpdate();
		this.fullPlotUpdate();
		return undo;
	}
	
	/**changes the plot type to a sequential bar plot type
	 * annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "To Sequential Bars", menuText = "Make Sequential Bar Plot", subMenuName="Change Format", orderRank=4)
	public CombinedEdit sequentialBarPlot() {
		CombinedEdit undo = barPlot();
		
		undo.addEditToList(
				forcePlotTypeTo(SEQUENTIAL_BARS));
		
		PlotAreaChangeUndo undo3 = new PlotAreaChangeUndo(this);
		
		
		this.autoCalculateAxisRanges();
		setRangeToFitBars();
		
		this.updateOffsets();
		this.onAxisUpdate();
		this.fullPlotUpdate();
		
		
		expandPlotToFitMeanBar();
		this.updateOffsets();
		this.onAxisUpdate();
		this.fullPlotUpdate();
		
		undo3.establishFinalState();
		undo.addEditToList(undo3);
		return undo;
		
	}
	
	/**changes the plot type*/
	private GroupedPlotTypeEdit forcePlotTypeTo( int targetPlotType) {
		GroupedPlotTypeEdit undo2 = new GroupedPlotTypeEdit(this);
		setGroupedPlotType(targetPlotType);
		this.updateOffsets();
		undo2.establishFinalState();
		return undo2;
	}
	
	
	/**Sets the range of the independant variable axis to what is needed for the current bars
	 * @return */
	protected PlotAreaChangeUndo setRangeToFitBars() {
		PlotAreaChangeUndo undo3 = new PlotAreaChangeUndo(this);
		/*calculates a maximum size*/
		double size = this.getAllDataSeries().size();
		size*=getAllDataSeries().get(0).getTheData().getAllPositions().length;
		 size+=1;
		if (getGroupedPlotType()!=SEQUENTIAL_BARS) size=getAllDataSeries().get(0).getTheData().getAllPositions().length;
		if (getGroupedPlotType()!=SEQUENTIAL_BARS) size+=0.75;
		getInDependantVariableAxis().getAxisData().setMaxValue(size);
		updateOffsets();
		undo3.establishFinalState();
		return undo3;
	}
	
	/**changes the plot type to one that shows scatter points*/
	@MenuItemMethod(menuActionCommand = "To Scatter Plot", menuText = "Make Scatter", subMenuName="Change Format", orderRank=6)
	public CombinedEdit scatterPlot() {
		GroupedPlotTypeEdit undo2 = new GroupedPlotTypeEdit(this);
		setGroupedPlotType(JITTER_POINTS);
		CombinedEdit undo = new CombinedEdit();//unfinished undo
		this.updateOffsets();
		for(BasicDataSeriesGroup a: getAllDataSeries()) {
			undo.addEditToList(
					a.removeBoxplot());
			undo.addEditToList(
					forceScatterBarToExclusion(a, ScatterPoints.NO_EXCLUSION));
			undo.addEditToList(
					forceBarToForm(a, DataBarShape.LINE_ONLY));
			undo.addEditToList(
					forceItemColors(a, a.getDataBar()));
			undo.addEditToList(
					forceErrorBarToForm(a, ErrorBarShowingShape.ErrorType.SEM));
			undo.addEditToList(
					forceItemColors(a, a.getErrorBar()));
		}
		fullPlotUpdate();
		undo2.establishFinalState();
		undo.addEditToList(undo2);
		return undo;
	}
	
	/**changes the colors for one object to match the style of a data series group*/
	private ColorEditUndo forceItemColors(BasicDataSeriesGroup a, ShapeGraphic s) {
		ColorEditUndo undo3 = new ColorEditUndo(s);
		s.setStrokeColor(a.getStyle().getColor().darker().darker().darker());
		undo3.establishFinalColors();
		return undo3;
	}
	
	/**changes the plot to a tukey boxplot, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "To Tukey", menuText = "Make Tukey Boxplot", subMenuName="Change Format", orderRank=12)
	public CombinedEdit tukeyBoxplotPlot() {
		CombinedEdit output = super.tukeyBoxplotPlot();
		output.addEditToList(
				forcePlotTypeTo(STAGGERED_BARS));
		return output;
	}

	/**changes the plot to a boxplot, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "To normal box", menuText = "Make Boxplot", subMenuName="Change Format", orderRank=11)
	public CombinedEdit normalBoxplotPlot() {
		
		CombinedEdit output =super.normalBoxplotPlot();
		
		output.addEditToList(
				forcePlotTypeTo(STAGGERED_BARS));
		
		this.fullPlotUpdate();
		return output;
	}

	/**work in progress, */
	public CombinedEdit barPlot() {
		return super.barPlot();
		/**CompoundEdit2 undo = new CompoundEdit2();
		for(GenericDataSeriesGroup a: getAllDataSeries()) {
			
			a.removeBoxplot();
			a.removeScatter();
			if (a.getDataBar()==null) a.addDataBar();
			a.getDataBar().setBarType(DataBarShape.Bar);
			if (a.getErrorBar()==null)
				a.addErrorBar();
			a.getErrorBar().setErrorDepiction(ErrorBarShowingShape.SEM);
		
			
		}
		fullPlotUpdate();*/
	}
	
	/**called after the plot axes are altered, updates the data*/
	@Override
	public void onAxisUpdate() {
		setLayoutToPlotArea();
		setUpPlotLabels();
			for(BasicDataSeriesGroup d: getAllDataSeries()) {
				
				d.onAxisUpdate();}
			updateOffsets();
		} 
	

	
	
	/**shows a dialog for adding a new data series to the plot, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Add Data", menuText = "New Data Series", subMenuName="Data", orderRank=19)
	public void addDataSeriesFromUser() {
		try {
			GroupedPlotDataSeriesGroup sample = this.getAllDataSeries().get(0);
			GroupedDataSeries sampledata = sample.getTheData();
			HashMap<Double, String> map = sampledata.getCategoryToLocationMap();
			GroupedDataSeries newseries = SeriesInoutForGroupPlots.getUserInputSeries(new GroupedDataSeries("new data", map), true);
			
			this.addManyNew( newseries);
			this.resetMinMax(false);
			this.fullPlotUpdate();
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	
	}
	
	
	/**shows a dialog for the user to add a category to the plot, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Add Category", menuText = "New Catagory", subMenuName="Data", orderRank=22)
	public void addCategoryFromUser() {
		try {
			ArrayList<String> seriesNames=new ArrayList<String>();
			for(GroupedPlotDataSeriesGroup data1: getAllDataSeries()) {
				seriesNames.add(data1.getName());
			}
			
			SeriesInoutForGroupPlots d = new SeriesInoutForGroupPlots("New category "+this.getAllDataSeries().size(), seriesNames.toArray(new String[seriesNames.size()]));
			d.showDialog();
			if (d.wasOKed()) {
				GroupedDataSeries newCategory = d.getInputSeries();
				String newName=newCategory.getName();
				
				for(GroupedPlotDataSeriesGroup eachSeries: this.getAllDataSeries()) {
					GroupedDataSeries data = eachSeries.getTheData();
					Basic1DDataSeries addition = newCategory.getValuesForPosition(data.getName()).getIncludedValues();
	
					data.addNewCategory(newName, addition);
				}
			}
			/**
			ComplexPlotDataSeriesGroup sample = this.getAllDataSeries().get(0);
			
			MultiCategorySeries sampledata = sample.getTheData();
			
			HashMap<Double, String> map = sampledata.getCategoryToLocationMap();
			MultiCategorySeries newseries = DataInputDialog2.getUserInputSeries(new MultiCategorySeries("new data", map), true);
			this.addManyNew( newseries);*/
			
			
			this.resetMinMax(false);
			this.fullPlotUpdate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	/**shows a dialog for the user to reorder the categories on the plot, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Reorder Category Data", menuText = "Change Category Order", subMenuName="Edit", orderRank=19)
	public void changeCategoryOrder() {
		HashMap<Double, String> map = getLocationMap();
		//Set<String> names = getAllDataSeries().get(0).getTheData().getAllSeriesNames();
		
		CategoryOrderDialog d = new CategoryOrderDialog(map, getAllDataSeries().get(0).getTheData().getAllSeriesNames());
		d.showDialog();
		boolean needsAxisChange=false;
		
		if (d.wasOKed()) {
			 try {
			/**Removes deleted categories first*/
			ArrayList<String> removed = d.getRemovedItems();
			ArrayList<String> freshAdd = d.getNewlyAddedItems();
			if (freshAdd.size()>0) {
				needsAxisChange=true;
			}
			ArrayList<Double> oldPositions=new 	ArrayList<Double> ();
			 oldPositions.addAll(map.keySet());
	
			for(Double position: oldPositions){
				String item = map.get(position);
				if (removed.contains(item)) 
					{
					map.remove(position); 
					map.remove(position, item);
					needsAxisChange=true;
					PlotLabel label = this.categoryLabels.get(item);
					categoryLabels.remove(item);
					this.remove(label);
					//
				}
				
				
				
				
			} 
			 }catch (Throwable t) {t.printStackTrace();}
		
			
			ArrayList<String> order = d.getNewOrder();
			
			imposeNewCategoryOrder(map, order);
			
			
		
			this.updateOffsets();
			if (needsAxisChange)resetIndependantVariableAxis(false);
			this.setUpPlotLabels();
			this.updateDisplay();
			this.onAxisUpdate();
			this.onPlotUpdate();
			this.fullPlotUpdate();
			this.updateDisplay();
		}
	}
	
	/**swaps the locations of two categoeis on the data plot*/
	public void swapCategorySpots(String name1, String name2) {
		ArrayList<String> order1 = this.getAllDataSeries().get(0).getTheData().getSeriesNamesInorder();
		if (!order1.contains(name1)) return ;
		if (!order1.contains(name2)) return ;
		new ArraySorter<String>().swapObjectPositionsInArray(name1, name2, order1);
		imposeNewCategoryOrder(getLocationMap(), order1);
	}
	
	/**reorders the categories on the plot*/
	private void imposeNewCategoryOrder(HashMap<Double, String> map, ArrayList<String> order) {
		map.clear();
		for(double di=0; di<order.size(); di++) {
			String s=order.get((int)di);
			map.put(di+1, s);
		}

		this.updateOffsets();
		this.setUpPlotLabels();
		this.updateDisplay();
		this.onAxisUpdate();
		this.onPlotUpdate();
		this.fullPlotUpdate();
	}
	
	/**returns a map that is used to keep track of the locations of each category*/
	private HashMap<Double, String> getLocationMap() {
		HashMap<Double, String> map1=null;
		for(GroupedPlotDataSeriesGroup d: getAllDataSeries()) {
			HashMap<Double, String> map2 = d.getTheData().getCategoryToLocationMap();
			if (map1==null) map1=map2;
			if (map2!=map1){ 
				
				IssueLog.log("map inconsistency issue?????");
				
			}
		}
		
		return map1;
	}
	
	/**shows a dialog for editing the bars of the plot, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Edit Plot Bar", menuText = "Data Bars", subMenuName="Edit")
	public void editMeanBar() {
		ArrayList<DataBarShape> bars = getMeanBars();
		if (bars.size()==0) return;
		MeanBarDialog d = new MeanBarDialog(bars.get(0), true);
		d.add("spacing", new NumberInputPanel("Bar spacing ", spacing, 0, 20));
		d.addAdditionalBars(bars);
		d.addDialogListener(new StandardDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				spacing=d.getNumberInt("spacing");
				updateOffsets();
				fullPlotUpdate();
			}});
		d.showDialog();
	}
	
	
	/**Flips the plot between vertical and horixontal orientations, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Flip axes", menuText = "Flip Axes", subMenuName="Edit")
	public AxisFlipUndo axisFlips() {
		flipPlotOrientation();
		for(BasicDataSeriesGroup a: getAllDataSeries()) {
			a.setOrientation(orientation);
		}
		fullPlotUpdate();
		return new AxisFlipUndo(this);
	}
	public int getGroupedPlotType() {
		return type;
	}
	public void setGroupedPlotType(int type) {
		this.type = type;
	}
	
	
	
	/**Replaces the data with new input data, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Replace Data", menuText = "Replace data", subMenuName="Data", orderRank=23)
	public void replaceDataWithSeriesFromUser() {

		ArrayList<GroupedPlotDataSeriesGroup> olderSeries = this.getAllDataSeries();
		
		ArrayList<GroupedDataSeries> cols = new ArrayList<GroupedDataSeries>();
		for(GroupedPlotDataSeriesGroup o: olderSeries) {cols.add(o.getDataSeries());}
		
		SmartDataInputDialog d2 = SmartDataInputDialog.createGroupedDataDialogFrom(cols);
		d2.setModal(true);d2.setWindowCentered(true);
		d2.showDialog();
		cols=d2.getCategoryDataSeriesUsingClassificationFolumn(0, 1, 2);
		
		replaceData(olderSeries, cols);
		
	}
	
	/**Replaces the data series with new data*/
	private void replaceData(ArrayList<GroupedPlotDataSeriesGroup> olderSeries, ArrayList<GroupedDataSeries> cols) {
		for(int i=0; i<cols.size()||i<olderSeries.size(); i++) {
			
			GroupedDataSeries novel = null;
			if (i<cols.size()) novel=cols.get(i);
			
			/**if Replacement need be done*/
			if (i<cols.size()&&i<olderSeries.size()) {
				GroupedDataSeries old = olderSeries.get(i).getDataSeries();
				
				boolean sameName = (old.getName().equals(novel.getName()));
				old.replaceData(novel, novel.getCategoryToLocationMap());
				old.setValueOffsetMap(novel.getValueOffsetMap());
				
				old.setName(novel.getName());
				if (!sameName) {
					olderSeries.get(i).getSeriesLabel().getParagraph().get(0).get(0).setText(novel.getName());
				}
				
				
			}
			
				if (!(i<cols.size())&&(i<olderSeries.size())) {
				this.remove(olderSeries.get(i));
			}
				
			if (i<cols.size()&&!(i<olderSeries.size())) {
				addManyNew(novel);
			}
			
		
		}
		
		clearUnusedPlotLabels(cols.get(0).getCategoryToLocationMap());
		setUpPlotLabels();
		this.updateOffsets();
		this.expandPlotToFitMeanBar();
		updateAxisRange();
	}
	
	
	
	/**called when the plot area is innitialized
	 * @return
	 */
	protected PlotAreaRectangle createStartingPlotArea() {
		return new  PlotAreaRectangle(this,new Rectangle(60, 50, startPlotWidth+75, startPlotHeight));
	}


}
