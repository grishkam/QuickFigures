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
 * Version: 2023.1
 */
package plotParts.Core;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

/**this class performs computations to determine the physical location
  that corresponds to a data value on the plot*/
public class PlotCordinateHandler implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AxesGraphic yAxis;
	private AxesGraphic xAxis;
	private boolean vertical;
	
	public PlotCordinateHandler(AxesGraphic xAxis, AxesGraphic yAxis, boolean vertical) {
		this.yAxis=yAxis;
		this.xAxis=xAxis;
		this.vertical=vertical;
	}
	
	/**returns now x and y axes but which are the dependant and independant variable axes*/
	public AxesGraphic getDependantVariableAxis() {
		if (vertical) return yAxis;
		return xAxis;
	}

	public AxesGraphic getInDependantVariableAxis() {
		if (vertical) return xAxis;
		return yAxis;
	}
	
	/**returns the distance in cordinates between one unit of position and another*/
	public double getPositionScalingFactor() {
		return this.translate(2, 0,0,0).distance(this.translate(1, 0,0,0));
	}
	
	/**translates a position and value into an x,y location*/
	public Point2D.Double translate(double position, double value, double positionOffset,  double valueOffset)
		{
		if (this.vertical) {
			double nx = xAxis.translate(position)+positionOffset;
			double ny = yAxis.translate(value)+valueOffset;
			return new Point2D.Double(nx, ny);
		}
		
		double ny = yAxis.translate(position)+positionOffset;
		double nx = xAxis.translate(value)+valueOffset;
		return new Point2D.Double(nx, ny);
		
		}

	/**returns true if the data point can be plotted with the current axis
	 * TODO: edit to account for gaps*/
	public boolean isPointWithinPlot(double position, double value) {
		Double point = this.translate(position, value, 0, 0);
		Rectangle b = getPlotArea();
		if (b.contains(point)) return true;
		return false;
	}

	public Rectangle getPlotArea() {
		return this.xAxis.getPlot().getPlotArea();
	}
	
}
