package pathGraphicToolFamily;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import graphicalObjects_BasicShapes.PathGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.PathPoint;
import utilityClassesForObjects.PathPointList;

/**A tool that allows the user to select multiple points on a Path and move the selected ones only*/
public class PathTool2 extends PathTool{
	
	{createSelector=true;}
	
	/**When the path is the selected item on a path press, this tool slects a handle and allows manipulating
	  the path by moving multiple points at a time*/
	public void onPathPress() {
		if(pathGraphic==null) return;
			pathGraphic.setHandleMode(PathGraphic.MOVE_ALL_SELECTED_HANDLES);
			
			Point2D locationClickInPathsCords=new Point2D.Double(getClickedCordinateX()-pathGraphic.getLocation().getX(), this.getClickedCordinateY()-pathGraphic.getLocation().getY());
			PathPoint pi = pathGraphic.getPoints().getNearest(locationClickInPathsCords.getX(), locationClickInPathsCords.getY());
			double dist = pi.getAnchor().distance(locationClickInPathsCords);
			if (dist<5) {
				if (!pi.isSelected())pi.select();
				if (pi.isSelected()&&this.altKeyDown())pi.deselect();
			}
	}
	
	@Override
	public String getToolName() {
		return "Move Multiple Anchor Points";
	}
	
	GraphicGroup createIcon() {
		GraphicGroup out = new GraphicGroup(); ;//RectangularGraphic.blankRect(new Rectangle(0,0,14,12), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFillColor(), this.getStrokeColor());
		/**	Point2D p1=new Point2D.Double(10,9);
			Point2D p2=new Point2D.Double(10, 11);
			Point2D p3=new Point2D.Double(9, 10);
			Point2D p4=new Point2D.Double(11,10);*/
		PathGraphic p = new PathGraphic(new Point(9,9));
		p.select();
		p.addPoint(new Point(12, 7));
		p.addPoint(new Point(6, 13));
		p.addPoint(new Point(2, 3));
		
		p.getPoints().get(1).select();
		p.getPoints().get(2).select();
		p.setStrokeColor(Color.green.darker());
		p.makeDashLess();
		out.getTheLayer().add(p);
			/**
			RectangularGraphic rect1 = RectangularGraphic.blankRect(new Rectangle(8,8,4,4), Color.white) ;
			rect1.setStrokeWidth(1);
			rect1.setStrokeColor(Color.black);
			RectangularGraphic rect = rect1.copy();
			
			out.getTheLayer().add(rect1);
			rect1=rect.copy();
			rect1.moveLocation(6, 5);
			out.getTheLayer().add(rect1);
			rect.setFillColor(Color.yellow);
			
			rect1=rect.copy();
			rect1.moveLocation(-7, -2);
			out.getTheLayer().add(rect1);
			
			rect1=rect.copy();
			rect1.moveLocation(7, -5);
			
		;*/
		//out.setAngle(this.getAngle());
		return out;
	}	
	
	/**overrides the superclass method such that instead of selecting the rois in 
	 * the selector region, this selects the points in one of the path graphics of that 
	 * region*/
	protected void selectRoisInDrawnSelector() {
		Rectangle2D rect = selection;
		//getImageWrapperClick().getSelectionManagger().select(rect, 0);
		if(rect!=null&&rect.getHeight()>2&&rect.getWidth()>2) {
		//	
			ArrayList<LocatedObject2D> items = this.getObjecthandler().getOverlapOverlaypingItems(rect.getBounds(),this.getImageWrapperClick());
			removeIgnoredAndHidden(items);
			for(Object i: items) {
				deselect(i);
				if (i instanceof PathGraphic)pathGraphic=(PathGraphic)i;
			}
			
			
			/**Selects the points inside the selector region*/
			if (pathGraphic!=null) {
			
				this.setSelectedObject(pathGraphic);
				Rectangle rect2 = rect.getBounds();
				rect2.x-=pathGraphic.getLocation().getX();
				rect2.y-=pathGraphic.getLocation().getY();
				PathPointList list = pathGraphic.getPoints();
				for(PathPoint p: list) {
					if (rect2.contains(p.getAnchor())) {p.select(); }else if (!shiftDown())p.deselect();
				}
			}
			
		}
		selection=null;
	}
	
}
