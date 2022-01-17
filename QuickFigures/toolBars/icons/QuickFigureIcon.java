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
 * Date Modified: Dec 5, 2021
 * Version: 2022.0
 */
package icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 An icon for the QuickFigure button. cosmetic improvement the old icon with few colors
 */
public class QuickFigureIcon  extends GraphicToolIcon {
	

	protected boolean dash=true;
	
	Color dashColor1=new Color(250,250,250, 100);
	Color dashColor2=new Color(250,250,250, 100);
	Paint fillColor=Color.black;
	Color plusColor = Color.green.darker();
	
	Color[] blotchColors=new Color[] {null, null, Color.yellow, Color.red, Color.green, new Color(150,150, 255)};
	
	
	public QuickFigureIcon() {
		super(0);
	}
	public QuickFigureIcon(int type) {
		super(type);
		
	}
	
	
	/**draws two letters in a black rectangle*/
	@Override
	public void paintObjectOntoIcon(Component arg0, Graphics arg10, int arg2, int arg3) {
		
		if (arg10 instanceof Graphics2D) {
		Graphics2D g2d = (Graphics2D) arg10;
		
		
	
		
		
		
		;
		int x = arg2+2;
		int y = arg3;
		int count=0;
		
		Rectangle2D[] r2 = getSeries1Rectangles( x, y);
		
		x-=1;
		
		Rectangle2D[] r3 = getSeries2Rectangles(x, y);
		
		
		for(Rectangle2D r:r2)
			{	drawRectangle(g2d, r, true, count);
				count++;
			}
			
		for(Rectangle2D r:r3)
			{drawRectangle(g2d, r, false, count);
			count++;
			}
		
		drawPlus(g2d, x, y);
		}
		
	
		}
	
	/**Draws a + mark in a colored circle
	 * @param g2d
	 * @param x
	 * @param y
	 */
	protected void drawPlus(Graphics2D g2d, int x, int y) {
		
		g2d.setColor(plusColor);
		Ellipse2D.Double s = new Ellipse2D.Double(x+12, y+6, 10,10);
		g2d.fill(s);
		g2d.setColor(Color.white);
		
		
		int plusSize=6;
		int plusThickness=2;
		g2d.fill(new Rectangle2D.Double(s.getCenterX()-plusSize/2, s.getCenterY()-plusThickness/2, plusSize, plusThickness));
		g2d.fill(new Rectangle2D.Double(s.getCenterX()-plusThickness/2, s.getCenterY()-plusSize/2,  plusThickness, plusSize));
		g2d.setStroke(new BasicStroke(1));
	}
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	protected Rectangle2D[] getSeries2Rectangles(int x, int y) {
		int size=7;
		int down=16;
		int move=size+1;
		Rectangle2D[] r3 = new Rectangle2D[] {
				new Rectangle2D.Double(x, y+down,size, size),
				new Rectangle2D.Double(x+move, y+down,size, size),
				new Rectangle2D.Double(x+move+move, y+down,size, size)
		};
		return r3;
	}
	/**
	 * @param size2
	 * @param x
	 * @param y
	 * @return
	 */
	protected Rectangle2D[] getSeries1Rectangles(int x, int y) {
		int size2=8;
		return new Rectangle2D[] {
				new Rectangle2D.Double(x, y,size2, size2),
				new Rectangle2D.Double(x+2, y+3,size2, size2),
				new Rectangle2D.Double(x+5, y+5,size2, size2)
		};
	}

	/**
	 * @param g2d
	 * @param r
	 */
	public void drawRectangle(Graphics2D g2d, Rectangle2D r, boolean stroke, int count) {
		g2d.setPaint(fillColor);
		g2d.fill(r);
		Color blotch = blotchColors[count];
		if(blotch!=null) {
			paintBlotchOnRectangle(g2d, r, count, blotch);
		}
		
		if (!stroke) return;
		int dashForm = 2;
		BasicStroke bs = new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12, new float[] {dashForm,dashForm}, 0);
		g2d.setColor(dashColor1);
		g2d.setStroke(bs);
		g2d.draw(r);
		
		bs = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12, new float[] {dashForm,dashForm}, dashForm);
		g2d.setColor(dashColor2);
		g2d.setStroke(bs);
		g2d.draw(r);
		
		
	}
	
	/**paints the blotches which are dabs of color to make the rectangles resemble images rather than plain color
	 * @param g2d
	 * @param r
	 * @param count
	 * @param blotch
	 */
	protected void paintBlotchOnRectangle(Graphics2D g2d, Rectangle2D r, int count, Color blotch) {
		ColorBlotchForIcon c=new ColorBlotchForIcon(new Rectangle(count/2,1,3,3), blotch);
		c.paintBlotch(g2d, (int)r.getX(), (int)r.getY());
		 c=new ColorBlotchForIcon(new Rectangle(count,3,1,3), blotch);
		c.paintBlotch(g2d, (int)r.getX(), (int)r.getY());
	}
	
	
	/**when given 1 or two color arguments, this sets up the colors for the two letters shown
	  if 1 color is given, both letters will be given the same color*/
	public void setColors(Color... c){
		if (c==null||c.length==0) return;
		 dashColor1=c[0];
		if (c.length==1) dashColor2=c[0];
		else  dashColor2=c[1];
	}

	@Override
	public GraphicToolIcon copy(int type) {
		return new QuickFigureIcon(type);
	}
	
}
