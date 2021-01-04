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
package advancedChannelUseGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import channelMerging.ChannelEntry;
import standardDialog.channels.ChannelEntryBox;

/**Renders the channel color cells in a channel selection JList*/
public class ChannelColorCellRenerer2 extends DefaultListCellRenderer {

	
	private ChannelListDisplay box;
	public int theindex=-1;// current index is stored here
	public int channelNumber=0;
	private static final long serialVersionUID = 1L;
	
	public ChannelColorCellRenerer2(ChannelListDisplay channelEntryBox) {
		box = channelEntryBox;
	
	}


	public void paint(Graphics g) {
		if(theindex==-1) theindex=box.getSelectedIndex();
		int dim=theindex;
		if (dim==-1) {dim=theindex;}
		
		
		
		if (box.elements.size()>0&&dim>-1) {
			ChannelEntry chan = box.elements.get(dim);
			if(box.getSelectedIndex()==dim) {
			g.setColor(Color.blue);
			String realChannelName = chan.getRealChannelName();
			if(realChannelName==null) {realChannelName ="Channel #"+chan.getOriginalChannelIndex();}
			g.fillRect(2, 2, (int)g.getFontMetrics().getStringBounds(realChannelName, g).getWidth(), g.getFont().getSize());
			}
			ChannelEntryBox.drawRainbowString(g, chan, 1,this.getFont().getSize()+1, "none");
			
		}
		else {ChannelEntryBox.drawRainbowString(g, null, 1,this.getFont().getSize()+1, "none");}
		
	}
	
	public  Component	getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		theindex=index;
		Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (out instanceof ChannelColorCellRenerer2) {
			ChannelColorCellRenerer2 c=(ChannelColorCellRenerer2) out;
			c.channelNumber=theindex;
			
			if (cellHasFocus) {
				c.channelNumber=theindex;
	
				}
		}
	
		return out;
			}

	public ChannelListDisplay getBox() {
		return box;
	}

	public void setBox(ChannelListDisplay box) {
		this.box = box;
		this.channelNumber=box.getSelectedIndex();
	}
}