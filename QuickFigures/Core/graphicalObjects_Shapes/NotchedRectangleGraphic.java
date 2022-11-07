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
 * Version: 2022.2
 */
package graphicalObjects_Shapes;

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
import handles.CountHandle;
import handles.RectangleEdgeHandle;
import handles.SmartHandle;
import handles.SmartHandleList;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import locatedObject.RectangleEdges;
import objectDialogs.RectangleGraphicOptionsDialog;

/**A rectangle with notches cut out of it. notches can take several different forms
 * A user can select which points */
public class NotchedRectangleGraphic extends RectangularGraphic {
	/**
	 * 
	 */
	
	{name="Notch Rect";}
	/**
	 * 
	 */
	
	/**A parameter that determines the notch size*/
	private RectangleEdgeParameter notchWidth=new RectangleEdgeParameter(this, 40, UPPER_LEFT, UPPER_RIGHT); {notchWidth.setRatioToMaxLength(0.33333);}

	/**A parameter that determines the notch size*/
	private RectangleEdgeParameter notchHeight=new RectangleEdgeParameter(this, 40, UPPER_LEFT, LOWER_LEFT);;{notchHeight.setRatioToMaxLength(0.33333);}
	
	boolean [] notchPositions=new boolean[] {true, false, false, false};
	
	/**codes for each notch type*/
	public static final int CURVE_CUT = 4, QUAD_CUT = 3, BEVEL_CUT = 2, OVAL_CUT = 1, RECTANGLE_CUT=0;
	/**A count parameter that determines the type of notch*/
	private CountParameter notchType=new CountParameter(this, 2); {notchType.setNames(new String[] {"Rect", "Oval", "Bevel", "Rounded", "Round 2"}); notchType.setMaxValue(4);}
	
	private static final long serialVersionUID = 1L;
	
	public NotchedRectangleGraphic(RectangularGraphic r) {
		super(r);
	}

	public NotchedRectangleGraphic(Rectangle r) {
		super(r);
	}
	
	

	

	public NotchedRectangleGraphic copy() {
		NotchedRectangleGraphic output = new NotchedRectangleGraphic(this);
		output.notchHeight=notchHeight.copy(output);
		output.notchWidth=notchWidth.copy(output);
		giveTraitsTo(output);
		output.notchPositions=notchPositions.clone();
		output.notchType.setValue(notchType.getValue());
		return output;
	}



	protected void giveTraitsTo(NotchedRectangleGraphic output) {
		notchType.giveTraitsTo(output.notchType);
	}
	

	
	
	/**returns the shape that will be subtracted to create a notch*/
	private Shape createShapeforSuctract(int locationCode) {
		double w = this.getObjectWidth()*notchWidth.getRatioToMaxLength();
		double h = this.getObjectHeight()*notchHeight.getRatioToMaxLength();
		
		int type=locationCode;
		Point2D loc = RectangleEdges.getLocation(type, getRectangle());
		if (notchType.getValue()==OVAL_CUT) 
			return new Ellipse2D.Double(loc.getX()-w, loc.getY()-h, 2*w, 2*h);
		if (notchType.getValue()==BEVEL_CUT) 
			return createBevel(loc.getX()-w, loc.getY()-h, 2*w, 2*h);
		if (notchType.getValue()==QUAD_CUT) 
			return createQuad(loc.getX()-w, loc.getY()-h, 2*w, 2*h);
		if (notchType.getValue()==CURVE_CUT) 
			return createCurve(loc.getX()-w, loc.getY()-h, 2*w, 2*h);
		
		
		return new Rectangle2D.Double(loc.getX()-w, loc.getY()-h, 2*w, 2*h);
	}

	
	/**creates a shape to cut out a bevel from the rectangle*/
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
	
	/**creates a shape to cut out a quad curve from a rectangle*/
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
	
	/**creates a shape to cut out a curve from a rectangle*/
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

	/**Adds a curve to a path*/
	private void curveTo(Path2D.Double path, Point2D p1, Point2D p2, Point2D p3) {
		path.curveTo(p3.getX(), p3.getY(), p2.getX(), p2.getY(), p1.getX(), p1.getY());
	}


	/**subtracts a particular notch from the total area of the shape */
	public void subtractFromArea(Area a, int locationType) {
		Area a2 = new Area(this.createShapeforSuctract(locationType));
		a.subtract(a2);
	}


	public Shape getShape() {
		Rectangle2D.Double double1 = new Rectangle2D.Double(x,y,getObjectWidth(),getObjectHeight());
		
		Area output = new Area(double1);
		for(int i=0; i<4;i++) if(notchPositions[i]) this.subtractFromArea(output, i);
		
		
		return output;
	}

	/**returns the icon*/
	RectangularGraphic shapeUsedForIcon() {
		NotchedRectangleGraphic iconshape = blankShape(new Rectangle(0,0,12,10), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFi
		iconshape.setNotchWidth(5);
		iconshape.setNotchHeight(5);
		giveTraitsTo(iconshape);
		return iconshape;
	}

	/**creates the shape for an illustrator script*/
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}

	
	
	@Override
	public void showOptionsDialog() {
		new RectangleGraphicOptionsDialog(this, false).showDialog();
	}



	public double getNotchWidth() {
		return notchWidth.getLength();
	}



	public void setNotchWidth(double arcw) {
		this.notchWidth.setLength(arcw);;
	}



	public double getNotchHeight() {
		return notchHeight.getLength();
	}


	public void setNotchHeight(double arch) {
		this.notchHeight.setLength( arch);
	}
	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		 {
			list.add(0,new RectangleEdgeHandle(this, notchHeight, Color.cyan, 18,RectangleEdgeHandle.RATIO_TYPE, 0.05));
			list.add(0,new RectangleEdgeHandle(this, notchWidth, Color.orange, 20,RectangleEdgeHandle.RATIO_TYPE, -0.05));
			int dx = 45;
			list.add(new CountHandle(this, notchType,239124, dx, 50, true, 3));
			for(int i=0; i<4; i++) list.add(new NotchHandle(this, i));
		 }
		return list;
	}
	
	
	public String getShapeName() {
		return "Notched Rectangle";
	}
	
	/**A special handle for selecting and unselecting position for a notch*/
	public class NotchHandle extends SmartHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int index;
		private NotchedRectangleGraphic rect;

		public NotchHandle(NotchedRectangleGraphic notchedRectangleGraphic, int i) {
		
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
			rect.notchPositions[index]=!checked();
			if (checked()) this.setSpecialFill(CHECK_MARK); else this.setSpecialFill(0);
		}

		protected boolean checked() {
			return rect.notchPositions[index];
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
	

	public static NotchedRectangleGraphic blankShape(Rectangle r, Color c) {
		NotchedRectangleGraphic r1 = new NotchedRectangleGraphic(r);
		
		
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	
}
