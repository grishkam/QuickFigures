package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import channelMerging. ChannelUseInstructions;
import channelMerging.MultiChannelWrapper;
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
		this(mw.getStack().getChannelUseInstructions());
		this.display=mw;
	}

	public void establishFinalLocations() {
		fChannels=chanUse.duplicate();
		
	}
	
	public void undo() {
		iChannels.makePartialMatching(chanUse);
		if(display!=null) display.updatePanels();
	}
	
	public void redo() {
		fChannels.makePartialMatching(chanUse);
		if(display!=null) display.updatePanels();
		}
	
	public static CompoundEdit2 createForMany(ArrayList<? extends PanelStackDisplay> mws ) {
		CompoundEdit2 ce2=new CompoundEdit2();
		for(PanelStackDisplay mw: mws) {
			ce2.addEditToList(new ChannelUseChangeUndo(mw));
		}
		
		return ce2;
	}
	


}
