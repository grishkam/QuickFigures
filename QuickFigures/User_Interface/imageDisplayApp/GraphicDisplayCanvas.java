/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 12, 2021
 * Version: 2021.1
 */
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
import appContext.RulerUnit;
import graphicalObjects.CordinateConverter;
import graphicalObjects_LayerTypes.GraphicLayer;
import handles.HasSmartHandles;
import locatedObject.Selectable;
import logging.IssueLog;

/**A component that displays the figures and objects within the display window
  All objects/figures in the worksheet are drawn here. 
  Rulers are also drawn to  indicate size of the figure
  @see GraphicSetDisplayWindow for information on where
  */
public class GraphicDisplayCanvas extends JComponent {

	/**
	 specifies how many extra pixels are needed on the bottom and right (they serve as space for the rulers)
	 */
	private static final int ADDITIONAL_SIZE = 60;
	
	
	/**
	 These numbers define the ruler appearance
	 */
	static final int INCH_MARK_SIZE = 10;
	static final int  FRACTION_OF_INCH_MARK_SIZE = 5;
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicSetDisplayWindow window;
	
	public GraphicDisplayCanvas() {
		super();
	
	}

	
	public void setDispWindow(GraphicSetDisplayWindow window) {
		this.window=window;
		
	}
	
	
	/**draws the component*/
	public void paintComponent(Graphics g) {

		if (g instanceof Graphics2D) {
				 Graphics2D g2 = (Graphics2D)g;
			
			
			
			
			drawWhiteCanvas(g2);
			
			
			
			if (window==null||window.getTheSet()==null) {IssueLog.log("warning, window null");}
			 
			drawObjectsAndRulers(g2);
		
	}
		
		
		;
}


	/**Draws every object in every layer
	 * @param g2
	 */
	void drawObjectsAndRulers(Graphics2D g2) {
		StandardWorksheet gmp = window.getTheSet();
		/**draws all of objects*/
		if (window!=null&&window.getTheSet()!=null) {
		
			
			GraphicLayer layerSet = window.getTheSet().getTopLevelLayer();
			CordinateConverter conv1 = getConverter();
			
			layerSet.draw(g2, conv1);
			
			gmp.getOverlaySelectionManagger().drawSelections(g2, getConverter());
			
			/**although there is no user option for this. a programmer can create a version of the 
			  window that does not use a scroll pane but instead indicates the position 
			  in the same way as imageJ does. in that case an indicator need be drawn*/
			if (!window.usesScrollPane())
				window.indicator.draw(g2, getConverter());
			
			
			 drawRulers(g2);
			 
			 drawSmartHandlesForSelectedItem(g2, conv1);
			
			 
		}
	}





	/**
	 Draws the canvas
	 */
	void drawWhiteCanvas(Graphics2D g2) {
		/**Fills the canvas with a greyish gradient.
		  After the white of the canvas is drawn,
		  the parts of the canvas object beyond the display canvas area will be greyish
		 */
		g2.setPaint(getGrayFillPaint());
		Rectangle r = new Rectangle(-1, -1, this.getWidth(), this.getHeight());
		Shape da = getDisplaySetArea();
		Area greyArea = (new Area(r));
		greyArea.subtract(new Area(da));;
		g2.fill(greyArea);
		
		
		
		/**fills the display canvas area with white so that user knows this is where they draw*/
		g2.setColor(Color.white);
		g2.fill( da);
		g2.setColor(Color.black);
		g2.drawRect(-1, -1, this.getWidth(), this.getHeight());
	}


	/**if a single item has be set as the primary selected item, any smart handles it has
	 * will be drawn over th other objects*/
	protected void drawSmartHandlesForSelectedItem(Graphics2D g2, CordinateConverter conv1) {
		Selectable sel = window.getDisplaySet().getSelectedItem();
		if (sel!=null&& sel instanceof HasSmartHandles) {
			 HasSmartHandles h=(HasSmartHandles) sel;
			 h.getSmartHandleList().draw(g2, conv1);
		}
	}
	
	
	/**Returns the area of the component where the white canvas is drawn*/
	public Shape getDisplaySetArea() {
		StandardWorksheet gmp = window.getTheSet();
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
	
	/**returns the cordinate system used*/
	public CordinateConverter getConverter() {
		return window.getZoomer().getConverter();
	}
	
	




		private ImageZoom getZoomer() {
			return this.window.getZoomer();
		}
	
		public Point2D getCenterOfZoom() {
			double canWidth=this.getCanvasWidthInUnits();//etTheCanvas().getWidth()/mag;
			double canHeight=this.getCanvasHeightInUnits();//getTheCanvas().getHeight()/mag;
			double xcent = getZoomer().getX0()+canWidth/2;
			double ycent = getZoomer().getY0()+canHeight/2;
			
			return new Point2D.Double(xcent, ycent);
		}
		
		/**returns how many units of coordinate space 
		 * are depicted across the width of the component*/
		double getCanvasWidthInUnits() {
			double mag=getZoomer().getZoomMagnification();
			return getWidth()/mag;
		}
		
		/**returns how many units of coordinate space 
		 * are dipicted across the height of the component*/
		double getCanvasHeightInUnits() {
			double mag=getZoomer().getZoomMagnification();
			return getHeight()/mag;
		}
		
		
		public void centerZoomAtPoint(Point2D p) {
			Point2D pcent = getCenterOfZoom();
			double xs = p.getX()-pcent.getX();
			double ys = p.getY()-pcent.getY();
			this.getZoomer().scroll(xs, ys);
		}
		
		/**returns the paint that fills the area beyond the canvas*/
		public Paint getGrayFillPaint() {
			
			GradientPaint output = new GradientPaint(new Point(0,0), Color.darkGray, new Point(5,5), Color.gray, true);
			
			
			return output;
		}
		
		
		/**Draws rulers so that each inch is 72 units*/
		protected void drawRulers(Graphics2D g) {
			Rectangle areaWhite = getDisplaySetArea().getBounds();
			
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(2));
			
			
			double positionx = areaWhite.getMinX();
			double positiony = areaWhite.getMaxY();
			g.setFont(new Font("Arial", Font.BOLD, getRulerUnit().getFontSize()));
			
				/**Draws the inch markers*/	
			for(int i=0; i<20; i++) {
				positionx+=getRulerUnit().getUnitSize()*this.getZoomer().getZoomMagnification();
				if (positionx>areaWhite.getMaxX()) break;
				int markSize=10;
				g.drawLine((int)positionx, (int)positiony, (int)positionx, (int)positiony+markSize);
				
				String str = (i+1)+getRulerUnit().getLabel();
				int shift=10;
				
				if (getRulerUnit().getUnitSize()*this.getZoomer().getZoomMagnification()<50) { str=""+(i+1);shift=4;}//so that the cm text does not overlap the numbers
				
				g.drawString(str, (int)positionx-shift, (int)positiony+markSize+g.getFont().getSize());
				
			}
			
			positionx = areaWhite.getBounds().getMinX();
			positiony = areaWhite.getBounds().getMaxY();
			
			/**Draws the 1/4 inch markers for horizontal ruler*/	
			for(int i=0; i<getRulerUnit().getMaxMark()*getRulerUnit().getFractionMark(); i++) {
				positionx+=getRulerUnit().getUnitSize()*this.getZoomer().getZoomMagnification()/getRulerUnit().getFractionMark();
				if (positionx>areaWhite.getMaxX()) break;
				g.drawLine((int)positionx, (int)positiony, (int)positionx, (int)positiony+FRACTION_OF_INCH_MARK_SIZE);
				
			}
			g.drawLine((int)areaWhite.getMinX(), (int)positiony, (int)areaWhite.getMaxX(), (int)positiony);
			
			
			
			/**now for vertical ruler*/
			positionx = areaWhite.getBounds().getMaxX();
			positiony = areaWhite.getBounds().getMinY();
			
			/**Draws the inch markers for the vertical ruler*/	
			for(int i=0; i<getRulerUnit().getMaxMark(); i++) {
				positiony+=getRulerUnit().getUnitSize()*this.getZoomer().getZoomMagnification();
				if (positiony>areaWhite.getMaxY()) break;
				g.drawLine((int)positionx, (int)positiony, (int)positionx+INCH_MARK_SIZE, (int)positiony);
				g.drawString((i+1)+getRulerUnit().getLabel(), (int)positionx+15, (int)positiony+g.getFont().getSize()/2);
				
			}
			
			positionx = areaWhite.getBounds().getMaxX();
			positiony = areaWhite.getBounds().getMinY();
			/**Draws the 1/4 inch markers for the vertical ruler*/	
			for(int i=0; i<getRulerUnit().getMaxMark()*getRulerUnit().getFractionMark(); i++) {
				positiony+=getRulerUnit().getUnitSize()*this.getZoomer().getZoomMagnification()/getRulerUnit().getFractionMark();
				if (positiony>areaWhite.getMaxY()) break;
				g.drawLine((int)positionx, (int)positiony, (int)positionx+FRACTION_OF_INCH_MARK_SIZE, (int)positiony);
				
				
			}
			g.drawLine((int)positionx, (int)areaWhite.getMinY(), (int)positionx, (int)areaWhite.getMaxY());
			
			
		}

		/**returns the preferred size of the component*/
		public Dimension getPreferredSize() {
			if (window==null||window.getTheSet()==null ||!window.usesScrollPane()) 
				return super.getPreferredSize();
			Rectangle b = this.getDisplaySetArea().getBounds();
			double w = b.getWidth()+ADDITIONAL_SIZE;
			double h = b.getHeight()+ADDITIONAL_SIZE;
			
			return new Dimension((int)w, (int)h);
		}


		public static RulerUnit getRulerUnit() {
			return ImageDPIHandler.getRulerUnit();
		}
}
