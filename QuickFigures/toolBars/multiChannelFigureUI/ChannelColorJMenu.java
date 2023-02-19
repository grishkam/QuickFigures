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
/**
 * Author: Greg Mazo
 * Date Modified: Dec 15, 2020
 * Version: 2023.1
 */
package multiChannelFigureUI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import channelMerging.ChannelColor;
import menuUtil.BasicSmartMenuItem;
import menuUtil.SmartJMenu;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;

/**A menu that is used to pick colors for channels. 
 * Written as a more elegant alternative to the color JMenu
 * Displays the menu items as a gradient of color
 * Color rectangles appear in the menu instead of normal menu items*/
public class ChannelColorJMenu extends SmartJMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	ArrayList<ColorInputListener> listens=new ArrayList<ColorInputListener> ();
	
	public ChannelColorJMenu(String name, Color[] standard) {
		super(name);
		for(Color c:standard) {
			add(new ColorJMenuItem(c));
		 }
	}
	
	
	
	
	
	
	public void addColorInputListener(ColorInputListener lsiten) {
		if(listens.contains(lsiten)) return;
		listens.add(lsiten);
	}
	
	
	
	

	


	
	
	
	
	public static void main(String[] args) {
		
		JFrame ff = new JFrame("frame");
		ff.setLayout(new FlowLayout());
		JMenuBar button = new JMenuBar();
		ff.add(button);
		
		ChannelColorJMenu cjm = new ChannelColorJMenu("Choose Color", standardLutColors);//new Color[] {Color.red, Color.green, Color.blue});
		
		button .add(cjm);
		
		
		ff.pack();
		
		ff.setVisible(true);
		
		
		
	}






	
	
	 static Color[] standardLutColors=new Color[] {Color.red, Color.GREEN, Color.blue, Color.cyan, Color.magenta, Color.yellow, Color.white, Color.black};
		
		public static ChannelColorJMenu getStandardColorJMenu(ColorInputListener t) {
			
			ChannelColorJMenu colors= new ChannelColorJMenu("Colors", standardLutColors); 
			
			colors.addColorInputListener(t);
			
			return colors;
		
			
			
		}


		class ColorJMenuItem extends BasicSmartMenuItem implements ActionListener {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private Color color;
			
			public ColorJMenuItem(Color c) {
				this.setColor(c);
				this.setForeground(c);
				this.setBackground(c);
				this.setText("Color ");
				this.addActionListener(this);
				Font font = new Font("Times", Font.BOLD, 25);
				HashMap<TextAttribute, Object> map = new HashMap<TextAttribute, Object> ();
				map.put(TextAttribute.FOREGROUND, color);
				font=font.deriveFont(map);
				this.setFont(font);
				this.setFocusPainted(false);
				this.setFocusable(false);
			}
			
			@Override
			public void paintComponent(Graphics g) { 
				super.paintComponent(g);
				Rectangle b = this.getBounds();
			
				if(g instanceof Graphics2D) {
					Graphics2D graphics2d = (Graphics2D) g;
					Color c1 = Color.black;
					Color c2 = color;
					if(color.equals(Color.black)) {
						c1=Color.white;
						c2=Color.black;
					}
					GradientPaint paint = new GradientPaint(new Point(getX(), 0+b.height/2), c1, new Point(getX()+b.width, 0+b.height/2), c2, false);
					graphics2d.setPaint(paint);
					graphics2d.fillRect(getX(), 0, b.width, b.height);
					graphics2d.setColor(c2.darker().darker());
					graphics2d.draw(b);
				}
			}
			
			public ColorJMenuItem(ChannelColor c) {
				this(c.getTopColor());
			}

			public Color getColor() {
				return color;
			}

			public void setColor(Color color) {
				this.color = color;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				notifyListens() ;
			}
			
			public void notifyListens() {
				for(ColorInputListener listen: listens) {
					listen.ColorChanged(new ColorInputEvent(null, this, color));
				}
			}
			
		}

}
