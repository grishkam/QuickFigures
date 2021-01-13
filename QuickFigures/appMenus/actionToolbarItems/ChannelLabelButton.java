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
 * Version: 2021.1
 */
package actionToolbarItems;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import channelLabels.ChannelLabelProperties;
import channelLabels.MergeLabelStyle;
import channelLabels.ChannelLabelTextGraphic;
import externalToolBar.AbstractExternalToolset;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.LocatedObject2D;
import selectedItemMenus.BasicMultiSelectionOperator;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.EditListener;

/**Performs a specified edit on a series of channel label Text objects
 * May change the label style*/
public class ChannelLabelButton extends BasicMultiSelectionOperator implements Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	MergeLabelStyle style=null;
	String mergeText=null;
	public static String[] mergeTexts=new String[] {"Merge", "merge", "Merged", "merged", "Overlay", "overlay"};
	

	
	
	public ChannelLabelButton( MergeLabelStyle type) {
		this.style=type;
	}
	
	public ChannelLabelButton( String mergeText) {
		
		this. mergeText=mergeText;
	}

	/**returns a variety of channel label operators*/
	public static ChannelLabelButton[] getAllMergeLabelFroms() {
		MergeLabelStyle[] values = MergeLabelStyle.values();
		ChannelLabelButton[] out = new  ChannelLabelButton[values.length+mergeTexts.length];
		for(int i=0; i<out.length; i++) {
			if(i<values.length)
				out[i]=new ChannelLabelButton(values[i]);
			else {
				out[i]=new ChannelLabelButton(mergeTexts[i-values.length]);
			}
		}
		return out;
	}


	@Override
	public String getMenuCommand() {
		if(style!=null)
			return style.getMenuText();
		if (this.mergeText!=null) {
			return "'"+mergeText+"'";
		}
		
		return "merge";
	}
	
	

	public void run() {
		
		if(selector==null) return;//can do nothing if no selection system present
		
		setSelection(this.selector.getSelecteditems());
		ArrayList<LocatedObject2D> all = getAllObjects();
		CombinedEdit edits=new CombinedEdit();
		
		
		
		for(LocatedObject2D a: all) {
			actOnObject(edits, a);
				}
		addUndo(edits);
		
		
	}


	
	/**returns true if the object is already in sthe stake that this class would transform it into*/
	public boolean objectIsAlready(Object a) {
		
		
		
		return false;
	}

	public void actOnObject(CombinedEdit edits, LocatedObject2D a) {
		if (a instanceof ChannelLabelTextGraphic ) {
			
			ChannelLabelTextGraphic c=(ChannelLabelTextGraphic) a;
			AbstractUndoableEdit2 undo = c.provideUndoForDialog();
			
			ChannelLabelProperties p = c.getChannelLabelProperties();
				
			if(style!=null)
				p.setMergeLabelStyle(style);
			
			
			if(mergeText!=null) {
				if(p.getMergeLabelStyle()==MergeLabelStyle.MULTIPLE_LINES||
						p.getMergeLabelStyle()==MergeLabelStyle.ONE_LINE_WITH_ALL_CHANNEL_NAMES) {
					p.setMergeLabelStyle(MergeLabelStyle.SIMPLY_LABEL_AS_MERGE);
				}
				p.setMergeText(mergeText);
			}
			
			c.setParaGraphToChannels();
			edits.addEditListener(new EditListener() {
				@Override
				public void afterEdit() {
					c.setParaGraphToChannels();
				}});
			
			undo.establishFinalState();
			edits.addEditToList(undo);
		} 
		
	}

	
	


String menPath=null;

public String getMenuPath() {
	
	return menPath;
}

public Component getInputPanel() {
	
	return null;
	}






		
		public Icon getIcon() {
			
			return  new SuperTextIcon();
		}
		
		

		class SuperTextIcon implements Icon, Serializable { 

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;



			@Override
			public int getIconHeight() {
				
				return AbstractExternalToolset.DEFAULT_ICONSIZE;
			}

			@Override
			public int getIconWidth() {
				
				return AbstractExternalToolset.DEFAULT_ICONSIZE;
			}

			@Override
			public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
				Font startFont=arg1.getFont();
				
				
				
				
				TextGraphic.setAntialiasedText(arg1, true);
				arg1.setColor(Color.black);
				
				arg1.setFont(new Font("Arial", 0, 6));
				
				if(style!=null)
				switch(style) {
					
				case RAINBOW_STYLE: {
					drawRainBowString(arg1, arg2-2, arg3+8, ChannelLabelProperties.split3MergeTexts[0], new Color[] {Color.red, Color.green, Color.blue}, false);
					break;
				}
				case ONE_LINE_WITH_ALL_CHANNEL_NAMES: {
					arg1.setFont(new Font("Arial", Font.BOLD, 6));
					drawRainBowString(arg1, arg2-2, arg3+8, new String[] {"C1", "C2", "C3"}, new Color[] {Color.red, Color.green, Color.blue}, false);
					break;
				}
				case MULTIPLE_LINES: {
					arg1.setFont(new Font("Arial", Font.BOLD, 6));
					drawRainBowString(arg1, arg2+2, arg3+4, new String[] {"C1", "C2", "C3"}, new Color[] {Color.red, Color.green, Color.blue}, true);
					break;
				}
				case OVERLAY_THE_COLORS: {
					arg1.setColor(new Color(220, 0, 220));
				}
				default: arg1.drawString("Merge", arg2-2, arg3+8);
					}
				else  {
					arg1.setFont(new Font("Arial", Font.BOLD, 20));
					arg1.drawString("=", arg2, arg3+22);
				}
				arg1.setFont(startFont);
				
			}

			

			
			}

		



		
		
/**draws the given array of strings as a line of text with several colors*/
public static void drawRainBowString(Graphics g, int x, int y,  String[] stringarr, Color[] colors, boolean down) {
	int ci=0;
	for(String st1: stringarr) {
		if (ci>=colors.length) ci=0;
		FontMetrics fm = g.getFontMetrics();
		g.setColor(colors[ci]);
		g.drawString(st1, x, y);
		if (down) {
			y+=fm.getHeight();
		} else
			x+=fm.stringWidth(st1);
		ci++;
	}
}
		
		
	}
	


