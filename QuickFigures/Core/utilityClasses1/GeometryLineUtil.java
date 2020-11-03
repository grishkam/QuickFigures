package utilityClasses1;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class GeometryLineUtil {
	
	

	static double getSlope(Line2D l) {
		double rise=l.getY2()-l.getY1();
		double run=l.getX2()-l.getX1();
		return rise/run;
	}
	static Point2D getMidpint(Line2D l) {
		double rise=l.getY2()+l.getY1();
		double run=l.getX2()+l.getX1();
		return new Point2D.Double(run/2, rise/2);
	}
	static double length(Line2D l) {
		double rise=l.getY2()-l.getY1();
		double run=l.getX2()-l.getX1();
		return Math.sqrt(rise*rise+run*run);
	}
	
	public static Line2D perpendicularBisector(Line2D l) {
		double m = getSlope(l);
		Point2D p = getMidpint(l);
		double len = length(l)/2;
		double theta = Math.atan(-1/m);
		Double p1 = new Point2D.Double(p.getX()+Math.cos(theta)*len,p.getY()+ Math.sin(theta)*len);
		Double p2 = new Point2D.Double(p.getX()-Math.cos(theta)*len, p.getY()-Math.sin(theta)*len);
		return new Line2D.Double(p1, p2);
	}
	
	
}
