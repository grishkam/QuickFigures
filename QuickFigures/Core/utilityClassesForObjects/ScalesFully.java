package utilityClassesForObjects;

import java.awt.geom.Point2D;

public interface ScalesFully extends Scales, RotatesFully {
	/**scales the path about point p, does not scale strokes and effects*/
	public void scaleAbout(Point2D p, double magx, double magy) ;
	
	}
