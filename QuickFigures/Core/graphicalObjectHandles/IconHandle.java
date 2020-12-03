package graphicalObjectHandles;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import applicationAdapters.CanvasMouseEvent;

/**A handle that displays an icon*/
public class IconHandle extends SmartHandle {
	
	private static final int _DEFAULT_MAX_SIZE = 22;
	protected int subtract = 0;
	
	/**Some icons are drawn shifted slightly*/
	protected int xShift=0;
	protected int yShift=0;
	
	/**The max size for the handle. Even if icons are larger, the handle will be this size*/
	int maxWidth=_DEFAULT_MAX_SIZE;
	int maxHeight=_DEFAULT_MAX_SIZE;

	public IconHandle(Icon i, Point2D p) {
		super((int)p.getX(), (int)p.getY());
		this.setCordinateLocation(p);
		this.icon=i;
	}
	
	public void drawIcon(Graphics2D graphics, Point2D pt) {
		if (getIcon()!=null) {
			getIcon().paintIcon(null, graphics, getxShift()+(int)pt.getX()-this.getIcon().getIconWidth()/2, getyShift()+(int)pt.getY()-getIcon().getIconHeight()/2);
			
		};
	}
	

	protected Shape createDrawnRect(Point2D pt) {
		double widthr =getDrawnHandleWidth();
		double heightr = icon.getIconHeight()-subtract;
		
		if(heightr>this.maxHeight)heightr =this.maxHeight;
		double xr = pt.getX()-widthr/2;
		double yr = pt.getY()-heightr/2;
		
		return new Rectangle2D.Double(xr,yr, widthr, heightr);
	}

	public int getDrawnHandleWidth() {
		double widthr= icon.getIconWidth()-subtract;
		if(widthr>this.maxWidth)widthr=this.maxWidth;
		return (int) widthr;
	}
	

	public int getxShift() {
		return xShift;
	}
	public int getyShift() {
		return yShift;
	}

	public void setyShift(int yShift) {
		this.yShift = yShift;
	}
	public void setxShift(int xShift) {
		this.xShift = xShift;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void showPopupMenu(CanvasMouseEvent canvasMouseEventWrapper) {
	}

}
