package utilityClassesForObjects;

import java.awt.geom.Point2D;

public interface Scales {

	/**scales the shape about point p. the fold scaling is determined by mag*/
	public void scaleAbout(Point2D p, double mag);
	
	public Object getScaleWarning();
	
}
