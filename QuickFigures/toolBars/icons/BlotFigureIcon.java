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
 * Date Created: Dec 5, 2021
 * Date Modified: Dec 5, 2021
 * Version: 2021.2
 */
package icons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 An icon for the blot figure button
 */
public class BlotFigureIcon extends QuickFigureIcon {
	
	
	public BlotFigureIcon(int type) {
		super(type);
		super.fillColor=Color.white;
		dashColor1=new Color(20,20,20);
		 dashColor2=new Color(20,20,20);
		 blotchColors=new Color[] {Color.lightGray, Color.black, Color.darkGray, Color.red, Color.green, new Color(150,150, 255)};
		 plusColor = Color.green.darker().darker();
	}
	
	/**not implemented for blot figures
	 * @param x
	 * @param y
	 * @return
	 */
	protected Rectangle2D[] getSeries2Rectangles(int x, int y) {
		int size=6;
		int down=1;
		int move=size+1;
		int width=size*3;
		
		Rectangle2D[] r3 = new Rectangle2D[] {
				
		};
		return r3;
	}
	/**
	 * @param size2
	 * @param x
	 * @param y
	 * @return
	 */
	protected Rectangle2D[] getSeries1Rectangles( int x, int y) {
		int size=5;
		int down=3;
		int move=size+1;
		int width=size*4;
		int x1 = x+1;
		int y1 = y+1;
		
		Rectangle2D[] r3 = new Rectangle2D[] {
				new Rectangle2D.Double(x1, y1+down,width, size),
				new Rectangle2D.Double(x1, y1+down+move,width, size),
				new Rectangle2D.Double(x1, y1+down+2*move,width, size)
		};
		return r3;
	}

	@Override
	public GraphicToolIcon copy(int type) {
		return new BlotFigureIcon(type);
	}
	
	/**paints the blotches which are bands of color to make the rectangles resemble blots rather than plain color
	 * @param g2d
	 * @param r
	 * @param count
	 * @param blotch
	 */
	protected void paintBlotchOnRectangle(Graphics2D g2d, Rectangle2D r, int count, Color blotch) {
		
		for(int i=0; i<3; i++) {
			int shift = 2+i*6;
			Color blotchcolor = blotch;
			if(i==1)
				blotchcolor=Color.darkGray;//some variability in the lanes
			ColorBlotchForIcon c=new ColorBlotchForIcon(new Rectangle(shift,2,4,2), blotchcolor);
			c.blotchRadius=(float) 12;
			c.paintBlotch(g2d, (int)r.getX(), (int)r.getY());
			
				
		}
	}
	
	protected void drawPlus(Graphics2D g2d, int x, int y) { 
		if(this.type!=GraphicToolIcon.NORMAL_ICON_TYPE)
			super.drawPlus(g2d, x, y);
	}
	
}
