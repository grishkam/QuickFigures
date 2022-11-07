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
 * Version: 2022.2
 */
package genericMontageLayoutToolKit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import externalToolBar.AbstractExternalToolset;
import icons.GraphicToolIcon;
import layout.PanelLayout;
import layout.basicFigure.BasicLayout;
import locatedObject.RectangleEdges;

/**An icon that displays a small picture of a layout*/
public class GeneralLayoutToolIcon extends GraphicToolIcon implements Icon{
	
	
	
	/**
	 * 
	 */
	private static final int COLOR_BRIGHTNESS = 180;

	protected static final Color RED_TONE=new Color(COLOR_BRIGHTNESS, 0,0), BLUE_TONE=new Color(00, 0,COLOR_BRIGHTNESS), GREEN_TONE=new Color(0,COLOR_BRIGHTNESS,0).darker();
	protected static final Color YELLOW_TONE = new Color(COLOR_BRIGHTNESS,COLOR_BRIGHTNESS, 0).darker();
	protected static final Color MAGENTA_TONE = new Color(COLOR_BRIGHTNESS,0,COLOR_BRIGHTNESS).darker();
	protected Color arrowColor = Color.black;
	protected int arrowthickNess = 1;
	
	private static final int ICONSIZE = AbstractExternalToolset.DEFAULT_ICONSIZE;
	protected Color[] panelColor    = new Color[] {BLUE_TONE};

	Color boundryColors = RED_TONE;
	protected boolean paintBoundry=true;

	public GeneralLayoutToolIcon(int type) {
		super(type);
		this.type=type;
	}
	public GeneralLayoutToolIcon(int type, boolean paintArrow) {
		super(type);
		super.paintCursorIcon=paintArrow;
		this.type=type;
	}
	
	public GeneralLayoutToolIcon() {
		this(NORMAL_ICON_TYPE);
	}
	
	protected Color getPanelColor(int i) {
		return getPanelColors()[i%getPanelColors().length];
	}

	/**
	returns a list of panel colors. 
	 */
	protected Color[] getPanelColors() {
		return panelColor;
	}
	
	protected Color getBoundryColor() {
		return boundryColors;
	}
	
	@Override
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2, int arg3) {
			Graphics2D g2d=(Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			PanelLayout layout = getDrawnLayout();
			layout.move(arg2, arg3);
			layout.resetPtsPanels();
			g2d.setStroke(new BasicStroke(1));
			int count=0;
			Rectangle2D[] panels = getDrawnPanels(layout);
			
			for(Rectangle2D p: panels) {
				drawPanel(g2d, p, count);
				count++;
			}
			g2d.setColor(getBoundryColor());
			if (paintBoundry)g2d.draw(layout.getBoundry());

	}
	
	/**draws the given panel of the icon
	 * @param g2d
	 * @param p
	 * @param count
	 */
	protected void drawPanel(Graphics2D g2d, Rectangle2D p, int count) {
		Color panelColor2 = getPanelColor(count);
		if(this.type==PRESSED_ICON_TYPE) {
			g2d.setColor(Color.lightGray);
			g2d.fill(p);
		}
		
		Color fill = deriveFillColor(panelColor2);
		g2d.setColor(fill);
		g2d.fill(p);
		g2d.setColor(derivePanelStrokeColor(panelColor2));
		g2d.draw(p);
	}
	
	/**alters the panel stroke color
	 * @param panelColor2
	 * @return
	 */
	protected Color derivePanelStrokeColor(Color panelColor2) {
		return panelColor2;
	}
	/**given the base color of a panel, returns the fill color used to give the panel a light tint
	 * @param panelColor2
	 * @return
	 */
	protected Color deriveFillColor(Color panelColor2) {
		Color fillColor=new Color(panelColor2.getRed(), panelColor2.getGreen(), panelColor2.getBlue(), 50);
		Color fill = fillColor.brighter().brighter();
		return fill;
	}
	/**
	
	 */
	protected Rectangle2D[] getDrawnPanels(PanelLayout layout) {
		return layout.getPanels();
	}
	
	public void paintArrow(Graphics2D g, int xStart, int yStart, int length, int direction, int size) {
		int x1=xStart;
		int x2=xStart;
		int y2=yStart;
		int y1=yStart;
		
		int yShift1=0,yShift2=0,xShift1=0,xShift2=0;
		
		if(direction==RectangleEdges.RIGHT) {x2=xStart+length; xShift1=-size; xShift2=xShift1; yShift1=size; yShift2=-size;}
		if(direction==RectangleEdges.LEFT) {x2=xStart-length; xShift1=size; xShift2=xShift1; yShift1=size; yShift2=-size;}
		if(direction==RectangleEdges.TOP) {y2=yStart-length;yShift1=size;yShift2=yShift1; xShift1=size;xShift2=-size;}
		if(direction==RectangleEdges.BOTTOM) {y2=yStart+length; yShift1=-size;yShift2=yShift1;xShift1=size;xShift2=-size;}
		
		if(direction==RectangleEdges.UPPER_LEFT) {y2=yStart-length; x2=xStart-length;xShift1=0; yShift1=size; xShift2=size; yShift2=0;}
		if(direction==RectangleEdges.LOWER_RIGHT) {y2=yStart+length; x2=xStart+length;xShift1=0; yShift1=-size; xShift2=-size; yShift2=0;}
		
		if(direction==RectangleEdges.LOWER_LEFT) {y2=yStart+length; x2=xStart-length;xShift1=0; yShift1=size; xShift2=size; yShift2=0;}
		
		g.setColor(arrowColor);
	
		g.setStroke(new BasicStroke(arrowthickNess));
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x2+xShift1, y2+yShift1, x2, y2);
		g.drawLine(x2+xShift2, y2+yShift2, x2, y2);
		
		
	}
	
	/**
	Draws text into the panel
	 */
	protected void drawLabelOnPanel(Graphics2D g2d, Rectangle2D p, String str) {
		Font oFont = g2d.getFont();
		{
			g2d.setFont(new Font("Times", Font.BOLD, 11));
			
			g2d.drawString(str, (int)p.getX()+2, (int)p.getY()+10);
		}
		g2d.setFont(oFont);
	}
	
	

	public PanelLayout getDrawnLayout() {
		return createSimpleIconLayout( type);
	}

	/**
	creates a layout for drawing and icon
	 */
	protected PanelLayout createSimpleIconLayout( int type) {
		BasicLayout layout = new BasicLayout(2, 2, 6, 6, 2,2, true);
		layout.setLabelSpaces(2, 2,2,2);
		layout.move(2,3);
		return layout;
	}

	@Override
	public int getIconHeight() {
		return ICONSIZE;
	}

	@Override
	public int getIconWidth() {
		return ICONSIZE;
	}
	
	/**creates a copy that is of a different category*/
	public GeneralLayoutToolIcon copy(int type) {
		GeneralLayoutToolIcon another = generateAnother(type);
		another.paintCursorIcon=paintCursorIcon;
		another.paintBoundry=paintBoundry;
		another.panelColor=this.panelColor;
		return another;
	}
	
	/**creates a similar icon but of a different category
	 * @param type
	 * @return
	 */
	protected GeneralLayoutToolIcon generateAnother(int type) {
		return new GeneralLayoutToolIcon(type);
	}
	
	

}
