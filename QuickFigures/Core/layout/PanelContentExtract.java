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
 * Date Modified: Jan 5, 2021
 * Version: 2021.1
 */
package layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects.ZoomableGraphic;
import locatedObject.ArrayObjectContainer;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;

/**a  class for storing the image and the objects within a panel.
  It is used mostly within another class and was originally written 
  as a nested class. */
public class PanelContentExtract {
	
	/**a list of objects that are considered part of the panel*/
	private ArrayList<LocatedObject2D> objectList;
	/**bounding box*/
	Rectangle2D bounds;
	
	public PanelContentExtract(Rectangle2D r) {
		bounds=r;
	} 
	
	public PanelContentExtract(Dimension dim) {
		bounds =new Rectangle(0,0, dim.width, dim.height);
	}

		
	/**moves all objects*/
		public void nudgeObjects(double xmov, double ymov) {
			if (objectList==null) return;
			for(LocatedObject2D loc:objectList) {
				if (loc==null) continue;
				loc.moveLocation((int)xmov, (int)ymov);
			}
		}
		
		/**Returns the dimensions of the object panel*/
		public Dimension dim() {
			return new Dimension((int)bounds.getWidth(), (int)bounds.getHeight());
		}
		
		/**returns a buffered image that shows the extrcted content*/
		private BufferedImage getImage() {
			BufferedImage output=null;
			
			
			if (output==null) {output=new BufferedImage(dim().width, dim().height, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics g = output.getGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, dim().width, dim().height);
		
			};
			BasicCoordinateConverter cords = new BasicCoordinateConverter();
			Graphics g = output.getGraphics();
			for(LocatedObject2D o: objectList) {
				if (o instanceof ZoomableGraphic && g instanceof Graphics2D) {
					ZoomableGraphic z=(ZoomableGraphic) o;
					z.draw((Graphics2D) g, cords);
				}
			}
			
			return output;
		}
		
		/**returns a small thumbnail that fits into d. I plan to eventually include a user interface feature
		  that shows previews of multiple parts to the user*/
		public Image getFittedImage(Dimension d) {
			 BufferedImage i=getImage();
			Rectangle2D fit = RectangleEdges.fit(bounds, d.width, d.height);
			return i.getScaledInstance((int)fit.getWidth(), (int)fit.getHeight(), Image.SCALE_FAST);
		}
		
		/**returns a rectangle that bounds all the contents of the panel.
		  not the same thing as the panel bounds as a panel may contain objects
		  that extends beyond its area*/
		public Rectangle getAreaSpannelByContents() {
			return ArrayObjectContainer.combineOutLines(objectList).getBounds();
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
		
		
		/**returns true if there are any objects in the panel*/
		public boolean hasObjects() {
			return objectList.size()>0;
		}

		/**getter method for the objects in this panel*/
		public ArrayList<LocatedObject2D> getObjectList() {
			return objectList;
		}
		/**setter method for the objects in this panel*/
		public void setObjectList(ArrayList<LocatedObject2D> obs) {
			this.objectList = obs;
		
		}
	}

