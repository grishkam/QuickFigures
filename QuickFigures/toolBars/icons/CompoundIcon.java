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
package icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;


/**An icon that paints multiple other icons on top of each other.
  Also draws a small triangle on some icons to indicate a menu*/
public class CompoundIcon implements Icon {

	private Icon[] icons;
	public boolean drawMenuIndicator=true;

	public CompoundIcon(Icon... icons) {
		this.icons=icons;
	}
	
	public CompoundIcon(boolean indicatorDrawn, Icon... icons) {
		this.icons=icons;
		drawMenuIndicator=indicatorDrawn;
	}

	@Override
	public int getIconHeight() {
		return icons[0].getIconHeight();
	}

	@Override
	public int getIconWidth() {
		return icons[0].getIconWidth();
	}

	/**Paints the icons*/
	@Override
	public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
		for(Icon icon: icons) {
			if (icon==null) continue;
			icon.paintIcon(arg0, arg1, arg2, arg3);
		}
		if (drawMenuIndicator) {
			if (arg1 instanceof Graphics2D) {
				((Graphics2D) arg1).setStroke(new BasicStroke(1));
				((Graphics2D) arg1).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
			}
			
			arg1.setColor(Color.red.darker());
			int liny=arg2+21;
			int linx=arg3+18;
			int linw=2;
			
			arg1.drawLine(linx, liny, linx+linw, liny);
			arg1.drawLine(linx, liny,      linx+linw/2, liny+1);
			arg1.drawLine(linx+linw, liny, linx+linw/2, liny+1);
		}

	}

}
