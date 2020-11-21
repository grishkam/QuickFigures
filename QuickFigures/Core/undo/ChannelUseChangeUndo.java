package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import channelMerging. ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;

/**An undoable edit for changes in channel use instructions*/
public class ChannelUseChangeUndo extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  ChannelUseInstructions iChannels;
	private  ChannelUseInstructions fChannels;
	private ChannelUseInstructions chanUse;
	private ImageDisplayLayer display;
	
	
	public ChannelUseChangeUndo(ImageDisplayLayer mw) {
		this(mw.getPanelList().getChannelUseInstructions());
		this.display=mw;
	}
	public ChannelUseChangeUndo( ChannelUseInstructions l) {
		this.chanUse=l;
		iChannels=chanUse.duplicate();
				
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
	
	/**changes the setters of the channel use instructions to conform with undo/redo*/
	private void match(ChannelUseInstructions aChannels2) {
		aChannels2.makePartialMatching(chanUse);
		aChannels2.getFrameUseInstructions().giveAllTraitsTo(chanUse.getFrameUseInstructions());
		aChannels2.getSliceUseInstructions().giveAllTraitsTo(chanUse.getSliceUseInstructions());
	}
	
	
	public static CombinedEdit createForMany(ArrayList<? extends ImageDisplayLayer> mws ) {
		CombinedEdit ce2=new CombinedEdit();
		for(ImageDisplayLayer mw: mws) {
			ce2.addEditToList(new ChannelUseChangeUndo(mw));
		}
		
		return ce2;
	}
	
}
