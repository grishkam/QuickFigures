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
package genericPlot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.undo.AbstractUndoableEdit;

import dialogs.BoxPlotDialog;
import dialogs.ErrorBarDialog;
import dialogs.MeanBarDialog;
import dialogs.ScatterPointsDialog;
import dialogs.SeriesStyleDialog;
import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.GenericMontageEditor;
import layout.basicFigure.GridLayoutEditEvent;
import layout.basicFigure.GridLayoutEditListener;
import layout.basicFigure.LayoutSpaces;
import menuUtil.PopupMenuSupplier;
import plotParts.Core.AxesGraphic;
import plotParts.Core.AxisLabel;
import plotParts.Core.PlotArea;
import plotParts.Core.PlotAreaRectangle;
import plotParts.Core.PlotAxes;
import plotParts.Core.PlotCordinateHandler;
import plotParts.Core.PlotLayout;
import plotParts.Core.PlotOrientation;
import plotParts.DataShowingParts.Boxplot;
import plotParts.DataShowingParts.DataBarShape;
import plotParts.DataShowingParts.DataShowingShape;
import plotParts.DataShowingParts.ErrorBarShowingShape;
import plotParts.DataShowingParts.FigureLegendShape;
import plotParts.DataShowingParts.PlotLabel;
import plotParts.DataShowingParts.PlotUtil;
import plotParts.DataShowingParts.ScatterPoints;
import plotParts.DataShowingParts.SeriesLabel;
import plotParts.DataShowingParts.SeriesLabelPositionAnchor;
import plotParts.DataShowingParts.SeriesStyle;
import plotTools.PlotIcon;
import undo.CombinedEdit;
import undo.UndoAbleEditForRemoveItem;
import undo.UndoAddItem;
import undoForPlots.AxisFlipUndo;
import undoForPlots.DataShapeUndo;
import undoForPlots.PlotAreaChangeUndo;
import utilityClasses1.NumberUse;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.AttachmentPosition;

/**A class for organizing parts of a plot.
   Has methods to produce a layout, and a pair of axes*/
public abstract class BasicPlot extends GraphicLayerPane implements PlotArea,  GridLayoutEditListener , LayoutSpaces, SeriesLabelPositionAnchor {

	/**
	 * 
	 */
	protected boolean autoMaticaxisSetD=true;
	protected boolean autoMaticaxisSetI=true;//for independant variable axis
	protected int orientation=PlotOrientation.BARS_VERTICAL;
	
	
	//protected static Color[] fillColors=new Color[] {Color.DARK_GRAY, Color.red, Color.green, Color.blue, Color.black, Color.cyan, Color.magenta, Color.darkGray};
	private ArrayList<SeriesStyle> availableStyles=SeriesStyle.getStyles(1, 15);
	
	
	protected int nSeries=0;
	protected AxesGraphic xAxis;
	protected AxesGraphic yAxis;
	protected PlotLayout plotLayout;

	protected PlotAreaRectangle areaRect;
	protected PlotLabel titleLabel;
	protected PlotLabel yLabel;
	protected PlotLabel xLabel;
	private AxesGraphic alternateYaxis;
	private PlotLabel yLabel2;
	
	
	
	private static final long serialVersionUID = 1L;
	
	public BasicPlot(String name) {
		super(name);
		generateLayout() ;
		GenerateAxes() ;
	}
	
	public void generateLayout() {
		this.plotLayout=new PlotLayout(new BasicLayout(1, 1, 200, 150, 0,0 , true));
		plotLayout.moveLocation(60, 50);
		this.add(plotLayout);
		plotLayout.getPanelLayout().getListeners().add(this);
		plotLayout.setPlotArea(this);
	}

	
	public void GenerateAxes() {
		areaRect = new  PlotAreaRectangle(this,new Rectangle(60, 50, 150, 125));
		this.add(areaRect);
		areaRect.setStrokeColor(Color.black);
		areaRect.setStrokeWidth(1);
		xAxis=new AxesGraphic(false);
		yAxis=new AxesGraphic(true);
		
		xAxis.setPlot(this);
		yAxis.setPlot(this);
		
		this.add(xAxis);
		this.add(yAxis);
	
	}
	
	public Rectangle getPlotArea() {
		if (areaRect==null) {}
		return areaRect.getBounds();
	//	return this.plotLayout.getPanelLayout().getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds();

	}
	
	public PlotAreaRectangle plotAreaDefiningRectangle() {
		return areaRect;
	}
	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
			xAxis.MatchToPlotArea();
			yAxis.MatchToPlotArea();
			super.draw(graphics, cords);
	}

	/**
	@Override
	public double transformX(double x) {
		return xAxis.translate(x);
	}

	@Override
	public double transformY(double y) {
		return yAxis.translate(y);
	
	}
*/
	
	@Override
	public PlotAxes getXaxis() {
		return xAxis.getAxisData();
	}

	@Override
	public PlotAxes getYaxis() {
		return yAxis.getAxisData();
	}
	
	public PlotAxes getSecondaryYaxis() {
		if (alternateYaxis==null||!hasItem(alternateYaxis)) return null;
		return this.alternateYaxis.getAxisData();
	}
	

public void fullPlotUpdate() {
	this.onPlotUpdate();
	this.onAxisUpdate();
}

@MenuItemMethod(menuActionCommand = "To Tukey", menuText = "Make Tukey Boxplot", subMenuName="Change Format", orderRank=12)
public CombinedEdit tukeyBoxplotPlot() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup a: getAllDataSeries()) {
		undo.addEditToList(a.removeLine());
		undo.addEditToList(
				forceScatterBarToExclusion(a, ScatterPoints.EXCLUDE_WITHIN15IQR));
		undo.addEditToList(
				forceBarToForm(a, DataBarShape.Ghost));
		undo.addEditToList(
				a.removeErrorBar());
		undo.addEditToList(
				forceBoxplotWhisker(a, Boxplot.TYPE_IQR));

		
	}
	fullPlotUpdate();
	return undo;
}

/**Changes the form of the plot to a normal boxplot*/
@MenuItemMethod(menuActionCommand = "To normal box", menuText = "Make Boxplot", subMenuName="Change Format", orderRank=11)
public CombinedEdit normalBoxplotPlot() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup a: getAllDataSeries()) {
		undo.addEditToList(a.removeLine());
		undo.addEditToList(
				a.removeScatter());
		undo.addEditToList(
				forceBarToForm(a, DataBarShape.Ghost));
		undo.addEditToList(
				a.removeErrorBar());
		undo.addEditToList(
				forceBoxplotWhisker(a,Boxplot.TYPE_Normal));
		
	}
	fullPlotUpdate();
	return undo;
}



	
	@Override
	public void onAxisUpdate() {
		setLayoutToPlotArea();
			for(BasicDataSeriesGroup d: getAllDataSeries()) {
				d.onAxisUpdate();}
	}
	
	public void setLayoutToPlotArea() {
		BasicLayout layout = this.plotLayout.getPanelLayout();
		Rectangle b = areaRect.getBounds();
		layout.setPanelSizes(b .width, b.height);
		layout.specialSpaceWidthTop=b.y-layout.labelSpaceWidthTop;
				layout.specialSpaceWidthLeft=b.x-layout.labelSpaceWidthLeft;
		if (xAxis!=null) xAxis.MatchToPlotArea();
		if (yAxis!=null) yAxis.MatchToPlotArea();
		if (alternateYaxis!=null) alternateYaxis.MatchToPlotArea();
	}
	
	public void setPlotAreaToLayout() {
		BasicLayout layout = this.plotLayout.getPanelLayout();
		 areaRect.setRectangle(layout.getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds());;
		
		
	}

	
	

@Override
public void editWillOccur(GridLayoutEditEvent e) {
	// TODO Auto-generated method stub
	
}

@Override
public void editOccuring(GridLayoutEditEvent e) {
	// TODO Auto-generated method stub
	
}

@Override
public boolean canRelease(ZoomableGraphic z) {
	if (z== yAxis) return false;
	if (z==xAxis) return false;
	if (z==plotLayout) return false;
	if (z==areaRect) return false;
	 
	return true;
}

public void removeItemFromLayer(ZoomableGraphic z) {
	/**Cannot allow the removal of the are rect, only hides it*/
	if (z==this.areaRect) {
		//areaRect.setHidden(true);
		areaRect.setStrokeColor(new Color(255,255,255,0));
		areaRect.setFillColor(new Color(255,255,255,0));
	} else if(z==this.xAxis||z==this.yAxis) {
		
	} else super.removeItemFromLayer(z);
}

@MenuItemMethod(menuActionCommand = "Add Title", menuText = "New Title Label", subMenuName="Add<Label", orderRank=40)
public void addTitleLabel() {
	if (this.hasItem(titleLabel)) this.remove(plotLayout);
	titleLabel=new PlotLabel("Plot Title", this);
	titleLabel.setAttachmentPosition(AttachmentPosition.defaultPlotTitle());
	this.add(titleLabel);
	titleLabel.getParagraph().get(0).get(0).setText("Plot Title");
	areaRect.addLockedItem(titleLabel);
	areaRect.snapLockedItems();
	titleLabel.putIntoSnapPosition();
	new GenericMontageEditor().expandSpacesToInclude(plotLayout.getPanelLayout(), titleLabel.getBounds());
	
}

@MenuItemMethod(menuActionCommand = "Add Y label", menuText = "New Y-Axis Label", subMenuName="Add<Label", orderRank=40)
public void addYAxiLabel() {
	if (this.hasItem(yLabel)) this.remove(yLabel);
	yLabel=new AxisLabel("Y-Axis Label", this);
	this.add(yLabel);
	yLabel.getParagraph().get(0).get(0).setText("Y Axis ");
	areaRect.addLockedItem(yLabel);
	
	yLabel.setAttachmentPosition(AttachmentPosition.defaultRowLabel());
	yLabel.setAngle(Math.PI/2);
	yLabel.getAttachmentPosition().setHorizontalOffset((int) (25+yAxis.getTicLength()));
	areaRect.snapLockedItems();
	yLabel.putIntoSnapPosition();
	
	new GenericMontageEditor().expandSpacesToInclude(plotLayout.getPanelLayout(), yLabel.getBounds());

}

@MenuItemMethod(menuActionCommand = "Add YLab2", menuText = "New Secondary Y-Axis Label", subMenuName="Add<Label", orderRank=50)
public UndoAddItem addSecondaryYAxiLabel() {
	if (this.hasItem(yLabel2)) this.remove(yLabel2);
	yLabel2=new AxisLabel("Y-Axis Label", this);
	this.add(yLabel2);
	yLabel2.getParagraph().get(0).get(0).setText("Y Axis 2");
	areaRect.addLockedItem(yLabel2);
	
	AttachmentPosition sn = AttachmentPosition.defaultRowLabel();
	sn.setLocationTypeExternal(RectangleEdges.RIGHT_SIDE_MIDDLE);
	yLabel2.setAttachmentPosition(sn);
	yLabel2.setAngle(Math.PI/2);
	yLabel2.getAttachmentPosition().setHorizontalOffset((int) (25+this.alternateYaxis.getTicLength()));
	areaRect.snapLockedItems();
	yLabel2.putIntoSnapPosition();
	new GenericMontageEditor().expandSpacesToInclude(plotLayout.getPanelLayout(), yLabel.getBounds());
return new UndoAddItem(this, yLabel2);
}

@MenuItemMethod(menuActionCommand = "Add X Label", menuText = "New X-Axis Label", subMenuName="Add<Label", orderRank=40)
public void addXAxiLabel(int offset) {
	if (this.hasItem(xLabel)) this.remove(xLabel);
	xLabel=new  AxisLabel("X-Axis Label", this);
	xLabel.setAttachmentPosition(AttachmentPosition.defaultRowLabel());
	xLabel.getAttachmentPosition().setLocationTypeExternal(RectangleEdges.BELOW_AT_MIDDLE);
	xLabel.getAttachmentPosition().setVerticalOffset((int) (offset+xAxis.getTicLength()+xAxis.getLabelText().getFont().getSize()));
	this.add(xLabel);
	xLabel.getParagraph().get(0).get(0).setText("X Axis ");
	areaRect.addLockedItem(xLabel);
	areaRect.snapLockedItems();
	xLabel.putIntoSnapPosition();
	
	new GenericMontageEditor().expandSpacesToInclude(plotLayout.getPanelLayout(), xLabel.getBounds());
	
}

/**Moves the axis labels such that they don't overlap with column/category labels*/
public void moxAxisLabelOutOfWay() {
	ArrayList<PlotLabel> labs = getBarLabels();
	Rectangle bb = ArrayObjectContainer.combineBounds(labs).getBounds();

	if (bb.contains(xLabel.getBounds())) {
		double m = bb.getMaxY()-xLabel.getBounds().getMinY();
		double oldOff = xLabel.getAttachmentPosition().getVerticalOffset();
		xLabel.getAttachmentPosition().setVerticalOffset(m+oldOff);
		//new GenericMontageEditor().expandSpacesToInclude(plotLayout.getPanelLayout(), xLabel.getBounds());
	}
	
	if (bb.contains(yLabel.getBounds())) {
		double m = bb.getMinX()-yLabel.getBounds().getMaxX();
		double oldOff = xLabel.getAttachmentPosition().getHorizontalOffset();
		yLabel.getAttachmentPosition().setHorizontalOffset(m+oldOff);
		//new GenericMontageEditor().expandSpacesToInclude(plotLayout.getPanelLayout(), yLabel.getBounds());
	}
}

/**Returns the labels under the bars of the plot*/
public ArrayList<PlotLabel> getBarLabels() {
	return this.getSeriesLabels();
}

public PopupMenuSupplier getMenuSupplier() {
	
	return new MenuItemExecuter(this);
}

@Override
public void setAreaDims(double number, double number2) {
	this.plotLayout.getPanelLayout().setPanelSizes((int)number, (int)number2);
}



/**returns now x and y axes but which are the dependant and independant variable axes*/
public AxesGraphic getDependantVariableAxis() {
	if (orientation==PlotOrientation.BARS_VERTICAL) return yAxis;
	return xAxis;
}

public AxesGraphic getInDependantVariableAxis() {
	if (orientation==PlotOrientation.BARS_VERTICAL) return xAxis;
	return yAxis;
}
public AxesGraphic getYAxis() {
	return yAxis;
}
public AxesGraphic getYAxisAlternate() {
	return alternateYaxis;
}

public AxesGraphic getXAxis() {
	return xAxis;
}

/**Automatically determines the range and ticmarks for the axis*/
protected void setDependantVariableAxisBasedOnMax(double max, boolean ticDistance, AxesGraphic yAxis) {
	yAxis.getAxisData().setMaxValue(max*1.25);
	yAxis.getAxisData().setMinValue(0);
	
	if (ticDistance) {
		double major = NumberUse.findNearest(max/2, new double[] {1 , 5, 10, 20, 25, 30, 40, 50, 75, 100, 200, 250, 500});
		yAxis.getAxisData().setMajorTic((int) major);
		yAxis.getAxisData().setMinorTic((int) major/5);
	}
}

protected void setInDependantVariableAxisBasedOnMax(double max, boolean ticDistance, AxesGraphic thisAxis) {
	thisAxis.getAxisData().setMaxValue(max+0.5);
	
	if (ticDistance) {
		double major = NumberUse.findNearest(max/5, new double[] {1 , 5, 10, 20, 25, 30, 40, 50, 75, 100, 200, 250, 500});
		thisAxis.getAxisData().setMajorTic((int) major);
		thisAxis.getAxisData().setMinorTic((int) major/5);
	}
}

/**Adds a series style to the data series*/
protected void setStylesForNewData(BasicDataSeriesGroup group) {

	if (availableStyles.size()>0) {
		SeriesStyle s = availableStyles.get(0);
		availableStyles.remove(s);
		s.applyTo(group);
	}
	
}


public void autoCalculateAxisRanges() {
	resetMinMax(true);
}

/**Automatically determines the axes based on the data series*/
public void resetMinMax(boolean ticDistance) {
	ArrayList<DataShowingShape> shapes = getAllDataShapes();
	double max = PlotUtil.findMaxNeededValueIn(shapes);
	setDependantVariableAxisBasedOnMax(max, ticDistance, getDependantVariableAxis());
	
	resetIndependantVariableAxis(ticDistance);
}

protected final void resetIndependantVariableAxis(boolean ticReset) {
	ArrayList<DataShowingShape> shapes2 = getAllDataShapes();
	double max2 = PlotUtil.findMaxNeededPositionFrom(shapes2);
	this.setInDependantVariableAxisBasedOnMax(max2,ticReset, this.getInDependantVariableAxis());
}

protected ArrayList<DataShowingShape> getAllDataShapes() {
	ArrayList<? extends BasicDataSeriesGroup> series = getAllDataSeries();
	ArrayList<DataShowingShape> allshapes = new ArrayList<DataShowingShape>();
	for(BasicDataSeriesGroup series1: series)
		{
		ArrayList<DataShowingShape> shapes = series1.getDataShapes();
		allshapes.addAll(shapes);
		}
	return allshapes;
}

/**called when there is some change that requires recalculation of the 
 * data series positions and shapes*/
public void onPlotUpdate(){
	setLayoutToPlotArea();
	for(BasicDataSeriesGroup d: getAllDataSeries()) {
		d.onPlotUpdate();
	}
}

@Override
protected void removeItemFromArray(ZoomableGraphic z) {
	super.removeItemFromArray(z);
	if (z instanceof BasicDataSeriesGroup) {
		BasicDataSeriesGroup d=(BasicDataSeriesGroup) z;
		afterSeriesRemovalFromLayer(d);
	}
}

/**swaps the array positions of the items*/
public void swapItemPositions(ZoomableGraphic z1, ZoomableGraphic z2) {
	super.swapItemPositions(z1, z2);
	if (z1 instanceof BasicDataSeriesGroup ||
		z2 instanceof BasicDataSeriesGroup 	) onSeriesPositionInLayerChanges();

	if (z1 instanceof BasicDataSeriesGroup &&
			z2 instanceof BasicDataSeriesGroup 	) {
		BasicDataSeriesGroup  g1=(BasicDataSeriesGroup) z1;
		BasicDataSeriesGroup  g2=(BasicDataSeriesGroup) z2;
		if (g2.getLegandShape()!=null &&g1.getLegandShape()!=null) {
			FigureLegendShape l1 = g1.getLegandShape();
			FigureLegendShape l2 = g2.getLegandShape();
			Point2D oldloc = l1.getLocationUpperLeft();
			l1.setLocationUpperLeft(l2.getLocationUpperLeft());
			l2.setLocationUpperLeft(oldloc);
			
		}
	}
}

protected void addItemToArray(ZoomableGraphic z) {
	super.addItemToArray(z);
	if (z instanceof BasicDataSeriesGroup) {
		BasicDataSeriesGroup d=(BasicDataSeriesGroup) z;
		d.setPlotArea(this);
		afterSeriesAdditionToLayer(d);
	}
}


/**Called after a data series has been removed from the plot*/
protected void afterSeriesRemovalFromLayer(BasicDataSeriesGroup d) {
	d.setPlotArea(null);
	nSeries--;
	availableStyles.add(0, d.getStyle());
	getAllDataSeries().remove(d);
	onSeriesPositionInLayerChanges();
	afterNumberOfDataSeriesChanges();
	onPlotUpdate();
}



/**called after a data series is added or removed from the plot,
   might update a figure legend or axis */
protected void afterNumberOfDataSeriesChanges() {
	//setInDependantVariableAxisFor1DData(this.getAllDataSeries().size(), this.getInDependantVariableAxis());
	double max = PlotUtil.findMaxNeededValueIn(this.getAllDataShapes());
	this.setDependantVariableAxisBasedOnMax(max, false, getDependantVariableAxis());
	max = PlotUtil.findMaxNeededPositionFrom(this.getAllDataShapes());
	this.setInDependantVariableAxisBasedOnMax(max, false, getInDependantVariableAxis());
}

/**Called when the user tries to move objects between layers*/
public boolean canAccept(ZoomableGraphic z) {
	if (z==this) return false;
	if (this.getParentLayer()!=null&&!getParentLayer().canAccept(z)) {
		return false;//returns false if a parent of this layer rejects the item
	}
	if (z instanceof BasicDataSeriesGroup) { return true;}
	if (z instanceof AxesGraphic) { return true;}
	if (z instanceof PlotLayout) { return true;}
	if (z instanceof TextGraphic) { return true;}
	if (z instanceof DataShowingShape) { return true;}
	if (z instanceof GraphicLayer) { return true;}
	return false;
}


ArrayList<ErrorBarShowingShape> getErrorBars() {
	ArrayList<ErrorBarShowingShape> output=new ArrayList<ErrorBarShowingShape>();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){
		if (t.getErrorBar()!=null) output.add(t.getErrorBar());
		}
	return output;
}

ArrayList<FigureLegendShape> getLegendShapes() {
	ArrayList<FigureLegendShape> output=new ArrayList<FigureLegendShape>();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){
		if (t.getErrorBar()!=null) output.add(t.getLegandShape());
		}
	return output;
}

protected ArrayList<DataBarShape> getMeanBars() {
	ArrayList<DataBarShape> output=new ArrayList<DataBarShape>();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){
		if (t.getDataBar()!=null) output.add(t.getDataBar());
		}
	return output;
}

@MenuItemMethod(menuActionCommand = "Edit Error Bar", menuText = "Error Bars", subMenuName="Edit")
public void editErrorBar() {
	ArrayList<ErrorBarShowingShape> bars = getErrorBars();
	if (bars.size()==0) return;
	ErrorBarDialog d = new ErrorBarDialog(bars.get(0), true);
	d.addAdditionalBars(bars);
	d.showDialog();
}

@MenuItemMethod(menuActionCommand = "Edit Plot Bar", menuText = "Data Bars", subMenuName="Edit")
public void editMeanBar() {
	ArrayList<DataBarShape> bars = getMeanBars();
	if (bars.size()==0) return;
	MeanBarDialog d = new MeanBarDialog(bars.get(0), true);
	d.addAdditionalBars(bars);
	d.showDialog();
}

//@MenuItemMethod(menuActionCommand = "Expand Plot to fit Plot Bar", menuText = "Expand Plot Size to Fit Data Bars", subMenuName="Edit")
public PlotAreaChangeUndo expandPlotToFitMeanBar() {
	PlotAreaChangeUndo undo = new PlotAreaChangeUndo(this);
	
	int neededSize = getNeededWidthOfPlot();
	if (neededSize==0) return null;
	
	if (this.getOrientation()==PlotOrientation.BARS_VERTICAL&& neededSize>this.getPlotArea().getWidth()) {
		this.areaRect.setWidth(neededSize);
	}
	if (this.getOrientation()==PlotOrientation.BARS_HORIZONTAL&& neededSize>this.getPlotArea().getHeight()) {
		this.areaRect.setHeight(neededSize);
	}
	xAxis.MatchToPlotArea();yAxis.MatchToPlotArea();
	this.onAxisUpdate();
	this.fullPlotUpdate();
	undo.establishFinalState();
	return undo;
}

public int getNeededWidthOfPlot() {
	ArrayList<DataBarShape> bars = getMeanBars();
	if (bars.size()==0) return 0;

	int size=0;
	for(DataBarShape b: bars) {size+=(2*b.getBarWidth()+4)*b.getTheData().getAllPositions().length;}
	int neededSize = size+bars.size()*2;
	return neededSize;
}

@MenuItemMethod(menuActionCommand = "Edit Points", menuText = "Scatter Points", subMenuName="Edit")
public void editScatterPoints() {
	ArrayList<ScatterPoints> bars = getScatterPoints();
	if (bars.size()==0) return;
	ScatterPointsDialog d = new ScatterPointsDialog(bars.get(0), true);
	d.addAdditionalBars(bars);
	d.showDialog();
}

@MenuItemMethod(menuActionCommand = "Edit Boxes", menuText = "Boxplots", subMenuName="Edit")
public void editBoxplots() {
	ArrayList<Boxplot> bars = getBoxplots();
	if (bars.size()==0) return;
	BoxPlotDialog d = new BoxPlotDialog(bars.get(0), true);
	d.addAdditionalBars(bars);
	d.showDialog();
}


private ArrayList<Boxplot> getBoxplots() {
	ArrayList<Boxplot>  output=new ArrayList<Boxplot> ();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){
		if (t.getBoxPlot()!=null) output.add(t.getBoxPlot());
		}
	return output;
}

private ArrayList<ScatterPoints> getScatterPoints() {
	ArrayList<ScatterPoints> output=new ArrayList<ScatterPoints> ();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){
		if (t.getScatterPoints()!=null) output.add(t.getScatterPoints());
		}
	return output;
}

private ArrayList<PlotLabel> getSeriesLabels() {
	ArrayList<PlotLabel> output=new ArrayList<PlotLabel> ();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){
		if (t.getSeriesLabel()!=null) output.add(t.getSeriesLabel());
		}
	return output;
}

@MenuItemMethod(menuActionCommand = "Add Scatter Plot", menuText = "New Scatter Points", subMenuName="Add")
public AbstractUndoableEdit addScatter() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.addScatter());}
	return undo;
}

@MenuItemMethod(menuActionCommand = "Add Secondary Vertical Axis", menuText = "Secondary Vertical Axis", subMenuName="Add", orderRank=500)
public AbstractUndoableEdit addVAxis() {
	CombinedEdit undo = new CombinedEdit();
	if (alternateYaxis!=null ) {
		undo.addEditToList(new UndoAbleEditForRemoveItem(this, alternateYaxis));
		this.remove(alternateYaxis); 
	}
	
	alternateYaxis = new AxesGraphic(true);
	alternateYaxis.setOnAlternateSide(true);
	alternateYaxis.setPlot(this);
	this.add(alternateYaxis);
	undo.addEditToList(new UndoAddItem(this,alternateYaxis ));
	
	undo.addEditToList(addSecondaryYAxiLabel());
	return undo;
}


/**Adds a data bar for every data series on the plot*/
@MenuItemMethod(menuActionCommand = "New Data Bar", menuText = "New Data Bars", subMenuName="Add")
public AbstractUndoableEdit addDataBar() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.addDataBar());}
	return undo;
}

/**Adds an error bar shape for every data series on the plot
*/
@MenuItemMethod(menuActionCommand = "Add Error Bar", menuText = "New Error Bars", subMenuName="Add")
public AbstractUndoableEdit addErrorBar() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.addErrorBar());}
	return undo;
}
/**Adds a boxplot bar shape for every data series on the plot
 * @return 
*/
@MenuItemMethod(menuActionCommand = "Add Boxplot", menuText = "New Boxplots", subMenuName="Add")
public AbstractUndoableEdit addBoxplotBar() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.addBoxPlot());;}
	return undo;
}




@MenuItemMethod(menuActionCommand = "Remove Scatter Plot", menuText = "Points", subMenuName="Remove")
public AbstractUndoableEdit removeScatter() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.removeScatter());}
	return undo;
}

@MenuItemMethod(menuActionCommand = "Remove Label", menuText = "Labels", subMenuName="Remove")
public AbstractUndoableEdit removeLabel() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.removeLabel());;}
	return undo;
}

@MenuItemMethod(menuActionCommand = "Remove Data Bar", menuText = "Data Bars", subMenuName="Remove")
public CombinedEdit removeDataBar() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.removeDataBar());;}
	return undo;
}


@MenuItemMethod(menuActionCommand = "Remove Error Bar", menuText = "Error Bars", subMenuName="Remove")
public CombinedEdit removeErrorBar() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.removeErrorBar());;}
	return undo;
}

@MenuItemMethod(menuActionCommand = "Remove Boxplot Bar", menuText = "Boxplots", subMenuName="Remove")
public CombinedEdit removeBoxplots() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup t: this.getAllDataSeries()){undo.addEditToList(t.removeBoxplot());;;}
	return undo;
}


@MenuItemMethod(menuActionCommand = "To Barplot", menuText = "Make Barplot", subMenuName="Change Format", orderRank=4)
public CombinedEdit barPlot() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup a: getAllDataSeries()) {
		
		undo.addEditToList(a.removeBoxplot());
		undo.addEditToList(a.removeScatter());
		undo.addEditToList(a.removeLine());
		
		undo.addEditToList(
				forceBarToForm( a, DataBarShape.Bar));
		
		undo.addEditToList(
				forceErrorBarToForm(a, ErrorBarShowingShape.SEM));
		
	
	}
	fullPlotUpdate();
	return undo;
}

protected CombinedEdit forceBarToForm( BasicDataSeriesGroup a, int type) {
	CombinedEdit undo = new CombinedEdit();
	if (a.getDataBar()==null) 
		undo.addEditToList(a.addDataBar());
	DataShapeUndo undo2 = new DataShapeUndo(a.getDataBar());
	a.getDataBar().setBarType(type);
	undo2.establishFinalState();undo.addEditToList(undo2);
	return undo;
}

protected CombinedEdit forceBoxplotWhisker( BasicDataSeriesGroup a, int  whisker) {
	CombinedEdit undo = new CombinedEdit();
	if (a.getBoxPlot()==null)
		undo.addEditToList(a.addBoxPlot());
	DataShapeUndo undo2 = new DataShapeUndo(a.getBoxPlot());
	a.getBoxPlot().setWhiskerType(whisker);
	undo2.establishFinalState();undo.addEditToList(undo2);
	return undo;
}

protected CombinedEdit forceErrorBarToForm( BasicDataSeriesGroup a, int  depiction) {
	CombinedEdit undo = new CombinedEdit();
	if (a.getErrorBar()==null) 
		undo.addEditToList(a.addErrorBar());
	DataShapeUndo undo2 = new DataShapeUndo(a.getErrorBar());
	a.getErrorBar().setErrorDepiction(depiction);
	undo2.establishFinalState();undo.addEditToList(undo2);
	return undo;
}

protected CombinedEdit forceScatterBarToExclusion( BasicDataSeriesGroup a, int  exclusion) {
	CombinedEdit undo = new CombinedEdit();
	if (a.getScatterPoints()==null) 
		undo.addEditToList(a.addScatter());
	DataShapeUndo undo2 = new DataShapeUndo(a.getScatterPoints());
	a.getScatterPoints().setExclusion(exclusion);
	undo2.establishFinalState();undo.addEditToList(undo2);
	return undo;
}

@MenuItemMethod(menuActionCommand = "To Scatter Plot", menuText = "Make Scatter", subMenuName="Change Format", orderRank=6)
public AbstractUndoableEdit scatterPlot() {
	CombinedEdit undo = new CombinedEdit();
	for(BasicDataSeriesGroup a: getAllDataSeries()) {
		
		undo.addEditToList(
				a.removeBoxplot());
		undo.addEditToList(
				forceScatterBarToExclusion(a, ScatterPoints.NO_Exclusion)
				);
		undo.addEditToList(
				forceBarToForm(a,DataBarShape.LineOnly ));
		undo.addEditToList(
				forceBarToForm(a, ErrorBarShowingShape.SEM));
	
		
	}
	fullPlotUpdate();
	return undo;
}




/**Called when the order of data series in the layer changes */
protected abstract void onSeriesPositionInLayerChanges();


/**Called after a data sereies item is added to this layer. updates the list of data
  series*/
protected abstract void afterSeriesAdditionToLayer(BasicDataSeriesGroup d) ;

/**Returns a list of the data series groups in this plot, returns the 
  same list used to keep track of their numbers. Subclasses should
  only add or remove items on the list if they are actually being added
  to the plot*/
public abstract ArrayList<? extends BasicDataSeriesGroup> getAllDataSeries();


@Override
public PlotCordinateHandler getCordinateHandler() {
	return  getCordinateHandler(0);
}

@Override
public PlotCordinateHandler getCordinateHandler(int n) {
	if (n==1&&this.alternateYaxis!=null&&this.hasItem(alternateYaxis))
		return new  PlotCordinateHandler(xAxis, alternateYaxis, this.orientation==PlotOrientation.BARS_VERTICAL);

	return new  PlotCordinateHandler(xAxis, yAxis, this.orientation==PlotOrientation.BARS_VERTICAL);

}

public void createFigureLegends() {
	Point2D p=new Point2D.Double(this.getPlotArea().getMaxX()+2, this.getPlotArea().getMinY());
	for(BasicDataSeriesGroup aaa: this.getAllDataSeries()) try {
		
		addLegandShapeTo(p, aaa);
		
		p.setLocation(p.getX(), p.getY()+aaa.getLegandShape().getBounds().height+2);
	} catch (Throwable t) {t.printStackTrace();}
	
	giveConsistentStanppingToLabelGroup(getSeriesLabels());
}

protected void giveConsistentStanppingToLabelGroup(ArrayList<PlotLabel> labels) {
	PlotLabel lab1 = labels.get(0);
	for(PlotLabel l: labels) {
		l.setAttachmentPosition(lab1.getAttachmentPosition());
	}
}


private void addLegandShapeTo(Point2D p, BasicDataSeriesGroup aaa) {
	if (aaa.getLegandShape()==null||!aaa.hasItem(aaa.getLegandShape()))
		aaa.addLegendShape();
	aaa.getLegandShape().setLocation(p.getX(), p.getY());

	if (aaa.getSeriesLabel()==null||!aaa.hasItem(aaa.getSeriesLabel()))
		aaa.addLabel();
	
	SeriesLabel l = aaa.getSeriesLabel();
			l.setSnapTo(aaa.getLegandShape());
			l.setAttachmentPosition(AttachmentPosition.defaultPlotLegand());
			l.setAngle(0);
			l.putIntoSnapPosition();
			new GenericMontageEditor().expandSpacesToInclude(plotLayout.getPanelLayout(), l.getBounds());
			new GenericMontageEditor().expandSpacesToInclude(plotLayout.getPanelLayout(), aaa.getLegandShape().getBounds());
	 giveConsistentStanppingToLabelGroup(getSeriesLabels());	
}

public Rectangle getPlotLabelLocationShape() {
	return this.areaRect.getBounds();
}


@MenuItemMethod(menuActionCommand = "Flip axes", menuText = "Flip Axes", subMenuName="Edit")
public AxisFlipUndo axisFlips() {
	flipPlotOrientation();
	
	fullPlotUpdate();
	return new AxisFlipUndo(this);
	
}

protected void flipPlotOrientation() {
	PlotAxes a1 = this.xAxis.getAxisData();
	PlotAxes a2 = this.yAxis.getAxisData();
	
	a1.setVertical(true);
	a2.setVertical(false);
	
	AxesGraphic oldy = this.yAxis;
	 this.yAxis=xAxis;
	this.xAxis=oldy;
	if (this.orientation==PlotOrientation.BARS_VERTICAL) 
		orientation=PlotOrientation.BARS_HORIZONTAL;
	else orientation=PlotOrientation.BARS_VERTICAL;
		
	for(BasicDataSeriesGroup aaa: this.getAllDataSeries()) try {
		aaa.setOrientation(orientation);
	} catch (Throwable t) {t.printStackTrace();}
	
	/**Switches the x and y labels*/
	if (null!=xLabel||null!=yLabel) {
		xLabel.getParagraph();
		yLabel.getParagraph();
		AttachmentPosition sx = xLabel.getAttachmentPosition();
		AttachmentPosition sy = yLabel.getAttachmentPosition();
		double ax = xLabel.getAngle();
		double ay = yLabel.getAngle();
		PlotLabel oldx = xLabel;
		xLabel=yLabel; xLabel.setAttachmentPosition(sx);
		xLabel.setAngle(ax);
		yLabel=oldx; yLabel.setAttachmentPosition(sy);
		yLabel.setAngle(ay);
		
	}
	
}

@MenuItemMethod(menuActionCommand = "Add Label", menuText = "New Series Labels", subMenuName="Add<Label", orderRank=40)
public CombinedEdit addSeriesLabels() {
	CombinedEdit undo = new CombinedEdit();
	AttachmentPosition snap1=null;
	for(BasicDataSeriesGroup t: getAllDataSeries()){
		undo.addEditToList(t.addLabel());
		if (snap1==null) snap1=t.getSeriesLabel().getAttachmentPosition();
		else {t.getSeriesLabel().setAttachmentPosition(snap1);}
	}
	return undo;
}

@MenuItemMethod(menuActionCommand = "Color edit 121", menuText = "Edit Series Colors", subMenuName="Edit", orderRank=40)
public void editPlotColors() {
	new SeriesStyleDialog(availableStyles, this.getAllDataSeries()).showDialog();;
}


public int getOrientation() {return orientation;}

@Override
public boolean moveEntirePlot(double dx, double dy) {
	if (plotLayout !=null ){
		plotLayout.moveLayoutAndContents(dx, dy);
		return true;
	}
	return false;
}


Icon icon=new PlotIcon();
@Override
public Icon getTreeIcon(boolean open) {
	
	return icon;

}

public ArrayList<SeriesStyle> getAvailableStyles() {
	return availableStyles;
}

public void setAvailableStyles(ArrayList<SeriesStyle> availableStyles) {
	this.availableStyles = availableStyles;
}

@Override
public void editDone(GridLayoutEditEvent e) {
	// TODO Auto-generated method stub
	
}

}
