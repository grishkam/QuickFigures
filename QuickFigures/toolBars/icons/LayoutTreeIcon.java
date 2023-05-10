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
 * Version: 2023.2
 */
package icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 an icon that shows a small group of panels similar to a layout
 */
public class LayoutTreeIcon extends GenericTreeIcon {
	
	public LayoutTreeIcon() {
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
