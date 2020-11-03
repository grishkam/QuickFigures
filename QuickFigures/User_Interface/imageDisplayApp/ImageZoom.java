package imageDisplayApp;

import graphicalObjects.BasicCordinateConverter;

/**Image zoom*/
public class ImageZoom {
	private double zoom=1;
	
	/**are used as displacement X, and Ys. When NOT in scrollpane mode
	  they are needed for scroll positions*/
	private double x0=0;
	private double y0=0;
	
	void setScrollX(double x) {
		x0=x;
	}
	
	public double getX0() {
		return x0;
	}
	public void setX0(double x0) {
		this.x0 = x0;
	}
	public double getY0() {
		return y0;
	}
	public void setY0(double y0) {
		this.y0 = y0;
	}
	public double getZoom() {
		return zoom;
	}
	
	public double get2SigFigZoom() {
		double factor=10000;
		
		int i=(int)(zoom*factor);
		
		return i/factor;
	}
	
	public void setZoom(double zoom) {
		this.zoom = zoom;
	}
	
	/**'Scrolls the view. Not relevant if the window is in scrollPane mode*/
	public void scroll(double xs, double ys) {
		if (x0+xs>=0)
		x0+=xs; else x0=0;
		if (y0+ys>=0)
		y0+=ys; else y0=0;
	}
	
	public void zoomIn() {
		zoom*=1.2;
	}
	
	public void zoomOut() {
		zoom/=1.2;
	}
	
	public BasicCordinateConverter getConverter() {
		return new BasicCordinateConverter(getX0(),getY0(), getZoom());
	}

	public void setScrollY(int i) {
		y0=i;	
	}

	
}
