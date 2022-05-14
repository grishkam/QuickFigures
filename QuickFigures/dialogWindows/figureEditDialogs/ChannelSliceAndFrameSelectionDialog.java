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
 * Version: 2022.1
 */
package figureEditDialogs;

import channelMerging.MultiChannelImage;
import standardDialog.StandardDialog;
import standardDialog.channels.ChannelEntryBox;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;

/**A dialog that promts the user to select a channel, a slice, a frame or all three from a multidimensional image*/
public class ChannelSliceAndFrameSelectionDialog extends StandardDialog {

	/**
	 * 
	 */
	{this.setModal(true);this.setWindowCentered(true);}
	private static final long serialVersionUID = 1L;
	private int channel=0;
	private int frame;
	private int slice;
	
	MultiChannelImage mw;
	
	public ChannelSliceAndFrameSelectionDialog( int channel, int slice, int frame, MultiChannelImage mw) {
		this.mw=mw;
		this.setChannel(channel);
		this.setFrame(frame);
		this.setSlice(slice);
		
		
		
	}
	
	/**Adds the options for the channel selection dialog. if there are no possible options,
	 * returns false*/
	public boolean addOptions(boolean haschannel, boolean hasslice, boolean hasframe) {
		
			StandardDialog dialog = this;
			MultiChannelImage multiChannel = mw;
		
			
			if(multiChannel!=null&&multiChannel.nFrames()<2) {
				hasframe=false;
			}
			if(multiChannel!=null&&multiChannel.nSlices()<2) {
				hasslice=false;
			}
			
			
			if (haschannel) {
				addChannelSelectionToDialog(dialog, multiChannel, getChannel());
				
				}
		if (hasframe) {
			addFrameSelectionToDialog(dialog, multiChannel,  getFrame());
			
		}
		if (hasslice) {
			addSliceSelectionToDialog(dialog, multiChannel, getSlice());
			
		}
		if(hasslice==false&&hasframe==false&&haschannel==false) return false;
		return true;
	}

	public static void addChannelSelectionToDialog(StandardDialog dialog, MultiChannelImage multiChannel, int chan) {
		if (multiChannel==null) {
		dialog.add("chan", new NumberInputPanel("The channel ", chan));
}
		if (multiChannel!=null) {
			ChannelEntryBox box = new ChannelEntryBox(multiChannel.getChannelEntriesInOrder(), "Merge");
			 ChoiceInputPanel mergeCombo = new standardDialog.choices.ChoiceInputPanel("Channel", box);
			 
			 dialog.add("chan",mergeCombo);
		}
	}

	public static void addSliceSelectionToDialog(StandardDialog dialog, MultiChannelImage multiChannel, int slice) {
		if (multiChannel!=null&&multiChannel.nFrames()<150) {
			dialog.add("slice", new NumberInputPanel("The slice ", slice, true, true, 1, multiChannel.nSlices()));
		}
		else 
		dialog.add("slice", new NumberInputPanel("The slice ", slice));
	}

	public static void addFrameSelectionToDialog(StandardDialog dialog, MultiChannelImage multiChannel, int frame) {
		if (multiChannel!=null&&multiChannel.nFrames()<150) {
			dialog.add("frame", new NumberInputPanel("The frame ",frame, true, true, 1, multiChannel.nFrames()));
		}
		else 
		dialog.add("frame", new NumberInputPanel("The frame ", frame));
	}
	
	@Override 
	public void onOK() {
		this.setFrame(this.getNumberInt("frame"));
		this.setSlice(this.getNumberInt("slice"));
		if (mw==null)
		this.setChannel(this.getNumberInt("chan"));
		else this.setChannel(this.getChoiceIndex("chan"));
	}
	
	public void show3DimensionDialog() {
		if (addOptions(true, true, true))
		this.showDialog();
	}
	public void show2DimensionDialog() {
		if (addOptions(false, true, true))
		this.showDialog();
	}
	
	/**if multiple time frames are available, shows a dialog for changing the slice indices
	 * of the panels
	 */
	public boolean showFrameDialog() {
		if (addOptions(false, false, true))
			{this.showDialog();
			return true;}
		
		return false;
	}
	
	/**if multiple slices are available, shows a dialog for changing the slice indices
	 * of the panels*/
	public boolean showSliceDialog() {
		if (addOptions(false, true, false))
		 {
			this.showDialog();
		 	return true;
		 	}
		 
		 return false;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getFrame() {
		return frame;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public int getSlice() {
		return slice;
	}

	public void setSlice(int slice) {
		this.slice = slice;
	}

}
