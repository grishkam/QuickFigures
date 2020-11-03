package kaplanMeierPlots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D.Double;

import dataSeries.KaplenMeierDataSeries;
import dialogs.CensorMarkDialog;
import graphicalObjects_BasicShapes.RectangularGraphic;
import plotParts.Core.PlotCordinateHandler;
import plotParts.DataShowingParts.DataLineShape;
import plotParts.DataShowingParts.DataShowingShape;
import plotParts.DataShowingParts.PointModel;

public class KaplanMeierCensorShower extends  DataShowingShape implements DataLineShape{

	/**This shape can plot a a bar, line or point at the mean value of a dataset*/
	public static final int LineOnly=0, CrossingLine=1, PlusMark=2, Circle=3, ShapeMark=4;;
	;{super.setName("Censored");
	super.setBarWidth(3);
	}
	
	int type=LineOnly;
	PointModel pointModel=new PointModel();
	KaplenMeierDataSeries dKap=null;
	
	public KaplanMeierCensorShower(KaplenMeierDataSeries data, int type) {
		super(data);
		dKap=data;
		this.setDashes(new float[] {});
		this.setStrokeWidth(1);
		this.setStrokeColor(Color.BLACK);
		this.setLineType(type);
	}
	
	public KaplanMeierCensorShower(KaplenMeierDataSeries data) {
		this(data, LineOnly);
	}
	
	/**sets the traits that must be consistent between series on the same plot*/
	public void copyTraitsFrom(DataLineShape m) {

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
			double censor=dKap.getNumberCensoredAtTime(indX);
			if (censor==0) continue;
			
			Double point = c.translate(indX, depY, 0,0);
			double barWidth = getBarWidth();
			Double point2 = c.translate(indX, depY, 0,-barWidth);//the previous height
			
			
			if (type==Circle) {
				java.awt.geom.Ellipse2D.Double e = new Ellipse2D.Double(point.getX()-barWidth, point.getY()-barWidth,  2*barWidth, 2*barWidth);
				path.append(e, false); continue;
			}
			if (type==ShapeMark) {
				RectangularGraphic cop = pointModel.createBasShapeCopy();
				cop.setWidth(2*barWidth);cop.setHeight(2*barWidth);
				cop.setLocation(point);
				path.append(cop.getRotationTransformShape(), false); continue;
			}
			
			if (type==CrossingLine||type==PlusMark) {
				 point = c.translate(indX, depY, 0,barWidth);
			}
			
			path.moveTo(point.getX(), point.getY());
			path.lineTo(point2.getX(), point2.getY());
			
			if (type==PlusMark) {
				 point = c.translate(indX, depY, barWidth, 0);
				 point2 = c.translate(indX, depY, -barWidth, 0);
				 path.moveTo(point.getX(), point.getY());
				 path.lineTo(point2.getX(), point2.getY());
			}
			
			
			//if (point.getY()!=point2.getY()) path.lineTo(point.getX(), point.getY());
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
	
	/**returns the area that this item takes up for 
	  receiving user clicks*/
	@Override
	public Shape getOutline() {
	 return	new BasicStroke(3).createStrokedShape(this.getShape());

	}

	@Override
	public double getMaxNeededValue() {return 1;}

	public int getBarType() {
		// TODO Auto-generated method stub
		return type;
	}

	public void updatePlotArea() {
		area.fullPlotUpdate();
		
	}

	public boolean showsAsPoint() {
		return type==ShapeMark;
	}

	public void setBarType(int choiceIndex) {
		type=choiceIndex;
	}
	
	public void showOptionsDialog() {
		new CensorMarkDialog(this, false).showDialog();;
	}
	
	public PointModel getPointModel() {
		return pointModel;
	}
}
