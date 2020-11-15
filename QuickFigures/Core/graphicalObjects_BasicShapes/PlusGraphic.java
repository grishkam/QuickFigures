package graphicalObjects_BasicShapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import applicationAdapters.ToolbarTester;
import imageDisplayApp.ImageAndDisplaySet;
import utilityClassesForObjects.RectangleEdges;

public class PlusGraphic extends TrapezoidGraphic {

	{ parameter=new RectangleEdgeParameter(this, 0.5, TOP, UPPER_LEFT); getParameter().setRatioToMaxLength(0.33333);
	name="Plus";
	
	}
	
	public PlusGraphic(RectangularGraphic r) {
		super(r);
		// TODO Auto-generated constructor stub
	}
	
	public PlusGraphic(Rectangle r) {
		super(r);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**implements a formula to produce a trapezoid*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		Rectangle2D r = this.getRectangle();
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		
		double ratioToMaxLength = getParameter().getRatioToMaxLength();
		double hPlus=ratioToMaxLength*rx*2;
		double vPlus=ratioToMaxLength*ry*2;
		
		Point2D startPoint = RectangleEdges.getLocation(LEFT, r);
		double x2 = startPoint .getX();
		double y2 = startPoint .getY();
		path.moveTo(x2, y2+vPlus/2);
		path.lineTo(x2, y2-vPlus/2);
		
		Point2D pCenter = RectangleEdges.getLocation(CENTER, r);
		x2=pCenter.getX()-hPlus/2;
		y2=pCenter.getY()-vPlus/2;
		path.lineTo(x2, y2);
		
		startPoint = RectangleEdges.getLocation(TOP, r);
		x2 = startPoint .getX();
		y2 = startPoint .getY();
		hLineAt(path, x2, y2, hPlus);
		
		x2=pCenter.getX()+hPlus/2;
		y2=pCenter.getY()-vPlus/2;
		path.lineTo(x2, y2);
		
		startPoint = RectangleEdges.getLocation(RIGHT, r);
		x2 = startPoint .getX();
		y2 = startPoint .getY();
		vLineAt(path, x2, y2, -vPlus);
		
		
		x2=pCenter.getX()+hPlus/2;
		y2=pCenter.getY()+vPlus/2;
		path.lineTo(x2, y2);
		
		startPoint = RectangleEdges.getLocation(BOTTOM, r);
		x2 = startPoint .getX();
		y2 = startPoint .getY();
		hLineAt(path, x2, y2, -hPlus);
		
		x2=pCenter.getX()-hPlus/2;
		y2=pCenter.getY()+vPlus/2;
		path.lineTo(x2, y2);
		
	
		path.closePath();
		this.setClosedShape(true);
		
		return path;
		
	}


	protected void hLineAt(Path2D.Double path, double x2, double y2, double hPlus) {
		path.lineTo(x2-hPlus/2, y2);
		path.lineTo(x2+hPlus/2, y2);
	}


	protected void vLineAt(Path2D.Double path, double x2, double y2, double vPlus) {
		path.lineTo( x2,y2+vPlus/2);
		path.lineTo( x2,y2-vPlus/2);
	}
	
	public PlusGraphic copy() {
		PlusGraphic output = new PlusGraphic(this);
		output.getParameter().setRatioToMaxLength(getParameter().getRatioToMaxLength());
		return output;
	}
	
	public RectangularGraphic blankShape(Rectangle r, Color c) {
		PlusGraphic r1 = new PlusGraphic(r);
		r1.getParameter().setRatioToMaxLength(this.getParameter().getRatioToMaxLength());
		r1.setDashes(new float[]{100000,1});
		r1.setStrokeWidth(4);
		r1.setStrokeColor(c);
		return r1;
	}

	
	public static void main(String[] args) {
		ImageAndDisplaySet ex = ToolbarTester.showExample(true);
		ComplexBlobShape z = new ComplexBlobShape(new Rectangle(20,20,200,200), 5, 0);
		z.setStrokeColor(Color.BLACK);
		PlusGraphic z2 = new PlusGraphic(z);
		ex.getImageAsWrapper().getGraphicLayerSet().add(z2);
		ex.updateDisplay();
	}
	
	public String getShapeName() {return "Plus Shape";}
}
