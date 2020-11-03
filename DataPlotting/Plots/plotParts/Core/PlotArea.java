package plotParts.Core;

import java.awt.Rectangle;
import java.io.Serializable;

public interface PlotArea extends Serializable {
	public Rectangle getPlotArea() ;

	//public void pullTopAxisTo(double y);

	//public void pullRightAxisTo(double x);
	
	
	/**Transforms a data x,y into plot cordintates x and y**/
	//public double transformX(double x);
	//public double transformY(double y);
	//public boolean isPointWithinPlot(double x, double y);
	
	PlotCordinateHandler getCordinateHandler();
	PlotCordinateHandler getCordinateHandler(int i);
	
	public PlotAxes getXaxis();
	public PlotAxes getYaxis();
	public PlotAxes getSecondaryYaxis();
	public void autoCalculateAxisRanges();
	
	

	public void onAxisUpdate();
	public void fullPlotUpdate();

	public void setAreaDims(double number, double number2);
	public int getOrientation();
	
	public boolean moveEntirePlot(double dx, double dy);
}
