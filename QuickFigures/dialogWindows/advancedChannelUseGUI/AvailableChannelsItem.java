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
 * Version: 2022.0
 */
package advancedChannelUseGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import channelMerging.ChannelEntry;
import figureOrganizer.PanelList;
import figureOrganizer.PanelListElement;
import multiChannelFigureUI.BasicChannelEntryMenuItem;

/**a menu item that adds and removes a channel entry from an panel
 * 
 * @see ChannenEntry
 * 
 * @see PanelListElement
 * */
public class AvailableChannelsItem extends BasicChannelEntryMenuItem{

/**
 * 
 */
private static final long serialVersionUID = 1L;
private PanelListElement panel;


public AvailableChannelsItem(ChannelEntry ce, PanelListElement e) {
	super(ce);
	this.panel=e;
	this.updateFont();
	this.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			onAction() ;
			
		}});
}

public void onAction() {
	boolean excluded = this.isExcludedChannel();
	setChannelIsIncluded(excluded, panel);
	this.updateFont();
	
}

/**
 either adds the target channel to the panel or removes it
 */
public void setChannelIsIncluded(boolean include, PanelListElement panel) {
	
	if (include)
		{
		panel.addChannelEntry(entry);
		}
	else {
		panel.removeChannelEntry(entry);
		ChannelEntry eEntry = PanelList.findEquivalent(entry, panel.getChannelEntries());
		panel.removeChannelEntry(eEntry);
	}
	panel.purgeDuplicateChannelEntries();
}


/**returns true if the channel is not present in the panel*/
@Override
protected boolean isExcludedChannel() {
	if (this.panel!=null) {
		return !panel.hasChannel(entry.getOriginalChannelIndex());
	}
	return false;
}}