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
 * Version: 2022.1
 */
package locatedObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**contains useful methods*/
public class Snap2Rectangle {
	
	public static   Point2D.Double[] RectangleVertices(Rectangle2D rect) {
		 Point2D.Double[] output = new Point2D.Double[4];
		 output[0]=new Point2D.Double(rect.getX(), rect.getY());
		 output[1]=new Point2D.Double(rect.getX()+rect.getWidth(), rect.getY());
		 output[2]=new Point2D.Double(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight());
		 output[3]=new Point2D.Double(rect.getX(), rect.getY()+rect.getHeight());
		 return output;
	  }

	/**If one rectangle is near the edge of another, returns
	  an altered version of the location in which the item location is snapped against
	  the parent panel
	  */
	public static Point snapBoundsSide(Rectangle movedItem, Rectangle nearbyPanel, int closeness) {
		//int closeness=20;
		
		int xclose = movedItem.width/closeness;
		int yclose = movedItem.height/closeness;
		
		Point p=new Point(movedItem.x, movedItem.y);
		if (Math.abs(movedItem.x-nearbyPanel.x)< xclose) p.x=nearbyPanel.x;// snaps the left side
		else if (Math.abs(movedItem.getCenterX()-nearbyPanel.getCenterX())<xclose) {
			p.x=(int)(nearbyPanel.getCenterX()-movedItem.getWidth()/2);
		}
		else if (Math.abs(movedItem.x+movedItem.width-nearbyPanel.x-nearbyPanel.width)<xclose) p.x=nearbyPanel.x+nearbyPanel.width-movedItem.width;// snaps the right side
		
		
		
		if (Math.abs(movedItem.y-nearbyPanel.y)<yclose ) p.y=nearbyPanel.y;// snaps the top side
		else if (Math.abs(movedItem.getCenterY()-nearbyPanel.getCenterY())<yclose ) {
			p.y=(int)(nearbyPanel.getCenterY()-movedItem.getHeight()/2);//snaps to center y
		}
		else if (Math.abs(movedItem.y+movedItem.height-nearbyPanel.y-nearbyPanel.height)<yclose ) p.y=nearbyPanel.y+nearbyPanel.height-movedItem.height;// snaps the bottom side
		
		
		/**snaps the external side*/
		if (Math.abs(movedItem.x+movedItem.width-nearbyPanel.x) <xclose ){ p.x=nearbyPanel.x-movedItem.width;}
		if (Math.abs(movedItem.x-nearbyPanel.x-nearbyPanel.width) <xclose ) {p.x=nearbyPanel.x+nearbyPanel.width;}
		if (Math.abs(movedItem.y+movedItem.height-nearbyPanel.y)<yclose ) {p.y=nearbyPanel.y-movedItem.height;}
		if (Math.abs(movedItem.y-nearbyPanel.y-nearbyPanel.height)<yclose ) {p.y=nearbyPanel.y+nearbyPanel.height;}
		
		
		
		
		return p;
	}
	


}
