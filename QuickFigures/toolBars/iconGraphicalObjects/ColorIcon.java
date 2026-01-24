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
 * Date Modified: Jan 24, 2026
 * Version: 2026.1
 */
package iconGraphicalObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import icons.MiniToolBarIcon;

/**An icon that is merely a colored circle*/
public class ColorIcon implements Icon, MiniToolBarIcon {
	

	private Color color;
	String text=null;
	int circle_shift = 3;
	int circle_size = 18;


	public ColorIcon(Color colorForColorModeIcon) {
		this.color=colorForColorModeIcon;
	}
	
	public ColorIcon(Color colorForColorModeIcon, String c, int circle_shift,  int circle_size) {
		this.color=colorForColorModeIcon;
		text=c;
		this.circle_shift=circle_shift;
		this.circle_size=circle_size;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
	
		
		java.awt.geom.Rectangle2D.Double ra = new Rectangle2D.Double(x+circle_shift, y+circle_shift, circle_size, circle_size);
		
		
		java.awt.geom.Ellipse2D.Double a = new Ellipse2D.Double();
		a.setFrame(ra);
		
		/**draws the objects*/
		if(g instanceof Graphics2D) {
			Graphics2D g2=(Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(color);
		
			g2.fill(a);
			
			g2.setColor(Color.black);
			g2.setStroke(new BasicStroke(1));
			g2.draw(a);
			if(text!=null) {
				Font f = g2.getFont().deriveFont((float) ra.height);
				g2.setFont(f);
				g2.setColor(Color.black);
				g2.drawString(text, (int)(ra.getMinX()+ra.getCenterX())/2, (int) (ra.getMaxY()+ra.getCenterY())/2);
			}
		
			
	}
	}

	@Override
	public int getIconWidth() {
		return ICON_SIZE;
	}

	@Override
	public int getIconHeight() {
		return ICON_SIZE;
	}
	
	

}