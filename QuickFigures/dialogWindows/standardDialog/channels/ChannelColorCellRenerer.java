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
package standardDialog.channels;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**Renders the channel color cells in a channel selection combo box*/
public class ChannelColorCellRenerer extends BasicComboBoxRenderer {
	
	/**
	 * 
	 */
	private final ChannelEntryBox channelEntryBox2;
	/**
	 * 
	 */
	public int channelNumber=0;
	private ChannelEntryBox box;
	public int theindex=-1;
	
	private static final long serialVersionUID = 1L;
	
	public ChannelColorCellRenerer(ChannelEntryBox channelEntryBox) {
		channelEntryBox2 = channelEntryBox;
	
	}


	public void paint(Graphics g) {
		super.paint(g);
		if(theindex==-1) theindex=box.getSelectedIndex();
		int dim=theindex;
		if (dim==-1) {dim=theindex;}
		//if (this.channelNumber==-1||channelNumber> theChannelentries.size()) this.channelNumber=box.getSelectedIndex();
		//dim = this.channelNumber;
	
		
		if (channelEntryBox2.theChannelentries.size()>0&&dim>0) ChannelEntryBox.drawRainbowString(g, channelEntryBox2.theChannelentries.get(dim-1), 1,this.getFont().getSize()+1, box.nullString);
		else {ChannelEntryBox.drawRainbowString(g, null, 1,this.getFont().getSize()+1, box.nullString);}
	
	}
	
	public  Component	getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		theindex=index;
		Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (out instanceof ChannelColorCellRenerer) {
			ChannelColorCellRenerer c=(ChannelColorCellRenerer) out;
			c.channelNumber=theindex-1;
				{this.setFont(this.getFont().deriveFont(Font.BOLD).deriveFont((float)20.0));}
			if (cellHasFocus) {
				c.channelNumber=theindex-1;
	
				}
		}
	
		//Font font=new Font(out.getFont().getFamily(), index, out.getFont().getSize());
		//out.setFont(font);
		return out;
			}

	public ChannelEntryBox getBox() {
		return box;
	}

	public void setBox(ChannelEntryBox box) {
		this.box = box;
		this.channelNumber=box.getSelectedIndex();
	}
}