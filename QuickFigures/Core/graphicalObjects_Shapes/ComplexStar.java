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
package graphicalObjects_Shapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import handles.SmartHandleList;

/**A star object with two different point lengths*/
public class ComplexStar extends SimpleStar {

	
	


	{name="Complex Star";}
	{ doesAngleShift=false;}
	private static final int STAR_RATIO_HANDLE2 = 88;
	private AngleParameter starRatio2=new AngleParameter(this); {starRatio2.setType(AngleParameter.RADIUS_TYPE); starRatio2.setRatioToMaxRadius(0.15);}
	
	
	public ComplexStar(Rectangle rectangle, int nV) {
		super(rectangle, nV);
	}

	public ComplexStar(ComplexStar complexStar) {
		super(complexStar);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String getPolygonType() {return "Elegant Star";}
	
	public RegularPolygonGraphic copy() {
		ComplexStar output = new ComplexStar(this);
		giveStarTraitsToo(output);
		output.setRatio2(getRatio2());
		return output;
	}
	
	public RectangularGraphic blankShape(Rectangle r, Color c) {
		RegularPolygonGraphic r1 = new ComplexStar(r, this.getNvertex());
		
		
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	/**Creates a certain number of vertices*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		
		double centx = x+rx;
		double centy = y+ry;
		double angle=getIntervalAngle();
		path.moveTo(centx+rx,centy);
		for(int i=1; i<getNvertex()*4;i++) {
				double factor=1;
				if(i%4==2) factor=getRatioInternalToExternal2();
				if(i%2!=0) factor=getRatioInternalToExternal();
				
				double currentAngle = angle*i;
				double curx=centx+Math.cos(currentAngle)*rx*factor;
				double cury=centy+Math.sin(currentAngle)*ry*factor;
				path.lineTo(curx, cury);
		}
		path.closePath();
		this.setClosedShape(true);
		
		return path;
		
	}
	
	public double getIntervalAngle() {
		return Math.PI/(getNvertex()*2);
	}

	public double getRatioInternalToExternal2() {
		return getRatio2();
	}
	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		list.add(new RegularPolygonAngleHandle(this, starRatio2, Color.blue, 0, STAR_RATIO_HANDLE2, -2)); 
		//list.add(new RatioHandle2(this));
		return list;
	}
	/**returns a point inside of the shape, defined by the ratio to the radius of an'
	 * enclosed oval*/
	public Point2D getInnerPointForHandle2(double factor) {
		double currentAngle =-Math.PI/(getNvertex());
		return getPointInside(factor, currentAngle);
	}
	
	/**returns a point inside of the shape, defined by the ratio to the radius of an'
	 * enclosed oval*/
	public Point2D getInnerPoint(double factor) {
		double currentAngle =-Math.PI/(getNvertex()*2);
		return getPointInside(factor, currentAngle);
	}

	
	

	public double getRatio2() {
		return  starRatio2.getRatioToMaxRadius();
	}

	public void setRatio2(double ieRatio2) {
		 starRatio2.setRatioToMaxRadius(ieRatio2);
	}

}
