/**
 * Author: Greg Mazo
 * Date Modified: Dec 5, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package multiChannelFigureUI;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;

import channelMerging.ChannelEntry;
import iconGraphicalObjects.ColorIcon;
import menuUtil.BasicSmartMenuItem;

/**
A menu item that has a color, text and appearance determined by a channel entry
 */
public abstract class BasicChannelEntryMenuItem extends BasicSmartMenuItem{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ChannelEntry entry;

	
	/**
	 
	 */
	public BasicChannelEntryMenuItem(ChannelEntry ce) {
		this.entry=ce;
		this.setText(entry.getShortLabel());
		updateFont();
	}


	/**
	 * 
	 */
	public void updateFont() {
		HashMap<TextAttribute, Object> mm = new HashMap<TextAttribute, Object> ();
		Font font2 = super.getFont();
		boolean strike=isExcludedChannel();
		if (strike) 
			{
			mm.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
			mm.put(TextAttribute.FOREGROUND, entry.getColor().darker().darker().darker());
			this.setIcon(null);
			}
		else 
		{
			mm.put(TextAttribute.STRIKETHROUGH, !TextAttribute.STRIKETHROUGH_ON);
			mm.put(TextAttribute.FOREGROUND, entry.getColor());
			this.setIcon(new ColorIcon(entry.getColor()));
		}
		this.setFont(font2.deriveFont(mm));
	}


	/**
	returns true if the channel is excluded
	 */
	protected abstract boolean isExcludedChannel();
	
	@Override
	public void setSelected(boolean b) {
		
		updateFont();
	}

}
