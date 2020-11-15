package graphicalObjects_BasicShapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjectHandles.CountHandle;
import graphicalObjectHandles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import objectDialogs.PolygonGraphicOptionsDialog;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.SimpleItemUndo;
import undo.UndoScalingAndRotation;
import undo.UndoStrokeEdit;

public class RegularPolygonGraphic extends RectangularGraphic {
	

	{name="Polygon";}
	/**
	 * 
	 */
	
	private CountParameter nvertex=new CountParameter(this, 5); {nvertex.parameterName="n sides";}
	
	private static final long serialVersionUID = 1L;
	
	
	public RectangularGraphic blankShape(Rectangle r, Color c) {
		RegularPolygonGraphic r1 = new RegularPolygonGraphic(r, getNvertex());
		
		r1.setDashes(new float[]{100000,1});
		r1.setStrokeWidth(4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	public String getPolygonType() {
		switch(getNvertex()) {
			case 3:
				return "Triangle";
			case 4:
				return "Parallelogram";
			case 5:
				return "Pentagon";
			case 6:
				return "Hexagon";
			case 7:
				return "Septagon";
			case 8:
				return "Octogon";
			case 9:
				return "Nonogon";
			case 10:
				return "Decagon";
			
		}
		
		
		return "Regular Polygon";
	}

	public RegularPolygonGraphic copy() {
		RegularPolygonGraphic output = new RegularPolygonGraphic(this);
		output.setNvertex(getNvertex());
		return output;
	}
	
	public RegularPolygonGraphic(Rectangle2D rectangle) {
		super(rectangle);
	}
	public RegularPolygonGraphic(Rectangle rectangle, int nV) {
		super(rectangle);
		this.setNvertex(nV);
	}
	
	public RegularPolygonGraphic(RectangularGraphic r) {
		super(r);
	}

	/**implements a formular to produce a regular polygon with a certain number of vertices*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		double centx = x+rx;
		double centy = y+ry;
		double angle=getIntervalAngle();
		path.moveTo(centx+rx,centy);
		for(int i=1; i<getNvertex();i++) {
				double curx=centx+Math.cos(angle*i)*rx;
				double cury=centy+Math.sin(angle*i)*ry;
				path.lineTo(curx, cury);
		}
		path.closePath();
		this.setClosedShape(true);
		
		return path;
		
	}

	public double getIntervalAngle() {
		return 2*Math.PI/getNvertex();
	}
	
	
	/**returns the points that define the stroke' handles location and reference location.
	   Precondition: the distance between the two points should be about half the stroke*/
		public Point2D[] getStrokeHandlePoints() {
			PathIterator pi = getShapeForStrokeHandlePoints().getPathIterator(null);
			selectSegmentForStrokeHandle(pi);
			double[] d=new double[6];pi.currentSegment(d);
			Point2D location2 =new Point2D.Double(d[0],d[1]);
			pi.next();d=new double[6];pi.currentSegment(d);
			Point2D location1 =new Point2D.Double(d[0],d[1]);
			this.getRotationTransform().transform(location2, location2);
			this.getRotationTransform().transform(location1, location1);
			return calculatePointsOnStrokeBetween(location1, location2);
		}

	protected Shape getShapeForStrokeHandlePoints() {
		return getShape();
	}
		
	
	protected void selectSegmentForStrokeHandle(PathIterator pi) {
		// TODO Auto-generated method stub
		
	}

	RectangularGraphic rectForIcon() {
		return  blankShape(new Rectangle(0,0,12,10), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
	}



	public int getNvertex() {
		return nvertex.getValue();
	}
	
	public void setNvertex(int n) {
		if(n>=minimumNVertex())
		nvertex.setValue(n);
	}

	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}

	

	public int minimumNVertex() {
		return 3;
	}
	
	@Override
	public void showOptionsDialog() {
		new PolygonGraphicOptionsDialog(this, false).showDialog();
	}
	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		nvertex.setMinValue(minimumNVertex());
		
		list.add(new CountHandle(this, nvertex, 900215));
		return list;
	}
	
	
	
	@Override
	public AbstractUndoableEdit provideUndoForDialog() {
		return new CombinedEdit(new UndoStrokeEdit(this), new UndoScalingAndRotation(this), new ColorEditUndo(this),new SimpleItemUndo<CountParameter> (nvertex));
	}
	
	
	
}
