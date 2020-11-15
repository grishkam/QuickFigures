package graphicalObjects_BasicShapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEventWrapper;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects.CordinateConverter;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import utilityClassesForObjects.RectangleEdgePosisions;
import utilityClassesForObjects.RectangleEdges;

public class RightTriangleGraphic extends RectangularGraphic implements RectangleEdgePosisions{
	


	{name="Right Triangle";}
	/**
	 * 
	 */
	private int type;
	
	private static final long serialVersionUID = 1L;
	
	
	public static RightTriangleGraphic blankShape(Rectangle r, Color c) {
		RightTriangleGraphic r1 = new RightTriangleGraphic(r);
		
		r1.setDashes(new float[]{100000,1});
		r1.setStrokeWidth(4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	

	public RightTriangleGraphic copy() {
		return new RightTriangleGraphic(this);
	}
	
	public RightTriangleGraphic(Rectangle rectangle) {
		super(rectangle);
	}
	
	public RightTriangleGraphic(RectangularGraphic r) {
		super(r);
	}

	/**implements a formular to produce a regular polygon with a certain number of vertices*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		
	
		ArrayList<Point2D> loc = getLocations();
		path.moveTo(loc.get(0).getX(),loc.get(0).getY());
		path.lineTo(loc.get(1).getX(),loc.get(1).getY());
		path.lineTo(loc.get(2).getX(),loc.get(2).getY());
		
		path.closePath();
		this.setClosedShape(true);
		
		return path;
		
	}
	
	ArrayList<Point2D> getLocations() {
		ArrayList<Point2D> output=new ArrayList<Point2D>();
		
		if(getType()==UPPER_LEFT) {
			output.add(RectangleEdges.getLocation(RectangleEdges.UPPER_RIGHT, getRectangle()));
			output.add(RectangleEdges.getLocation(RectangleEdges.LOWER_LEFT, getRectangle()));
			output.add(RectangleEdges.getLocation(RectangleEdges.UPPER_LEFT, getRectangle()));
			
		} else 
		if(getType()==UPPER_RIGHT) {
			output.add(RectangleEdges.getLocation(RectangleEdges.UPPER_RIGHT, getRectangle()));
			output.add(RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, getRectangle()));
			output.add(RectangleEdges.getLocation(RectangleEdges.UPPER_LEFT, getRectangle()));
			
		} else if(getType()==LOWER_RIGHT) {
			output.add(RectangleEdges.getLocation(RectangleEdges.UPPER_RIGHT, getRectangle()));
			output.add(RectangleEdges.getLocation(RectangleEdges.LOWER_RIGHT, getRectangle()));
			output.add(RectangleEdges.getLocation(RectangleEdges.LOWER_LEFT, getRectangle()));
		} else {
			output.add(RectangleEdges.getLocation(UPPER_LEFT, getRectangle()));
			output.add(RectangleEdges.getLocation(LOWER_RIGHT, getRectangle()));
			output.add(RectangleEdges.getLocation(LOWER_LEFT, getRectangle()));
		}
		
		return output;
	
	}
	
	
	
	

	RectangularGraphic rectForIcon() {
		RightTriangleGraphic ss = blankShape(new Rectangle(0,0,12,10), Color.BLACK);
		ss.setType(getType());
		return ss;
		
	}

	

	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}



	public int getType() {
		return type;
	}



	public void setType(int type) {
		this.type = type;
	}

	
	RectangularGraphic createIcon() {
		RectangularGraphic out = rectForIcon() ;//RectangularGraphic.blankRect(new Rectangle(0,0,14,12), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFillColor(), this.getStrokeColor());
		out.setAntialize(true);
		out.setStrokeWidth(1);
		out.copyColorsFrom(this);
		if(super.isIconTooWhite()) {
			this.setStrokeColor(whiteIcon);
		}
		return out;
	}

	
	
	
	protected SmartHandleList createSmartHandleList() {
		SmartHandleList list = super.createSmartHandleList();
		RigthAngleHandle handle = new RigthAngleHandle(this);
		handle.setHandleColor(Color.green);
		list.add(0,handle);
		return list;
	}
	
	public class RigthAngleHandle extends SmartHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private RightTriangleGraphic triangle;

		public RigthAngleHandle(RightTriangleGraphic rightTriangleGraphic) {
			super(0,0);
			triangle=rightTriangleGraphic;
			this.setHandleNumber(900471);
		}
		
		public Point2D getCordinateLocation() {
			int t = triangle.getType();
			Point2D l1 = RectangleEdges.getLocation(t, triangle.getRectangle());
			Point2D l2 = RectangleEdges.getLocation(CENTER, triangle.getRectangle());
			Point2D p = ShapeGraphic.betweenPoint(l1, l2, 0.7);
			triangle.undoRotationCorrection(p);
			return p;
		}
		
		public void handleDrag(CanvasMouseEventWrapper lastDragOrRelMouseEvent) {
			Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
			triangle.performRotationCorrection(p2);;
			int e = RectangleEdges.getNearestEdgeFromList(triangle.getRectangle(), new int[] {UPPER_RIGHT, LOWER_LEFT, UPPER_LEFT, LOWER_RIGHT}, p2);
			triangle.setType(e);
		}
		
		@Override
		public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
			graphics.setStroke(new BasicStroke(1));
			graphics.setColor(Color.black);
			int t = triangle.getType();
			for (int i:RectangleEdges.adjacentPositions(t)) {
				Point2D l1 = RectangleEdges.getLocation(t, triangle.getRectangle());
				Point2D l2 = RectangleEdges.getLocation(i, triangle.getRectangle());
				Point2D p = ShapeGraphic.betweenPoint(l1, l2, 0.7);
				triangle.undoRotationCorrection(p);
				super.drawLineBetweenPoints(graphics, cords, p, getCordinateLocation());
			}
			super.draw(graphics, cords);
		}

	}
	
	
}
