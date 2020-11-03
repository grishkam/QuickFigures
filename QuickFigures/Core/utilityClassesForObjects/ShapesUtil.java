package utilityClassesForObjects;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class ShapesUtil {

	public ShapesUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static Rectangle addInsetsToRectangle(Rectangle r, Insets insets) {
		if (insets==null) return r;
		r.x-=insets.left;
		r.y=r.y-insets.top;
		r.width+=insets.left+insets.right;
		r.height+=insets.top+insets.bottom;
		return r;
	}
	public static Rectangle2D addInsetsToRectangle(Rectangle2D.Double r, Insets insets) {
		if (insets==null) return r;
		r.x-=insets.left;
		r.y-=insets.top;
		r.width+=insets.left+insets.right;
		r.height+=insets.top+insets.bottom;
		return r;
	}

}
