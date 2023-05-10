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
package iconGraphicalObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import channelMerging.ChannelEntry;
import handles.IconHandle;
import infoStorage.BasicMetaDataHandler;

/**
An icon with multiple colors depending on the given channel entries
 */
public class ChannelUseIcon implements Icon, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static final int NORMAL_CHANNEL_USE = 0, ADVANCED=1, VERTICAL_BARS=3;
	
	
	ArrayList<ChannelEntry> chans=null;
	
	private int style=NORMAL_CHANNEL_USE;

	/**set to true if channel names should be used as a basic for the icon colors*/
	private boolean useNames;
	
	
	
	/**
	constructor for a channel icon with the given channel entries
	 */
	public ChannelUseIcon(ArrayList<ChannelEntry> c) {
		chans=c;
		if(c==null||c.size()<2) setupGenericChannelList();
	}
	
	/**
	constructor for a channel icon with the given channel entries
	 */
	public ChannelUseIcon(ArrayList<ChannelEntry> c, int style1, boolean useNames) {
		this(c);
		this.style=style1;
		this.useNames=useNames;
	}

	/**
	 * 
	 */
	public ChannelUseIcon() {
		setupGenericChannelList();
	}

	/**
	 * 
	 */
	void setupGenericChannelList() {
		chans=new ArrayList<ChannelEntry>();
		chans.add(new ChannelEntry("Red", Color.red, 1));
		chans.add(new ChannelEntry("Green", Color.green, 2));
		chans.add(new ChannelEntry("Blue", Color.blue, 3));
	}

	@Override
	public void paintIcon(Component c, Graphics g1, int x, int y) {
		if (g1 instanceof Graphics2D) {
			Graphics2D g2d=(Graphics2D) g1;
			Stroke oldStroke = g2d.getStroke();
			g2d.setStroke(new BasicStroke(3));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			if (style==VERTICAL_BARS) {
				g2d.setStroke(new BasicStroke(2));
				drawBars(g2d, x, y);
			}
			else
				drawStandard(g2d, x, y);
			
			g2d.setStroke(oldStroke);
			
		}
		g1.setColor(Color.black);

	}
	
	/**
	 * @param g2d
	 * @param x
	 * @param y
	 */
	void drawBars(Graphics2D g2d, int x, int y) {
		double yi = x+this.getIconWidth()*0.2;
		double yf = x+this.getIconWidth()*(1-0.2);
		
		ArrayList<ChannelEntry> channels = getAllColors();
		if (channels==null) return;
		double nChan=channels.size();
		double xi = x+this.getIconWidth()*0.2;
		double xf = x+this.getIconWidth()*(1);
		double xWidth = xf-xi;
		for(int i=0; i<nChan; i++) {
			double xc = xi+xWidth*i/nChan;
			g2d.setColor(channels.get(i).getColor());
			if(useNames) {
				Color c2 = BasicMetaDataHandler.determineNewChannelColor(channels.get(i).getRealChannelName());
				if(c2!=null) g2d.setColor(c2);
			}
			Path2D p=new Path2D.Double();
			p.moveTo(xc, yi);
			
			p.lineTo( xc, yf);
			g2d.draw(p);
		}
	}


	/** draws a horizontal series of strokes for each channel
	 * @param g2d
	 * @param x
	 * @param y
	 */
	void drawStandard(Graphics2D g2d, int x, int y) {
		double xi = x+this.getIconWidth()*0.2;
		double xf = x+this.getIconWidth()*(1-0.2);
		
		ArrayList<ChannelEntry> channels = getAllColors();
		if (channels==null) return;
		double nChan=channels.size();
		double yi = y+this.getIconHeight()*0.2+3;
		double yf = y+this.getIconHeight()*(1);
		double yWidth = yf-yi;
		for(int i=0; i<nChan; i++) {
			double yc = yi+yWidth*i/nChan;
			g2d.setColor(channels.get(i).getColor());
			Path2D p=new Path2D.Double();
			p.moveTo(xi, yc);
			double curveControlShift = 0.7;
			if (isAdvanced()) curveControlShift*=-1;
			p.quadTo((xi+xf)/2+(isAdvanced()? xi:0), yc-curveControlShift*yWidth/nChan, xf, yc);
			g2d.draw(p);
		}
	}

	/**
	 * @return
	 */
	boolean isAdvanced() {
		return style==ADVANCED;
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
