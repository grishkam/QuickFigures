package plotParts.Core;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

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

	/**returns true if the data point can be plotted with the current axis*/
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
