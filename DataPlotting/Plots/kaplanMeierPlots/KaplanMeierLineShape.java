package kaplanMeierPlots;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D.Double;

import dataSeries.KaplenMeierDataSeries;
import plotParts.Core.PlotCordinateHandler;
import plotParts.DataShowingParts.AbstractDataLineShape;
import plotParts.DataShowingParts.RegressionLineShape;

public class KaplanMeierLineShape extends  AbstractDataLineShape {

	/**This shape can plot a a bar, line or point at the mean value of a dataset*/
	public static int LineOnly=0;
	;{super.setName("Line");}
	
	int type=LineOnly;
	
	KaplenMeierDataSeries dKap=null;
	
	public KaplanMeierLineShape(KaplenMeierDataSeries data, int type) {
		super(data);
		dKap=data;
		this.setDashes(new float[] {});
		this.setStrokeWidth(1);
		this.setStrokeColor(Color.BLACK);
		this.setLineType(type);
	}
	
	public KaplanMeierLineShape(KaplenMeierDataSeries data) {
		this(data, LineOnly);
	}
	
	
	
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(RegressionLineShape m) {
		this.type=m.getLineType();
		super.copyStrokeFrom(m);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void updateShape() {
		if (dKap==null||getTheData().getAllPositions().length==1||area==null) {
			if (area==null)currentDrawShape= new Rectangle(0,0,10,10);return;
		}
		
		PlotCordinateHandler c = super.getCordinateHandler();
			
		java.awt.geom.Path2D.Double path = new Path2D.Double();
		double[] all = dKap.getAllPositions();
		
		double lastPercent=1;
			
		for(int i=0; i<all.length; i++) {
			double indX = all[i];
			double depY = dKap.getEstimatorAtTime(indX);
			Double point = c.translate(indX, depY, 0,0);
			Double point2 = c.translate(indX, lastPercent, 0,0);//the previous height
			if (!area.getPlotArea().contains(point)) {
				//IssueLog.log("Plot axis problem");
				continue;
			}
			if (i==0) {
				path.moveTo(point.getX(), point.getY());
				lastPercent=depY;
				continue;
			};
			
			path.lineTo(point2.getX(), point2.getY());
			if (point.getY()!=point2.getY()) path.lineTo(point.getX(), point.getY());
			lastPercent=depY;
		}
		
		currentDrawShape=path;
	}
	

	public int getLineType() {
		return type;
	}

	public void setLineType(int type) {
		this.type = type;
	}
	


	@Override
	public double getMaxNeededValue() {return 1;}
}
