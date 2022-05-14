/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 4, 2021
 * Version: 2022.1
 */
package locatedObject;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import objectDialogs.DefaultPaintProviderDialog;

/**A class that provides a color or a gradient paint to fill a specific shape*/
public class DefaultPaintProvider implements PaintProvider{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color fillColor=Color.white;
	private Color fillColor2=Color.white;
	protected Point2D point1=new Point(0,0);
	protected Point2D point2=new Point(100,100);
	private int fe1=RectangleEdges.UPPER_LEFT;
	private int fe2=RectangleEdges.LOWER_RIGHT;
	transient Shape s;
	private int nCycles=1;
	
	
	public static final int SOLID_COLOR=0, SHAPE_GRADIENT_PAINT=1, RADIAL_GRADIENT_PAINT=2;
	public static String[] types=new String[] {"Fill Solid", "Fill Gradient", "Radial"};
	private int type=SOLID_COLOR;
	

	
	public DefaultPaintProvider(Color fillColor) {
		super();
		this.fillColor = fillColor;
	}

	@Override
	public Paint getPaint() {
		if (type==SHAPE_GRADIENT_PAINT) {
			return new GradientPaint(point1, fillColor,point2, fillColor2, this.getnCycles()>1);
		}
		
		if (type==RADIAL_GRADIENT_PAINT) {
		
			return new RadialGradientPaint(point1, (float) point1.distance(point2), new float[] {(float) 0,(float) 0.6}, new Color[] {fillColor, fillColor2});
		}
		// TODO Auto-generated method stub
		return fillColor;
	}
	
	@Override
	public void showOptionsDialog() {
		new DefaultPaintProviderDialog(this).showDialog();
		// TODO Auto-generated method stub
		
	}

	public Color getFillColor2() {
		return fillColor2;
	}

	public void setFillColor2(Color fillColor2) {
		this.fillColor2 = fillColor2;
	}

	public Point2D getPoint1() {
		return point1;
	}

	public void setPoint1(Point2D point1) {
		this.point1 = point1;
	}

	public Point2D getPoint2() {
		return point2;
	}

	public void setPoint2(Point2D point2) {
		this.point2 = point2;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public void fillShape(Graphics2D g, Shape s) {
		Paint oldp = g.getPaint();
		setPaintedShape(s);
		g.setPaint(getPaint());
		if (s==null) return;
		g.fill(s);
		g.setPaint(oldp);
		
	}
	
	@Override
	public void strokeShape(Graphics2D g, Shape s) {
		Paint oldp = g.getPaint();
		setStrokeShape(s);
		g.setPaint(getPaint());
		if (s==null) return;
		g.draw(s);
		g.setPaint(oldp);
		
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return this.fillColor;
	}

	@Override
	public void setColor(Color c) {
		fillColor=c;
		
	}

	@Override
	public void setPaintedShape(Shape s) {
		if (s==null) return;
		Rectangle2D b = s.getBounds2D();
		point1=RectangleEdges.getLocation(getFe1(), b.getBounds());
		point2=RectangleEdges.getLocation(getFe2(), b.getBounds());
		if (getnCycles()>1) {
		double xd = point2.getX()-point1.getX();
		double yd = point2.getY()-point1.getY();
		xd/=getnCycles();
		yd/=getnCycles();
		point2=new Point2D.Double(point1.getX()+xd, point1.getY()+yd);
		
		}
		
		this.s=s;
	}
	
	@Override
	public void setStrokeShape(Shape s) {
		if (s==null) return;
		Rectangle2D b = s.getBounds2D();
		point1=RectangleEdges.getLocation(getFe1(), b.getBounds());
		point2=RectangleEdges.getLocation(getFe2(), b.getBounds());
		AffineTransform.getTranslateInstance(-3, 0).transform(point1, point1);
		AffineTransform.getTranslateInstance(3, 0).transform(point2, point2);
		if (getnCycles()>1) {
		double xd = point2.getX()-point1.getX();
		double yd = point2.getY()-point1.getY();
		xd/=getnCycles();
		yd/=getnCycles();
		point2=new Point2D.Double(point1.getX()+xd, point1.getY()+yd);
		
		}
		
		this.s=s;
	}

	public int getFe1() {
		return fe1;
	}

	public void setFe1(int fe1) {
		this.fe1 = fe1;
	}

	public int getFe2() {
		return fe2;
	}

	public void setFe2(int fe2) {
		this.fe2 = fe2;
	}

	@Override
	public Color getColor(int i) {
		if (i==0) return this.getColor();
		return this.getFillColor2();
	}

	@Override
	public void setColor(int i, Color c) {
		if (i==0) {
			this.setColor(c);
			return;
		}
		this.setFillColor2(c);
		
	}
	
	
/**returns the number of times the gradient cycles between the two colors*/
	public int getnCycles() {
		return nCycles;
	}

	public void setnCycles(int nCycles) {
		this.nCycles = nCycles;
	}

	
}
