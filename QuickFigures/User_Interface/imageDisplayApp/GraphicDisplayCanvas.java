package imageDisplayApp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import appContext.ImageDPIHandler;
import graphicalObjectHandles.HasSmartHandles;
import graphicalObjects.CordinateConverter;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import utilityClassesForObjects.Selectable;

/**A component that displays the figure within the display window
  All objects in the figure are drawn here*/
public class GraphicDisplayCanvas extends
		JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicSetDisplayWindow window;
	
	public GraphicDisplayCanvas() {
		super();
		this.addKeyListener(new KeyDownTracker());
	
	}


	public void setDispWindow(GraphicSetDisplayWindow window) {
		this.window=window;
		
	}
	
	
	
	public void paintComponent(Graphics g) {

		if (g instanceof Graphics2D) {
			 Graphics2D g2 = (Graphics2D)g;
		GraphicContainingImage gmp = window.getTheSet();
		
		/**marks the parts of the canvas object beyond the display canvas area with black and grey gradient*/
		g2.setPaint(getGrayFillPaint());//.setColor(Color.darkGray);
		Rectangle r = new Rectangle(-1, -1, this.getWidth(), this.getHeight());
		Shape da = getDisplaySetArea();
		Area greyArea = (new Area(r));
		greyArea.subtract(new Area(da));;
		((Graphics2D) g).fill(greyArea);
		
		/**fills the display canvas area with white so that user knows this is where they draw*/
		g2.setColor(Color.white);
	 	g2.fill( da);
		g.setColor(Color.black);
		g.drawRect(-1, -1, this.getWidth(), this.getHeight());
		
		
		
		if (window==null||window.getTheSet()==null) {IssueLog.log("warning, window null");}
		 
		/**draws all of objects*/
		if (window!=null&&window.getTheSet()!=null) {
		
			
			GraphicLayer layerSet = window.getTheSet().getGraphicLayerSet();
			CordinateConverter<?> conv1 = getConverter();
			
			layerSet.draw(g2, conv1);
			
			gmp.getOverlaySelectionManagger().drawSelections(g2, getConverter());
			
			/**although there is no user option for this. a programmer can create a version of the 
			  window that does not use a scroll pane but instead indicates the position 
			  in the same way as imageJ does. in that case an indicator need be drawn*/
			if (!window.usesScrollPane()) window.indicator.draw(g2, getConverter());
			
			 drawRulers((Graphics2D) g);
			 drawSmartHandlesForSelectedItrm(g2, conv1);
			
			 
		}
		// IssueLog.log(new GraphicEncoder(gmp).getBytes());
		
	}
		
		
		;
}



	protected void drawSmartHandlesForSelectedItrm(Graphics2D g2, CordinateConverter<?> conv1) {
		Selectable sel = window.getDisplaySet().getSelectedItem();
		if (sel!=null&& sel instanceof HasSmartHandles) {
			 HasSmartHandles h=(HasSmartHandles) sel;
			 h.getSmartHandleList().draw(g2, conv1);
		}
	}
	
	
	/**Returns the area of the canvas that Actually Contains the Graphic Display Set*/
	public Shape getDisplaySetArea() {
		GraphicContainingImage gmp = window.getTheSet();
		Rectangle r5 = new Rectangle(0,0, gmp.getWidth(), gmp.getHeight());
		AffineTransform at = getConverter().getAffineTransform();
		Point2D p = new Point(0,0);

		Point2D p2 = new Point2D.Double(0,0);
		at.transform(p, p2);
	
		
		Shape r=at.createTransformedShape(r5);
		
		
		return r;
	}
	
	/**returns the amount of grey space*/
	double getSlackSpaceW() {
		return getWidth()-getDisplaySetArea().getBounds2D().getMaxX();
	}
	/**returns the amount of grey space*/
	double getSlackSpaceH() {
		return getHeight()-getDisplaySetArea().getBounds2D().getMaxY();
	}
	
	
	
	public CordinateConverter<?> getConverter() {
		return window.getZoomer().getConverter();
	}
	
	 double getCanvasWidthInUnits() {
			double mag=getZoomer().getZoomMagnification();
			return getWidth()/mag;
		}
		
		double getCanvasHeightInUnits() {
			double mag=getZoomer().getZoomMagnification();
			return getHeight()/mag;
		}



		private ImageZoom getZoomer() {
			// TODO Auto-generated method stub
			return this.window.getZoomer();
		}
	
		public Point2D getCenterOfZoom() {
			double canWidth=this.getCanvasWidthInUnits();//etTheCanvas().getWidth()/mag;
			double canHeight=this.getCanvasHeightInUnits();//getTheCanvas().getHeight()/mag;
			double xcent = getZoomer().getX0()+canWidth/2;
			double ycent = getZoomer().getY0()+canHeight/2;
			
			return new Point2D.Double(xcent, ycent);
		}
		
		public void centerZoomAtPoint(Point2D p) {
			Point2D pcent = getCenterOfZoom();
			double xs = p.getX()-pcent.getX();
			double ys = p.getY()-pcent.getY();
			this.getZoomer().scroll(xs, ys);
		}
		
		public Paint getGrayFillPaint() {
			
			GradientPaint output = new GradientPaint(new Point(0,0), Color.darkGray, new Point(5,5), Color.gray, true);
			
			
			return output;
		}
		
		
		/**Draws rulers so that each inch is 72 units*/
		private void drawRulers(Graphics2D g) {
			Rectangle areaWhite = getDisplaySetArea().getBounds();
			
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(2));
			
			
			double positionx = areaWhite.getMinX();
			double positiony = areaWhite.getMaxY();
			g.setFont(new Font("Arial", Font.BOLD, 14));
				/**Draws the inch markers*/	
			for(int i=0; i<20; i++) {
				positionx+=ImageDPIHandler.getStandardDPI()*this.getZoomer().getZoomMagnification();
				if (positionx>areaWhite.getMaxX()) break;
				g.drawLine((int)positionx, (int)positiony, (int)positionx, (int)positiony+10);
				g.drawString((i+1)+" in", (int)positionx-10, (int)positiony+10+g.getFont().getSize());
				
			}
			
			positionx = areaWhite.getBounds().getMinX();
			positiony = areaWhite.getBounds().getMaxY();
			
			/**Draws the 1/4 inch markers*/	
			for(int i=0; i<80; i++) {
				positionx+=ImageDPIHandler.getStandardDPI()*this.getZoomer().getZoomMagnification()/4;
				if (positionx>areaWhite.getMaxX()) break;
				g.drawLine((int)positionx, (int)positiony, (int)positionx, (int)positiony+5);
				
			}
			g.drawLine((int)areaWhite.getMinX(), (int)positiony, (int)areaWhite.getMaxX(), (int)positiony);
			
			
			
			/**now for vertical ruler*/
			positionx = areaWhite.getBounds().getMaxX();
			positiony = areaWhite.getBounds().getMinY();
			
			/**Draws the inch markers*/	
			for(int i=0; i<20; i++) {
				positiony+=ImageDPIHandler.getStandardDPI()*this.getZoomer().getZoomMagnification();
				if (positiony>areaWhite.getMaxY()) break;
				g.drawLine((int)positionx, (int)positiony, (int)positionx+10, (int)positiony);
				g.drawString((i+1)+" in", (int)positionx+15, (int)positiony+g.getFont().getSize()/2);
				
			}
			
			positionx = areaWhite.getBounds().getMaxX();
			positiony = areaWhite.getBounds().getMinY();
			/**Draws the 1/4 inch markers*/	
			for(int i=0; i<80; i++) {
				positiony+=ImageDPIHandler.getStandardDPI()*this.getZoomer().getZoomMagnification()/4;
				if (positiony>areaWhite.getMaxY()) break;
				g.drawLine((int)positionx, (int)positiony, (int)positionx+5, (int)positiony);
				
				
			}
			g.drawLine((int)positionx, (int)areaWhite.getMinY(), (int)positionx, (int)areaWhite.getMaxY());
			
			
		}

		
		public Dimension getPreferredSize() {
			if (window==null||window.getTheSet()==null ||!window.usesScrollPane()) 
				return super.getPreferredSize();
			Rectangle b = this.getDisplaySetArea().getBounds();
			double w = b.getWidth()+60;
			double h = b.getHeight()+60;
			
			return new Dimension((int)w, (int)h);
		}
}
