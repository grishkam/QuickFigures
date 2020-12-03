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
package graphicalObjects_BasicShapes;

import java.awt.Shape;
import java.awt.geom.Point2D;

import utilityClassesForObjects.PathPointList;

/**A Rhomboid shape */
public class RhombusGraphic extends RectangularGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double angleBend=0;
	
	@Override
	public Shape getShape() {
		PathPointList p = new PathPointList();
		for(int i=0; i<4; i++) p.addPoint(getVertexPoint(i));
		
		return p.createPath(true);
		
	}
	
	
	/**returns a point along the rhombus. */
	public Point2D getVertexPoint(int i) {
		if (i==1) return  new Point2D.Double(x+getObjectWidth(),y);
		if (i==2) return  new Point2D.Double(x+getObjectWidth()+Math.tan(getAngleBend())*getObjectHeight(),y+getObjectHeight());
		if (i==3) return  new Point2D.Double(x+Math.tan(getAngleBend())*getObjectHeight(),y+getObjectHeight());
		return new Point2D.Double(x,y);
		
		
		
		
	}


	public double getAngleBend() {
		return angleBend;
	}


	public void setAngleBend(double angleBend) {
		this.angleBend = angleBend;
	}
	

}
