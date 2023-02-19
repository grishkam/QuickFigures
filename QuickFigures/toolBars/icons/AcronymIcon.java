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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import graphicalObjects_SpecialObjects.TextGraphic;
import layout.basicFigure.BasicLayout;

/**An icon for text tools*/
public class AcronymIcon extends GraphicToolIcon{

	String text="QF";
	
	private Font font=new Font("Arial", Font.BOLD, 14);
	boolean drawRectangle=false;
	Color frameColor=Color.black;

	public AcronymIcon(String starting, int type) {
		super(type);
		text=starting;
	}
	
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2,
			int arg3) {
		Font oFont = g.getFont();//stores the original font
		TextGraphic.setAntialiasedText(g, true);
		g.setColor(Color.black);
		g.setFont(font);
		Graphics2D g2=(Graphics2D) g;
		
		BasicLayout bl = new BasicLayout();
		bl.setHorizontalBorder(2);
		bl.setVerticalBorder(2);
		
		
			bl.setLayoutBasedOnRect(new Rectangle(arg2+2, arg3+2, 20,20));
			bl.setCols(1);
			bl.setRows(1);
			for(int i=0; i<bl.getPanels().length; i++) {
				Rectangle2D p=bl.getPanels()[i];
				
				if (drawRectangle) {
					g2.setPaint(frameColor);
					g2.draw(p);
				}
				g2.drawString(text, (int)p.getX()+3, (int)p.getY()+16);
			}
			g2.setPaint(Color.black);
		g.setFont(oFont);
 		
	}

	@Override
	public
	GraphicToolIcon copy(int type) {
		return new  AcronymIcon(text, type);
	}

}
