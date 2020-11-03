package genericMontageKit;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import applicationAdapters.ImageWrapper;

/***/
public interface PanelLayout {

	
	/**returns the array of panel rectangles. does not re-innitialize the rectangles 
	   unless the array is null or empty*/
	public Rectangle2D[] getPanels() ;
	
	/**returns a rectangle representing the panel at the index*/
	public Rectangle2D getPanel(int index) ;
	
	/**return the array of points representing the upper left corners of each panel*/
	public Point2D[] getPoints() ;
	
	/**returns the point representing the upper left hand corner of the panel at index*/
	public Point2D getPoint(int index) ;
	
	public Rectangle2D getNearestPanel(double d, double e);
	public int getNearestPanelIndex(double d, double e);
	
	public void move(double x, double y);
	/**get the location*/
	public Point2D getReferenceLocation();
	
	public Shape allPanelArea();
	public Shape getBoundry();
	
	/**sets the Width of the panel*/ 
	public void setPanelWidth(int panel, double width);
	
	/**sets the Height of the panel*/ 
	public void setPanelHeight(int panel, double height);
	
	/**recalculated the points and panels*/
	 public void resetPtsPanels() ;
	 
	 /**returns the standard panel dimensions*/
	 public double getStandardPanelWidth();
	 public double getStandardPanelHeight();
	 
	 public void setStandardPanelWidth(double width);
	 public void setStandardPanelHeight(double height);
	 
	 public boolean doesPanelUseUniqueWidth(int panel);
	 public boolean doesPanelUseUniqueHeight(int panel);

	public void nudgePanel(int panelnum, double dx, double dy);
	public void nudgePanelDimensions(int panelnum, double dx, double dy);
	
	public ImageWrapper getWrapper() ;

	public int nPanels();

	public void setWrapper(ImageWrapper genericImage);
	 
}
