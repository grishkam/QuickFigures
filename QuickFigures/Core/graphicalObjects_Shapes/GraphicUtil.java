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
package graphicalObjects_Shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import graphicalObjects.CordinateConverter;
import handles.HandleRect;


public class GraphicUtil {
	static int defaulthandleSize=3;
	 int handlesize=defaulthandleSize;
	 Color handleColor=Color.white;
	 public ArrayList<HandleRect> lastHandles;
	 
	 public void setHandleFillColor(Color c) {
		 if (c==null)handleColor=Color.WHITE;
		 else handleColor=c;
	 }
	
	 public void setHandleSize(int size) {
		 	if (size<1) handlesize=defaulthandleSize;
		 	else handlesize=size;
	 }
	 
	 public BasicStroke getStroke() {
			return new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[] {2}, 2);
		}
	
	public void drawPolygon(Graphics2D g, CordinateConverter<?> cords, Double[] rotatedBoundsPrecise, boolean handles) {
		Polygon p2 = getAlteredPolygon(g,  cords, rotatedBoundsPrecise);
		g.drawPolygon(p2);
		if (handles) {
			lastHandles=drawHandles(g, p2);
		}
	}
	

	
	public static Shape shapeFromArray(Point2D.Double[] in, boolean looptostart) {
		
		java.awt.geom.Path2D.Double output = new Path2D.Double();
		if (in.length==0) return output;
		int i=0;
		output.moveTo(in[i].x, in[i].y);
		i++;
		while(i<in.length) {
			output.lineTo(in[i].x, in[i].y);
			i++;
		}
		if (looptostart) {
			i=0;
			output.lineTo(in[i].x, in[i].y);
		}
		return output;
	}
	
public  void drawRectangle(Graphics2D g, CordinateConverter<?> cords, Rectangle2D r2, boolean handles) {
		Rectangle r=cords.getAffineTransform().createTransformedShape(r2).getBounds();//getAlteredRectangle(g,  cords, r2);	
	
		g.drawRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		if (handles) drawHandles(g, r);
	}

public  void fillRectangle(Graphics2D g, CordinateConverter<?> cords, Rectangle2D r2) {
	Rectangle r=cords.getAffineTransform().createTransformedShape(r2).getBounds();//getAlteredRectangle(g,  cords, r2);	

	g.fillRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
}




public  void drawLine(Graphics2D g, CordinateConverter<?> cords, Point2D point, Point2D baseLineEnd, boolean handles) {
	int x1=(int)cords.transformX( point.getX());
	int y1=(int)cords.transformY( point.getY());
	int x2=(int)cords.transformX( baseLineEnd.getX());
	int y2=(int)cords.transformY( baseLineEnd.getY());
	g.drawLine(x1, y1, x2, y2);
	if (handles){
	ArrayList<HandleRect> outhandles = new ArrayList<HandleRect>();
	HandleRect h1 = new HandleRect( x1, y1);
	HandleRect h2 = new HandleRect(x2, y2);
	this.drawHandleRect(g, h1);
	this.drawHandleRect(g, h2);
	outhandles.add(h1);
	outhandles.add(h2);
	lastHandles=outhandles;
	}
	//g.drawRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
	//if (handles) drawHandles(g, r);
}

public  void drawStrokedShape(Graphics2D g, CordinateConverter<?> cords, Shape s) {
	g.draw(cords.getAffineTransform().createTransformedShape(s));
}


/**draws a dot right above and to the left of the point*/
public  void drawDot(Graphics2D g, CordinateConverter<?> cords, Point2D.Double point) {
	
	int x1=(int)cords.transformX( point.getX());
	int y1=(int)cords.transformY( point.getY());
	
	Rectangle r1 = new Rectangle(x1-1, y1-1,1,1);
	g.draw(r1);

}

	
	public  Polygon getAlteredPolygon(Graphics2D g, CordinateConverter<?> cords, Double[] rotatedBoundsPrecise) {
		Polygon p2=new Polygon();
		for(int i=0; i<rotatedBoundsPrecise.length; i++) {
			int sx = (int)cords.transformX(rotatedBoundsPrecise[i].x);
		    int sy = (int)cords.transformY(rotatedBoundsPrecise[i].y);
		    p2.addPoint(sx, sy);
		}
		return p2;
	}
/**
	public  Rectangle getAlteredRectangle(Graphics2D g, CordinateConverter<?> cords, Rectangle2D r2) {
		int sx = (int)cords.transformX((int)r2.getX());
	    int sy = (int)cords.transformY((int)r2.getY());
	    double swidth=cords.getMagnification()*r2.getWidth();
	    double sheight=cords.getMagnification()*r2.getHeight();
	    return new Rectangle(sx,sy, (int)swidth, (int)sheight);
	}*/
	
	public void drawString(Graphics2D g, CordinateConverter<?> cords, String text, Point2D p,  Font f, Color c, double angle) {
		
		
		Font font = cords.getScaledFont(f);
		g.setFont(font);
		g.setColor(c);
		 double sx = cords.transformX(p.getX());
		 double sy = cords.transformY(p.getY());
		 
		 g.rotate(-angle, sx, sy);
		 
	 	 g.drawString(text, (int)sx, (int)sy); 
	 	 g.rotate(angle, sx, sy);
	}
	

	
	
	

	
	private void drawHandleRect(Graphics2D g, Rectangle2D r) {
		g.setStroke(new BasicStroke());
		g.setColor(handleColor);
		g.fillRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		g.setColor(Color.black);
		g.drawRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
	}
	
	private  ArrayList<HandleRect> drawHandles(Graphics2D g, Polygon p) {
		ArrayList<HandleRect> output = new ArrayList<HandleRect>();
		for(int i=0; i<p.npoints; i++) {
			HandleRect r=new HandleRect(p.xpoints[i], p.ypoints[i]);
			output.add(r);
			drawHandleRect(g,r);
		}
		
		return output;
	}
	
	/**draws a handle at the given point and returns its rectangle*/
	private HandleRect drawHandle(Graphics2D g, Point2D p) {
		HandleRect r=new HandleRect((int)p.getX(), (int)p.getY(), handlesize);
		drawHandleRect(g,r);
		return r;
	}
	
	public ArrayList<HandleRect> drawHandlesAtPoints(Graphics2D g, CordinateConverter<?> cords, ArrayList<Point2D> points) {
		ArrayList<HandleRect> output = new ArrayList<HandleRect>();
		for(Point2D p : points) {
			Double p2 = new Point2D.Double(cords.transformX(p.getX()), cords.transformY(p.getY()));
			output.add(drawHandle(g,  p2)) ;
		}
		lastHandles=output;
		return output;
		
	}
	
	public HandleRect drawHandlesAtPoint(Graphics2D g, CordinateConverter<?> cords, Point2D p) {
		
			Double p2 = new Point2D.Double(cords.transformX(p.getX()), cords.transformY(p.getY()));
		return	drawHandle(g,  p2) ;
	}
	
	public HandleRect drawSizeHandlesAtPoint(Graphics2D g, CordinateConverter<?> cords, Point2D p, Point2D pfinish) {
		
		Double p2 = new Point2D.Double(cords.transformX(p.getX()), cords.transformY(p.getY()));
		Double p2finish = new Point2D.Double(cords.transformX(pfinish.getX()), cords.transformY(pfinish.getY()));
		g.setColor(Color.black);
		g.drawLine((int)p2.getX(), (int)p2.getY(), (int)p2finish.getX(), (int)p2finish.getY());
		return	drawHandle(g,  p2) ;
	
}
	
	/**
private  ArrayList<Rectangle> drawHandles(Graphics2D g, Iterable<Point2D.Double> list) {
		ArrayList<Rectangle> output = new ArrayList<Rectangle>();
		for(Point2D.Double p : list) {
			output.add(drawHandle(g,  p)) ;
		}
		lastHandles=output;
		return output;
	}

	private  ArrayList<Rectangle> drawHandles(Graphics2D g, Point2D.Double... list) {
		ArrayList<Rectangle> output = new ArrayList<Rectangle>();
		for(Point2D.Double p : list) {
			output.add(drawHandle(g,  p)) ;
		}
		lastHandles=output;
		return output;
	}*/

	
	private  void drawHandles(Graphics2D g, Rectangle r) {
		ArrayList<HandleRect> output = new ArrayList<HandleRect>();
		
		output.add(new HandleRect( (int)r.getX(), (int)r.getY()));
		output.add(new HandleRect( (int)(r.getX()+r.getWidth()), (int)r.getY()));
		output.add(new HandleRect( (int)(r.getX()+r.getWidth()), (int)(r.getY()+r.getHeight())));
		output.add(new HandleRect( (int)r.getX(), (int)(r.getY()+r.getHeight())));
			for(Rectangle2D r2: output) {drawHandleRect(g,r2);}
		lastHandles=output;
	}
	
	 public Polygon PolygonFromRect(Rectangle2D rect, AffineTransform af) {
		int npoints=4;
		Point[] pts=new Point[npoints];
		Point[] pts2=new Point[npoints];
		  int[] xpoints=new int[npoints];
		  int[] ypoints=new int[npoints];
		  
		  xpoints[0]=(int)rect.getX();
		  ypoints[0]=(int)rect.getY();
		
		  xpoints[1]=(int)rect.getX()+(int)rect.getWidth();
		  ypoints[1]=(int)rect.getY();
		  xpoints[2]=(int)rect.getX()+(int)rect.getWidth();
		  ypoints[2]=(int)rect.getY()+(int)rect.getHeight();
		  xpoints[3]=(int)rect.getX();
		  ypoints[3]=(int)rect.getY()+(int)rect.getHeight();
		  for(int i=0;i<npoints; i++ ) {
			  pts[i]=new Point(xpoints[i], ypoints[i]);
			  pts2[i]=new Point(xpoints[i], ypoints[i]);
		  }
		  
		  if (af!=null)  af.transform(pts, 0, pts2, 0, 4);
		  for(int i=0;i<npoints; i++ ) {
			  xpoints[i]=pts2[i].x;
			  ypoints[i]=pts2[i].y;
		  }
		  
		 return new Polygon(xpoints, ypoints, 4);
		  
	}
}
