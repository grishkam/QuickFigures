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
		double h=this.getSnappingBehaviour().getSnapHOffset();
		double v=this.getSnappingBehaviour().getSnapVOffset();
		super.scaleAbout(p, mag);
		
		getSnappingBehaviour().setSnapHOffset( Math.round(h*mag));
		getSnappingBehaviour().setSnapVOffset( Math.round(v*mag));
		this.putIntoSnapPosition();
		
	}
}
