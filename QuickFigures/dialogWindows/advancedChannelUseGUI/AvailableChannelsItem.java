/**
 * Author: Greg Mazo
 * Date Modified: Dec 5, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package advancedChannelUseGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import channelMerging.ChannelEntry;
import genericMontageKit.PanelList;
import genericMontageKit.PanelListElement;
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