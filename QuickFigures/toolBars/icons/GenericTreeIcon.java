/**
 * Author: Greg Mazo
 * Date Modified: Dec 23, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Icon;

import iconGraphicalObjects.IconTraits;

/**
 
 * 
 */
public class GenericTreeIcon  implements Icon, IconTraits{
	int height = TREE_ICON_HEIGHT;
	int width = TREE_ICON_WIDTH;
	protected boolean dash=true;
	
	Color dashColor1=Color.white;
	Color dashColor2=Color.black;
	Color fillColor=Color.black;
	
	
	@Override
	public int getIconHeight() {
	
		return height;
	}

	@Override
	public int getIconWidth() {
		
		return width;
	}
	
	/**draws two letters in a black rectangle*/
	@Override
	public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
		
		if (arg1 instanceof Graphics2D) {
		Graphics2D g2d = (Graphics2D) arg1;
		Rectangle r = new Rectangle(arg2, arg3, width,height);
		
		arg1.setColor(fillColor);
		g2d.fill(r);
		
		int dashForm = 2;
		BasicStroke bs = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12, new float[] {dashForm,dashForm}, 0);
		arg1.setColor(dashColor1);
		g2d.setStroke(bs);
		g2d.draw(r);
		
		bs = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 12, new float[] {dashForm,dashForm}, dashForm);
		arg1.setColor(dashColor2);
		g2d.setStroke(bs);
		g2d.draw(r);
		}
		}
	
	
	/**when given 1 or two color arguments, this sets up the colors for the two letters shown
	  if 1 color is given, both letters will be given the same color*/
	public void setColors(Color... c){
		if (c==null||c.length==0) return;
		 dashColor1=c[0];
		if (c.length==1) dashColor2=c[0];
		else  dashColor2=c[1];
	}
	
}
