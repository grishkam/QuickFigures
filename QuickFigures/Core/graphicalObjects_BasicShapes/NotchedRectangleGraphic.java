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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjectHandles.CountHandle;
import graphicalObjectHandles.RectangleEdgeHandle;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import objectDialogs.RectangleGraphicOptionsDialog;
import utilityClassesForObjects.RectangleEdges;

/**A rectangle with notches cut out of it*/
public class NotchedRectangleGraphic extends RectangularGraphic {
	{name="Notch Rect";}
	/**
	 * 
	 */

	private RectangleEdgeParameter arcw=new RectangleEdgeParameter(this, 40, UPPER_LEFT, UPPER_RIGHT); {arcw.setRatioToMaxLength(0.33333);}

	private RectangleEdgeParameter arch=new RectangleEdgeParameter(this, 40, UPPER_LEFT, LOWER_LEFT);;{arch.setRatioToMaxLength(0.33333);}
	
	boolean [] validCounts=new boolean[] {true, false, false, false};
	//private CountParameter nNotches=new CountParameter(this, 1); {nNotches.setMaxValue(4);nNotches.setMinValue(1);}
	private CountParameter notchType=new CountParameter(this, 2); {notchType.setNames(new String[] {"Rect", "Oval", "Bevel", "Rounded", "Round 2"}); notchType.setMaxValue(4);}
	//private CountParameter starting=new CountParameter(this, 0, 0, 5); { {starting.setNames(new String[] {"ABCD", "BCDA", "CDAB", "DABC", "ACBD", "inside"});}}
	
	private static final long serialVersionUID = 1L;
	
	
	public static NotchedRectangleGraphic blankShape(Rectangle r, Color c) {
		NotchedRectangleGraphic r1 = new NotchedRectangleGraphic(r);
		
		
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	

	public NotchedRectangleGraphic copy() {
		NotchedRectangleGraphic output = new NotchedRectangleGraphic(this);
		output.arch=arch.copy(output);
		output.arcw=arcw.copy(output);
		giveTraitsTo(output);
		
		return output;
	}



	protected void giveTraitsTo(NotchedRectangleGraphic output) {
	//	nNotches.giveTraitsTo(output.nNotches);
		//starting.giveTraitsTo(output.starting);
		notchType.giveTraitsTo(output.notchType);
	}
	

	public NotchedRectangleGraphic(RectangularGraphic r) {
		super(r);
	}

	public NotchedRectangleGraphic(Rectangle r) {
		super(r);
	}
	
	
	private Shape createShapeforSuctract(int index) {
		double w = this.getObjectWidth()*arcw.getRatioToMaxLength();
		double h = this.getObjectHeight()*arch.getRatioToMaxLength();
		
		int type=getTypeFor(index);
		Point2D loc = RectangleEdges.getLocation(type, getRectangle());
		if (notchType.getValue()==1) 
			return new Ellipse2D.Double(loc.getX()-w, loc.getY()-h, 2*w, 2*h);
		if (notchType.getValue()==2) 
			return createBevel(loc.getX()-w, loc.getY()-h, 2*w, 2*h);
		if (notchType.getValue()==3) 
			return createQuad(loc.getX()-w, loc.getY()-h, 2*w, 2*h);
		if (notchType.getValue()==4) 
			return createCurve(loc.getX()-w, loc.getY()-h, 2*w, 2*h);
		
		
		return new Rectangle2D.Double(loc.getX()-w, loc.getY()-h, 2*w, 2*h);
	}

	private int getTypeFor(int index) {
	/**	int out=index+starting.getValue();
		if (starting.getValue()<4)
			return out%4;
		
		int[] c=new int[] {UPPER_LEFT, LOWER_RIGHT, LOWER_LEFT, UPPER_RIGHT};
		
		if (starting.getValue()==5) {
			 c=new int[] {LEFT, RIGHT, BOTTOM, TOP};
			 out=index+starting.getValue()-1;
				
		}
		
		return c[out%4];*/
		return index;
	}



	private Shape createBevel(double d, double e, double f, double g) {
		Path2D.Double path=new Path2D.Double();
		
		Double r = new Rectangle2D.Double(d,e,f,g);
		Point2D p1 = RectangleEdges.getLocation(TOP, r);
		path.moveTo(p1.getX(), p1.getY());
		
		 p1 = RectangleEdges.getLocation(RIGHT, r);
		path.lineTo(p1.getX(), p1.getY());
		
		 p1 = RectangleEdges.getLocation(BOTTOM, r);
			path.lineTo(p1.getX(), p1.getY());
			
			 p1 = RectangleEdges.getLocation(LEFT, r);
				path.lineTo(p1.getX(), p1.getY());
				
				path.closePath();
		
		return path;
	}
	
	private Shape createQuad(double d, double e, double f, double g) {
		Path2D.Double path=new Path2D.Double();
		
		Double r = new Rectangle2D.Double(d,e,f,g);
		Point2D p1 = RectangleEdges.getLocation(TOP, r);
		Point2D p2 = RectangleEdges.getLocation(CENTER, r);
		path.moveTo(p1.getX(), p1.getY());
		
		 p1 = RectangleEdges.getLocation(RIGHT, r);
		 path.quadTo(p2.getX(), p2.getY(), p1.getX(), p1.getY());
			
		 p1 = RectangleEdges.getLocation(BOTTOM, r);
		 path.quadTo(p2.getX(), p2.getY(), p1.getX(), p1.getY());
			
			 p1 = RectangleEdges.getLocation(LEFT, r);
			 path.quadTo(p2.getX(), p2.getY(), p1.getX(), p1.getY());
			
			 p1 = RectangleEdges.getLocation(TOP, r);
			 path.quadTo(p2.getX(), p2.getY(), p1.getX(), p1.getY());
				 
			 
				path.closePath();
		
		return path;
	}
	
	private Shape createCurve(double d, double e, double f, double g) {
		Path2D.Double path=new Path2D.Double();
		
		Double r = new Rectangle2D.Double(d,e,f,g);
		Point2D p1 = RectangleEdges.getLocation(TOP, r);
	
		path.moveTo(p1.getX(), p1.getY());
		
		 p1 = RectangleEdges.getLocation(RIGHT, r);
		 curveTo(path, p1, RectangleEdges.getMidPointLocation(CENTER, RIGHT, r),RectangleEdges.getMidPointLocation(CENTER, TOP, r));
			
		 p1 = RectangleEdges.getLocation(BOTTOM, r);
		 curveTo(path, p1, RectangleEdges.getMidPointLocation(CENTER, BOTTOM, r),RectangleEdges.getMidPointLocation(CENTER, RIGHT, r));
				
			 p1 = RectangleEdges.getLocation(LEFT, r);
			 curveTo(path, p1, RectangleEdges.getMidPointLocation(CENTER, LEFT, r),RectangleEdges.getMidPointLocation(CENTER, BOTTOM, r));
						
			 p1 = RectangleEdges.getLocation(TOP, r);
			 curveTo(path, p1, RectangleEdges.getMidPointLocation(CENTER, TOP, r),RectangleEdges.getMidPointLocation(CENTER, LEFT, r));
							 
			 
				path.closePath();
		
		return path;
	}



	protected void curveTo(Path2D.Double path, Point2D p1, Point2D p2, Point2D p3) {
		path.curveTo(p3.getX(), p3.getY(), p2.getX(), p2.getY(), p1.getX(), p1.getY());
	}



	public void subtractFromArea(Area a, int type) {
		Area a2 = new Area(this.createShapeforSuctract(type));
		a.subtract(a2);
	}


	public Shape getShape() {
		Rectangle2D.Double double1 = new Rectangle2D.Double(x,y,getObjectWidth(),getObjectHeight());
	//	if (nNotches.getValue()==0)return double1;
		
		Area output = new Area(double1);
		for(int i=0; i<4;i++) if(validCounts[i]) this.subtractFromArea(output, i);
		
		
		return output;
	}
	
	
	
	
	
	
	RectangularGraphic rectForIcon() {
		NotchedRectangleGraphic iconshape = blankShape(new Rectangle(0,0,12,10), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
		iconshape.setArcw(5);
		iconshape.setArch(5);
		giveTraitsTo(iconshape);
		return iconshape;
	}





	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}

	
	
	@Override
	public void showOptionsDialog() {
		new RectangleGraphicOptionsDialog(this, false).showDialog();
	}



	public double getArcw() {
		return arcw.getLength();
	}



	public void setArcw(double arcw) {
		this.arcw.setLength(arcw);;
	}



	public double getArch() {
		return arch.getLength();
	}



	public void setArch(double arch) {
		this.arch.setLength( arch);
	}
	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		 {
			list.add(0,new RectangleEdgeHandle(this, arch, Color.cyan, 18,RectangleEdgeHandle.RATIO_TYPE, 0.05));
			list.add(0,new RectangleEdgeHandle(this, arcw, Color.orange, 20,RectangleEdgeHandle.RATIO_TYPE, -0.05));
			int dx = 45;
		//	list.add(new CountHandle(this, nNotches, 34912, dx,25, false, 1));
			list.add(new CountHandle(this, notchType,239124, dx, 50, true, 3));
		//	list.add(new CountHandle(this, starting,19128, dx, 75, true, 2));
			for(int i=0; i<4; i++) list.add(new notchHandle(this, i));
		 }
		return list;
	}
	
	
	public String getShapeName() {
		return "Notched Rect";
	}
	
	public class notchHandle extends SmartHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int index;
		private NotchedRectangleGraphic rect;

		public notchHandle(NotchedRectangleGraphic notchedRectangleGraphic, int i) {
		
			this.index=i;
			this.rect=notchedRectangleGraphic;
			this.setHandleNumber(98200+i);
			this.decorationColor=Color.red;
			if (checked()) this.setSpecialFill(CHECK_MARK); else this.setSpecialFill(0);
		}
		
		public Color getHandleColor() {
			return checked()? Color.LIGHT_GRAY: Color.darkGray;
		}
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			rect.validCounts[index]=!checked();
			if (checked()) this.setSpecialFill(CHECK_MARK); else this.setSpecialFill(0);
		}

		protected boolean checked() {
			return rect.validCounts[index];
		}
		public Point2D getCordinateLocation() {
			Double r = rect.getRectangle();
			double z=10;
			Double r2 = new Rectangle2D.Double(r.x-z, r.y-z, r.width+2*z, r.height+2*z);
			Point2D loc = RectangleEdges.getLocation(index, r2);
			
			rect.undoRotationCorrection(loc);
			
			return loc;
		}
	
	
	}
	

	
	
	
}
