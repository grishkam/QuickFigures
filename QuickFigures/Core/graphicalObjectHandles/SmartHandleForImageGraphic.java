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
package graphicalObjectHandles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import graphicalObjects.CordinateConverter;
import graphicalObjects_BasicShapes.RectangularGraphic;
import utilityClassesForObjects.RectangleEdges;

/**work in progress smart handle for image graphics*/
public class SmartHandleForImageGraphic extends SmartHandle {

	int position=0;
	RectangularGraphic rectangle=null;
	

	
	public SmartHandleForImageGraphic( RectangularGraphic rect, int position) {
	
		this.rectangle=rect;
		this.position=position;
		
	}

	public Point2D getCordinateLocation() {
		Point2D p = RectangleEdges.getLocation(position, rectangle.getBounds());
		return p;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {

		if (rectangle.getLocationType()==position)this.setHandleColor(Color.red); else
			this.setHandleColor(Color.black);
	//	
		 super.draw(graphics, cords);
	}

}
