package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import channelMerging. ChannelUseInstructions;
import channelMerging.PanelStackDisplay;

public class ChannelUseChangeUndo extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  ChannelUseInstructions iChannels;
	private  ChannelUseInstructions fChannels;
	private ChannelUseInstructions chanUse;
	private PanelStackDisplay display;
	
	public ChannelUseChangeUndo( ChannelUseInstructions l) {
		this.chanUse=l;
				iChannels=chanUse.duplicate();
				
	}
	
	public ChannelUseChangeUndo(PanelStackDisplay mw) {
		this(mw.getPanelList().getChannelUseInstructions());
		this.display=mw;
	}

	public void establishFinalLocations() {
		fChannels=chanUse.duplicate();
		
	}
	
	public void undo() {
		match(iChannels);
		
		if(display!=null) display.updatePanels();
	}

	public void redo() {
		match(fChannels);
		if(display!=null) display.updatePanels();
		}
	
	private void match(ChannelUseInstructions aChannels2) {
		aChannels2.makePartialMatching(chanUse);
		aChannels2.getFrameUseInstructions().giveAllTraitsTo(chanUse.getFrameUseInstructions());
		aChannels2.getSliceUseInstructions().giveAllTraitsTo(chanUse.getSliceUseInstructions());
	}
	

	
	public static CombinedEdit createForMany(ArrayList<? extends PanelStackDisplay> mws ) {
		CombinedEdit ce2=new CombinedEdit();
		for(PanelStackDisplay mw: mws) {
			ce2.addEditToList(new ChannelUseChangeUndo(mw));
		}
		
		return ce2;
	}
	


}
