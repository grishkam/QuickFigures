package genericMontageKit;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;


public class RectanglePlacements {

	/**when given the dimensios of the bounding box a rectangle this returns a list of points with the
	  rectangle located at a position that depends on p */
	public static Point[] putRelativeToCorner(Rectangle bounds, Rectangle2D[] panels, int position, int barxoff, int baryoff) {
		if (panels==null||bounds==null) return new Point[] {};
		Point[] output=new Point[panels.length];
		for(int i=0; i<panels.length; i++) {
			output[i] =putRelativeToCorner(bounds,  panels[i],  position, barxoff, baryoff);
		}
		return output;
	}
	
	public static Point putRelativeToCorner(Rectangle bounds, Rectangle2D panel,  int position, int barxoff, int baryoff) {
		Point output=new Point();
		if (position==TOP_LEFT) output=new Point((int)panel.getX()+barxoff, (int)panel.getY()+baryoff);
		if (position==BOTTOM_LEFT) output=new Point((int)panel.getX()+ barxoff, (int)panel.getY()+(int)(panel.getHeight()-baryoff-bounds.getHeight()));
		if (position==TOP_RIGHT) output=new Point((int) ((int)panel.getX()-barxoff+panel.getWidth()-bounds.getWidth()),(int)panel.getY()+ baryoff);
		if (position==BOTTOM_RIGHT) output=new Point((int) ((int)panel.getX()-barxoff+panel.getWidth()-bounds.getWidth()),(int)panel.getY()+(int)(panel.getHeight()-baryoff-bounds.getHeight()));
		if (position==TOP_CENTER) output=new Point((int)(panel.getX()+panel.getWidth()/2-bounds.getWidth()/2+barxoff), (int)panel.getY()+baryoff);
		if (position==BOTTOM_CENTER) output=new Point((int)(panel.getX()+panel.getWidth()/2-bounds.getWidth()/2+ barxoff), (int)panel.getY()+(int)(panel.getHeight()-baryoff-bounds.getHeight()));
		if (position==LEFT_CENTER) {output=new Point((int)panel.getX()+barxoff, (int)(panel.getY()+panel.getHeight()/2-bounds.getHeight()/2+baryoff));}
		if (position==RIGHT_CENTER) {output=new Point((int)(panel.getX()+panel.getWidth()-bounds.getWidth()-barxoff), (int)(panel.getY()+panel.getHeight()/2-bounds.getHeight()/2+baryoff));}
		if (position==CENTER) output=new Point((int) ((int)panel.getX()-barxoff+panel.getWidth()/2-bounds.getWidth()/2),(int)panel.getY()+(int)(panel.getHeight()/2-baryoff-bounds.getHeight()/2));
	return output;
	}
	
	 public String [] placments=new String[] {"Top Left", "Bottom Left", "Top Right", "Bottom Right", "Top Center", "Bottom Center", "Left Center", "Right Center", "Center"};
	 public static final int TOP_LEFT=0, BOTTOM_LEFT=1, TOP_RIGHT=2, BOTTOM_RIGHT=3, TOP_CENTER=4, BOTTOM_CENTER=5, LEFT_CENTER=6, RIGHT_CENTER=7, CENTER=8;
}
