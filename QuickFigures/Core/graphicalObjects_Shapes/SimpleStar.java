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
 * Date Modified: Jan 5, 2021
 * Version: 2022.0
 */
package graphicalObjects_Shapes;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import handles.AngleHandle;
import handles.SmartHandleList;

/**A star shaped polygon*/
public class SimpleStar extends RegularPolygonGraphic {

	
	{name="Star";}
	private AngleParameter starRatio=new AngleParameter(this); {starRatio.setType(AngleParameter.ANGLE_RATIO_AND_RAD_TYPE); starRatio.setRatioToMaxRadius(0.5);}
	private AngleParameter angleRatio=new AngleParameter(this); {angleRatio.setType(AngleParameter.ANGLE_RATIO_TYPE); angleRatio.setRatioToMaxRadius(0.5);}
	
	
	protected boolean doesAngleShift=true;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public SimpleStar(Rectangle rectangle, int nV) {
		super(rectangle, nV);
	}

	
	public SimpleStar(RectangularGraphic r) {
		super(r);
	}
	
	public RegularPolygonGraphic copy() {
		SimpleStar output = new SimpleStar(this);
		giveStarTraitsToo(output);
		return output;
	}

	public void giveStarTraitsToo(SimpleStar output) {
		output.setNvertex(this.getNvertex());
		output.setStarRatio(this.getStarRadiusRatio());
		output.setStartAngleRatio(this.getStarAngleRatio());
		output.doesAngleShift=this.doesAngleShift;
	}
	
	public RectangularGraphic blankShape(Rectangle r, Color c) {
		RegularPolygonGraphic r1 = new SimpleStar(r, this.getNvertex());
		
		
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
		for(int i=1; i<getNvertex()*2;i++) {
				double factor=1;
				double currentAngle = angle*i;
				if(i%2!=0) {
					factor=getRatioInternalToExternal();
					currentAngle+=getStarAngleRatio()*getIntervalAngle();
				}
				
				double curx=centx+Math.cos(currentAngle)*rx*factor;
				double cury=centy+Math.sin(currentAngle)*ry*factor;
				path.lineTo(curx, cury);
		}
		path.closePath();
		this.setClosedShape(true);
		
		return path;
		
	}

	public String getPolygonType() {return "Star";}
	
	/**distance between points of the star*/
	public double getIntervalAngle() {
		return Math.PI/getNvertex();
	}

	/**returns the ratio  */
	public double getRatioInternalToExternal() {
		return getStarRadiusRatio();
	}
	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		addStarHandlesToList(list); 
		return list;
	}

	/**Adds handles that allow the user to edit the style of the star*/
	protected void addStarHandlesToList(SmartHandleList list) {
		list.add(new RegularPolygonAngleHandle(this, starRatio, Color.green, 0, 893, -1)); 
		if (doesAngleShift) {
			RegularPolygonAngleHandle e = new RegularPolygonAngleHandle(this, angleRatio, Color.yellow, 0, 8935, -3);
			e.handlesize=2;
			list.add(e);
		}
	}
	/**Called */
	protected void updateStarHandles() {
		angleRatio.setRatioToMaxRadius(starRatio.getRatioToMaxRadius());//keeps the position consistent
		starRatio.setRatioToStandardAngle(angleRatio.getRatioToStandardAngle()); starRatio.setAngle(angleRatio.getAngle());
	}
	
	
	/**returns a point inside of the shape, defined by the ratio to the radius of an'
	 * enclosed oval*/
	public Point2D getInnerPoint(double factor) {
		double currentAngle =-Math.PI/getNvertex()+this.getIntervalAngle()*getStarAngleRatio();
		return getPointInside(factor, currentAngle);
	}

	
	public int minimumNVertex() {
		return 2;
	}

	/***/
	public double getStarRadiusRatio() {
		return starRatio.getRatioToMaxRadius();
	}
	/***/
	public void setStarRatio(double ieRatio) {
		starRatio.setRatioToMaxRadius(ieRatio);
		 angleRatio.setRatioToMaxRadius(ieRatio);
	}
	/***/
	public double getStarAngleRatio() {
		return angleRatio.getRatioToStandardAngle();
	}
	/***/
	public void setStartAngleRatio(double angleShiftRatio) {
		angleRatio.setRatioToStandardAngle(angleShiftRatio);
	}
	
	
	@Override
	public SmartHandleList getSmartHandleList() {
		updateStarHandles();
		return super.getSmartHandleList();
	}


	
	/**S special angle handle for polygons and starts*/
	static class RegularPolygonAngleHandle extends  AngleHandle {
		public RegularPolygonGraphic polygon;
		
		private int pointNumber=1;
		
		public RegularPolygonAngleHandle(RegularPolygonGraphic r, AngleParameter angle, Color c, double startAngle,
				int handleNumber, int point) {
			super(r, angle, c, startAngle, handleNumber);
			polygon=r;
			 pointNumber=point;
			 this.setType(angle.getType());
			
		}
		

		public double getHandleDrawAngle() {
			return getPointNumber()*polygon.getIntervalAngle();
		}
		
		public double getStandardAngle() {
			return polygon.getIntervalAngle();
		}

		public int getPointNumber() {
			return pointNumber;
		}



		public void setPointNumber(int pointNumber) {
			this.pointNumber = pointNumber;
		}



		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		}
}
