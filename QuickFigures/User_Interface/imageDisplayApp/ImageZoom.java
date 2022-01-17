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
 * Version: 2022.0
 */
package imageDisplayApp;

import graphicalObjects.BasicCoordinateConverter;
import utilityClasses1.NumberUse;

/**Image zoom. stores information about the zoom level and x/y shift of a canvas*/
public class ImageZoom {
	
	/**A list of possible magnification levels. */
	public static final double[] possibleMagnificationLevels=new double[] {
														0.01, 0.015,	0.02,	0.025, 0.05,0.06, 0.075, 0.1,0.12, 0.15, 0.2, 0.25, 0.33, 0.4,0.45, 0.50, 0.6, 0.75,0.8, 1, 1.2, 1.25, 1.5, 1.75, 2,2.25, 2.5, 3, 3.5, 4, 4.5, 5, 6, 7,8, 9, 10, 12,14,15,16,18,20};
	
	/**the magnification level*/
	private double zoom=1;//unmagnified is 1
	
	/**are used as displacement X, and Ys. used when window is NOT in scrollpane mode
	  they are needed for scroll positions*/
	private double x0=0;
	private double y0=0;
	
	/**the current magnification level*/
	public double getZoomMagnification() {
		return zoom;
	}
	
	
	/**returns the zoom level as a double but with only a limited number of digits*/
	public double getZoomAsFewDigits() {
		double factor=10000;
		
		int i=(int)(zoom*factor);
		return i/factor;
	}
	/**sets the zoom level. 1 indicates no zoom*/
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
	/**zooms in*/
	public void zoomIn() {
		zoom*=1.2;
		zoom=NumberUse.findNearest(zoom, possibleMagnificationLevels);
	}
	/**zooms out*/
	public void zoomOut() {
		zoom/=1.2;
		zoom=NumberUse.findNearest(zoom, possibleMagnificationLevels);
	}
	
	/**returns the coordinate converter that will transform the x/y cordinates of the
	  java component into figure coordinates*/
	public BasicCoordinateConverter getConverter() {
		return new BasicCoordinateConverter(getX0(),getY0(), getZoomMagnification());
	}

	/**when the canvas is not within a scroll pane, this sets the scroll location Y*/
	public void setScrollY(int i) {
		y0=i;	
	}
	/**when the canvas is not within a scroll pane, this sets the scroll location X*/
	void setScrollX(double x) {
		x0=x;
	}
	
	
	/**used when the canvas is not within a scroll pane. this returns scroll location X*/
	public double getX0() {
		return x0;
	}
	/**used when the canvas is not within a scroll pane. this sets the scroll location X*/
	public void setX0(double x0) {
		this.x0 = x0;
	}
	
	/**used when the canvas is not within a scroll pane. this returns scroll location Y*/
	public double getY0() {
		return y0;
	}
	/**used when the canvas is not within a scroll pane. this sets the scroll location Y*/
	public void setY0(double y0) {
		this.y0 = y0;
	}

	
}
