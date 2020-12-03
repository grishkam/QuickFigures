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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import graphicalObjectHandles.SmartHandleList;
import utilityClassesForObjects.PathPoint;
import utilityClassesForObjects.PathPointList;

public class BlobShape extends SimpleStar {
	{ setStarRatio(0.75);}
	{name="Blob";}
	
	BlobCurveParameterGroup parameters=createParameters();



	public BlobCurveParameterGroup createParameters() {
		AngleParameter ccParameter1=createCCAngleParameter(this); {ccParameter1.setRatioToStandardAngle(-0.25);}
		AngleParameter ccParameter2=createCCAngleParameter(this);{ccParameter2.setRatioToStandardAngle(0.25);}
		AngleParameter anchorParameter2=createCCAngleParameter(this);
		ccParameter2.setRatioToMaxRadius(0.4);
		BlobCurveParameterGroup blobCurveParameterGroup = new BlobCurveParameterGroup(anchorParameter2, ccParameter1, ccParameter2);
		blobCurveParameterGroup.anchorHidden=true;
		return blobCurveParameterGroup;
	}
	

	
	
	private SmartHandleList blobList; 
	


	public BlobShape(RectangularGraphic r) {
		super(r);
		
	}

	protected AngleParameter createCCAngleParameter(BlobShape blobShape) {
		AngleParameter n = new AngleParameter(this);
		n.setType(AngleParameter.ANGLE_RATIO_AND_RAD_TYPE);
		n.setRatioToMaxRadius(.9);
		
		return n;
	}

	public BlobShape(Rectangle r, int i, double d) {
		super(r,i);
	}
	
	public RegularPolygonGraphic copy() {
		BlobShape output = new BlobShape(this);
		output.setNvertex(this.getNvertex());
		this.giveStarTraitsToo(output);
		output.parameters=parameters.copy();
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
			
			PathPoint p2 = createPathPointWithStarTransform(i);

			path.add(p2);
		}
		
		this.setClosedShape(true);
		
		
		return path.createPath(true);
		
	}

	public PathPoint createPathPointWithStarTransform(int i) {
		double aa=0;
		if(i%2!=0) aa=this.getStarAngleRatio();
		
		PathPoint p2 = getPathPointAtAngularLocation(i, aa);
		
		if(i%2!=0)p2.applyAffine(this.getTransformForRatios());
		return p2;
	}

	public PathPoint getPathPointAtAngularLocation(int i, double aa) {
		Point2D anchor = getPointForPosition(i, aa,1);
		Point2D cc1 = getPointForPosition(i, parameters.curve1.getRatioToStandardAngle()+aa,parameters.curve1.getRatioToMaxRadius());
		Point2D cc2 = getPointForPosition(i,parameters.curve2.getRatioToStandardAngle()+aa,parameters.curve2.getRatioToMaxRadius());
			
		PathPoint p2 =new PathPoint(anchor);
		p2.setCurveControl1(cc1);
		p2.setCurveControl2(cc2);
		return p2;
	}
	
	public AffineTransform getTransformForBaseLocation() {
		Rectangle2D r = this.getRectangle();
		return AffineTransform.getTranslateInstance(r.getMaxX(),r.getCenterY());
	}
	
	public Point2D getPointForPosition( int i, double angleRatio, double radRatio) {
		double rx=getObjectWidth()/2;
		double ry=getObjectHeight()/2;
		double centx = x+rx;
		double centy = y+ry;
		double angle=getIntervalAngle()*(i+angleRatio);
		
		double curx=centx+Math.cos(angle)*rx*radRatio;
		double cury=centy+Math.sin(angle)*ry*radRatio;
		return new Point2D.Double(curx, cury);
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
	//	double angle=getIntervalAngle()*getStarAngleRatio()/2;
		AffineTransform af = new AffineTransform();
		
		af.translate(centx, centy);
		//af.rotate(angle, 0, 0);
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
		
	/**	if (handleProxyPath==null){
			handleProxyPath=new PathGraphic();
			
			handleProxyPath.select();
		
	}
		handleProxyPath.setLocation(getBounds().getMaxX()+5, getBounds().getCenterY());
		handleProxyPath.updatePathFromPoints();
	*/
		if (blobList==null) {
			blobList=parameters.createHandlesList(this);
		}
		
		return SmartHandleList.combindLists(list1, blobList);
	}
	
	
	static class BlobCurveParameterGroup extends SmartHandleList{
		
		public AngleParameter anchor;
		public AngleParameter curve1;
		public AngleParameter curve2;
		int handleIDBase = 99932;
		int point = -2;
		boolean anchorHidden=false;
		
		

		public BlobCurveParameterGroup(AngleParameter anchor, AngleParameter curve1, AngleParameter curve2) {
			this.anchor=anchor;
			this.curve1=curve1;
			this.curve2=curve2;
			anchor.setAttachedParameters(curve1, curve2);
		}
		
		
		public BlobCurveParameterGroup copy() {
			BlobCurveParameterGroup out = new BlobCurveParameterGroup(anchor.copy(), curve1.copy(), curve2.copy());
			out.point=point;
			out.handleIDBase=handleIDBase;
			out.anchorHidden=anchorHidden;
		
			return out;
		}
		
		public SmartHandleList createHandlesList(RegularPolygonGraphic poly) {
			SmartHandleList list = new SmartHandleList();
			
			
			RegularPolygonAngleHandle e = new RegularPolygonAngleHandle(poly, curve1, Color.green.darker(), 0, handleIDBase, point);
			e.maxRatio=2;
			list.add(e); 
			RegularPolygonAngleHandle e2 = new RegularPolygonAngleHandle(poly, curve2, Color.red.darker(), 0,  handleIDBase+2, point);
			e2.maxRatio=2;
			list.add(e2); 
			
			RegularPolygonAngleHandle e3 = new RegularPolygonAngleHandle(poly, anchor, Color.LIGHT_GRAY, 0,  handleIDBase+4, point);
			list.add(e3);
			e3.maxRatio=1.5;
			
			e.setLineConnectionHandle(e3);
			e2.setLineConnectionHandle(e3);
			e3.setHidden(anchorHidden);
			
			return list;
		}
		
		public void setPositions(double baseRadia, double radiaShift, double baseAngle, double angleShift) {
			anchor.setRatioToStandardAngle(baseAngle);
			anchor.setRatioToMaxRadius(baseRadia);
			
			curve1.setRatioToMaxRadius(baseRadia+radiaShift);
			curve1.setRatioToStandardAngle(baseAngle+angleShift);
			
			curve2.setRatioToMaxRadius(baseRadia-radiaShift);
			curve2.setRatioToStandardAngle(baseAngle-angleShift);
			
		}
		
		public void moveCurveControlOutward(double d) {
			curve1.increaseRadiusRatio(d);
			curve2.increaseRadiusRatio(d);
		}
		
		public void movePointOutward(double d) {
			anchor.increaseRadiusRatio(d);
			curve1.increaseRadiusRatio(d);
			curve2.increaseRadiusRatio(d);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		}
}
