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
 * Version: 2022.0
 */
package plotTools;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.ImageWorkSheet;
import dataSeries.DataSeries;
import genericPlot.BasicDataSeriesGroup;
import genericPlot.BasicPlot;
import genericTools.Object_Mover;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_Shapes.ConnectorGraphic;
import icons.IconWrappingToolIcon;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import plotParts.Core.PlotAreaRectangle;
import plotParts.Core.PlotOrientation;
import plotParts.DataShowingParts.Boxplot;
import plotParts.DataShowingParts.DataBarShape;
import plotParts.DataShowingParts.DataShowingShape;

/**a superclass for multiple tools that edit a plot*/
public class BasicPlotTool extends Object_Mover {
	
	/**stores the objects that were near the mouse press and mouse drag*/
	protected DataShowingShape pressShape;
	protected DataShowingShape dragShape;
	protected ConnectorGraphic preliminaryPath;
	protected DataSeries dataSeriesPressed;
	protected DataSeries dataSeriesDragged;
	private Shape markPress;
	private Shape markDrag;

	public CanvasMouseEvent alternativeMouseEvent;

	{super.iconSet=IconWrappingToolIcon.createIconSet(new PlotIcon());}


	
	public void mousePressed() {
		super.mousePressed();
		LocatedObject2D o = this.getPrimarySelectedObject();
		int clickedCordinateX = getClickedCordinateX();
		int clickedCordinateY = getClickedCordinateY();
		
		pressOnShape(o, clickedCordinateX, clickedCordinateY);
	}

	/**Called when the user presses their mouse key on the object at a given location
	 * @param o
	 * @param clickedCordinateX
	 * @param clickedCordinateY
	 */
	public void pressOnShape(LocatedObject2D o, int clickedCordinateX, int clickedCordinateY) {
		if (o instanceof DataShowingShape) {
			pressShape=(DataShowingShape) o;
			DataSeries dataPress = pressShape.getTheData();
		
			
			
			if (dataPress .getAllPositions().length>1) {
				dataSeriesPressed=findPressedSeries(pressShape, clickedCordinateX, clickedCordinateY);
			}
			if (dataPress .getAllPositions().length==1) {
				dataSeriesPressed=dataPress.getIncludedValues();
			}
			markPress=findSubshapeClicked(pressShape, clickedCordinateX, clickedCordinateY);
			if(markPress==null)
				markPress=pressShape.getOutline();
			
		} else
		{
			pressShape=null;
			markPress=null;
		}
	}
	
	/**attempts to locate the data series that corresponds to a click location*/
	private DataSeries findPressedSeries(DataShowingShape pressShape2, int dx, int dy) {	
		pressShape2 = getShapeWithSubshapeList(pressShape2);
		return pressShape2.getPartialSeriesDrawnAtLocation(dx, dy);
	}
	
	/**returns the shape used to draw the data series at the click location*/
	private Shape findSubshapeClicked(DataShowingShape pressShape2, int dx, int dy) {	
		pressShape2 = getShapeWithSubshapeList(pressShape2);
		return pressShape2.getPartialShapeAtLocation(dx, dy);
	}

	/**not all data shapes cary information on the splitting of the data into categories
	  if there is none checks tha parent layer for other data shapes*/
	private DataShowingShape getShapeWithSubshapeList(DataShowingShape pressShape2) {	
		if (pressShape2.getPartialShapeMap().size()==0 && pressShape2.getParentLayer() instanceof BasicDataSeriesGroup) {
			BasicDataSeriesGroup group=(BasicDataSeriesGroup) pressShape2.getParentLayer();
			if (group.getDataBar()!=null) pressShape2=group.getDataBar();
			else if (group.getErrorBar()!=null) pressShape2=group.getErrorBar();
			else if (group.getBoxPlot()!=null) pressShape2=group.getBoxPlot();
		}
		return pressShape2;
	}


	public void mouseDragged() {
		
		/***/
		if (pressShape==null)  {
			super.mouseDragged(); //if no data showing shape was pressed, nothing else need be done
			return;
			}
		
		
		int dragX = this.getDragCordinateX();
		int dragY = this.getDragCordinateY();
		
		ImageWorkSheet imageClicked = getImageClicked();
		
		onDragWithinImage(this.getClickedCordinateX(), this.getClickedCordinateY(),dragX, dragY, imageClicked);
		
	
	}

	/**Determines which shape is being dragged over and displays a marker over it
	 * @param dragX
	 * @param dragY
	 * @param imageClicked
	 */
	public void onDragWithinImage(int pressX, int pressY, int dragX, int dragY, ImageWorkSheet imageClicked) {
		LocatedObject2D roi2 = getObjectAt(imageClicked, dragX, dragY);
		
		if (!(roi2 instanceof DataShowingShape) && roi2 instanceof PlotAreaRectangle) try {
			 PlotAreaRectangle plot=(PlotAreaRectangle) roi2;
			BasicPlot layer = (BasicPlot) plot.getParentLayer();
			PlotOrientation or = layer.getOrientation();
			
			int searchWidth=30;
			Rectangle2D searchArea = new Rectangle2D.Double(dragX, plot.getBounds().getY(), searchWidth, plot.getBounds().getHeight());
			if (or==PlotOrientation.BARS_HORIZONTAL) {
				searchArea = new Rectangle2D.Double(plot.getBounds().getX(), dragY,  plot.getBounds().getWidth(), searchWidth);
			}
			
			
			ArrayList<LocatedObject2D> allObjects = imageClicked.getLocatedObjects();
			for(LocatedObject2D possibleShape: allObjects) {
				if ((possibleShape instanceof DataShowingShape) && (possibleShape.getOutline().intersects(searchArea)) ) {
					roi2=possibleShape;
					if(roi2 instanceof DataBarShape)
						break;
					if(roi2 instanceof Boxplot)
						break;
				}
			}
		}
		 catch (Throwable t) {
			 IssueLog.logT(t);
		 }
		
		if (roi2 instanceof DataShowingShape) {
			dragShape=(DataShowingShape) roi2;
			setupDragDataSeries(dragX, dragY);
			
			}
		else dragShape=null;
		if(dragShape==null||pressShape==null) return;
		createMarker(pressX,  pressY, dragX, dragY,  imageClicked);
	}

	/**stores the dragged data series 
	 * @param dragX
	 * @param dragY
	 */
	public void setupDragDataSeries(int dragX, int dragY) {
		DataSeries dataDrag = dragShape.getTheData();
		if (dataDrag .getAllPositions().length>1) {
			dataSeriesDragged=findPressedSeries(dragShape, dragX, dragY);
		}
		if (dataDrag .getAllPositions().length==1) {
			dataSeriesDragged=dataDrag.getIncludedValues();
		}
		markDrag=findSubshapeClicked(dragShape,dragX, dragY);
		
		if (markDrag==null) {
			markDrag=dragShape.getOutline();
		}
	}


	
	
	/**overlays a marker above the clicked and dragged shapes such that the user
	 * can see what is being targetted*/
	protected void createMarker(int pressX, int pressY, int dragX, int dragY, ImageWorkSheet imageClicked) {
		
			GraphicGroup sg = generateMarkerForSwitch();
			sg.deselect();
			sg.hideHandles(true);
			
			
				if(imageClicked==null)
					{
					/**find another way to show marker*/
					 imageClicked =alternativeMouseEvent.getAsDisplay().getImageAsWorksheet();
					}
				
			imageClicked.getOverlaySelectionManagger().setSelection(sg, 0);
		
	}

	
	/**generates marker rectangles above the clicked data series and the dragged on*/
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

	/**called after a mouse release*/
	@Override
	protected void afterRelease() {
		super.afterRelease();
		if (   pressShape!=null&&dragShape!=null)  {
			afterPlotRelease(this.getImageClicked());
		}
	}

	/**not implemented here, subclasses implements it*/
	public void afterPlotRelease(ImageWorkSheet imageClicked) {
		
		
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
		
	}

}
