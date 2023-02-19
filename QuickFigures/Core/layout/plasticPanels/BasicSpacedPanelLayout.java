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
 * Date Modified: Jan 4, 2021
 * Version: 2023.1
 */
package layout.plasticPanels;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashMap;

/**superclass for layouts consisting of panels of many different sizes that can be aligned 
 * with even spacing*/
public abstract class BasicSpacedPanelLayout extends FundamentalPanelLayout implements Serializable, SpacedPanelLayout {
	private int horizontalBorder=10;
	private int verticalBorder=10;
	
	NeighborFinder<PlasticPanel> finder=null;
	

	public int getHorizontalBorder() {
		return horizontalBorder;
	}
	public int getVerticalBorder() {
		return verticalBorder;
	}

	public void setHorizontalBorder(int horizontalBorder) {
		this.horizontalBorder = horizontalBorder;
	}


	public void setVerticalBorder(int verticalBorder) {
		this.verticalBorder = verticalBorder;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	


	
	
	
	
	private double getVRange() {
		return getVerticalBorder()*2.5+1;
	}
	
	private double getHRange() {
		return getHorizontalBorder()*2.5+1;
	}
	
	public NeighborFinder<PlasticPanel> getneighborFinder() {
		if (finder==null)finder= new NeighborFinder<PlasticPanel>(this, getHRange() , getVRange());
		finder.setHRange(getHRange() );
		finder.setVRange( getVRange());
		return finder;
	}
	
	public void autoLocatePanels() {
		Rectangle2D[] pan=getPanels();
		HashMap<Rectangle2D, Rectangle2D> lmap = this.getneighborFinder().getLeftNeighborMap(pan);
		HashMap<Rectangle2D, Rectangle2D> amap = this.getneighborFinder().getUpNeighborMap(pan);
		
		for(Rectangle2D p:pan) {
			Rectangle pnew = p.getBounds();
			getneighborFinder().autoLocatePanel(pnew, this.getHorizontalBorder(), this.getVerticalBorder(), amap.get(p), lmap.get(p));
			p.setRect(pnew);
		}
	}
	
	/***/
	public Rectangle autoLocatePanel(Rectangle p) {
		return this.getneighborFinder().autoLocatePanel(p, this.getHorizontalBorder(), this.getVerticalBorder());
	
	}

	

	
	

	

}
