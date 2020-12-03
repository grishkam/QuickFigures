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
package genericMontageKit;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import applicationAdapters.ImageWrapper;

/**This interface contains all methods that are crucial for any type of panel layout
  Although I have written a few implementations of this, the basic montage layout */
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
	 
	 /**returns true if panel number panel does not use the standard panel size for the layout*/
	 public boolean doesPanelUseUniqueWidth(int panel);
	 public boolean doesPanelUseUniqueHeight(int panel);

	 /**called when a user drags the center handle of the panel*/
	public void nudgePanel(int panelnum, double dx, double dy);
	/**called when a user drags a corner handle of the panel*/
	public void nudgePanelDimensions(int panelnum, double dx, double dy);
	
	public ImageWrapper getWrapper() ;

	public int nPanels();

	public void setWrapper(ImageWrapper genericImage);
	 
}
