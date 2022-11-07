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
 * Date Modified: Jan 16, 2021
 * Version: 2022.2
 */
package icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Icon;

import iconGraphicalObjects.IconTraits;

/**An icon that consists of 1-2 letters within a rectangle
 */
public class TreeIconWithText implements Icon, IconTraits {


	private static final int DEFAULT_TEXT_SIZE = 10;
	
	
	private Font font;
	int textSize=DEFAULT_TEXT_SIZE;
	private String letter1="a";
	private String letter2="b";
	private Color color1=Color.black;
	private Color color2=Color.black;
	private boolean drawBorder=true;//set to true if a border should be drawn
	
	/**Creates an icon containing a single character*/
	public TreeIconWithText(Character c, boolean border) {
		this(null, ""+c, Color.black);
		this.drawBorder=border;
	}
	
	/**constructs an icon with the first 2 letters of the string in the colors given*/
	public TreeIconWithText(Font font, String t, Color...theColors) {
		if(font==null) {
			font=new Font("Monospaced", Font.BOLD, 12);
		}
		this.setFont(font);
		if(t==null) t=letter1+letter2;
		if (t.length()>0) this.letter1=t.substring(0,1);
		
		if (t.length()>1)	this.letter2=t.substring(1,2);
			else letter2=null;
		
		setColors(theColors);
	}
	
	/**when given 1 or two color arguments, this sets up the colors for the two letters shown
	  if 1 color is given, both letters will be given the same color*/
	public void setColors(Color... c){
		if (c==null||c.length==0) return;
		 color1=c[0];
		if (c.length==1) color2=c[0];
		else  color2=c[1];
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
		Font oFont = arg1.getFont();
		Color oColor=arg1.getColor();
		
		if (arg1 instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) arg1;
			
			if(drawBorder) {
				BasicStroke bs = new BasicStroke(1);
				Rectangle r = new Rectangle(arg2, arg3, getIconHeight(),getIconHeight());
				arg1.setColor(Color.black);
				g2d.setStroke(bs);
				g2d.draw(r);
			}
				
				g2d.setColor(color1);
				g2d.setFont(getFont().deriveFont((float )getTextSize()));
				g2d.drawString(letter1, arg2+1, arg3+getTextSize()-1);
				
				if(letter2!=null) {
					g2d.setColor(color2);
					g2d.drawString(letter2, arg2+1+getTextSize()/2, arg3+getTextSize()-1);
				}
				
		}
		arg1.setFont(oFont);
		arg1.setColor(oColor);
	}

	/**
	 returns the size used
	 */
	protected int getTextSize() {
		return textSize;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}}