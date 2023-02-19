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
 * Date Modified: Mar 7, 2021
 * Date Created: Mar 13, 2021
 * Version: 2023.1
 */
package actionToolbarItems;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.Icon;

import graphicalObjects_SpecialObjects.BarGraphic;
import icons.MiniToolBarIcon;

/**
 An icon that indicates whether the text for a scale bar is showings
 */
public class BarTextIcon implements Icon,MiniToolBarIcon {

	private BarGraphic scaleBar;//the scale bar for the icon

	/**
	 * @param modelItem
	 */
	public BarTextIcon(BarGraphic modelItem) {
		this.scaleBar=modelItem;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setFont(new Font("Arial", Font.BOLD, 9));
		g.drawString("text", x+3, y+12);
		if ((g instanceof Graphics2D)) {
			 Graphics2D g2=(Graphics2D) g;
			 Stroke sOriginal = g2.getStroke();
			 Color oColor = g2.getColor();
			 g2.setStroke(new BasicStroke(2));
			 g.drawLine(x+4, y+15, x+17, y+15);
			if (scaleBar!=null && !scaleBar.isShowText()) {
			g.setColor(Color.RED);
			g.drawLine(x+2, y+2, x+20, y+20);
			
			}
			g.setColor(oColor);
			
			g2.setStroke(sOriginal);
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
