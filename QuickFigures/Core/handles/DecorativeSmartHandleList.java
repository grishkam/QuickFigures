/**
 * Author: Greg Mazo
 * Date Modified: Dec 27, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package handles;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 a handle list that draws a specified number of handles at certain locations
 these are simple handles that are not coded to do anything other than indicate
 their locations
 */
public class DecorativeSmartHandleList extends SmartHandleList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public  DecorativeSmartHandleList(ArrayList<Point2D> points) {
		setPoints(points);
	}
	
	public  DecorativeSmartHandleList() {
	
	}

	/**
	 * @param points
	 */
	public void setPoints(ArrayList<Point2D> points) {
		/***/
		if(points.size()!=this.size()) {
			this.clear();
			for(int i=0; i<points.size(); i++) {
				this.addNewPoint(points.get(i), i+1);
			}
		}
		else {
			for(int i=0; i<points.size(); i++) {
				this.get(i).setCordinateLocation(points.get(i));
			}
		}
		
	}

	/**Adds a new point to this list
	 * @param point2d
	 */
	private void addNewPoint(Point2D point2d, int handleID) {
		SmartHandle p = new SmartHandle();
		p.setCordinateLocation(point2d);
		p.setHandleNumber(handleID);
		p.handlesize*=0.66;
		add(p);
	}

}
