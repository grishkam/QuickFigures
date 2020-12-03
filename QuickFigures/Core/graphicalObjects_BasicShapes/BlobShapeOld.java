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
package graphicalObjects_BasicShapes;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import graphicalObjectHandles.SmartHandleForPathGraphic;
import graphicalObjectHandles.SmartHandleList;
import utilityClassesForObjects.PathPoint;
import utilityClassesForObjects.PathPointList;

public class BlobShapeOld extends SimpleStar {
	{ setStarRatio(0.75);}

	PathPoint refPoint = new PathPoint(0,0);
	private PathGraphic handleProxyPath;
	private SmartHandleForPathGraphic proxyHandle1;
	private SmartHandleForPathGraphic proxyHandle2; {
		refPoint.moveCurveControl(0, -20);
		refPoint.moveCurveControl2(0, 20);
	}
	

	public PathPoint createPathPointList() {
		//PathPointList p2 = new PathPointList();
		
		PathPoint point1 = refPoint.copy();
		point1.applyAffine(this.getTransformForBaseLocation());
		//p2.add(point1);
	
		return point1;
	}

	public BlobShapeOld(RectangularGraphic r) {
		super(r);
		
	}

	public BlobShapeOld(Rectangle r, int i, double d) {
		super(r,i);
	}
	
	public RegularPolygonGraphic copy() {
		BlobShapeOld output = new BlobShapeOld(this);
		output.setNvertex(getNvertex());
		this.giveStarTraitsToo(output);
		output.refPoint=refPoint.copy();
		return output;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**implements a formular to produce a regular polygon with a certain number of vertices*/
	@Override
	public Shape getShape() {
		PathPointList path=new PathPointList();
		
		
		
		for(int i=0; i<getNvertex()*2;i++) {
				PathPoint p2 = createPathPointList(); 
				if(i%2!=0)p2.applyAffine(this.getTransformForRatios());
				p2.applyAffine(getTransformForPosition(i));
				
				p2.applyAffine(getTransformForAspectRatioScale());
				path.add(p2);
		}
		
		this.setClosedShape(true);
		
		
		return path.createPath(true);
		
	}
	
	public AffineTransform getTransformForBaseLocation() {
		Rectangle2D r = this.getRectangle();
		return AffineTransform.getTranslateInstance(r.getMaxX(),r.getCenterY());
	}
	
	public AffineTransform getTransformForPosition( int i) {
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		double centx = x+rx;
		double centy = y+ry;
		double angle=getIntervalAngle();
		AffineTransform af = new AffineTransform();
		af.rotate(angle*i, centx, centy);
		
		return af;
	}
	
	AffineTransform getTransformForAspectRatioScale() {
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		double centx = x+rx;
		double centy = y+ry;
		AffineTransform af = new AffineTransform();
		af.translate(centx, centy);
		af.scale(1, ry/rx);
		af.translate(-centx, -centy);
		return af;
	}
	
	public AffineTransform getTransformForRatios() {
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		double centx = x+rx;
		double centy = y+ry;
		double angle=getIntervalAngle()*getStarAngleRatio()/2;
		AffineTransform af = new AffineTransform();
		af.rotate(angle, centx, centy);
		af.translate(centx, centy);
		af.scale(getStatRatio(), getStatRatio());
		af.translate(-centx, -centy);
		return af;
	}
	
	
	public double getIntervalAngle() {
		return Math.PI/(getNvertex());
	}
	
	public String getPolygonType() {return "Blob";}

	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		
		return list;
	}
	
	@Override
	public SmartHandleList getSmartHandleList() {
		SmartHandleList list1 = super.getSmartHandleList();
		SmartHandleList list=new SmartHandleList();
		if (handleProxyPath==null){
			handleProxyPath=new PathGraphic();handleProxyPath.getPoints().add(refPoint);
			
			proxyHandle1 = new SmartHandleForPathGraphic(handleProxyPath, refPoint, 1, 57);
			
			proxyHandle2 = new SmartHandleForPathGraphic(handleProxyPath, refPoint, 2, 57);
			
			proxyHandle2.select();
			handleProxyPath.select();
			handleProxyPath.moveLocation(getBounds().getCenterX()+15, getBounds().getCenterY());
	}
		handleProxyPath.setLocation(getBounds().getMaxX()+5, getBounds().getCenterY());
		handleProxyPath.updatePathFromPoints();
		list.add(proxyHandle1);
		list.add(proxyHandle2);
		
		return SmartHandleList.combindLists(list1, list);
	}
}
