/**
 * Author: Greg Mazo
 * Date Modified: Dec 5, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package iconGraphicalObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import channelMerging.ChannelEntry;
import graphicalObjectHandles.IconHandle;

/**
An icon with multiple colors depending on the given channel entries
 */
public class ChannelUseIcon implements Icon, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<ChannelEntry> chans=null;
	/**
	constructor for a channel icon with the given channel entries
	 */
	public ChannelUseIcon(ArrayList<ChannelEntry> c) {
		chans=c;
	}

	/**
	 * 
	 */
	public ChannelUseIcon() {
		chans=new ArrayList<ChannelEntry>();
		chans.add(new ChannelEntry("Red", Color.red, 1));
		chans.add(new ChannelEntry("Green", Color.green, 2));
		chans.add(new ChannelEntry("Blue", Color.blue, 3));
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (g instanceof Graphics2D) {
			Graphics2D g2d=(Graphics2D) g;
			g2d.setStroke(new BasicStroke(3));
			
			double xi = x+this.getIconWidth()*0.2;
			double xf = x+this.getIconWidth()*(1-0.2);
			
			ArrayList<ChannelEntry> channels = getAllColors();
			if (channels==null) return;
			double nChan=channels.size();
			double yi = y+this.getIconHeight()*0.2;
			double yf = y+this.getIconHeight()*(1);
			double yWidth = yf-yi;
			for(int i=0; i<nChan; i++) {
				double yc = yi+yWidth*i/nChan;
				g.setColor(channels.get(i).getColor());
				Path2D p=new Path2D.Double();
				p.moveTo(xi, yc);
				p.quadTo((xi+xf)/2, yc-0.5*yWidth/nChan, xf, yc);
				g2d.draw(p);
			}
			
			
			
		}
		

	}



	
	public ArrayList<ChannelEntry> getAllColors() {
		
		return chans;
	}

	@Override
	public int getIconWidth() {
		return IconHandle._DEFAULT_MAX_SIZE;
	}

	@Override
	public int getIconHeight() {
		return IconHandle._DEFAULT_MAX_SIZE;
	}
}
