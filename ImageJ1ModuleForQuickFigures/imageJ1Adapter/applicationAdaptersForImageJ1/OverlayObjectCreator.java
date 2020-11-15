package applicationAdaptersForImageJ1;

import ij.gui.ImageRoi;
import ij.gui.Roi;
import ij.gui.TextRoi;
import utilityClassesForObjects.LocatedObject2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import applicationAdapters.ObjectCreator;
import applicationAdapters.PixelWrapper;

/**an implementation of object creator for an imageplus*/
public class OverlayObjectCreator implements ObjectCreator {

	@Override
	public LocatedObject2D createTextObject(String label, Color c, Font font, FontMetrics f, int lx,
			int ly, double angle, boolean antialiasedText) {
		// TODO Auto-generated method stub
		//=this.generageFontMetrics(font);
		double d=0;
		if (f!=null) d+=f.getDescent();
		TextRoi t = new TextRoi(lx,ly-d, label);
		t.setCurrentFont(font); t.setStrokeColor(c); t.setJustification(TextRoi.LEFT );
		return new RoiWrapper(t);
	}

	@Override
	public LocatedObject2D createImageObject(String name, PixelWrapper pix,
			int x, int y) {
		// TODO Auto-generated method stub
		ImageRoi i1=new ImageRoi(x,y,(BufferedImage) pix.image());
		i1.setName(name);
		return new RoiWrapper(i1);
	}

	@Override
	public LocatedObject2D createRectangularObject(Rectangle r) {
		Roi output = new Roi(r);
		return new RoiWrapper(output);
	}

}
