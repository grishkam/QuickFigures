package utilityClassesForObjects;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import utilityClasses1.NumberUse;

public class LobeMaker extends BasicShapeMaker {

	/**
	 * 
	 */
	public double lobe_Width=10;
	public double lobe_Height=7.5;
	
	
	public double sFactor=.33;
	public double nFactor=0.1;
	
	public double sheet_Length=25;
	public double sheet_Thickness=6;
	public double sheet_Shrink=4;
	public double sheet_Spacing=11;
	
	public int _n_Preceding_Lobes=1;
	public int _n_Succeding_Lobes=3;
	
	private static final long serialVersionUID = 1L;

	private PathPointList creatLobe(java.awt.geom.Rectangle2D.Double double1) {
		PathPointList pp = new PathPointList();
		ArrayList<Point2D> points = RectangleEdges.getLocationsForHandles(double1);
		
		for(int i=0; i<4; i++) {
			Point2D point1 = points.get(i);
			pp.addPoint(point1);
			PathPoint added = pp.get(pp.size()-1);
			double rad=sFactor;
			double rad2=sFactor;
			if (i%2==0) rad=double1.width; else rad2=double1.width;
			if (i%2!=0) rad=double1.height; else rad2=double1.height;
			rad*=sFactor;
			rad2*=sFactor;
			
			double angle=Math.PI/4-i*Math.PI/2;
			Double curvep = NumberUse.getPointFromRadDeg(point1, rad , angle);
			added.setCurveControl2(curvep );
			curvep = NumberUse.getPointFromRadDeg(point1, rad2 , angle+Math.PI);
			added.setCurveControl(curvep );
		}
		
		
		
		
		//
		
		/**
		Point2D cc1 = points.get(5);
		Double curve1 = new Point2D.Double(cc1.getX(), cc1.getY()-double1.height/sFactor);
		pp.get(0).setCurveControl2(curve1);
		pp.get(1).setCurveControl(curve1);
		
		
		cc1 = points.get(6);
		curve1 = new Point2D.Double(cc1.getX()+double1.width/sFactor, cc1.getY());
		pp.get(1).setCurveControl2(curve1);
		pp.get(2).setCurveControl(curve1);
		
		cc1 = points.get(7);
		curve1 = new Point2D.Double(cc1.getX(), cc1.getY()+double1.height/sFactor);
		pp.get(2).setCurveControl2(curve1);
		pp.get(3).setCurveControl(curve1);
		
		
		cc1 = points.get(8);
		curve1 = new Point2D.Double(cc1.getX()-double1.width/sFactor, cc1.getY());
		pp.get(3).setCurveControl2(curve1);
		pp.get(0).setCurveControl(curve1);
		*/
		
		return pp;
	}
	
	
	public PathPointList getPathPointList() {
		PathPointList output = createSheet(sheet_Length);;
		for(int i=1; i<_n_Succeding_Lobes+1; i++) {
			addShrunkSheet(output, i);
			
		}
		for(int i=1; i<this._n_Preceding_Lobes+1; i++) {
			addShrunkSheet(output, -i);
			
		}
		
		moveAllIntoView(output);
		return output;
	}
	
	void addShrunkSheet(PathPointList output, int i) {
		PathPointList ns = createSheet(sheet_Length-Math.abs(i)*sheet_Shrink);
		ns.applyAffine(AffineTransform.getTranslateInstance(-Math.abs(i)*sheet_Shrink*2, sheet_Spacing*i));
		output.concatenateClosed(ns);
	}
	
	/**Creates a Golgi sheet of the given length*/
	public PathPointList createSheet(double sheet_Length) {
		
		double lobe0=0;//-(sheet_Thickness-lobe_Height)/2;
		java.awt.geom.Rectangle2D.Double rect = new Rectangle2D.Double(0,lobe0, lobe_Width, lobe_Height);
		
		
		PathPointList out =new PathPointList();
		double sheetZeroH=0;
		
		out.addPoint(0-sheet_Length, sheetZeroH);
		PathPoint l2 = out.getLastPoint();
		
		
		double xCurveTowardCenter = 0-sheet_Length*3/2;
		double xCurveAwayCenter = 0-sheet_Length/2;
		double yCurveOfSheet=sheet_Thickness*nFactor;
		
		l2.setCurveControl2(new Point2D.Double(xCurveAwayCenter, sheetZeroH+yCurveOfSheet));
		l2.setCurveControl(new Point2D.Double(xCurveTowardCenter, sheetZeroH-yCurveOfSheet));
		
		
		PathPointList lobe = creatLobe(rect);
		lobe.get(0).setAngleOfCurveControls(-Math.PI/10+Math.PI*1.25);
		lobe.get(3).setAngleOfCurveControls(-Math.PI/10);
		out.concatenate(lobe);
		
		double bottomOfSheetY=sheetZeroH+sheet_Thickness;
		
		out.addPoint(0-sheet_Length,bottomOfSheetY);
		out.getLastPoint().setCurveControl(new Point2D.Double(xCurveAwayCenter, bottomOfSheetY-yCurveOfSheet));
		out.getLastPoint().setCurveControl2(new Point2D.Double(xCurveTowardCenter,bottomOfSheetY+yCurveOfSheet));
		
		
		super.addHorizontalFlippedIn(out, -sheet_Length*2);
		
		return out;
		//return null;
	}

	public static void main(String[] args) {
		
		
		
		
		LobeMaker cpc = new LobeMaker();
		int l = cpc.getClass().getDeclaredFields().length;
		showShape(cpc);
		
	}
}
