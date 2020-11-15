package imageDisplayApp;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import applicationAdapters.ObjectCreator;
import applicationAdapters.PixelWrapper;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import utilityClassesForObjects.LocatedObject2D;

/**an implementation of the object creator interface*/
public class BasicGraphicalObjectCreator implements ObjectCreator {

	
	/**returns null for now but will eventually create Labels. its optional to provide font metrics*/
	@Override
	public LocatedObject2D createTextObject(String label, Color c, Font font, FontMetrics f, int lx,
			int ly, double angle, boolean antialiasedText) {
		TextGraphic tg = new TextGraphic(label);
		tg.setTextColor(c);
		tg.setFont(font);
		tg.setLocation(lx, ly);
		tg.setAngle(angle);
		return tg;
	}

	@Override
	public LocatedObject2D createImageObject(String name, PixelWrapper pix,
			int x, int y) {
		ImagePanelGraphic output = new ImagePanelGraphic((BufferedImage) pix.image());
		output.setName(name);
		output.setLocationUpperLeft(new Point(x,y));
		return output;
	}

	@Override
	public LocatedObject2D createRectangularObject(Rectangle r) {
		RectangularGraphic output = new RectangularGraphic();
		output.setRectangle(r);
		return output;
	}

}
