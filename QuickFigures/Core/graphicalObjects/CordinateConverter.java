package graphicalObjects;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public interface CordinateConverter<ImageType> {
	
	/**converts from an absolute cordinate system to a display system*/
	double transformX(double ox);
	 double transformY(double oy);
	 Point2D transformP(Point2D op);
	double getMagnification();
	public AffineTransform getAfflineTransform();
	Font getScaledFont(Font font) ;
	BasicStroke getScaledStroke(BasicStroke bs) ;
	Point2D[] transformPoints(Point2D[] upperLeftCornerOfBounds);
	double unTransformX(double ox);
	double unTransformY(double oy);
	Point2D unTransformP(Point2D op);
	
	CordinateConverter<?> getCopyTranslated(int dx, int dy);
}