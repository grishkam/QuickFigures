/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
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
/**An implementation of the CoordinateConverter interface. @see CordinateConverter
  
  This class stores a magnification and a displacement that determines
  how the graphics (@see ZoomableGraphic) are drawn onto a Graphics2d. 
  Assuming that objects are viewed at a zoom and from a shifted view position
  this object carries the information needed to draw objects accordingly 
  In most cases, they will be drawn on a particular component (@see GraphicDisplayCanvas) , 
  in a window (@see GraphicSetDisplayWindow) .
  Certain items, like handles are drawn at the same size regardless of magnification.
  However, their locations are still shifted
  */
public class BasicCoordinateConverter implements CordinateConverter, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**The x displacement. if x is positive, the objects are drawn to the left*/
	private double x=0;
	/**The y displacement. if y is positive, the viewer */
	private double y=0;
	/**The magnification of the */
	private double magnification=1;
	
	public BasicCoordinateConverter() {}
	
	public BasicCoordinateConverter(double x2, double y2, double scale) {
		setX(x2);
		setY(y2);
		setMagnification(scale);
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

	public void setMagnification(double magnification) {
		this.magnification = magnification;
	}
	@Override
	public double getMagnification() {
		return magnification;
	}
	
	/**transforms the Graphics2D coordinates*/
	public void transformGraphics(Graphics2D g) {
		g.translate(-x, -y);
		g.scale(magnification, magnification);
	}
	
	/**reverses the transform graphics2D coordinates*/
	public void unTransformGraphics(Graphics2D g) {
	
		g.scale(1/magnification, 1/magnification);
		g.translate(x, y);
	}
	
	
	/**given a mouse event on the canvas with object locations
	  drawn based on this coordinate converter, returns the click location
	 in the non-zoomed, non-shifted coordinates*/
	public Point2D unTransformClickPoint(MouseEvent me) {
		return unTransformP(new Point(me.getX(), me.getY()));
	}
	
	
	/**converts an x location*/
	@Override
	public double transformX(double ox) {
		return (ox-getX())*getMagnification();
	}

	/**converts a y location*/
	@Override
	public double transformY(double oy) {
		return (oy-getY())*getMagnification();
	}
	
	/**Reverse the transformX method*/
	@Override
	public double unTransformX(double ox) {
		return ox/getMagnification()+getX();
	}

	/**Reverse the transformY method*/
	@Override
	public double unTransformY(double oy) {
		return oy/getMagnification()+getY();
	}
	
	/**transforms a point*/
	@Override
	public Point2D transformP(Point2D op) {
		return new Point2D.Double(transformX(op.getX()), transformY(op.getY()));
	}
	/**reverses the transformP*/
	@Override
	public Point2D unTransformP(Point2D op) {
		return new Point2D.Double(unTransformX(op.getX()), unTransformY(op.getY()));
	}
	
	
	/**returns an affine transform that can be used to transform a shape into a form that
	 * can be drawn onto the canvas*/
	public AffineTransform getAffineTransform() {
		AffineTransform af = AffineTransform.getTranslateInstance(this.transformX(0), this.transformY(0));
		  af.scale(getMagnification(), getMagnification());
		  return af;
	}
	

	

	/**returns a font scaled by the current magnification*/
	@Override
	public Font getScaledFont(Font font) {
		return scaleFont(font, getMagnification());
	}
	
	/**returns a font scaled by a given amount.*/
	public static Font scaleFont(Font font, double mag) {
		return font.deriveFont((float)(font.getSize()*mag));
	}

	/**returns a BasicStroke scaled by the current magnification*/
	@Override
	public BasicStroke getScaledStroke(BasicStroke stroke) {    
		return scaleStroke(stroke, this.getMagnification());
	    }
	
	/**scales instances of basic stroke to account for the magnification level mag*/
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

	/**
	private Point2D.Double[] transformPoints(Point2D[] points) {
		 Point2D.Double[] output=new  Point2D.Double[points.length];
		 for(int i=0; i<points.length; i++) {
			 Point2D pt=points[i];
			 if (pt==null) continue;
			 output[i]=new Point2D.Double(transformX(pt.getX()), transformY(pt.getY()));
		 }
		return null;
	}*/
	
	
	/**returns an inverse of the transform for this converter.*/
	public AffineTransform getAfflineTransformInv() {
		try {
			return getAffineTransform().createInverse();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	
	




	
	void testConsistency(Point p) {
		IssueLog.log("Points should be equal");
		IssueLog.log(transformP(p));
		IssueLog.log(getAffineTransform().transform(p, new Point()));
	}
	
	void testInversion(Point p) {
		IssueLog.log("Inverted Points should be equal");
		Point2D pt = getAffineTransform().transform(p, new Point());
		IssueLog.log(pt);
		IssueLog.log(getAfflineTransformInv().transform(pt, new Point()));
	}
	
	static void testConsistenCy1(int x, int y, double d) {
		BasicCoordinateConverter bb = new BasicCoordinateConverter(x,y,d);
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
	public CordinateConverter getCopyTranslated(int dx, int dy) {
		return new BasicCoordinateConverter(x+dx,y+dy,magnification);
	}
}
