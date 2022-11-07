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
 * Version: 2022.2
 */
package genericPlot;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.undo.AbstractUndoableEdit;

import dataSeries.DataSeries;
import dialogs.SeriesDialog;
import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_SpecialObjects.TextGraphic;
import iconGraphicalObjects.IconUtil;
import locatedObject.AttachmentPosition;
import menuUtil.PopupMenuSupplier;
import plotParts.Core.AxesGraphic;
import plotParts.Core.PlotArea;
import plotParts.Core.PlotLayout;
import plotParts.Core.PlotOrientation;
import plotParts.DataShowingParts.Boxplot;
import plotParts.DataShowingParts.DataBarShape;
import plotParts.DataShowingParts.DataLineShape;
import plotParts.DataShowingParts.DataShowingShape;
import plotParts.DataShowingParts.ErrorBarShowingShape;
import plotParts.DataShowingParts.FigureLegendShape;
import plotParts.DataShowingParts.MeanLineShape;
import plotParts.DataShowingParts.PlotComponent;
import plotParts.DataShowingParts.PlotUtil;
import plotParts.DataShowingParts.ScatterPoints;
import plotParts.DataShowingParts.SeriesLabel;
import plotParts.DataShowingParts.SeriesLabelPositionAnchor;
import plotParts.DataShowingParts.SeriesStyle;
import undo.CombinedEdit;
import undo.UndoAbleEditForRemoveItem;
import undo.UndoAddItem;
import undoForPlots.AxisChangeUndo;
import undoForPlots.AxisResetUndoableEdit;

/**A specialized layer that contains and organizes objects for displaying a particular data series*/
public abstract class BasicDataSeriesGroup extends GraphicLayerPane implements PlotComponent {
	
	public static final int USE_FIRST_AXIS=0, USE_SECOND_AXIS=1;
	
	protected PlotArea plotArea;//which plot contains the data series
	protected PlotOrientation orientation=PlotOrientation.BARS_VERTICAL;//whether the plot is horizontal or vertical
	private int axisChoice=USE_FIRST_AXIS;//a plot may have more than one y axis (or more chan one x), this indicates which should be used
	
	/**The position on the plot,The data series on this plot are on a particular order
	   This order may not be consequential depending on the subclass and plot type
	   because it is only used by some subclasses*/
	protected int position;//
	
	
	protected SeriesLabel seriesLabel;
	
	/**a stored boxplot*/
	private Boxplot boxPlot;
	
	/**A stoted data bar*/
	private DataBarShape bar;
	
	/**A stored set of scatter points*/
	private ScatterPoints scatter;
	
	/**A stored error bar*/
	private ErrorBarShowingShape errorBar;
	
	/**an object for the figure legend*/
	private FigureLegendShape legandShape;
	
	/**A line drawn at the mean of the data*/
	protected MeanLineShape line;

	protected Color groupColor=Color.gray;
	private SeriesStyle style;
	
	
	public BasicDataSeriesGroup(String name) {
		super(name);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DataBarShape getDataBar() {
		if (!this.hasItem(bar)) return null;
		return bar;
	}

	public ErrorBarShowingShape getErrorBar() {
		if (!this.hasItem(errorBar)) return null;
		return errorBar;
	}

	@Override
	public double getMaxNeededValue() {
		ArrayList<DataShowingShape> shapes = getDataShapes();
		return PlotUtil.findMaxNeededValueIn(shapes);
	}


	/**returns the largest number that needs to be within the plot axes*/
	@Override
	public double getMaxNeededPosition() {
		ArrayList<DataShowingShape> shapes = getDataShapes();
		return  PlotUtil.findMaxNeededPositionFrom(shapes);
	}
	
	/**returns an array of data showing shapes*/
	public ArrayList<DataShowingShape> getDataShapes() {
		ArrayList<DataShowingShape> output=new ArrayList<DataShowingShape>();
		if (scatter!=null) output.add(scatter);
		if (bar!=null)output.add(bar);
		if (errorBar!=null)output.add(errorBar);
		if (getBoxPlot()!=null)output.add(getBoxPlot());
		if (line!=null) output.add(line);
		return output;
		
	}
	
	/**returns the scatter points*/
	public ScatterPoints getScatterPoints() {
		if (!hasItem(scatter)) return null;
		return scatter;
	}

	
	/**sets up the plot area for every shape*/
	public void setPlotArea(PlotArea plotLayers) {
		plotArea=plotLayers;
		for(DataShowingShape shape: this.getDataShapes()) {
			if (shape!=null)
			shape.setPlotArea(plotLayers);}
	}
	
	/**sets the data showing shape to display this data series*/
	public void setupDataShape(DataShowingShape d) {
		if (d==null) return;
		d.setTheData(getDataSeries());
		d.setOrientation(orientation);
		d.setPlotArea(plotArea);
		d.setAxisChoice(this.axisChoice);
	}
	
	/**Sets the color for this group*/
	public void setGroupColor(Color color) {
		
		groupColor=color;
	}
	
	/**Adds the data bar, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "New Data Bar", menuText = "New Data Bar", subMenuName="Add")
	public AbstractUndoableEdit addDataBar() {
		AbstractUndoableEdit e1=null;
		if (bar!=null) e1=removeDataBar();
		add(new DataBarShape(getDataSeries()));
		return new CombinedEdit(e1, new UndoAddItem(this, getDataBar()));
	}
	
	/**Adds the scatter points, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Add Scatter Plot", menuText = "New Scatter Points", subMenuName="Add")
	public AbstractUndoableEdit addScatter() {
		AbstractUndoableEdit e1=null;
		if (getScatterPoints()!=null) e1=removeScatter();
		add(createScatter());
		return new CombinedEdit(e1, new UndoAddItem(this, getScatterPoints()));
	}

	/**creates scatter points*/
	protected ScatterPoints createScatter() {
		return new ScatterPoints(getDataSeries(), false);
	}
	
	/**Adds the error bar, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Add Error Bar", menuText = "New Error Bar", subMenuName="Add")
	public CombinedEdit addErrorBar() {
		AbstractUndoableEdit e1=null;
		if (getErrorBar()!=null) e1=removeErrorBar();
		this.errorBar=new ErrorBarShowingShape(getDataSeries());
		add(errorBar);
		return new CombinedEdit(e1, new UndoAddItem(this, getErrorBar()));
	}
	
	
	/**removes the data bar, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Remove Data Bar", menuText = "Data Bar", subMenuName="Remove", orderRank=21, permissionMethod="getDataBar")
	public UndoAbleEditForRemoveItem removeDataBar() {
		if (bar!=null) {
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, bar);
			this.remove(bar);
		return undo;
			}
		return null;
	}
	
	/**removes the scatter point, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Remove Scatter Plot", menuText = "Scatter Points", subMenuName="Remove", orderRank=23, permissionMethod="getScatterPoints")
	public UndoAbleEditForRemoveItem removeScatter() {
		if (this.getScatterPoints()!=null){
					UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, scatter);
					remove(scatter);
					scatter=null;
					return undo;
							} 
		return null;
	}

	
	/**removes the shape showing the error bar, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Remove Error Bar", menuText = "Error Bar", subMenuName="Remove", orderRank=22, permissionMethod="getErrorBar")
	public UndoAbleEditForRemoveItem removeErrorBar() {
		if (this.errorBar!=null) 
			{
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, errorBar);
			remove(errorBar);
			this.errorBar=null;
			return undo;
		}
		return null;
	}
	

	/**adds a boxplot shape, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Add Box Plot", menuText = "New Boxplot", subMenuName="Add")
	public CombinedEdit addBoxPlot() {
		AbstractUndoableEdit e1=null;
		if (this.boxPlot!=null) e1=removeBoxplot() ;
		boxPlot=new Boxplot(this.getDataSeries());
		add(boxPlot);
		return new CombinedEdit(e1, new UndoAddItem(this, getBoxPlot()));
	}

	/**removes the boxplot shape, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Remove Boxplot", menuText = "Boxplot", subMenuName="Remove", orderRank=25, permissionMethod="getBoxPlot")
	public UndoAbleEditForRemoveItem removeBoxplot() {
		if (getBoxPlot()!=null) {
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, getBoxPlot());
			this.remove(boxPlot);
			return undo;
			}
		boxPlot=null;
		return null;
	}
	
	/**Sets the y axis used to the seconday one*/
	@MenuItemMethod(menuActionCommand = "Edit axis to secondary", menuText = "Use Secondary Y-axis", subMenuName="Axis", orderRank=100, permissionMethod="isSecondaryAxisAvailable")
	public AxisChangeUndo toSecondaryY() {
		return setAxis(USE_SECOND_AXIS);
	}
	/**Sets the y axis used to the primary one*/
	@MenuItemMethod(menuActionCommand = "Edit axis to primary", menuText = "Use Main Y-axis", subMenuName="Axis", orderRank=100, permissionMethod="isPrimaryAxisAvailable")
	public AxisChangeUndo toPrimaryY() {
		return setAxis(USE_FIRST_AXIS);
	}
	
	/**sets the axis used*/
	public AxisChangeUndo setAxis(int i) {
		AxisChangeUndo undo = new AxisChangeUndo(this, axisChoice);
		this.axisChoice=i;
		undo.setFinalAxis(axisChoice);
		refreshShapes();
		return undo;
	}
	
	/**returns true if the user has the option to swith to a secondary axis*/
	public boolean isSecondaryAxisAvailable() {
		if (this.plotArea.getSecondaryYaxis()==null) return false;
		return this.axisChoice==USE_FIRST_AXIS;
	}
	/**returns true if the user has the option to swith to the primary axis*/
	public boolean isPrimaryAxisAvailable() {
		return this.axisChoice==USE_SECOND_AXIS;
	}

	/**updates the data showing shapes to match changes in the data series*/
	protected void refreshShapes() {
		for(DataShowingShape ds: this.getDataShapes()) {
			this.setupDataShape(ds);
		};
		this.onAxisUpdate();
		this.onPlotUpdate();
	}
	
	/**returns the data series*/
	public abstract DataSeries getDataSeries();

	/**Sets the style of the data series*/
	public void setStyle(SeriesStyle seriesStyle) {
		this.style=seriesStyle;
		
	}
	
	/**returns the style of the data series*/
	public SeriesStyle getStyle() {
		return style;
	}
	
	/**called whenever a user makes changes to the x or y axis*/
	public void onAxisUpdate() {
		for(DataShowingShape shape:  getDataShapes()) {
			if (shape!=null)
			shape.demandShapeUpdate();
		}
		if (this.scatter!=null){ 
			this.scatter.needsPlotPointUpdate=true;
			this.scatter.needsJitterUpdate=true;
		}

		if (getSeriesLabel()!=null)getSeriesLabel().setSnapTo(getSeriesLabelPositionAnchor());

	}
	
	/**returns the object that determines the locaton of the series label*/
	protected SeriesLabelPositionAnchor getSeriesLabelPositionAnchor() {
		
		return null;
	}

	/**Called for every time the plot area is changed. makes sure all the datashapes match*/
	public void onPlotUpdate() {
		if (scatter!=null) scatter.demandShapeUpdate();
		if (bar!=null)bar.demandShapeUpdate();
		if (errorBar!=null)errorBar.demandShapeUpdate();
		if (getBoxPlot()!=null)getBoxPlot().demandShapeUpdate();
		if (getSeriesLabel()!=null) getSeriesLabel().setSnapTo(getSeriesLabelPositionAnchor());
	}
	
	
	/**method shows a dialog to change the color of the data series, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Color edit", menuText = "Color", subMenuName="Edit", orderRank=8)
	public void editColor() {
		this.getStyle().colorSetDialog();
		getStyle().applyTo(this);
	}
	
	/**method shows a dialog to change the data bar, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Bar edit", menuText = "Data Bar", subMenuName="Edit", orderRank=12, permissionMethod="getDataBar")
	public void editDataBarDialog() {
		if (getDataBar()!=null)getDataBar().showOptionsDialog();
	}
	/**shows a dialog to change the error bar style, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Error bar edit", menuText = "Error Bar", subMenuName="Edit", orderRank=15, permissionMethod="getErrorBar")
	public void editErrorBar() {
		if (getErrorBar()!=null)getErrorBar().showOptionsDialog();
	}
	/**shows a dialog to change the box plot appearance, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Box plot edit", menuText = "Boxplot", subMenuName="Edit", orderRank=19, permissionMethod="getBoxPlot")
	public void editBoxPlot() {
		if (getBoxPlot()!=null)getBoxPlot().showOptionsDialog();
	}
	/**shows a dialog to change the scatter points, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Scatter point edit", menuText = "Points", subMenuName="Edit", orderRank=19, permissionMethod="getScatterPoints")
	public void editScatter() {
		if (this.getScatterPoints()!=null)getScatterPoints().showOptionsDialog();
	}
	
	/**adds the label for the data series, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Add Label", menuText = "New Series Label", subMenuName="Add", orderRank=30)
	public CombinedEdit addLabel() {
		UndoAbleEditForRemoveItem el=null;
		if (this.seriesLabel!=null) {
			 el= removeLabel();
			}
		createLabel();
		setUpSeriesLabel(seriesLabel);
		setFor(seriesLabel);
		this.add(seriesLabel);
		return new CombinedEdit(el, new UndoAddItem(this, this.getSeriesLabel()));
	}

	/**creates the label for the data series*/
	protected void createLabel() {
		this.seriesLabel=new SeriesLabel(this.getDataSeries().getName(), false);
		setUpSeriesLabel(seriesLabel);
	}
	
	/**removes the label for the data series, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Remove Label", menuText = "Label", subMenuName="Remove", orderRank=5, permissionMethod="getSeriesLabel")
	public UndoAbleEditForRemoveItem removeLabel() {
		if (this.seriesLabel!=null) {
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, seriesLabel);
			this.remove(seriesLabel);
			this.seriesLabel=null;
			return undo;
		}
		
		return null;
	}

	/**returns the boxplot*/
	public Boxplot getBoxPlot() {
		if (!hasItem(boxPlot)) return null;
		return boxPlot;
	}

	/**sets the boxplot for the data series, the same box plot must also be in this layer to be valid*/
	public void setBoxPlot(Boxplot boxPlot) {
		this.boxPlot = boxPlot;
	}

	/**returns the label for this data series*/
	public SeriesLabel getSeriesLabel() {
		return this.seriesLabel;
	}
	
	/**adds plot parts to this data series such that it contains everything the argument contains
	 * @param template */
	protected void addPartsBasedOn(BasicDataSeriesGroup template) {
		if (template==null)	{addStandardParts(); return;}//without template, no more is needed
		
		addShapePartsBasedOn(template);
		
		if (template.getSeriesLabel()!=null ){
			addLabel();
			getSeriesLabel().copyAttributesFrom(template.getSeriesLabel());
		}
		
		
	}

	/**adds plot shapes to this data series such that it contains similar items to the template
	 * @param template a model for what this data series should appear as*/
	protected void addShapePartsBasedOn(BasicDataSeriesGroup template) {
		if (template.getDataBar()!=null) {
			addDataBar();
			this.bar.copyTraitsFrom(template.getDataBar());
		}
		
		if (template.getErrorBar()!=null) {
			addErrorBar();
			errorBar.copyTraitsFrom(template.getErrorBar());
		}
		
		if (template.getScatterPoints()!=null){ 
			addScatter();
			scatter.copyTraitsFrom(template.getScatterPoints());
		}
		
		if (template.getBoxPlot()!=null ){
			this.addBoxPlot();
			getBoxPlot().copyAttributesFrom(template.getBoxPlot());
		}
	}
	
	/**Adds the plot components for a bar plot with scatter points*/
	protected void addStandardParts() {
		addDataBar();
		
		addErrorBar();
		
		addScatter();
		
		addLabel();
	}

	
	/**Sets the orientaton of the plot, right now, only horizontal and vertical versions exist*/
	public void setOrientation(PlotOrientation orientation2) {
		this.orientation=orientation2;
		for(DataShowingShape s: getDataShapes()) {
			s.setOrientation(orientation2);
			s.demandShapeUpdate();
		}
		if (this.seriesLabel!=null)this.seriesLabel.setPlotOrientation(orientation2);
	}
	
	/**This layer does not allow removal of certain components*/
	@Override
	public boolean canRelease(ZoomableGraphic z) {
		if (z instanceof DataShowingShape) return false;
		if (z==this.seriesLabel) return false;
		return true;
	}

	/**sets up the data series label for this plot*/
	protected void setFor(SeriesLabel d) {
		d.setPlotArea(plotArea);
		d.setTheData(this.getDataSeries());
		d.setPlotOrientation(orientation);
		d.setPosition((double) position);
	}
	
	/**sets the data series label but does not perform a setup process.
	 * TODO: determine if needed*/
	void setSeriesLabel(SeriesLabel seriesLabel) {
		this.seriesLabel = seriesLabel;
	}
	
	/**removes a data series from the plot, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Remove Data Series From Plot", menuText = "Remove Data Series From Plot", subMenuName="Data", orderRank=43)
	public CombinedEdit removeDataSeries() {
		CombinedEdit cc = new CombinedEdit();
		if(getParentLayer() instanceof BasicPlot) {
			cc.addEditToList(AxisResetUndoableEdit.createFor((BasicPlot) getParentLayer()));
		}
		UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(getParentLayer(), this);
		getParentLayer().remove(this);
		cc.addEditToList(undo);

		return cc;
	}

	/**returns the menu supplier for this layer*/
	public PopupMenuSupplier getMenuSupplier() {
		
		return new MenuItemExecuter(this, true);
	}
	
	/**the icon is a folder icon*/
	@Override
	public Icon getTreeIcon(boolean open) {
		return  IconUtil.createFolderIcon(open, groupColor);
	}
	
	/**Called when the user tries to move objects between layers*/
	public boolean canAccept(ZoomableGraphic z) {
		if (z==this) return false;
		if (this.getParentLayer()!=null&&!getParentLayer().canAccept(z)) {
			return false;//returns false if a parent of this layer rejects the item
		}
		if (z instanceof DataShowingShape ) return true;
		if (z instanceof GraphicLayer) { return false;}
		if (z instanceof AxesGraphic) { return true;}
		if (z instanceof PlotLayout) { return true;}
		if (z instanceof TextGraphic) { return true;}
		return true;
	}
	
	
	/**shows an options dialog, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Series Options", menuText = "Data Series Options", subMenuName="Edit", orderRank=32)
	public void showOptionsDialog() {
		new SeriesDialog(this).showDialog();
	}
	
	/**adds the figure legend shape for this data series
	 * @return */
	public UndoAddItem addLegendShape() {
		if (legandShape==null||!this.hasItem(legandShape))
			{legandShape=new FigureLegendShape(this);
			this.add(legandShape);
			}
		return new UndoAddItem(this, legandShape);
	}
	
	/**returns the figure legend shape for this data series group*/
	public FigureLegendShape getLegandShape() {return legandShape;}

	/**sets up the part of the figure legend for this data series
	  with the series label right next to the figure legend shape
	 * TODO: finish the undo*/
	public CombinedEdit addLegandPart(FigureLegendShape template, SeriesLabel seriesLabelTemplate) {
		CombinedEdit undo = new CombinedEdit(
				addLegendShape()
				);
		
		if (template!=null)getLegandShape().setLocation(template.getBounds().getX(), template.getBounds().getY()+template.getBounds().height+2);

		if (getSeriesLabel()==null)addLabel();
		
		SeriesLabel l = getSeriesLabel();
				if (seriesLabelTemplate!=null) l.copyAttributesFrom(seriesLabelTemplate);
				setUpSeriesLabel(l);
				
			return undo;
	}
	
	/**attaches the series label to the figure legend*/
	public void setUpSeriesLabel(SeriesLabel l) {
				l.setLegend(true);
				l.setSnapTo(getLegandShape());
				l.setAttachmentPosition(AttachmentPosition.defaultPlotLegand());
				l.setAngle(0);
				l.putIntoAnchorPosition();
	}
	
	/**if a plot part is added to this grouop, 
	 * performs a setup process for the plot part and stores it*/
	@Override
	public void addItemToLayer(ZoomableGraphic z) {
		super.addItemToLayer(z);
		if (z instanceof DataShowingShape) {
			onDataShapeAddition((DataShowingShape) z);
		}
		if (z instanceof SeriesLabel) {
			this.seriesLabel=(SeriesLabel) z;
		}
	}

	/**Called after a data showing shape is added to the plot*/
	private void onDataShapeAddition(DataShowingShape z) {
		setupDataShape(z);
		setFieldsToAdded(z);
		
		if (this.getStyle()!=null)this.getStyle().applyTo(z);
		setupDataShape(z);
		z.demandShapeUpdate();
		
	}
	
	/**depending on what kind of plot part is added, sets the given part*/
	protected void setFieldsToAdded(DataShowingShape z) {
		if (z instanceof DataBarShape)     { bar=(DataBarShape) z; }
		if (z instanceof ErrorBarShowingShape) {errorBar=(ErrorBarShowingShape) z;}
		if (z instanceof ScatterPoints) {scatter=(ScatterPoints) z;}
		if (z instanceof Boxplot) {boxPlot=(Boxplot) z;}
		if (z instanceof MeanLineShape) line=(MeanLineShape) z;
	}
	
	/**removes a data line from the plot, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "Remove Line", menuText = "Line", subMenuName="Remove", orderRank=21, permissionMethod="getLine")
	public UndoAbleEditForRemoveItem removeLine() {
		if (getLine()!=null&&hasItem(line)) {
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, getLine());
			remove(getLine());
			return undo;
		}
		setLine(null);
		return null;
	}

	/**sets the line for the plot*/
	protected void setLine(MeanLineShape line) {
		this.line = line;
	}

	/**returns the line for the plot*/
	public MeanLineShape getLine() {
		if (!hasItem(line)) return null;
		return line;
	}
	
	/**adds a line to the plot, annotation indicates that it should be called by a popup menu*/
	@MenuItemMethod(menuActionCommand = "New Line", menuText = "New Line", subMenuName="Add", orderRank=0)
	public CombinedEdit addLine() {
		AbstractUndoableEdit e1=null;
		if (getLine()!=null) e1=removeLine() ;
		line=new MeanLineShape(this.getDataSeries());
		add(line);
		if (this.getStyle()!=null)this.getStyle().applyTo(line);
		return new CombinedEdit(e1, new UndoAddItem(this, line));
	}

	/**returns the data line shape*/
	public DataLineShape getFunctionLine() {
		return line;
	}

}
