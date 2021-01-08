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
 * Version: 2021.1
 */
package plotParts.DataShowingParts;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import dataSeries.Basic1DDataSeries;
import dataSeries.DataSeries;
import plotParts.Core.PlotCordinateHandler;

/**A data showing shape that consists of a line connecting several distinct points*/
public class MeanLineShape extends AbstractDataLineShape implements DataLineShape{

	
	;{super.setName("Line");}
	
	
	
	 public MeanLineShape(DataSeries data) {
		super(data);
		this.setDashes(new float[] {});
		this.setStrokeWidth(1);
		this.setStrokeColor(Color.BLACK);
		
	}
	
	
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(MeanLineShape m) {
		
		this.setBarWidth(m.getBarWidth());
		super.copyStrokeFrom(m);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void updateShape() {
		if (getTheData().getAllPositions().length==1)
		currentDrawShape=new Rectangle(0,0,0,0);
		
		else {
			
			/**Combines output shapes for each position in the series*/
			Path2D outputShape=new Path2D.Double();
			double[] pos = getTheData().getAllPositionsInOrder();
			if (pos.length<2) {currentDrawShape=outputShape; return;}
			/**appends the error bars for all positions with enough points for a bar*/
			
			int firstPoint=0;
			for(int i=0; i<pos.length; i++) {
				DataSeries datai = getTheData().getValuesForPosition(pos[i]);
			
				if (isDataSeriesInvalid(datai)) continue;
				
				Point2D point1 = this.getShapeForDataPoint(datai.getIncludedValues());
				
				if (point1==null) { 
					if (i==firstPoint) firstPoint++; 
					continue;
				}
				if (i==firstPoint) outputShape.moveTo(point1.getX(), point1.getY());
				else outputShape.lineTo(point1.getX(), point1.getY());
						}
			currentDrawShape=outputShape;
		}
	}
	
	
	
	/**creates a shape for the data point d*/
	Point2D getShapeForDataPoint(Basic1DDataSeries datai) {
		if (datai==null||datai.length()==0) return null;
		if (area==null) return new Point(0,0);
		double mean = datai.getMean();
		double offset = datai.getPositionOffset();
		double position = datai.getPosition(0);
		
		
		double vOff = super.getValueOffset(position);
		PlotCordinateHandler c = getCordinateHandler();
		return c.translate(position, mean+vOff, offset, 0);
	}

	public void updatePlotArea() {
		
		
	}
	

	
	

}
