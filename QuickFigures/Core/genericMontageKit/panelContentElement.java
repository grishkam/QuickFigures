package genericMontageKit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import applicationAdapters.PixelWrapper;
import graphicalObjects.BasicCordinateConverter;
import graphicalObjects.ZoomableGraphic;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.RectangleEdges;

/**a  class for storing the image data and the objects within a panel temporarily
  It is used mostly within another class and was originally written 
  as a nested class. */
public class panelContentElement {
	public panelContentElement(Rectangle2D r) {
		bounds=r;
	} 
	
		public panelContentElement(Dimension dim) {
		bounds =new Rectangle(0,0, dim.width, dim.height);
	}

		public PixelWrapper ip;
		private ArrayList<LocatedObject2D> obs;
		Rectangle2D bounds;
		
		public void nudgeObjects(double xmov, double ymov) {
			if (obs==null) return;
			for(LocatedObject2D loc:obs) {
				if (loc==null) continue;
				loc.moveLocation((int)xmov, (int)ymov);
			}
		}
		
		public Dimension dim() {
			
			return new Dimension((int)bounds.getWidth(), (int)bounds.getHeight());
		}
		
		/**returns a buffered image with this contents. great */
		private BufferedImage getImage() {
			BufferedImage output=null;
			
			
			if (output==null) {output=new BufferedImage(dim().width, dim().height, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics g = output.getGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, dim().width, dim().height);
			if (ip!=null) try{g.drawImage(ip.image(), 0, 0, null) ; } catch  (Throwable t) {}
			};
			BasicCordinateConverter cords = new BasicCordinateConverter();
			Graphics g = output.getGraphics();
			for(LocatedObject2D o: obs) {
				if (o instanceof ZoomableGraphic && g instanceof Graphics2D) {
					ZoomableGraphic z=(ZoomableGraphic) o;
					z.draw((Graphics2D) g, cords);
				}
			}
			
			return output;
		}
		
		/**returns a thumbnail*/
		public Image getFittedImage(Dimension d) {
			 BufferedImage i=getImage();
			Rectangle2D fit = RectangleEdges.fit(bounds, d.width, d.height);
			return i.getScaledInstance((int)fit.getWidth(), (int)fit.getHeight(), Image.SCALE_FAST);
		}
		
		/**returns a rectangle that bounds all the contents of the panel.
		  not the same thing as the panel bounds as a panel may contain objects
		  that extends beyond its area*/
		public Rectangle getAreaSpannelByContents() {
			return ArrayObjectContainer.combineOutLines(obs).getBounds();
		}
		
		/**returns a rectangle that bounds all the contents of the panel.
		  not the same thing as the panel bounds as a panel may contain objects
		  that extends beyond its area. This does not count space below (0,0)*/
		public Rectangle getAreaSpannelByContents2() {
			Rectangle c = getAreaSpannelByContents();
			if (c.x<0) {c.width+=c.x; c.x=0;}
			if (c.y<0) {c.height+=c.y; c.y=0;}
			return c;
		}
		
		
		/**returns true if there are any objects*/
		public boolean hasObjects() {
			return obs.size()>0;
		}

		public ArrayList<LocatedObject2D> getObjectList() {
			return obs;
		}

		public void setObjectList(ArrayList<LocatedObject2D> obs) {
			this.obs = obs;
		}
	}

