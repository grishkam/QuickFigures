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
 * Date Modified: Jan 6, 2021
 * Version: 2021.2
 */
package standardDialog.colors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import locatedObject.ColorDimmer;

/**A JCombo box designed for the user to select a color dimming effect
 * The menu text appear in the altered colors to indicate the effects of each menu option
 * @see ColorDimmer class for more information*/
public class ColorDimmingBox extends JComboBox<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**the colors used to display the text of the menu options*/
	static Color[] segColors=new Color[] {Color.red, Color.green, Color.blue, Color.cyan, Color.magenta, Color.yellow, Color.GRAY};
	
	/**creates a new color dimming box*/
	public ColorDimmingBox() {
		super(ColorDimmer.colorModChoices2);
		ColorDimmingCellRenerer cc = new ColorDimmingCellRenerer();
		cc.setBox(this);
		cc.setFont(cc.getFont().deriveFont((float)20));
		this.setRenderer(cc);
	}
	
	/**creates a new color dimming box with initial value initial*/
	public ColorDimmingBox(int innitial) {
		this();
		this.setSelectedIndex(innitial);
		
	}

	/**draws the given text in several colors*/
	public static void drawRainbowString(Graphics g, int x, int y,  String st, int[] ints, Color[] colors) {
		ArrayList<String> stringarr = ComplexTextGraphic .splitStringBasedOnArray(st, ints);
		drawRainBowString(g,x,y, stringarr, ints, colors);
		
	}
	/**draws the given array of strings as a line of text with several colors*/
	public static void drawRainBowString(Graphics g, int x, int y,  ArrayList<String> stringarr, int[] ints, Color[] colors) {
		int ci=0;
		for(String st1: stringarr) {
			if (ci>=colors.length) ci=0;
			FontMetrics fm = g.getFontMetrics();
			g.setColor(colors[ci]);
			g.drawString(st1, x, y);
			x+=fm.stringWidth(st1);
			ci++;
		}
	}
	
	
	/**determines how to render the combo box choices*/
	public class ColorDimmingCellRenerer extends BasicComboBoxRenderer {
		
		/**
		 * 
		 */
		public int colorDims=0;
		private ColorDimmingBox box;
		
		private static final long serialVersionUID = 1L;
		
		public ColorDimmingCellRenerer() {
		
		}
	

		public void paint(Graphics g) {
			super.paint(g);
			int dim = this.colorDims;
			if (this.colorDims==-1) dim=box.getSelectedIndex();
			drawRainbowStringForDimmingBox(g, ColorDimmer.values()[dim], this.getText());
			
		}

		/**Based on the dimming type, draws a many colored text from the string given with the dimmed colors*/
		public void drawRainbowStringForDimmingBox(Graphics g, ColorDimmer dimmingType, String text) {
			Font font = this.getFont();
			drawRainbowStringForBox(g, dimmingType, text, font, 1,1);
		}



		
		public  Component	getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (out instanceof ColorDimmingCellRenerer) {
				ColorDimmingCellRenerer c=(ColorDimmingCellRenerer) out;
				c.colorDims=index;
				
					{this.setFont(this.getFont().deriveFont(Font.BOLD).deriveFont((float)20.0));}
					
				if (cellHasFocus) {
					c.colorDims=getBox().getSelectedIndex();
		
					}
			}
		
			return out;
				}

		public ColorDimmingBox getBox() {
			return box;
		}

		public void setBox(ColorDimmingBox box) {
			this.box = box;
			this.colorDims=box.getSelectedIndex();
		}
	}
	
	/**Based on the dimming type, draws a many colored text from the string given with the dimmed colors using the */
	public static void drawRainbowStringForBox(Graphics g, ColorDimmer dimmingType, String text, Font font, int x, int y) {
		ColorDimmingBox.drawRainbowString(g, x,font.getSize()+y, text, new int[]{3,2,2}, ColorDimmer.modifyArray( segColors, dimmingType, true));
	}
	
	public static void main(String[] arg) {
	
	}

}
