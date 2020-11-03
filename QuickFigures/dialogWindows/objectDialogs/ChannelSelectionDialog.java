package objectDialogs;

import channelMerging.MultiChannelWrapper;
import standardDialog.ChannelEntryBox;
import standardDialog.ComboBoxPanel;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;

public class ChannelSelectionDialog extends StandardDialog {

	/**
	 * 
	 */
	{this.setModal(true);}
	private static final long serialVersionUID = 1L;
	private int channel=0;
	private int frame;
	private int slice;
	
	MultiChannelWrapper mw;
	
	public ChannelSelectionDialog( int channel, int slice, int frame, MultiChannelWrapper mw) {
		this.mw=mw;
		this.setChannel(channel);
		this.setFrame(frame);
		this.setSlice(slice);
		
	
		
	}
	
	public void addOptions(boolean haschannel, boolean hasslice, boolean hasframe) {
			if (haschannel &&mw==null) {
			this.add("chan", new NumberInputPanel("The channel ", getChannel()));
		}
			if (haschannel &&mw!=null) {
				ChannelEntryBox box = new ChannelEntryBox(mw.getChannelEntriesInOrder(), "Merge");
				 ComboBoxPanel mergeCombo = new standardDialog.ComboBoxPanel("Channel", box);
				 
				 this.add("chan",mergeCombo);
			}
			
			
			
		if (hasframe) {
			this.add("frame", new NumberInputPanel("The frame ", getFrame()));
			
		}
		if (hasslice) {
			this.add("slice", new NumberInputPanel("The slice ", getSlice()));
			
		}
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
		addOptions(true, true, true);
		this.showDialog();
	}
	public void show2DimensionDialog() {
		addOptions(false, true, true);
		this.showDialog();
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
