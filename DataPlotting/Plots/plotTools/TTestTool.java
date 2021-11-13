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
 * Date Modified: April 28, 2021
 * Version: 2021.2
 */
package plotTools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import columnPlots.ColumnPlot;
import dataSeries.DataSeries;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import icons.IconWrappingToolIcon;
import layout.BasicObjectListHandler;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import plotParts.Core.PlotArea;
import plotParts.Core.PlotOrientation;
import plotParts.DataShowingParts.DataShowingShape;
import storedValueDialog.ReflectingFieldSettingDialog;
import storedValueDialog.UserChoiceField;
import undo.UndoAddItem;
import utilityClasses1.ArraySorter;

/**a tool for t tests on plots, */
public class TTestTool extends BasicPlotTool {
	{
		{super.iconSet=IconWrappingToolIcon.createIconSet(new PlotToolIcon());}
		
};


@UserChoiceField(optionsForUser = { "p-Vale<x", "Stars", "Exact" })
public int markType=StatTestShower .LESS_THAN_MARK;


public static final int LINK_WITH_LINE=0, NO_LINK=1;
@UserChoiceField(optionsForUser = { "Connection Line", "Put significance above databar" })
public int linkType=LINK_WITH_LINE;

@UserChoiceField(optionsForUser = { "Assume Unequal Variances", "Assume Equal Variances", "Paired T-Test" })
public int tTestType=StatTestShower.NORMAL_T_TEST;


@UserChoiceField(optionsForUser = { "Two-Tailed", "One-Tailed" })
public int numberTails=StatTestShower.TW0_TAIL;


	
	protected void createMarker() {
		preliminaryPath = createLinkingLineForShapes(getPressShape(), getDragShape(), this.getClickedCordinateX(), this.getClickedCordinateY(), this.getDragCordinateX(), this.getDragCordinateY());
		
		super.getImageClicked().getOverlaySelectionManagger().setSelection(new GraphicGroup(true, getTTextMarkingGraphic(true), generateMarkerForSwitch()), 0);
	}


	/**When given two data shapes and where they were clicked on, creates a linker graphic for the shapes*/
	private ConnectorGraphic createLinkingLineForShapes(DataShowingShape pressShape2, DataShowingShape dragShape2, double pressX, double pressY, double dragX, double dragY) {
		if (pressShape2==null||dragShape2==null) return null;
		ConnectorGraphic p3 = prepareForVerticalBar(pressShape2, dragShape2, pressX, pressY, dragX, dragY);
		if(pressShape2.getOrientation()==PlotOrientation.BARS_HORIZONTAL) {
			 p3 = prepareForHorizontalBar(pressShape2, dragShape2, pressX, pressY, dragX, dragY);
		}
		
		/**moves the shape a bit further to from the shapes*/
		if(p3.horizontal)
			p3 .moveLocation(20, 0);
		else
			p3 .moveLocation(0, -20);
		;
		 p3.setStrokeColor(Color.black);
		 p3.setStrokeWidth(1);
		return p3;
	}


	/**prepares a connector for comparison of vertical bars
	 * @param pressShape2
	 * @param dragShape2
	 * @param pressX
	 * @param pressY
	 * @param dragX
	 * @param dragY
	 * @return
	 */
	public ConnectorGraphic prepareForVerticalBar(DataShowingShape pressShape2, DataShowingShape dragShape2,
			double pressX, double pressY, double dragX, double dragY) {
		Point2D.Double pt0 = highestPointInDataShape(pressShape2, pressX, pressY) ;
		Point2D.Double pt1 = highestPointInDataShape(pressShape2, pressX, pressY) ;//end of horizontal bar
		Point2D.Double pt2 = highestPointInDataShape(dragShape2, dragX, dragY) ;//end of horixontal bar
		Point2D.Double pt3 = highestPointInDataShape(dragShape2, dragX, dragY) ;
		if (pt1.y<pt2.y) pt2.y=pt1.y; else pt1.y=pt2.y; 
		
		PathGraphic p1=new PathGraphic(pt1, pt2);
		
		while (doesOverLapDataShapes(p1)) {
			p1.getPoints().applyAffine(AffineTransform.getTranslateInstance(0, -5));
			p1.updatePathFromPoints();
			pt1.y-=5;
			pt2.y-=5;
		}
		
		//p1.moveLocation(0, -10);
		p1=new PathGraphic(pt0, pt1, pt2, pt3);
		
		
		Double pmid = new Point2D.Double((pt1.getX()+pt2.getX())/2, pt1.getY());
		ConnectorGraphic p3 = new ConnectorGraphic(false,pt0, pmid, pt3);
		return p3;
	}
	
	/**prepares a connector for comparison of vertical bars
	 * @param pressShape2
	 * @param dragShape2
	 * @param pressX
	 * @param pressY
	 * @param dragX
	 * @param dragY
	 * @return
	 */
	public ConnectorGraphic prepareForHorizontalBar(DataShowingShape pressShape2, DataShowingShape dragShape2,
			double pressX, double pressY, double dragX, double dragY) {
		Point2D.Double pt0 = rightmostPointInDataShape(pressShape2, pressX, pressY) ;
		Point2D.Double pt1 = rightmostPointInDataShape(pressShape2, pressX, pressY) ;//end of bar
		Point2D.Double pt2 = rightmostPointInDataShape(dragShape2, dragX, dragY) ;//end of bar
		Point2D.Double pt3 = rightmostPointInDataShape(dragShape2, dragX, dragY) ;
		if (pt1.x>pt2.x) pt2.x=pt1.x; else pt1.x=pt2.x; 
		
		
		PathGraphic p1=new PathGraphic(pt1, pt2);
		
		/**moves line to the right until it no longer overlaps any data shape*/
		while (doesOverLapDataShapes(p1)) {
			p1.getPoints().applyAffine(AffineTransform.getTranslateInstance(5, 0));
			p1.updatePathFromPoints();
			pt1.x+=5;
			pt2.x+=5;
		}
		
		//p1.moveLocation(0, -10);
		p1=new PathGraphic(pt0, pt1, pt2, pt3);
		
		
		Double pmid = new Point2D.Double( pt1.getX(), (pt1.getY()+pt2.getY())/2);
		ConnectorGraphic p3 = new ConnectorGraphic( true, pt0, pmid, pt3);
		
		return p3;
	}

/**returns true if the horizontal line overlaps with data shapes*/
	protected boolean doesOverLapDataShapes(PathGraphic p1) {
		Rectangle r = p1.getBounds();
		r.height=2;
		ArrayList<LocatedObject2D> items = new BasicObjectListHandler().getOverlapOverlaypingOrContainedItems(r, this.getImageClicked());
		items=new ArraySorter<LocatedObject2D>().getThoseOfClass(items, DataShowingShape.class);
		boolean overlapsDataShapes=items.size()>0;
		
		return overlapsDataShapes;
	}

	/**returns the point in the shape with the lowest y value*/
	private Point2D.Double highestPointInDataShape(DataShowingShape pressShape2, double x, double y) {
		/**if (pressShape2 instanceof DataBarShape) {
			GraphicLayer p = pressShape2.getParentLayer();
			if (p instanceof GenericDataSeriesGroup) {
				if (null!=((GenericDataSeriesGroup) p).getErrorBar())
				return highestPointInDataShape(((GenericDataSeriesGroup) p).getErrorBar(), x, y);
			}
		}*/
		
		Rectangle bounds = pressShape2.getBounds();
		DataSeries part = pressShape2.getPartialSeriesDrawnAtLocation(x, y);
		if (part!=null) bounds=pressShape2.getPartialShapeAtLocation(x, y).getBounds();
		
		return new Point2D.Double(bounds.getCenterX(),bounds.getMinY());
	
	}
	
	/**returns the point in the shape with the highest value*/
	private Point2D.Double rightmostPointInDataShape(DataShowingShape pressShape2, double x, double y) {
		/**if (pressShape2 instanceof DataBarShape) {
			GraphicLayer p = pressShape2.getParentLayer();
			if (p instanceof GenericDataSeriesGroup) {
				if (null!=((GenericDataSeriesGroup) p).getErrorBar())
				return highestPointInDataShape(((GenericDataSeriesGroup) p).getErrorBar(), x, y);
			}
		}*/
		
		Rectangle bounds = pressShape2.getBounds();
		DataSeries part = pressShape2.getPartialSeriesDrawnAtLocation(x, y);
		if (part!=null) bounds=pressShape2.getPartialShapeAtLocation(x, y).getBounds();
		
		return new Point2D.Double(bounds.getMaxX(),bounds.getCenterY());
	
	}
	
	/**Called after the mouse is released ober a plot, */
	protected void afterPlotRelease() {
		
		PlotArea a1 = getPressShape().getPlotArea();
		PlotArea a2 = getDragShape().getPlotArea();
		
		if (a1!=a2) return;
		
		ZoomableGraphic toAdd = getTTextMarkingGraphic(this.useLinkingLine());
			if (toAdd instanceof ShapeGraphic) return;
		
			GraphicLayer layer = getPressShape().getParentLayer().getParentLayer();
			
			layer.add(toAdd);
			
			this.getImageDisplayWrapperClick().getUndoManager().addEdit(new UndoAddItem(layer, toAdd));
		
	}

	
	private ZoomableGraphic getTTextMarkingGraphic(boolean linkline) {
		ZoomableGraphic toAdd=null;
			TextGraphic text = getTTestResult();
			if (linkline)
					{
				if (text==null) return preliminaryPath;
					GraphicLayerPane pane = new GraphicLayerPane("t-test");
					toAdd=pane;
					pane.add(preliminaryPath);
					pane.add(text);
					}
			else toAdd=text;
		return toAdd;
	}

	private TextGraphic getTTestResult() {
		if (isColumnPlot() &&pressShape==dragShape ) return null;
		DataSeries d1;
		
		DataSeries d2; 
		if (isColumnPlot()) {
			d1= getPressShape().getTheData();
			d2= getDragShape().getTheData();
		} else {
			if (dataSeriesPressed==null||dataSeriesDragged==null) return null;
			d1 = dataSeriesPressed;
			d2 = dataSeriesDragged;
		}
		
		if ( dataSeriesPressed==dataSeriesDragged) return null;
		
		TextGraphic text = createTextForTest(d1, d2);
		return text;
	}

	private boolean isColumnPlot() {
		return pressShape.getPlotArea() instanceof ColumnPlot;
	}

	

	private boolean useLinkingLine() {
		return linkType==LINK_WITH_LINE;
	}


	
	public String getToolName() {
		return "Perform T-Test (Drag from one column to another)";
	}
	
	/**creates a text item that displays the results of the test*/
	private TextGraphic createTextForTest(DataSeries data1, DataSeries data2) {
		if (data1.getIncludedValues().length()<3) return null;
		if (data2.getIncludedValues().length()<3) return null;
		StatTestShower test = new StatTestShower(tTestType, numberTails, markType);
		double pValue;
		try {
			pValue = test.calculatePValue(data1, data2);
			
		} catch (Exception e) {
			return null;
		}
		
		
		TextGraphic text = test.createTextForPValue(pValue);
		double ty=0; 
		double tx=0;
		if(preliminaryPath!=null) {
			ty = preliminaryPath.getBounds().getMinY();
			tx = preliminaryPath.getBounds().getCenterX();
		}
		if (!useLinkingLine()) {
			Point2D.Double h = this.highestPointInDataShape(getDragShape(), this.getDragCordinateX(), this.getDragCordinateY());
			ty=h.getY();
			tx=h.getX();
		}
		text.setLocationType(RectangleEdges.BOTTOM);
		text.setLocation(tx, ty);
		return text;
	}
	
	public class PlotToolIcon extends PlotIcon {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String name="**";
		public PlotToolIcon() {
			super(new Color[] {Color.gray, Color.black, Color.darkGray}, new int[] {10, 15, 20});
		}
		

		@Override
		public void paintLayer2Icon(Component arg0, Graphics g, int arg2, int arg3) {
			TextGraphic.setAntialiasedText(g, true);
			//new PlotIcon().paintIcon(arg0, g, arg2, arg3);
			
			g.setFont(new Font("Arial",Font.BOLD, 18 ));
			g.setColor(Color.black);
			g.drawString(name, arg2+5, arg3+14);
				}}

	
	@Override
	public void showOptionsDialog() {
		new ReflectingFieldSettingDialog(this, "markType", "linkType", "tTestType", "numberTails").showDialog();
	}
}
