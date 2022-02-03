/**
 * Author: Greg Mazo
 * Date Modified: Feb 1, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package lineprofile;

import java.util.ArrayList;

import channelMerging.ChannelEntry;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import multiChannelFigureUI.BasicChannelEntryMenuItem;
import undo.AbstractUndoableEdit2;

/**
 A menu that is used to control which channels are shown in a profile line plot
 */
public class ProfileLineChannelMenu extends SmartJMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProfileLine profileLine;
	
	public  ProfileLineChannelMenu(ProfileLine profile) {
		super("Plot Channels");
		this.profileLine=profile;
		
		
		ArrayList<ChannelMenuItem> m =new  ArrayList<ChannelMenuItem> ();
		for(ChannelEntry ce: profileLine.getSourceDisplay().getMultiChannelImage().getChannelEntriesInOrder()) { 
			ChannelMenuItem e = new ChannelMenuItem(ce);
			m.add(e);
			this.add(e);
			
		}
		
	}
	
	public class ChannelMenuItem extends BasicChannelEntryMenuItem {

		/**
		 * @param ce
		 */
		public ChannelMenuItem(ChannelEntry ce) {
			super(ce);
			boolean strike=isExcludedChannel();
			super.setSelected(!strike);
			updateFont();
			
			
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean isExcludedChannel() {
			return !profileLine.getChannelChoices().contains(entry.getOriginalChannelIndex());
		} 
		
	
		protected  AbstractUndoableEdit2 onPressAction() {
			if(this.isExcludedChannel())
				profileLine.addChannelToPlot(entry);
			else profileLine.removeChannelFromPlot(entry.getOriginalChannelIndex());
			profileLine.updatePlot();
			updateFont();
			return null;
		}	
	}

}
