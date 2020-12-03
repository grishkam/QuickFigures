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
package utilityClassesForObjects;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Snap2Rectangle {
	
	public static   Point2D.Double[] RectangleVertices(Rectangle2D rect) {
		 Point2D.Double[] output = new Point2D.Double[4];
		 output[0]=new Point2D.Double(rect.getX(), rect.getY());
		 output[1]=new Point2D.Double(rect.getX()+rect.getWidth(), rect.getY());
		 output[2]=new Point2D.Double(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight());
		 output[3]=new Point2D.Double(rect.getX(), rect.getY()+rect.getHeight());
		 return output;
	  }

	
	public static Point snapBoundsSide(Rectangle r1, Rectangle r2, int closeness) {
		//int closeness=20;
		
		int xclose = r1.width/closeness;
		int yclose = r1.height/closeness;
		
		Point p=new Point(r1.x, r1.y);
		if (Math.abs(r1.x-r2.x)< xclose) p.x=r2.x;// snaps the left side
		else if (Math.abs(r1.getCenterX()-r2.getCenterX())<xclose) {
			p.x=(int)(r2.getCenterX()-r1.getWidth()/2);
		}
		else if (Math.abs(r1.x+r1.width-r2.x-r2.width)<xclose) p.x=r2.x+r2.width-r1.width;// snaps the right side
		
		
		
		if (Math.abs(r1.y-r2.y)<yclose ) p.y=r2.y;// snaps the top side
		else if (Math.abs(r1.getCenterY()-r2.getCenterY())<yclose ) {
			p.y=(int)(r2.getCenterY()-r1.getHeight()/2);//snaps to center y
		}
		else if (Math.abs(r1.y+r1.height-r2.y-r2.height)<yclose ) p.y=r2.y+r2.height-r1.height;// snaps the bottom side
		
		
		/**snaps the external side*/
		if (Math.abs(r1.x+r1.width-r2.x) <xclose ){ p.x=r2.x-r1.width;}
		if (Math.abs(r1.x-r2.x-r2.width) <xclose ) {p.x=r2.x+r2.width;}
		if (Math.abs(r1.y+r1.height-r2.y)<yclose ) {p.y=r2.y-r1.height;}
		if (Math.abs(r1.y-r2.y-r2.height)<yclose ) {p.y=r2.y+r2.height;}
		
		
		
		
		return p;
	}
	


}
