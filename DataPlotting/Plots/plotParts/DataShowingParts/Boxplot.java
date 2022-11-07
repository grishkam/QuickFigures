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
 * Version: 2022.2
 */
package plotParts.DataShowingParts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import dataSeries.Basic1DDataSeries;
import dataSeries.DataSeries;
import dialogs.BoxPlotDialog;
import export.pptx.OfficeObjectMaker;
import export.svg.SVGExporter;
import graphicalObjects.CordinateConverter;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.BasicShapeGraphic;
import handles.SmartHandleList;
import plotParts.Core.PlotCordinateHandler;

/**The shape of a boxplot*/
public class Boxplot extends DataShowingShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_IQR=1, TYPE_NORMAL_BOX_PLOT=0;
	private int type=TYPE_NORMAL_BOX_PLOT;
	
	Path2D allFillRect=new Path2D.Double();
	private Rectangle2D fillRect;
	
	/**the ratio of the cap to the width of the box*/
	private double capSize=0.5;



	public Boxplot(DataSeries data) {
		super(data);
	}
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(Boxplot  m) {
		this.setWhiskerType(m.getWhiskerType());
		this.setBarWidth(m.getBarWidth());
		super.copyStrokeFrom(m);
	}
	
	public void copyEverythingFrom(Boxplot  m) {
		this.copyTraitsFrom(m);
		this.copyStrokeFrom(m);
		this.copyColorsFrom(m);
		this.copyAttributesFrom(m);
	}
	
	public Boxplot copy() {
		Boxplot output = new Boxplot(getTheData());
		output.copyEverythingFrom(this);
		return output;
	}

	@Override
	public void draw(Graphics2D g, CordinateConverter cords) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, isAntialize()?RenderingHints.VALUE_ANTIALIAS_ON: RenderingHints.VALUE_ANTIALIAS_OFF);
	
		/**Draws the boxes fill*/
		if (this.isFilled()&&this.allFillRect!=null) {
			Shape fr = cords.getAffineTransform().createTransformedShape(allFillRect);
			g.setColor(getFillColor());
			g.fill(fr);
				//RectangularGraphic r = new RectangularGraphic(fillRect.getBounds());
			//	r.setFillColor(getFillColor());r.setStrokeColor(new Color(255,255,255, 0));
				//r.draw(g, cords);
			}
	super.draw(g, cords);
	
	}
	
	/**updates the shape to account for changes to the style and data*/
	@Override
	protected void updateShape() {
		super.partialShapes=new HashMap<Shape, DataSeries> ();
		allFillRect=new Path2D.Double();
		Path2D output = new Path2D.Double();
		if (area==null) return;
		
		/**if (this.getTheData().getAllPositions().length==1) {
			Basic1DDataSeries series0 = this.getTheData().getIncludedValues();
			
			output.append(addPathPartsForDataSeries(series0), false);
			allFillRect.append(fillRect, false);
			partialShapes.put(output, series0)
			;
		}
		else*/ {
			DataSeries[] all = PlotUtil.getAllSeriesFor(getTheData());
			for(DataSeries a: all) {
				if (isDataSeriesInvalid(a)) continue;
				if (a.length()>2)
					{
					Shape pShape=addPathPartsForDataSeries(a.getIncludedValues());
					output.append(pShape, false);
					allFillRect.append(fillRect, false);
					partialShapes.put(pShape, a);}
				}
		}
				

		
		super.currentDrawShape=output;
	}

	protected Shape addPathPartsForDataSeries(Basic1DDataSeries series0) {
		 return addPathsForPVBox(new Path2D.Double(), series0);
	
	}
	
	protected Shape addPathsForPVBox(Path2D output, Basic1DDataSeries data) {
		double vmin=data.getMin();
		double vq1=data.getQ1();//location of bottom of bar
		double v2=data.getMedian();
		double vq3=data.getQ3();//location of bottom of bar
		double vmax=data.getMax();
		
		//double x1=0;
		//double x2=0;
		//double xcenter=0;
		double position=data.getPosition(0);
		double pOffset=data.getPositionOffset();
		
		if (type==TYPE_IQR) {
			vmin=data.getMinExcludingOutliers();
			vmax=data.getMaxExcludingOutliers();
			
		}
		
		double barWidth2=this.getBarWidth()*this.capSize;
		double boxWidth=this.getBarWidth();
		
		PlotCordinateHandler c = this.getCordinateHandler();
		
		java.awt.geom.Point2D.Double topCross1 = c.translate(position, vmax, pOffset-barWidth2, 0);
		java.awt.geom.Point2D.Double topCross2 = c.translate(position, vmax, pOffset+barWidth2, 0);
		java.awt.geom.Point2D.Double botCross1 = c.translate(position, vmin, pOffset-barWidth2, 0);
		java.awt.geom.Point2D.Double botCross2 = c.translate(position, vmin, pOffset+barWidth2, 0);
		if (barWidth2>0) {
			super.lineBetween(output, topCross1, topCross2);
			super.lineBetween(output, botCross1, botCross2);
	}
		
		java.awt.geom.Point2D.Double topOfUpperLine = c.translate(position, vmax, pOffset, 0);
		java.awt.geom.Point2D.Double botOfUpperLine = c.translate(position, vq3, pOffset, 0);
		super.lineBetween(output, botOfUpperLine, topOfUpperLine);
		if (barWidth2>0&&super.getStrokeCap()==BasicStroke.CAP_ROUND) super.lineTo(output, topCross2);
				
		java.awt.geom.Point2D.Double topOfLowerLine = c.translate(position, vmin, pOffset, 0);
		java.awt.geom.Point2D.Double botOfLowerLine = c.translate(position, vq1, pOffset, 0);
		super.lineBetween(output, botOfLowerLine, topOfLowerLine);
		if (barWidth2>0&&super.getStrokeCap()==BasicStroke.CAP_ROUND) super.lineTo(output, botCross2);
				
		java.awt.geom.Point2D.Double medLine1 = c.translate(position, v2, pOffset-boxWidth, 0);
		java.awt.geom.Point2D.Double medLine2  = c.translate(position, v2, pOffset+boxWidth, 0);
		super.lineBetween(output,  medLine1, medLine2);
		
		/**The top and bottom caps*/
		
		
		java.awt.geom.Point2D.Double b1 = c.translate(position, vq3, pOffset-boxWidth, 0);
		java.awt.geom.Point2D.Double b2 = c.translate(position, vq3, pOffset+boxWidth, 0);
		java.awt.geom.Point2D.Double b3 = c.translate(position, vq1, pOffset+boxWidth, 0);
		java.awt.geom.Point2D.Double b4 = c.translate(position, vq1, pOffset-boxWidth, 0);
		
		java.awt.geom.Path2D.Double rectpath = new Path2D.Double();
		super.lineBetween(rectpath, b1, b2);
		super.lineBetween(rectpath, b3, b4);
				fillRect=rectpath.getBounds2D();
			
				
				output.append(fillRect, false);
				
				return output;
	}
	


	public void updatePlotArea() {
		
		
	}

	public int getWhiskerType() {
		return type;
	}
	
	public void setWhiskerType(int t) {
		type=t;
	}
	
	public void showOptionsDialog() {
		new BoxPlotDialog(this, false).showDialog();
	}
	
	
	public GraphicLayerPane getBreakdownGroup() {
		GraphicLayerPane output = new GraphicLayerPane(this.getName());
		
		BasicShapeGraphic r = new BasicShapeGraphic(allFillRect);
		r.setFillColor(getFillColor());r.setStrokeColor(new Color(255,255,255, 0));
		
		output.add(r);
		
		BasicShapeGraphic shape2 = new BasicShapeGraphic(currentDrawShape);
		shape2.copyAttributesFrom(this);shape2.copyColorsFrom(this);shape2.copyStrokeFrom(this);
		
		output.add(shape2);
		
		
		return output;
	}
	
	@Override
	public SVGExporter getSVGEXporter() {
		return  getBreakdownGroup().getSVGEXporter();
	}
	
	@Override
	public OfficeObjectMaker getObjectMaker() {
		return new GraphicGroup(getBreakdownGroup()).getObjectMaker();
	}
	
	@Override
	public double getMaxNeededValue() {
		Basic1DDataSeries data = this.getTheData().getIncludedValues();
		return data.getMax();

	}
	/**returns the area that this item takes up for 
	  receiving user clicks*/
	@Override
	public Shape getOutline() {
	 return	createOutlineForShape(getShape());

	}
	

	/**sets the ratio of the cap to the width of the box*/
	public void setCapSize(double number) {
		this.capSize=number;
		
	}
	/**returns the ratio of the cap to the width of the box*/
	public double getCapSize() {
		return capSize;
	}
	
	/**returns a smart handle list with a hangle for changing the width*/
	@Override
	public SmartHandleList getSmartHandleList() {
		if (smartHandles==null)
			smartHandles=new DataBarSmartHandleList(this);
		return SmartHandleList.combindLists(smartHandles,super.getButtonList());
	}

}
