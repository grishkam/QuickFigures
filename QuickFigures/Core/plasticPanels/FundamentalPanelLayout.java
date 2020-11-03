package plasticPanels;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import applicationAdapters.ImageWrapper;
import genericMontageKit.PanelLayout;

/**A very basic panel layout superclass that other classes can extends*/
public abstract class FundamentalPanelLayout implements PanelLayout, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Point location=new Point();
	private transient ImageWrapper wrapper;
	
	@Override
	public Point2D getReferenceLocation() {
		// TODO Auto-generated method stub
		return location;
	}
	
	
	@Override
	public Shape allPanelArea() {
		Area output = new Area();
		for(int i=0; i<nPanels(); i++) {
			//panel=this.getPanel(i+1);
			Rectangle2D panel = getPanel(i+1);
			if (panel!=null) output.add(new Area(panel));
		}
		return output;
	}
	
	

	@Override
	public Shape getBoundry() {
		// TODO Auto-generated method stub
		return allPanelArea().getBounds();
	}
	
	@Override
	public Point2D getPoint(int index) {
		Rectangle2D panel = this.getPanel(index);
		
		return  new Point2D.Double(panel.getX(), panel.getY());
	}

	@Override
	public Rectangle2D getNearestPanel(double d, double e) {
		PanelOperations<Rectangle2D> pops = new PanelOperations<Rectangle2D> ();
		return pops.getNearestPanel(this.getPanels(), d, e);
	}
	
	@Override
	public Rectangle2D[] getPanels() {
		Rectangle2D[] p = new Rectangle2D[nPanels()] ;
		for(int i=0; i<nPanels(); i++) {
			//panel=this.getPanel(i+1);
			p[i]=getPanel(i+1);
		}
		return p;
	}
	
	@Override
	public Point2D[] getPoints() {
		int size = getPanels().length;
		Point2D[] p = new Point2D[size] ;
		for(int i=0; i<size; i++) {
			//panel=this.getPanel(i+1);
			p[i]=getPoint(i+1);
		}
		return p;
	}
	@Override
	public ImageWrapper getWrapper() {
		// TODO Auto-generated method stub
		return wrapper;
	}

	public void setWrapper(ImageWrapper wrapper) {
		this.wrapper = wrapper;
	}
	

}
