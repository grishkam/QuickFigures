/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package imageDisplayApp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

/**although there is no user option for this presently. A programmer can create a version of the 
window that does not use a scroll pane but instead indicates the position 
in the same way as imageJ does. in that case a scroll indicator need be drawn.
using this class
*/
public class ScrollIndicator implements ZoomableGraphic{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicSetDisplayWindow display;
	
	/**the size of the scroll indicator compared to the images*/
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
		return getDisplay().getZoomer().getZoomMagnification();
	}
	
	/**draws too rectangles to indicated the size and position of the view ara compared to the entire canvas*/
	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
		
		/**if the entire view area contains the entire figure canvas does not need to draw a scroll indicator*/
		if ( areRectsSame() ) return;
		
		
		graphics.setColor(Color.blue);
		graphics.setStroke(new BasicStroke(1));
		graphics.draw(getTotalRect());
		
		graphics.setColor(Color.green);
		graphics.setStroke(new BasicStroke(1));
		graphics.draw(getInnerRect());
		
	}
	
	/**if true if the rectangles are identical*/
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
	/**method from interface, not critical to the function of this class*/
	@Override
	public GraphicLayer getParentLayer() {
		return layer;
	}
	/**method from interface, not critical to the function of this class*/
	@Override
	public void setParentLayer(GraphicLayer parent) {
		layer=parent;
		
	}

}
