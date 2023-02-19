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
 * Version: 2023.1
 */
package layout.plasticPanels;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import applicationAdapters.ImageWorkSheet;
import layout.PanelLayout;

/**A very basic panel layout superclass that other classes can extend*/
public abstract class FundamentalPanelLayout implements PanelLayout, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Point location=new Point();
	private transient ImageWorkSheet wrapper;
	
	@Override
	public Point2D getReferenceLocation() {
		return location;
	}
	
	
	@Override
	public Shape allPanelArea() {
		Area output = new Area();
		for(int i=0; i<nPanels(); i++) {
			Rectangle2D panel = getPanel(i+1);
			if (panel!=null) output.add(new Area(panel));
		}
		return output;
	}
	
	

	@Override
	public Shape getBoundry() {
		return allPanelArea().getBounds();
	}
	
	/**returns the location of the a panel
	 * @param index the index of the panel*/
	@Override
	public Point2D getPoint(int index) {
		Rectangle2D panel = this.getPanel(index);
		
		return  new Point2D.Double(panel.getX(), panel.getY());
	}

	/**returns the nearest panel*/
	@Override
	public Rectangle2D getNearestPanel(double d, double e) {
		PanelOperations<Rectangle2D> pops = new PanelOperations<Rectangle2D> ();
		return pops.getNearestPanel(this.getPanels(), d, e);
	}
	
	/**returns the  panels*/
	@Override
	public Rectangle2D[] getPanels() {
		Rectangle2D[] p = new Rectangle2D[nPanels()] ;
		for(int i=0; i<nPanels(); i++) {
			p[i]=getPanel(i+1);
		}
		return p;
	}
	
	/**returns the locations of the panels*/
	@Override
	public Point2D[] getPoints() {
		int size = getPanels().length;
		Point2D[] p = new Point2D[size] ;
		for(int i=0; i<size; i++) {
			p[i]=getPoint(i+1);
		}
		return p;
	}
	@Override
	public ImageWorkSheet getVirtualWorksheet() {
		return wrapper;
	}

	public void setVirtualWorkSheet(ImageWorkSheet wrapper) {
		this.wrapper = wrapper;
	}
	

}
