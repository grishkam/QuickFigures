import java.awt.Polygon;
import java.awt.geom.PathIterator;

@SuppressWarnings("unused")
public class WasteBin {

	/**creates an awt polygon*/
	private static Polygon shapeToPolygon(PathIterator s) {
		PathIterator pi = s;
		double[] d=new double[6];
		Polygon poly = new Polygon();
		
		while (!pi.isDone()) {
			pi.currentSegment(d);
			//if (d[0]==0&& d[1]==0) {} else
			poly.addPoint((int)d[0], (int)d[1]);
			
			pi.next();
		}
		return poly;
	}
	
}
