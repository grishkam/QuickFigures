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
package plotParts.DataShowingParts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D.Double;
import java.util.HashMap;

import dataSeries.Basic1DDataSeries;
import dataSeries.DataSeries;
import dataSeries.ErrorBarStyle;
import dialogs.ErrorBarDialog;
import plotParts.Core.PlotCordinateHandler;

public class ErrorBarShowingShape extends DataShowingShape implements ErrorBarStyle{

	
	
	private boolean upper=true;
	private boolean lower=true;{super.setName("Error Bar");}
	private int type=DRAW_AS_BAR;
	private int errorShownAs=SEM;
	
	public ErrorBarShowingShape(DataSeries data, int type) {
		super(data);
		this.setDashes(new float[] {});
		this.setStrokeWidth(1);
		this.setStrokeColor(Color.BLACK);
		this.setBarType(type);
	}
	
	public ErrorBarShowingShape(DataSeries data) {
		this(data, DRAW_AS_BAR);
	}
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(ErrorBarShowingShape m) {
		this.type=m.getBarType();
		this.setBarWidth(m.getBarWidth());
		this.lower=m.lower;
		this.upper=m.upper;
		errorShownAs=m.errorShownAs;
		super.copyStrokeFrom(m);
	}
	
	public ErrorBarShowingShape copy() {
		ErrorBarShowingShape output = new ErrorBarShowingShape(this.getTheData());
		output.copyEverythingFrom(this);
		return output;
	}
	
	public void copyEverythingFrom(ErrorBarShowingShape eI) {
		 copyTraitsFrom(eI);
		  copyColorsFrom(eI);
		  copyAttributesFrom(eI);
		  copyStrokeFrom(eI);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void updateShape() {
		super.partialShapes=new HashMap<Shape, DataSeries> ();
		/**if (getTheData().getAllPositions().length==1) {
			
			currentDrawShape=getShapeForDataPoint(getTheData().getIncludedValues());//good enough for 1d data
			partialShapes.put(currentDrawShape, getTheData().getIncludedValues());
		
		}else */{
			
			/**Combines output shapes for each position in the series*/
			Path2D outputShape=new Path2D.Double();
			double[] pos = getTheData().getAllPositions();
			
			/**appends the error bars for all positions with enough points for a bar*/
			for(int i=0; i<pos.length; i++) {
				DataSeries datai2 = getTheData().getValuesForPosition(pos[i]);
				if (isDataSeriesInvalid(datai2)) continue;
				Basic1DDataSeries datai = datai2.getIncludedValues();
				Shape pShape = getShapeForDataPoint(datai);
				if (datai.length()>2) {
					outputShape.append(pShape, false);
					partialShapes.put(pShape, datai2);
					}
				
			}
			currentDrawShape=outputShape;
			
		}
	}
	
	/**creates a shape for the data point d*/
	private Shape getShapeForDataPoint(Basic1DDataSeries series) {
		if (area==null) return new Rectangle(0,0,20,10);
		if (series==null) return new Rectangle();
		double mean = series.getMean();
		double position=series.getPosition(0);
		
		
		
		return createShapeFor(mean, position, getErrowBarLength(series, LOWER_BAR), getErrowBarLength(series, UPPER_BAR));
		
	}

	/**returns the error bar length for the data series*/
	private double getErrowBarLength(Basic1DDataSeries series, int upperOrLower) {
		return series.getErrorBarLength(getErrorDepiction(), upperOrLower);
	}

	/**Given a mean, a position on the axis, and error bar value, returns the shape
	 * for the error bar*/
	protected Path2D createShapeFor(double mean, double position, double barExtendsDown, double barExtendsUp) {
		
		double width=getBarWidth()/2;
		if (type==DRAW_AS_LINE_ONLY) {width=0;}
		double vOffset=getValueOffset(position);
		double lower=mean-barExtendsDown+vOffset;
		double upper=mean+barExtendsUp+vOffset;
		double pOffset=getTheData().getPositionOffset();
		
		PlotCordinateHandler c = getCordinateHandler();
		
		Path2D output = new Path2D.Double();

		
		Double p4 = c.translate(position, mean+vOffset, pOffset, 0);
		if (this.isUpperBarShown()) {
			Double p1 = c.translate(position, upper, pOffset-width, 0);
			Double p2 = c.translate(position, upper, pOffset+width, 0);
			Double p3 = c.translate(position, upper, pOffset, 0);
			
			this.lineBetween(output, p1, p2);
			this.lineBetween(output, p3, p4);
		}
		
		if (this.isLowerBarShown()) {
			Double p1 = c.translate(position, lower, pOffset-width,0);
			Double p2 = c.translate(position, lower, pOffset+width, 0);
			Double p3 = c.translate(position, lower, pOffset, 0);
			this.lineBetween(output, p1, p2);
			this.lineBetween(output, p3, p4);
		
		}
		/**
		if (this.onVertical()) {
			
			
				y1=area.transformY(lower);
				y2=area.transformY(mean);
				y3=area.transformY(upper);
			x3=area.transformX(position)+this.getTheData().getPositionOffset();
			x1=x3-width;
			x2=x3+width;
			
		
			if (this.isUpperBarShown()) {
				output.moveTo(x1, y3);
				output.lineTo(x2, y3);
				output.moveTo(x3, y3);
				output.lineTo(x3, y2);
			
			}
			
			if (this.isLowerBarShown()) {
				output.moveTo(x1, y1);
				output.lineTo(x2, y1);
				output.moveTo(x3, y1);
				output.lineTo(x3, y2);
			
			}
			
			
		}
		
		else {

			x1=area.transformX(lower);
			x2=area.transformX(mean);
			x3=area.transformX(upper);
			y3=area.transformY(position)+this.getTheData().getPositionOffset();
			y1=y3-width;
			y2=y3+width;
		
		
		if (this.isUpperBarShown()) {
			output.moveTo(x3, y1);
			output.lineTo(x3, y2);
			output.moveTo(x3, y3);
			output.lineTo(x2, y3);
		
		}
		
		if (this.isLowerBarShown()) {
			output.moveTo(x1, y1);
			output.lineTo(x1, y2);
			output.moveTo(x1, y3);
			output.lineTo(x2, y3);
		
		}
		
		}*/
		return output;
	}
	
	
	public Shape getOutline() {
		return	new BasicStroke(3).createStrokedShape(this.getShape());
	//	return this.getShape().getBounds2D();
	}

	public void updatePlotArea() {
		// TODO Auto-generated method stub
		
	}

	public int getBarType() {
		return type;
	}

	public void setBarType(int type) {
		this.type = type;
	}

	public boolean isUpperBarShown() {
		return upper;
	}

	public void setUpperBarShown(boolean upper) {
		this.upper = upper;
	}

	public boolean isLowerBarShown() {
		return lower;
	}

	public void setLowerBarShown(boolean lower) {
		this.lower = lower;
	}
	
	public void showOptionsDialog() {
		new ErrorBarDialog(this, false).showDialog();;
	}

	public int getErrorDepiction() {
		// TODO Auto-generated method stub
		return errorShownAs;
	}
	
	public void setErrorDepiction(int form) {
		errorShownAs=form;
	}
	

}
