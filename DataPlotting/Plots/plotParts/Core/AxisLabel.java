package plotParts.Core;

import java.awt.geom.Point2D;

import genericPlot.BasicPlot;
import plotParts.DataShowingParts.PlotLabel;

public class AxisLabel extends PlotLabel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AxisLabel(String name) {
		super(name);
		
	}

	public AxisLabel(String string, BasicPlot basicPlot) {
		super(string, basicPlot);
	}

	@Override
	public void scaleAbout(Point2D p, double mag) {
		double h=this.getSnapPosition().getHorizontalOffset();
		double v=this.getSnapPosition().getVerticalOffset();
		super.scaleAbout(p, mag);
		
		getSnapPosition().setHorizontalOffset( Math.round(h*mag));
		getSnapPosition().setVerticalOffset( Math.round(v*mag));
		this.putIntoSnapPosition();
		
	}
}
