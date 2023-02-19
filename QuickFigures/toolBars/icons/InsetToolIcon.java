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
package icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;

/**
 An icon for the inset tool. cosmetic improvement the old icon with few colors
 */
public class InsetToolIcon  extends GraphicToolIcon {
	




	protected boolean dash=true;
	
	Color dashColor1=new Color(250,250,250);
	Color dashColor2=new Color(250,250,250);
	Paint fillColor=new Color(150,150,150, 20);
	
	Color[] blotchColors=new Color[] {Color.blue, Color.green.darker(), Color.red.darker(), Color.yellow, new Color(150,150, 255), Color.magenta.darker(), Color.cyan.darker()};
	
	Color[] bigBlotchColors=new Color[] {Color.blue, Color.red.darker(), Color.red.darker(), Color.yellow};
	
	
	/**
	
	 */
	public InsetToolIcon(int type) {
		super(type);
		super.paintCursorIcon=false;
	}
	
	/**draws two letters in a black rectangle*/
	@Override
	public void paintObjectOntoIcon(Component arg0, Graphics arg10, int arg2, int arg3) {
		
		if (arg10 instanceof Graphics2D) {
		Graphics2D g2d = (Graphics2D) arg10;
		
	
		;
		int x = arg2;
		int y = arg3;
		int shrink=4;
		Rectangle2D.Double bigRect=new Rectangle2D.Double(x+shrink,y+shrink,this.getIconWidth()-shrink*2, this.getIconHeight()-shrink*2);

		

		Rectangle2D[] r2 = new Rectangle2D[] {
				createSmallRect(RectangleEdges.UPPER_RIGHT, bigRect),
				createSmallRect(RectangleEdges.RIGHT, bigRect),
				createSmallRect(RectangleEdges.LOWER_RIGHT, bigRect),
				createSmallRect(RectangleEdges.LEFT, bigRect)
		};
		
		
		
		int count=0;
		for(Rectangle2D r:r2)
			{	drawRectangle(g2d, r, true, count);
				count++;
			}
			
	
		
		
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(Color.black);
		g2d.draw(bigRect);
		g2d.setStroke(new BasicStroke(1));
		}
		
	
		}

	/**
	 * @param placement
	 * @param bigRect
	 * @return 
	 */
	public  Rectangle2D createSmallRect(int placement, Rectangle2D.Double bigRect) {
		AttachmentPosition a=new AttachmentPosition();
		Rectangle2D.Double smallRect=new Rectangle2D.Double(0,0, 6,6);
		a.doInternalSnapEdgePointToEdgePoint(placement, smallRect, bigRect);
		return smallRect;
	}

	/**
	 * @param g2d
	 * @param r
	 */
	public void drawRectangle(Graphics2D g2d, Rectangle2D r, boolean stroke, int count) {
		g2d.setPaint(getFillPaint(count));
		g2d.fill(r);
		
		 Color blotch = bigBlotchColors[count];
		 if(blotch!=null) {
				ColorBlotchForIcon c=new ColorBlotchForIcon(new Rectangle(5,1,2,5), blotch);
				c.blotchRadius=(float)8;
				c.paintBlotch(g2d, (int)r.getX(), (int)r.getY());
		 }
		
		 blotch = blotchColors[count];
		if(blotch!=null) {
			ColorBlotchForIcon c=new ColorBlotchForIcon(new Rectangle(3+count/3,3,3,3), blotch);
			c.paintBlotch(g2d, (int)r.getX(), (int)r.getY());
			 c=new ColorBlotchForIcon(new Rectangle(count,4,3,3), blotch);
			c.paintBlotch(g2d, (int)r.getX(), (int)r.getY());
			
			if(count>1) {
				blotch = blotchColors[count-1];
				c=new ColorBlotchForIcon(new Rectangle(1,1,2,2), blotch);
				c.paintBlotch(g2d, (int)r.getX(), (int)r.getY());
			}
			
			blotch = blotchColors[count+2];
			c=new ColorBlotchForIcon(new Rectangle(4,2,2,2), blotch);
			c.paintBlotch(g2d, (int)r.getX(), (int)r.getY());
		}
		
		
		
		int dashForm = 2;
		BasicStroke bs = new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12, new float[] {dashForm,dashForm}, 0);
		g2d.setColor(dashColor1);
		g2d.setStroke(bs);
		g2d.draw(r);
		
		bs = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12, new float[] {dashForm,dashForm}, dashForm);
		if (type==ROLLOVER_ICON_TYPE) {dashColor2=Color.gray;} else dashColor2=Color.white;
		g2d.setColor(dashColor2);
		g2d.setStroke(bs);
		g2d.draw(r);
		
		
	}

	/**
	 * @param count which paint to use
	 * @return
	 */
	Paint getFillPaint(int count) {
		return fillColor;
	}
	
	
	

	@Override
	public GraphicToolIcon copy(int type) {
		return new InsetToolIcon(type);
	}
	
}
