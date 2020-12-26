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

/**
 
 * 
 */
public class LayoutIcon extends GenericTreeIcon {
	
	public LayoutIcon() {
		this.dashColor1=Color.red.darker();
		this.dashColor2=Color.red.darker();
		this.fillColor=Color.white;
	}

	/**draws two letters in a black rectangle*/
	@Override
	public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
		super.paintIcon(arg0, arg1, arg2, arg3);
		int shift = 2;
		int panelSize = TREE_ICON_HEIGHT/3;
		int spacing =1;
		Rectangle r0=new Rectangle(arg2+shift, arg3+shift-1, panelSize, panelSize);
		
		Rectangle r1 = new Rectangle(r0);
		r0.translate(panelSize+spacing, 0);
		Rectangle r2 = new Rectangle(r0);
		r0.translate(0, panelSize+spacing);
		Rectangle r3 = new Rectangle(r0);
		r0.translate(-panelSize+-spacing, 0);
		
		arg1.setColor(Color.blue.darker());
		if (arg1 instanceof Graphics2D) {
			Graphics2D g=(Graphics2D) arg1;
			g.setStroke(new BasicStroke(1));
			g.draw(r1);
			g.draw(r2);
			g.draw(r3);
			g.draw(r0);
		}
		
		
	}
	
}
