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
 * Date Modified: Dec 4, 2022
 * Version: 2023.1
 */
package multiChannelFigureUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;

import channelMerging.ChannelEntry;
import iconGraphicalObjects.ColorIcon;
import menuUtil.BasicSmartMenuItem;
import undo.AbstractUndoableEdit2;

/**
A menu item that has a color, text and appearance determined by a channel entry
 */
public abstract class BasicChannelEntryMenuItem extends BasicSmartMenuItem{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ChannelEntry entry;
	public boolean useStrike=true;

	public boolean invert=false;
	
	/**
	 
	 */
	public BasicChannelEntryMenuItem(ChannelEntry ce) {
		if(ce==null)
			return;
		this.entry=ce;
		this.setText(entry.getLabelForMenuItem());
		updateFont();
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractUndoableEdit2 undo = onPressAction();
				if(undo!=null)
					getUndoManager().addEdit(
							undo	
						);
			}

		});
		
	}
	

	public void setIconPresent(boolean strike) {
		if(invert)
			strike=!strike;
		if (strike) {
			this.setIcon(null);
		}	 else {
			this.setIcon(new ColorIcon(getDisplayColor()));
		} 
	}
	
	/**
	 changes the font depecting on the state of the channel
	 */
	public void updateFont() {
		HashMap<TextAttribute, Object> mm = new HashMap<TextAttribute, Object> ();
		Font font2 = super.getFont();
		font2=font2.deriveFont(fontStyle());
		boolean strike=isExcludedChannel();
		if (entry!=null) {
			if (strike) {
				if(useStrike)
					mm.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
				else 
					mm.put(TextAttribute.STRIKETHROUGH, !TextAttribute.STRIKETHROUGH_ON);
				mm.put(TextAttribute.FOREGROUND, getDisplayColor().darker().darker().darker());
			
			} else {
				if(useStrike)mm.put(TextAttribute.STRIKETHROUGH, !TextAttribute.STRIKETHROUGH_ON);
				mm.put(TextAttribute.FOREGROUND,  getTextColor());
				
			} 
			setIconPresent(strike);
		}
		this.setFont(font2.deriveFont(mm));
		
	}

	/**The color of the text of the menu item*/
	public Color getTextColor() {
		return getDisplayColor();
	}

	/**
	 * @return the color used for this item
	 */
	public Color getDisplayColor() {
		if (entry==null) return Color.black;
		return entry.getColor();
	}

	int fontStyle() {return Font.BOLD;}
	
	
	/**
	returns true if the channel is excluded
	 */
	protected abstract boolean isExcludedChannel();
	
	@Override
	public void setSelected(boolean b) {
		
		updateFont();
	}
	protected  AbstractUndoableEdit2 onPressAction() {
		// TODO Auto-generated method stub
		return null;
	}	
	

}
