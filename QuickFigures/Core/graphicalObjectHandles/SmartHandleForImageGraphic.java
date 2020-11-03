package graphicalObjectHandles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import graphicalObjects.CordinateConverter;
import graphicalObjects_BasicShapes.RectangularGraphic;
import utilityClassesForObjects.RectangleEdges;

/**work in progress smart handle for image graphics*/
public class SmartHandleForImageGraphic extends SmartHandle {

	int position=0;
	RectangularGraphic rectangle=null;
	
	public SmartHandleForImageGraphic(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}
	
	public SmartHandleForImageGraphic( RectangularGraphic rect, int position) {
		super(0,0);
		this.rectangle=rect;
		this.position=position;
		
	}

	public Point2D getCordinateLocation() {
		Point2D p = RectangleEdges.getLocation(position, this.getBounds());
		return p;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {

		if (rectangle.getLocationType()==position)this.setHandleColor(Color.red); else
			this.setHandleColor(Color.black);
	//	
		 super.draw(graphics, cords);
	}

}
