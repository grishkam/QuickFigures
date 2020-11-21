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
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import iconGraphicalObjects.IconUtil;
import menuUtil.PopupMenuSupplier;
import plotParts.Core.AxesGraphic;
import plotParts.Core.PlotArea;
import plotParts.Core.PlotLayout;
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
import utilityClassesForObjects.SnappingPosition;

public abstract class BasicDataSeriesGroup extends GraphicLayerPane implements PlotComponent {
	
	
	protected PlotArea plotArea;
	protected int orientation=0;//whether the plot is horizontal or vertical
	private int axisChoice=0;//set to 0 for the primary Y-Axis, 1 for the secondary
	
	/**The position on the plot,The data series on this plot are on a particular order
	   This order may not be consequential depending on the subclass and plot type
	   because it is only used by some subclasses*/
	protected int position;//
	
	
	protected SeriesLabel seriesLabel;
	private Boxplot boxPlot;
	private DataBarShape bar;
	private ScatterPoints scatter;
	private ErrorBarShowingShape errorBar;
	private FigureLegendShape legandShape;
	protected MeanLineShape line;

	protected Color groupColor=Color.gray;
	private SeriesStyle style;
	
	
	public BasicDataSeriesGroup(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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



	@Override
	public double getMaxNeededPosition() {
		ArrayList<DataShowingShape> shapes = getDataShapes();
		return  PlotUtil.findMaxNeededPositionFrom(shapes);
	}
	
	public ArrayList<DataShowingShape> getDataShapes() {
		ArrayList<DataShowingShape> output=new ArrayList<DataShowingShape>();
		if (scatter!=null) output.add(scatter);
		if (bar!=null)output.add(bar);
		if (errorBar!=null)output.add(errorBar);
		if (getBoxPlot()!=null)output.add(getBoxPlot());
		if (line!=null) output.add(line);
		return output;
		
	}
	
	public ScatterPoints getScatterPoints() {
		if (!hasItem(scatter)) return null;
		return scatter;
	}

	
	
	public void setPlotArea(PlotArea plotLayers) {
		plotArea=plotLayers;
		for(DataShowingShape shape: this.getDataShapes()) {
			if (shape!=null)
			shape.setPlotArea(plotLayers);}
	}
	
	public void setupDataShape(DataShowingShape d) {
		if (d==null) return;
		d.setTheData(getDataSeries());
		d.setOrientation(orientation);
		d.setPlotArea(plotArea);
		d.setAxisChoice(this.axisChoice);
	}
	

	public void setGroupColor(Color color) {
		
		groupColor=color;
	}
	
	@MenuItemMethod(menuActionCommand = "New Data Bar", menuText = "New Data Bar", subMenuName="Add")
	public AbstractUndoableEdit addDataBar() {
		AbstractUndoableEdit e1=null;
		if (bar!=null) e1=removeDataBar();
		add(new DataBarShape(getDataSeries()));
		return new CombinedEdit(e1, new UndoAddItem(this, getDataBar()));
	}
	

	@MenuItemMethod(menuActionCommand = "Add Scatter Plot", menuText = "New Scatter Points", subMenuName="Add")
	public AbstractUndoableEdit addScatter() {
		AbstractUndoableEdit e1=null;
		if (getScatterPoints()!=null) e1=removeScatter();
		add(createScatter());
		return new CombinedEdit(e1, new UndoAddItem(this, getScatterPoints()));
	}

	protected ScatterPoints createScatter() {
		return new ScatterPoints(getDataSeries(), false);
	}
	

	@MenuItemMethod(menuActionCommand = "Add Error Bar", menuText = "New Error Bar", subMenuName="Add")
	public CombinedEdit addErrorBar() {
		AbstractUndoableEdit e1=null;
		if (getErrorBar()!=null) e1=removeErrorBar();
		this.errorBar=new ErrorBarShowingShape(getDataSeries());
		add(errorBar);
		return new CombinedEdit(e1, new UndoAddItem(this, getErrorBar()));
	}
	
	

	@MenuItemMethod(menuActionCommand = "Remove Data Bar", menuText = "Data Bar", subMenuName="Remove", orderRank=21, permissionMethod="getDataBar")
	public UndoAbleEditForRemoveItem removeDataBar() {
		if (bar!=null) {
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(this, bar);
			this.remove(bar);
		return undo;
			}
		return null;
	}
	
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
	

	@MenuItemMethod(menuActionCommand = "Add Box Plot", menuText = "New Boxplot", subMenuName="Add")
	public CombinedEdit addBoxPlot() {
		AbstractUndoableEdit e1=null;
		if (this.boxPlot!=null) e1=removeBoxplot() ;
		boxPlot=new Boxplot(this.getDataSeries());
		add(boxPlot);
		return new CombinedEdit(e1, new UndoAddItem(this, getBoxPlot()));
	}

	
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
	
	
	@MenuItemMethod(menuActionCommand = "Edit axis to secondary", menuText = "Use Secondary Y-axis", subMenuName="Axis", orderRank=100, permissionMethod="isSecondaryAxisAvailable")
	public AxisChangeUndo toSecondaryY() {
		return setAxis(1);
	}
	@MenuItemMethod(menuActionCommand = "Edit axis to primary", menuText = "Use Main Y-axis", subMenuName="Axis", orderRank=100, permissionMethod="isPrimaryAxisAvailable")
	public AxisChangeUndo toPrimaryY() {
		return setAxis(0);
	}
	
	public AxisChangeUndo setAxis(int i) {
		AxisChangeUndo undo = new AxisChangeUndo(this, axisChoice);
		this.axisChoice=i;
		undo.setFinalAxis(axisChoice);
		refreshShapes();
		return undo;
	}
	
	public boolean isSecondaryAxisAvailable() {
		if (this.plotArea.getSecondaryYaxis()==null) return false;
		return this.axisChoice==0;
	}
	public boolean isPrimaryAxisAvailable() {
		return this.axisChoice==1;
	}

	protected void refreshShapes() {
		for(DataShowingShape ds: this.getDataShapes()) {
			this.setupDataShape(ds);
		};
		this.onAxisUpdate();
		this.onPlotUpdate();
	}
	
	
	public abstract DataSeries getDataSeries();

	public void setStyle(SeriesStyle seriesStyle) {
		this.style=seriesStyle;
		
	}
	
	public SeriesStyle getStyle() {
		return style;
	}
	
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
	
	

	@MenuItemMethod(menuActionCommand = "Color edit", menuText = "Color", subMenuName="Edit", orderRank=8)
	public void editColor() {
		this.getStyle().colorSetDialog();
		getStyle().applyTo(this);
	}
	@MenuItemMethod(menuActionCommand = "Bar edit", menuText = "Data Bar", subMenuName="Edit", orderRank=12, permissionMethod="getDataBar")
	public void editDataBarDialog() {
		if (getDataBar()!=null)getDataBar().showOptionsDialog();
	}
	@MenuItemMethod(menuActionCommand = "Error bar edit", menuText = "Error Bar", subMenuName="Edit", orderRank=15, permissionMethod="getErrorBar")
	public void editErrorBar() {
		if (getErrorBar()!=null)getErrorBar().showOptionsDialog();
	}
	@MenuItemMethod(menuActionCommand = "Box plot edit", menuText = "Boxplot", subMenuName="Edit", orderRank=19, permissionMethod="getBoxPlot")
	public void editBoxPlot() {
		if (getBoxPlot()!=null)getBoxPlot().showOptionsDialog();
	}
	@MenuItemMethod(menuActionCommand = "Scatter point edit", menuText = "Points", subMenuName="Edit", orderRank=19, permissionMethod="getScatterPoints")
	public void editScatter() {
		if (this.getScatterPoints()!=null)getScatterPoints().showOptionsDialog();
	}
	
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

	protected void createLabel() {
		this.seriesLabel=new SeriesLabel(this.getDataSeries().getName(), false);
		setUpSeriesLabel(seriesLabel);
	}
	
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

	public Boxplot getBoxPlot() {
		if (!hasItem(boxPlot)) return null;
		return boxPlot;
	}

	public void setBoxPlot(Boxplot boxPlot) {
		this.boxPlot = boxPlot;
	}


	public SeriesLabel getSeriesLabel() {
		return this.seriesLabel;
	}
	
	protected void addPartsBasedOn(BasicDataSeriesGroup template) {
		if (template==null)	{addStandardParts(); return;}//without template, no more is needed
		
		addShapePartsBasedOn(template);
		
		if (template.getSeriesLabel()!=null ){
			addLabel();
			getSeriesLabel().copyAttributesFrom(template.getSeriesLabel());
		}
		
		
	}

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
	
	protected void addStandardParts() {
		addDataBar();
		
		addErrorBar();
		
		addScatter();
		
		addLabel();
	}

	
	/**Sets the orientaton of the plot, right now, only horizontal and vertical versions exist*/
	public void setOrientation(int orientation2) {
		this.orientation=orientation2;
		for(DataShowingShape s: getDataShapes()) {
			s.setOrientation(orientation2);
			s.demandShapeUpdate();
		}
		if (this.seriesLabel!=null)this.seriesLabel.setPlotOrientation(orientation2);
	}
	
	@Override
	public boolean canRelease(ZoomableGraphic z) {
		if (z instanceof DataShowingShape) return false;
		if (z==this.seriesLabel) return false;
		return true;
	}

	protected void setFor(SeriesLabel d) {
		d.setPlotArea(plotArea);
		d.setTheData(this.getDataSeries());
		d.setPlotOrientation(orientation);
		d.setPosition((double) position);
	}
	


	public void setSeriesLabel(SeriesLabel seriesLabel) {
		this.seriesLabel = seriesLabel;
	}
	
	@MenuItemMethod(menuActionCommand = "Remove Data Series From Plot", menuText = "Remove Data Series From Plot", subMenuName="Data", orderRank=43)
	public UndoAbleEditForRemoveItem removeDataSeries() {
		UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(getParentLayer(), this);
		getParentLayer().remove(this);
		return undo;
	}


	public PopupMenuSupplier getMenuSupplier() {
		
		return new MenuItemExecuter(this);
	}
	
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
	
	@MenuItemMethod(menuActionCommand = "Series Options", menuText = "Data Series Options", subMenuName="Edit", orderRank=32)
	public void showOptionsDialog() {
		new SeriesDialog(this).showDialog();
	}

	public void addLegendShape() {
		if (legandShape==null||!this.hasItem(legandShape))
			{legandShape=new FigureLegendShape(this);
			this.add(legandShape);
			//if (getSeriesLabel()
			}
	}
	


	
	public FigureLegendShape getLegandShape() {return legandShape;}

	
	public void addLegandPart(FigureLegendShape template, SeriesLabel seriesLabelTemplate) {
		addLegendShape();
		
		if (template!=null)getLegandShape().setLocation(template.getBounds().getX(), template.getBounds().getY()+template.getBounds().height+2);

		if (getSeriesLabel()==null)addLabel();
		
		SeriesLabel l = getSeriesLabel();
				if (seriesLabelTemplate!=null) l.copyAttributesFrom(seriesLabelTemplate);
				setUpSeriesLabel(l);
				
	}
	
	public void setUpSeriesLabel(SeriesLabel l) {
		l.setLegend(true);
				l.setSnapTo(getLegandShape());
				l.setSnapPosition(SnappingPosition.defaultPlotLegand());
				l.setAngle(0);
				l.putIntoSnapPosition();
	}
	
	
	public void addItemToLayer(ZoomableGraphic z) {
		super.addItemToLayer(z);
		if (z instanceof DataShowingShape) {
			onDataShapeAddition((DataShowingShape) z);
		}
		if (z instanceof SeriesLabel) {
			this.seriesLabel=(SeriesLabel) z;
		}
	}

	private void onDataShapeAddition(DataShowingShape z) {
		setupDataShape(z);
		setFieldsToAdded(z);
		
		if (this.getStyle()!=null)this.getStyle().applyTo(z);
		setupDataShape(z);
		z.demandShapeUpdate();
		
	}

	protected void setFieldsToAdded(DataShowingShape z) {
		if (z instanceof DataBarShape)     { bar=(DataBarShape) z; }
		if (z instanceof ErrorBarShowingShape) {errorBar=(ErrorBarShowingShape) z;}
		if (z instanceof ScatterPoints) {scatter=(ScatterPoints) z;}
		if (z instanceof Boxplot) {boxPlot=(Boxplot) z;}
		if (z instanceof MeanLineShape) line=(MeanLineShape) z;
	}
	
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

	public void setLine(MeanLineShape line) {
		this.line = line;
	}

	public MeanLineShape getLine() {
		if (!hasItem(line)) return null;
		return line;
	}
	@MenuItemMethod(menuActionCommand = "New Line", menuText = "New Line", subMenuName="Add", orderRank=0)
	public CombinedEdit addLine() {
		AbstractUndoableEdit e1=null;
		if (getLine()!=null) e1=removeLine() ;
		line=new MeanLineShape(this.getDataSeries());
		add(line);
		if (this.getStyle()!=null)this.getStyle().applyTo(line);
		return new CombinedEdit(e1, new UndoAddItem(this, line));
	}

	public DataLineShape getFunctionLine() {
		return line;
	}

}
