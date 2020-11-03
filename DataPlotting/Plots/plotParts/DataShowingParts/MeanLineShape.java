package plotParts.DataShowingParts;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import dataSeries.Basic1DDataSeries;
import dataSeries.DataSeries;
import plotParts.Core.PlotCordinateHandler;

public class MeanLineShape extends AbstractDataLineShape implements DataLineShape{

	/**This shape can plot a a bar, line or point at the mean value of a dataset*/
	public static int LineOnly=0;
	;{super.setName("Line");}
	
	int type=LineOnly;
	
	
	public MeanLineShape(DataSeries data, int type) {
		super(data);
		this.setDashes(new float[] {});
		this.setStrokeWidth(1);
		this.setStrokeColor(Color.BLACK);
		this.setLineType(type);
	}
	
	public MeanLineShape(DataSeries data) {
		this(data, LineOnly);
	}
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(MeanLineShape m) {
		this.type=m.getLineType();
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
		return getShapeForDataPoint(datai, type);
	}
	
	/**creates a shape for the data point d*/
	Point2D getShapeForDataPoint(Basic1DDataSeries datai, int type) {
		if (area==null) return new Point(0,0);
		double mean = datai.getMean();
		double offset = datai.getPositionOffset();
		double position = datai.getPosition(0);
		
		
		double vOff = super.getValueOffset(position);
		PlotCordinateHandler c = getCordinateHandler();
		return c.translate(position, mean+vOff, offset, 0);
	}

	public void updatePlotArea() {
		// TODO Auto-generated method stub
		
	}
	

	public int getLineType() {
		return type;
	}

	public void setLineType(int type) {
		this.type = type;
	}
	

}
