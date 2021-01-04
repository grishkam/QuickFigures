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
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.Icon;

import externalToolBar.AbstractExternalToolset;
import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects_Shapes.ArrowGraphic;

/**interface for tool icons that are placed in the toolbar
   */
	public abstract class GraphicToolIcon implements Icon{

		protected Component lastComponent;
		public static int NORMAL_ICON_TYPE=0, PRESSED_ICON_TYPE=1, ROLLOVER_ICON_TYPE=2;
		protected int type=NORMAL_ICON_TYPE;
		private int borderLineWidth=1;
		protected int iconDim=AbstractExternalToolset.DEFAULT_ICONSIZE;
		protected boolean paintCursorIcon=false;
		
		/**two grey values for the pressed and unpressed icon colors*/
		Color lightColor=new Color(210,210,210);
		Color darkColor=new Color(120,120,120);

		public GraphicToolIcon(int type) {
			this.type=type;
		}
		
		public GraphicToolIcon() {
			this(0);
		}

		/**
		 * 
		 */
		
		/**paints and icon that consists of a grey rectangle with borders of black/white
		  depending on the subclass, another graphic will be drawn within the rectangle*/
		public void paintIcon(Component arg0, Graphics g, int arg2, int arg3) {
			Color oColor = g.getColor();//the original color
			this.lastComponent=arg0;
			g.setColor(lightColor);
			if (type==PRESSED_ICON_TYPE) {
				g.setColor(darkColor);
			}
			int w0 = getIconWidth()-borderLineWidth*2;
			int h0 =getIconHeight()-borderLineWidth*2;
			
			g.fillRect(arg2+borderLineWidth, arg3+borderLineWidth, w0,h0);
			
			/**this next part depends on subclasses*/
			paintObjectOntoIcon(arg0, g, arg2, arg3);
			
			if (paintCursorIcon) {
				paintArrowOntoIcon(arg0, g, arg2, arg3);
			}
			
			paintBorder(g, arg2, arg3);
			g.setColor(oColor);//restores the original color
		}

		/**
		 Paints an icon border of black and white lines
		 */
		protected void paintBorder(Graphics g, int arg2, int arg3) {
			if (g instanceof Graphics2D) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(1));
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				}
			
			int w2 = getIconWidth()-borderLineWidth;
			int h2 =getIconHeight()-borderLineWidth;
			g.setColor(Color.white);
			g.drawLine(arg2+getIconWidth(), arg3, arg2, arg3);
			g.setColor(Color.white);
			g.drawLine(arg2, arg3, arg2, arg3+h2);
			
			if (g instanceof Graphics2D) {
				Graphics2D g2d=(Graphics2D) g;
				g2d.setStroke(new BasicStroke(borderLineWidth));
				
			}
			g.setColor(Color.black);
			g.drawLine(arg2+getIconWidth(), arg3+h2, arg2, arg3+h2);
			g.setColor(Color.darkGray);
			g.drawLine(arg2+w2, arg3, arg2+w2, arg3+h2);
		}
		
		
		
		protected abstract void paintObjectOntoIcon(Component arg0, Graphics g, int arg2,
				int arg3);


		/**draws a cursor-like arrow above the icon*/
		protected void paintArrowOntoIcon(Component arg0, Graphics g, int arg2, int arg3) {
			ArrowGraphic a = new ArrowGraphic();
			a.setPoints(new Point(15,20), new Point(8,3));
			a.setStrokeColor(Color.black);
			a.getHead().setArrowStyle(ArrowGraphic.NORMAL_HEAD);
			a.moveLocation(arg2, arg3);
			a.setStrokeWidth(2);
			//a.setNotchAngle(Math.PI);
			a.getHead().setArrowHeadSize(13);
			if (g instanceof Graphics2D)
			a.draw((Graphics2D) g, new BasicCoordinateConverter());
		}

		public int getIconHeight() {return iconDim;}
	
		public int 	getIconWidth(){return iconDim;}
		
		
		public abstract GraphicToolIcon copy(int type) ;
		
		public static IconSet createIconSet(GraphicToolIcon i) {
			return new IconSet(i.copy(NORMAL_ICON_TYPE), i.copy(PRESSED_ICON_TYPE), i.copy(ROLLOVER_ICON_TYPE));
		}
		
		public IconSet generateIconSet() {
			return createIconSet(this);
		}
		
		public Icon getMenuVersion() {return new ExtractedIcon(this);}
		
		
		/**
		public NakedIcon getVariablePortion() {return new NakedIcon();}
		
		class NakedIcon implements Icon {

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				paintObjectOntoIcon(c, g, x, y);
			}

			@Override
			public int getIconWidth() {
				return AbstractExternalToolset.DEFAULT_ICONSIZE;
			}

			@Override
			public int getIconHeight() {
				return AbstractExternalToolset.DEFAULT_ICONSIZE;
			}}*/
	
}
