package imageDisplayApp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

/**although there is no user option for this. a programmer can create a version of the 
window that does not use a scroll pane but instead indicates the position 
in the same way as imageJ does. in that case a scroll indicator need be drawn*/
public class ScrollIndicator implements ZoomableGraphic{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicSetDisplayWindow display;
	double factor=0.1;
	double x0=0;
	double y0=0;
	
	public ScrollIndicator(GraphicSetDisplayWindow display) {
		this.display=display;
		
	}
	
	
	/**returns the outer rectangle that indicates the entire image size*/
	Rectangle2D getTotalRect() {
		
		double width = this.getDisplay().getTheSet().getWidth()*factor;
		double height = this.getDisplay().getTheSet().getHeight()*factor;
		return new Rectangle(0,0,(int)width, (int)height);
	}
	
	/**returns the inner rectangle that indicates the view area (in comparison to the entire image of the total rect)*/
	Rectangle2D getInnerRect() {
		double x=x0;
		double y=y0;
		x+=display.getZoomer().getX0();
		y+=display.getZoomer().getY0();
		double mag=getDisplayMag();
		double canWidth=this.getDisplay().getTheCanvas().getWidth()/mag;
		double canHeight=this.getDisplay().getTheCanvas().getHeight()/mag;
		
		
		return new Rectangle((int)(x*factor),(int)(y*factor),(int)(canWidth*factor), (int)(canHeight*factor));
	}
	
	/**returns the magnification*/
	double getDisplayMag() {
		return getDisplay().getZoomer().getZoom();
	}
	
	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
		
		graphics.setColor(Color.blue);
		graphics.setStroke(new BasicStroke(1));
		
		if ( areRectsSame() ) return;
		
		graphics.draw(getTotalRect());
		
		
		graphics.setColor(Color.green);
		graphics.setStroke(new BasicStroke(1));
		graphics.draw(getInnerRect());
		
	}
	
	
	boolean areRectsSame() {
		if (getTotalRect().equals(getInnerRect())) return true;		
		return false;
	}
	/**returns true if the widths are similar*/
 boolean areRectWidthsSame() {
	double ratio = getTotalRect().getWidth()/getInnerRect().getWidth();
	if (ratio<1.02&&ratio>0.98) return true;
	return false;
 }
 /**returns true if the locations' x are similar*/
 boolean areRectXSame() {
	 double dif = getTotalRect().getX()-getInnerRect().getX();
	 if (dif<0.02) return true;
	 return false;
 }
 /**returns true if the locations' y are similar*/
 boolean areRectYSame() {
	 double dif = getTotalRect().getY()-getInnerRect().getY();
	 if (dif<0.02) return true;
	 return false;
 }
 
 /**returns true if the locations' height are similar*/
 boolean areRectHeightsSame() {
	double ratio = getTotalRect().getHeight()/getInnerRect().getHeight();
	if (ratio<1.02&&ratio>0.98) return true;
	return false;
 }
 

	public GraphicSetDisplayWindow getDisplay() {
		return display;
	}

	public void setDisplay(GraphicSetDisplayWindow display) {
		this.display = display;
	}





	private transient GraphicLayer layer;
	@Override
	public GraphicLayer getParentLayer() {
		return layer;
	}

	@Override
	public void setParentLayer(GraphicLayer parent) {
		layer=parent;
		
	}

}
