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
package iconGraphicalObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Arc2D.Double;

import javax.swing.Icon;

import channelMerging.ChannelUseInstructions;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import icons.MiniToolBarIcon;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import utilityClassesForObjects.RectangleEdges;

/**An icon for the change color mode option*/
public class ColorModeIcon implements Icon,MiniToolBarIcon {
	
	
	private ImagePanelGraphic imagePanel;
	private Color color;
	
	public ColorModeIcon(ImagePanelGraphic firstImage) {
		imagePanel=firstImage;
	}

	public ColorModeIcon(Color colorForColorModeIcon) {
		this.color=colorForColorModeIcon;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
	
		java.awt.geom.Rectangle2D.Double ra = new Rectangle2D.Double(x+3, y+3, 18, 18);
		
		/**creates a black and white gradient*/
		GradientPaint gp = new GradientPaint(RectangleEdges.getLocation(RectangleEdges.TOP, ra), Color.white, RectangleEdges.getLocation(RectangleEdges.BOTTOM, ra), Color.black);
		GradientPaint gp2 = gp;
		
		/**creates a gradient paint for the color of the image's channel entry*/
		Color iColor = this.getImageColor();
		if (iColor!=null) 
			gp2=new GradientPaint(RectangleEdges.getLocation(RectangleEdges.TOP, ra), iColor, RectangleEdges.getLocation(RectangleEdges.BOTTOM, ra), Color.black);
		
	/**creates two half circles*/
		Double a = new Arc2D.Double(ra, 90, 180, Arc2D.CHORD);
		Double a2 = new Arc2D.Double(ra, -90, 180, Arc2D.CHORD);
		
		/**draws the objects*/
		if(g instanceof Graphics2D) {
			Graphics2D g2=(Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2.setPaint(gp);
			g2.fill(a);
			g2.setPaint(gp2);
			g2.fill(a2);
			
			/**draws red around one acr depending on which color mode is actually used by the target image's */
			g2.setStroke(new BasicStroke());
			g2.setColor(Color.red.darker());
			if(this.getImageColorMode()) {g2.draw(a);} else {g2.draw(a2);}
	}
		g.setColor(Color.black);
	}

	@Override
	public int getIconWidth() {
		return ICON_SIZE;
	}

	@Override
	public int getIconHeight() {
		return ICON_SIZE;
	}
	
	/**Returns the color of the channel entry used to create the target image panel*/
	public Color getImageColor() {
		if (imagePanel==null) return color;
		try {
			ChannelPanelEditingMenu cc = new ChannelPanelEditingMenu(imagePanel);
			
			return cc.getChannelEntryList().get(0).getColor();
		} catch (Exception e) {
		}
		
		return color;
	}
	
	/**returns the current color mode*/
	public boolean getImageColorMode() {
		
		try {
			ChannelPanelEditingMenu cc = new ChannelPanelEditingMenu(imagePanel);
			return cc.getPressedPanelManager().getPanelList().getChannelUseInstructions().channelColorMode==ChannelUseInstructions.CHANNELS_IN_GREYSCALE;
		} catch (Exception e) {
			
		}
	return false;
	}

}