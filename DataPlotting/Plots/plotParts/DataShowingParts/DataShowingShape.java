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
 * Date Modified: Nov 13, 2021
 * Version: 2022.0
 */
package plotParts.DataShowingParts;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import dataSeries.Basic1DDataSeries;
import dataSeries.DataSeries;
import graphicalObjects.CordinateConverter;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.BasicShapeGraphic;
import handles.HasSmartHandles;
import handles.miniToolbars.ActionButtonHandleList;
import illustratorScripts.ArtLayerRef;
import logging.IssueLog;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import plotParts.Core.PlotArea;
import plotParts.Core.PlotCordinateHandler;
import plotParts.Core.PlotOrientation;
import utilityClasses1.NumberUse;

/**An abstract class for shapes that reflect a particular data series in a plot*/
public abstract class DataShowingShape extends BasicShapeGraphic implements HasUniquePopupMenu, HasSmartHandles, PlotComponent{

	protected PlotArea area;
	/**values are relevant to one dimensional data in categories*/
	private DataSeries theData;
	protected transient Shape currentDrawShape;
	protected HashMap<Shape, DataSeries> partialShapes=new HashMap<Shape, DataSeries>();
	
	/**width used for object*/
	private double barWidth=6;

	private PlotOrientation orientation=PlotOrientation.BARS_VERTICAL;//orietnation is either PlotOrientation.barsvertical or horizontal

	/**the index of the axis that will be used to display data*/
	private int axisChoice=0;
	
	protected transient DataBarSmartHandleList smartHandles;
	
	/**returns the dependent and independent variable values for i-th point*/
	protected double getDependantVariableValue(int i) {
		return getTheData().getValue(i);
	}
	protected double getIndependantVariableValue(int i) {
		return getTheData().getPosition(i);
	}
	

	
	public DataShowingShape(Shape shape2) {
		super(shape2);
	}
	
	public DataShowingShape(DataSeries data) {
		this(new Rectangle());
		setTheData(data);
		setShape(getShape());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**returns the shape being drawn*/
	public Shape getShape() {
		if (currentDrawShape==null)updateShape();;
		return currentDrawShape;
	}
	
	/**each subclass implements this differently*/
	protected void updateShape() {
		
	}
	
	/**lets the object know that the shape must be updated to reflect the current plot axes*/
	public void requestShapeUpdate() {
		currentDrawShape=null;
	}
	
	
	protected boolean onVertical() {
		return this.orientation==PlotOrientation.BARS_VERTICAL;
		
	}
	

	/**sets the plot area that the shape is drawn on*/
	public void setPlotArea(PlotArea plotArea) {
		this.area=plotArea;
		
	}
	
	/**returns the plot area*/
	public PlotArea getPlotArea() {
		return area;
	}
	
	public int isUserLocked(){
		return 1;
	}
	
	void updateShape(Shape s) {
		currentDrawShape=s;
	}
	
	public void demandShapeUpdate() {
		currentDrawShape=null;
	}

	public double getBarWidth() {
		return barWidth;
	}

	public void setBarWidth(double barWidth) {
		this.barWidth = barWidth;
	}
	
	
	@Override
	public void moveLocation(double xmov, double ymov) {
		//x=x+xmov;
		//y=y+ymov;
		currentDrawShape=	AffineTransform.getTranslateInstance(xmov, ymov).createTransformedShape(currentDrawShape);
		notifyListenersOfMoveMent();
	}
	public int getPosistion() {
		return (int) getTheData().getPosition(0);
	}
	public DataSeries getTheData() {
		return theData;
	}
	public void setTheData(DataSeries theData) {
		this.theData = theData;
	}
	
	
	public PopupMenuSupplier getMenuSupplier(){
		GraphicLayer par = this.getParentLayer();
		if (par instanceof HasUniquePopupMenu) {
				return ((HasUniquePopupMenu) par).getMenuSupplier();
			}
		return null;
		}
	
	public void setOrientation(PlotOrientation orientation2) {
		this.orientation=orientation2;
	}
	
	/**the plot orientation*/
	public PlotOrientation getOrientation() {
		return this.orientation;
	}

	
	@Override
	public double getMaxNeededValue() {
		
		double max = 0;
		
		for(double p: getTheData().getAllPositions()) {
			DataSeries data2=getTheData().getValuesForPosition(p);
			if (isDataSeriesInvalid(data2)) continue;
			Basic1DDataSeries data = data2.getIncludedValues();
			double max2 = data.getMean()+data.getSDDev();
			if (max2>max) max=max2;
		}
		
		double vOff = 0;
		for(double p: getTheData().getAllPositions()) {
			if (getTheData().getValueOffsetMap()==null) continue;
			Double val = getTheData().getValueOffsetMap().get(p);
			DataSeries m = getTheData().getValuesForPosition(p);
			if (isDataSeriesInvalid(m)) continue;
			
			double mean=m.getIncludedValues().getMean();;
			if (val!=null&&val+mean>vOff) vOff=val+mean;
		}
		if (vOff>max) return vOff;
		
		return max;

	}
	public boolean isDataSeriesInvalid(DataSeries m) {
		if(m.length()==0) return true;
		return m==null;
	}

	@Override
	public double getMaxNeededPosition() {
		double max = NumberUse.findMax(getTheData().getAllPositions());
		if (getCordinateHandler()!=null)
     	max+=getTheData().getPositionOffset()/getCordinateHandler().getPositionScalingFactor();
		return max;
	}
	
	
	public PointModel getPointModel() {
		return null;
	}
	
	public PlotCordinateHandler getCordinateHandler() {
		if (area==null) {IssueLog.log("null plot area"); return null;}
		return area.getCordinateHandler(this.getAxisChoice());
	}
	
	protected void lineBetween(Path2D path, Point2D p1,  Point2D p2) {
		path.moveTo(p1.getX(), p1.getY());
		path.lineTo(p2.getX(), p2.getY());
	}
	
	protected void lineTo(Path2D path,  Point2D p2) {
		
		path.lineTo(p2.getX(), p2.getY());
	}

	public double getValueOffset(double position) {
		HashMap<Double, Double> vOffMap = this.getTheData().getValueOffsetMap();
		if (vOffMap==null || !vOffMap.containsKey(position))
			return 0;
		
			return vOffMap.get(position);
	
	}
	
	@Override
	public void scaleAbout(Point2D p, double mag) {
		super.scaleAbout(p, mag);
		barWidth*=mag;
		requestShapeUpdate();
		area.fullPlotUpdate();
	}
	
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter cords) {
		if (selected) {
			/**SmartHandleList smlist = getSmartHandleList();
			if(smlist!=null)
				smlist.draw(g2d, cords);
			else */{
				ArrayList<Point2D> list = new ArrayList<Point2D> ();
				PathIterator shape2 =getRotationTransformShape().getPathIterator(AffineTransform.getTranslateInstance(0, 0));
				while(!shape2.isDone())
				{
					double[] arr = new double[6];
					shape2.currentSegment(arr);
					list.add(new Point2D.Double(arr[0], arr[1]));
					shape2.next();
				}
				
				getGrahpicUtil().drawHandlesAtPoints(g2d, cords, list);
			   handleBoxes=getGrahpicUtil().lastHandles;
			}
		}
		
	}
	public int getAxisChoice() {
		return axisChoice;
	}
	public void setAxisChoice(int axisChoice) {
		this.axisChoice = axisChoice;
	}
	
	protected Shape createOutlineForShape(Shape shape) {
		return	new BasicStroke(3).createStrokedShape(shape);
	}
	
	/**returns the data series that this object drew at a given xy coordinate
	 */
	public DataSeries getPartialSeriesDrawnAtLocation(double dx, double dy) {
		Shape s = getPartialShapeAtLocation(dx, dy);
		if (s==null) return null;
		return partialShapes.get(s);
	}
public Shape getPartialShapeAtLocation(double dx, double dy) {
		for(Shape s: this.partialShapes.keySet()) {
			if (createOutlineForShape(s).contains(dx, dy)) return s;
		}
		return null;
	}
	
	public HashMap<Shape, DataSeries> getPartialShapeMap() {
		return partialShapes;
	}
	
	/**returns one partial shape that is the rightmost shape*/
	public Shape getLastPartialShape() {
		Set<Shape> set = getPartialShapeMap().keySet();
		
		if(set.size()==0)
			return null;
		
		Shape output=null;
		double  maxX = Double.MIN_VALUE;
		for(Shape s: set) {
			if(this.orientation==PlotOrientation.BARS_VERTICAL) {
					if(s.getBounds().getMaxX()>maxX) {
						
						output=s;
						maxX=s.getBounds().getMaxX();
					}
					
					}
			if(this.orientation==PlotOrientation.BARS_HORIZONTAL) {
				if(s.getBounds().getMaxY()>maxX) {
					
					output=s;
					maxX=s.getBounds().getMaxY();
				}
				
				}
		} 
		
		return output;
	}
	
	
	@Override
	public void onAxisUpdate() {
			requestShapeUpdate();
	}
	
	
	/**returns the shape that will be used for the path copy
	needed for proper export
	 */
	public Shape getShapeForPathCopy() {
		return getRotationTransformShape();
	}
	
	
	/**implementation of an interface required for generating adobe illustrator scripts*/
	@Override
	public Object toIllustrator(ArtLayerRef aref) {
		return this.createPathCopy().toIllustrator(aref);
	}
	

	/**user does not control the angles of these shapes*/
	public boolean doesSetAngle() {return false;}
	
	
	/**
	Creates an action handle list for the object
	 */
	public ActionButtonHandleList createActionHandleList()  {
		ActionButtonHandleList list = super.createActionHandleList();
		
		MiniToolbars.addExtraHandles(this,list);
		
		return list;
		
		
	}
	
	

}
