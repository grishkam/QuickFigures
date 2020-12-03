/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package externalToolBar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Icon;

import iconGraphicalObjects.IconTraits;

/**An icon that consists of two letter within a rectangle*/
public class TreeIconForTextGraphic implements Icon, IconTraits {


	private static final int TEXT_SIZE = 10;
	private Font font;
	private String letter1="a";
	private String letter2="b";
	private Color c1=Color.black;
	private Color c2=Color.black;

	/**constructs an icon with the first 2 letters of the string in the colors given*/
	public TreeIconForTextGraphic(Font font, String t, Color...theColors) {
		this.setFont(font);
		if(t==null) t=letter1+letter2;
		if (t.length()>0) this.letter1=t.substring(0,1);
		if (t.length()>1)	this.letter2=t.substring(1,2);
		setColors(theColors);
	}
	
	/**when given 1 or two color arguments, this sets up the colors for the two letters shown
	  if 1 color is given, both letters will be given the same color*/
	public void setColors(Color... c){
		if (c==null||c.length==0) return;
		 c1=c[0];
		if (c.length==1) c2=c[0];
		else  c2=c[1];
	}
	
	@Override
	public int getIconHeight() {
		return TREE_ICON_HEIGHT;
	}

	@Override
	public int getIconWidth() {
		return TREE_ICON_WIDTH;
	}

	/**draws two letters in a black rectangle*/
	@Override
	public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
		if (arg1 instanceof Graphics2D) {
		Graphics2D g2d = (Graphics2D) arg1;
		
		BasicStroke bs = new BasicStroke(1);
		Rectangle r = new Rectangle(arg2, arg3, getIconHeight(),getIconHeight());
		arg1.setColor(Color.black);
		g2d.setStroke(bs);
		g2d.draw(r);
			
			
			g2d.setColor(c1);
			g2d.setFont(getFont().deriveFont((float )getTextSize()));
			g2d.drawString(letter1, arg2+1, arg3+getTextSize()-1);
			g2d.setColor(c2);
			g2d.drawString(letter2, arg2+1+getTextSize()/2, arg3+getTextSize()-1);
			
		}
		
	}

	/**
	 returns the size used
	 */
	protected int getTextSize() {
		return TEXT_SIZE;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}}