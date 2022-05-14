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
 * Date Created: Dec 11, 2021
 * Version: 2022.1
 */
package iconGraphicalObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import icons.MiniToolBarIcon;

/**An icon that is merely a square with a checkbox inside*/
public class CheckBoxIcon implements Icon, MiniToolBarIcon {
	

	private Color color=Color.black;
	private boolean checked=false;
	
	

	public CheckBoxIcon(Color colorForColorModeIcon, boolean checked) {
		this.color=colorForColorModeIcon;
		this.setChecked(checked);
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Color originalColor = g.getColor();
		Font originalFont = g.getFont();
		int rectX = x+5;
		int rectY = y+5;
		java.awt.geom.Rectangle2D.Double ra = new Rectangle2D.Double(rectX, rectY, 18, 18);
		
		
		
		
		/**draws the objects*/
		if(g instanceof Graphics2D) {
			Graphics2D g2=(Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			
			g2.setColor(Color.black);
			g2.setStroke(new BasicStroke(1));
			g2.draw(ra);
		
			g2.setColor(color);
			g2.setFont(new Font("LucidaSans", Font.BOLD, 16));
			if(isChecked())
				g2.drawString("\u2713", rectX+2, rectY+14);
	}
		
		
		g.setColor(originalColor);
		g.setFont(originalFont);
	}

	@Override
	public int getIconWidth() {
		return ICON_SIZE;
	}

	@Override
	public int getIconHeight() {
		return ICON_SIZE;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	

}