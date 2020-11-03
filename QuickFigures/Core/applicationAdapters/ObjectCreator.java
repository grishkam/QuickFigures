package applicationAdapters;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;

import utilityClassesForObjects.LocatedObject2D;

public interface ObjectCreator {

	public LocatedObject2D createTextObject(String label, Color c, Font font,FontMetrics f, int lx, int ly,
			double angle, boolean antialiasedText);
	
	public LocatedObject2D createImageObject(String name, PixelWrapper pix, int x, int y) ;

	public LocatedObject2D createRectangularObject(Rectangle r) ;
	
}
