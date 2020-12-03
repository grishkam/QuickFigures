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
		double h=this.getAttachmentPosition().getHorizontalOffset();
		double v=this.getAttachmentPosition().getVerticalOffset();
		super.scaleAbout(p, mag);
		
		getAttachmentPosition().setHorizontalOffset( Math.round(h*mag));
		getAttachmentPosition().setVerticalOffset( Math.round(v*mag));
		this.putIntoSnapPosition();
		
	}
}
