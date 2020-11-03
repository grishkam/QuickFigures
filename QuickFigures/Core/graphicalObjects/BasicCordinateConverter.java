package graphicalObjects;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Serializable;

import logging.IssueLog;

public class BasicCordinateConverter implements CordinateConverter<Object>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x=0;
	private double y=0;
	private double magnification=1;
	
	public BasicCordinateConverter() {}
	
	public void transformGraphics(Graphics2D g) {
		g.translate(-x, -y);
		g.scale(magnification, magnification);
	}
	
public void unTransformGraphics(Graphics2D g) {
	
	g.scale(1/magnification, 1/magnification);
	g.translate(x, y);
	}
	
	public BasicCordinateConverter(double x2, double y2, double scale) {
		setX(x2);
		setY(y2);
		setMagnification(scale);
	}
	
	public Point2D transformME(MouseEvent me) {
		return unTransformP(new Point(me.getX(), me.getY()));
	}
	
	

	@Override
	public double transformX(double ox) {
		return (ox-getX())*getMagnification();
	}

	@Override
	public double transformY(double oy) {
		return (oy-getY())*getMagnification();
	}
	
	
	
	public AffineTransform getAfflineTransform() {
		AffineTransform af = AffineTransform.getTranslateInstance(this.transformX(0), this.transformY(0));
		  af.scale(getMagnification(), getMagnification());
		  return af;
		/**
		//AffineTransform af = AffineTransform.getTranslateInstance(this.transformX(0), this.transformY(0));
		AffineTransform af=new AffineTransform();
		//AffineTransform af = AffineTransform.getTranslateInstance(
		//	af.translate(getX(), getY());
		af.translate(-getX()*getMagnification(), -getY()*getMagnification());
		
		af.scale(getMagnification(), getMagnification());
	
		//af.translate(-getX()*getMagnification(), -getY()*getMagnification());
		return af;*/
	}
	
	@Override
	public double unTransformX(double ox) {
		return ox/getMagnification()+getX();
	}

	@Override
	public double unTransformY(double oy) {
		return oy/getMagnification()+getY();
	}
	/**
	public void setToAffine(AffineTransform af) {
		this.setMagnification(af.getScaleX());
		this.setX(af.getTranslateX());
		this.setX(af.getTranslateY());
	}*/

	@Override
	public double getMagnification() {
		return magnification;
	}

	@Override
	public Font getScaledFont(Font font) {
		return scaleFont(font, getMagnification());
	//	return font.deriveFont((float)(font.getSize()*getMagnification()));
	}
	
	public static Font scaleFont(Font font, double mag) {
		return font.deriveFont((float)(font.getSize()*mag));
	}

	@Override
	public BasicStroke getScaledStroke(BasicStroke stroke) {    
	       /** double mag = getMagnification();
	        if (mag!=1.0) {
	            float width = (float)(stroke.getLineWidth()*mag);
	            float[] oldDash = stroke.getDashArray();
	            float[] newDash=new float[stroke.getDashArray().length] ;
	            for(int i=0; i<newDash.length; i++) {newDash[i]=(float) (oldDash[i]*mag);}
	            //return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
	            return new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), newDash, stroke.getDashPhase());
	        } else
	            return stroke;*/
		return scaleStroke(stroke, this.getMagnification());
	    }
	
	public static BasicStroke scaleStroke(BasicStroke stroke, double mag) {
		 if (mag!=1.0&&stroke!=null) {
	            float width = (float)(stroke.getLineWidth()*mag);
	    
	            float[] oldDash = stroke.getDashArray();
	            if (oldDash!=null) {
	            float[] newDash=new float[stroke.getDashArray().length] ;
	            for(int i=0; i<newDash.length; i++) {newDash[i]=(float) (oldDash[i]*mag);}
	            
	          
	            return new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), newDash, stroke.getDashPhase());
	            }
	            else {
	            	 return new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit());
	            }
	        } else
	            return stroke;
	}


	public Point2D.Double[] transformPoints(Point2D[] points) {
		 Point2D.Double[] output=new  Point2D.Double[points.length];
		 for(int i=0; i<points.length; i++) {
			 Point2D pt=points[i];
			 if (pt==null) continue;
			 output[i]=new Point2D.Double(transformX(pt.getX()), transformY(pt.getY()));
		 }
		return null;
	}
	
	
	
	public AffineTransform getAfflineTransformInv() {
		try {
			return getAfflineTransform().createInverse();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		//AffineTransform af = AffineTransform.getScaleInstance(1/getMagnification(), 1/getMagnification());
		//af.translate(-transformX(0), -transformY(0));
		//af.translate(getX()/getMagnification(), getY()/getMagnification()); 
		//return af;
	}

	public void setMagnification(double magnification) {
		this.magnification = magnification;
	}
	
	

	@Override
	public Point2D transformP(Point2D op) {
		return new Point2D.Double(transformX(op.getX()), transformY(op.getY()));
	}
	
	@Override
	public Point2D unTransformP(Point2D op) {
		return new Point2D.Double(unTransformX(op.getX()), unTransformY(op.getY()));
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	void testConsistency(Point p) {
		IssueLog.log("Points should be equal");
		IssueLog.log(transformP(p));
		IssueLog.log(getAfflineTransform().transform(p, new Point()));
	}
	
	void testInversion(Point p) {
		IssueLog.log("Inverted Points should be equal");
		Point2D pt = getAfflineTransform().transform(p, new Point());
		IssueLog.log(pt);
		IssueLog.log(getAfflineTransformInv().transform(pt, new Point()));
	}
	
	static void testConsistenCy1(int x, int y, double d) {
		BasicCordinateConverter bb = new BasicCordinateConverter(x,y,d);
		bb.testConsistency(new Point(50,40));
		bb.testConsistency(new Point(-50,40));
		bb.testConsistency(new Point(50,4));
	}
	
	
	public static void main(String[] args) {
		 testConsistenCy1(100,30,2);
		 testConsistenCy1(100,300,0.5);
		 testConsistenCy1(-100,300,1.5);
		 testConsistenCy1(0,300,0.5);
	}

	@Override
	public CordinateConverter<?> getCopyTranslated(int dx, int dy) {
		// TODO Auto-generated method stub
		return new BasicCordinateConverter(x+dx,y+dy,magnification);
	}
}
