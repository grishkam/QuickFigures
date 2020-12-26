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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D.Double;

import handles.AngleHandle;
import handles.SmartHandleList;

public class GearShape extends SimpleStar {

	{name="Gear";}
	
	public String getPolygonType() {return "Gear";}
	
	int complexity=2;
	
	AngleParameter hole=new AngleParameter(this); {hole.setRatioToMaxRadius(0.15);}
	
	public GearShape(Rectangle rectangle, int nV, double ratio) {
		super(rectangle, nV);
		setStarRatio(ratio);
	}

	public GearShape(GearShape gearShape) {
		super(gearShape);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RegularPolygonGraphic copy() {
		GearShape output = new GearShape(this);
		output.hole.setRatioToMaxRadius(hole.getRatioToMaxRadius());
		giveStarTraitsToo(output);
		return output;
	}

	/**Creates a certain number of vertices*/
	@Override
	public Shape getShape() {
		Path2D.Double path = getShapeWithoutHoles();
		
		if(hole.getRatioToMaxRadius()>0.1&&hole.getRatioToMaxRadius()<getStatRatio()) try {
			Area a = new Area(path);
			a.subtract(new Area(getInnerHoleShape()));
			return a;
		} catch (Throwable t) {}
		
		return path;
		
	}
	
	protected Shape getShapeForStrokeHandlePoints() {
		return getShapeWithoutHoles();
	}

	private Path2D.Double getShapeWithoutHoles() {
		Path2D.Double path=new Path2D.Double();
		
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		
		double centx = x+rx;
		double centy = y+ry;
		double angle=getIntervalAngle();
		path.moveTo(centx+rx,centy);
		for(int i=1; i<getNvertex()*complexity*2;i++) {
				double factor=1;
				double currentAngle = angle*i;
				if(isStepANotch(i)) {
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
	
	public Shape getInnerHoleShape() {
		
		Double r = new Ellipse2D.Double(x, y, getObjectWidth()*hole.getRatioToMaxRadius(), getObjectHeight()*hole.getRatioToMaxRadius());
		Point2D c = this.getCenterOfRotation();
		r.y=(int) (c.getY()-r.height/2);
		r.x=(int) (c.getX()-r.width/2);
		return r;
		
	}

	public boolean isStepANotch(int i) {
		int j = i%(2*complexity);
		if(j>1 &&j<=1+complexity)
			return true;
		return false;
	}
	
	public double getIntervalAngle() {
		return super.getIntervalAngle()/(complexity);
	}
	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		
			AngleHandle holeHandle = new AngleHandle(this, hole, Color.cyan, 0, 223190);
			holeHandle.setType(AngleHandle.RADIUS_TYPE);
			list.add(holeHandle);
			
		return list;
	}
	
	
}
