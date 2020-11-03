package plotTools;

import java.awt.Color;
import java.awt.Shape;

import dataSeries.DataSeries;
import externalToolBar.IconWrappingToolIcon;
import genericMontageUIKit.Object_Mover;
import genericPlot.BasicDataSeriesGroup;
import graphicalObjects_BasicShapes.BasicShapeGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import plotParts.DataShowingParts.DataShowingShape;
import utilityClassesForObjects.LocatedObject2D;

public class BasicPlotTool extends Object_Mover {
	
	protected DataShowingShape pressShape;
	protected DataShowingShape dragShape;
	protected ConnectorGraphic preliminaryPath;
	protected DataSeries dataSeriesPressed;
	protected DataSeries dataSeriesDragged;
	private Shape markPress;
	private Shape markDrag;


	{super.set=IconWrappingToolIcon.createIconSet(new PlotIcon());}


	
	public void mousePressed() {
		super.mousePressed();
		LocatedObject2D o = this.getSelectedObject();
		
		if (o instanceof DataShowingShape) {
			pressShape=(DataShowingShape) o;
			DataSeries dataPress = pressShape.getTheData();
		
			if (dataPress .getAllPositions().length>1) {
				dataSeriesPressed=findPressedSeries(pressShape, this.getClickedCordinateX(), this.getClickedCordinateY());
			}
			if (dataPress .getAllPositions().length==1) {
				dataSeriesPressed=dataPress.getIncludedValues();
			}
			markPress=findSubshapeClicked(pressShape, getClickedCordinateX(), this.getClickedCordinateY());
			
		} else
		{
			pressShape=null;
			markPress=null;
		}
	}
	
	
	private DataSeries findPressedSeries(DataShowingShape pressShape2, int dx, int dy) {	
		pressShape2 = getShapeWithSubshapeList(pressShape2);
		return pressShape2.getPartialSeriesDrawnAtLocation(dx, dy);
	}
	
	private Shape findSubshapeClicked(DataShowingShape pressShape2, int dx, int dy) {	
		pressShape2 = getShapeWithSubshapeList(pressShape2);
		return pressShape2.getPartialShapeAtLocation(dx, dy);
	}


	private DataShowingShape getShapeWithSubshapeList(DataShowingShape pressShape2) {
		/**not all data shapes cary information on the splitting of the data
		  there is none it must check another datashape*/
		if (pressShape2.getPartialShapeMap().size()==0) {
			BasicDataSeriesGroup group=(BasicDataSeriesGroup) pressShape2.getParentLayer();
			if (group.getDataBar()!=null) pressShape2=group.getDataBar();
			else if (group.getErrorBar()!=null) pressShape2=group.getErrorBar();
			else if (group.getBoxPlot()!=null) pressShape2=group.getBoxPlot();
		}
		return pressShape2;
	}


	public void mouseDragged() {
		
		if (pressShape==null)  {super.mouseDragged(); return;}
		
		
		LocatedObject2D roi2 = getObject(getImageWrapperClick(), this.getDragCordinateX(), this.getDragCordinateY());
		if (roi2 instanceof DataShowingShape) {
			dragShape=(DataShowingShape) roi2;
			DataSeries dataDrag = dragShape.getTheData();
			if (dataDrag .getAllPositions().length>1) {
				dataSeriesDragged=findPressedSeries(dragShape, this.getDragCordinateX(), this.getDragCordinateY());
			}
			if (dataDrag .getAllPositions().length==1) {
				dataSeriesDragged=dataDrag.getIncludedValues();
			}
			markDrag=findSubshapeClicked(dragShape,getDragCordinateX(), getDragCordinateY());
			
			}
		else dragShape=null;
		if(dragShape==null||pressShape==null) return;
		createMarker();
		
	
	}


	
	
	
	protected void createMarker() {
		
			GraphicGroup sg = generateMarkerForSwitch();
			
			super.getImageWrapperClick().getSelectionManagger().setSelection(sg, 0);
		
	}


	protected GraphicGroup generateMarkerForSwitch() {
		BasicShapeGraphic z1=null;
		BasicShapeGraphic z2=null;
		
		if (markPress!=null) {
		z1= new BasicShapeGraphic(markPress);
		z1.setStrokeColor(Color.green.darker().darker());z1.setStrokeWidth(6);
		}
		if (markDrag!=null) {
			z2= new BasicShapeGraphic(markDrag);
			z2.setStrokeColor(Color.blue.darker().darker());
			z2.setStrokeWidth(6);
		}
		GraphicGroup sg = new GraphicGroup(true, z1, z2);
		return sg;
	}


	protected void afterRelease() {
		super.afterRelease();
		if (   pressShape!=null&&dragShape!=null)  {
			afterPlotRelease();
		}
	}


	protected void afterPlotRelease() {
		// TODO Auto-generated method stub
		
	}


	public DataShowingShape getPressShape() {
		return pressShape;
	}


	public void setPressShape(DataShowingShape pressShape) {
		this.pressShape = pressShape;
	}


	public DataShowingShape getDragShape() {
		return dragShape;
	}


	public void setDragShape(DataShowingShape dragShape) {
		this.dragShape = dragShape;
	}

	@Override
	public String getToolName() {
		return "Plot Tool";
	}
	public String getToolTip() {
		return getToolName();
	}
	
	@Override
	public void showOptionsDialog() {
		//new ReflectingFieldSettingDialog(this).showDialog();
	}

}
