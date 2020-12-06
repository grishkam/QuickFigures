/**
 * Author: Greg Mazo
 * Date Modified: Dec 6, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package figureEditDialogs;

import java.util.ArrayList;

import javax.swing.JTabbedPane;

import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import channelMerging.SubStackSelectionInstructions;
import channelMerging.SubStackSelectionInstructions.FrameUseInstructions;
import channelMerging.SubStackSelectionInstructions.SliceUseInstructions;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.InfoDisplayPanel;
import standardDialog.StandardDialog;
import standardDialog.StringInputPanel;
import utilityClasses1.NumberUse;

/**
 This dialog allows a user to input which frames and slices 
 from a multidimensional image will be used. 
 */
public class SubStackDialog extends StandardDialog {

	private ArrayList<? extends ImageDisplayLayer> displayLayers;
	private boolean empty=true;
	private PanelStackDisplayOptions parentDialog;

	/**
	 constructor that creates a dialog for many display layers
	 */
	public SubStackDialog(ArrayList<? extends ImageDisplayLayer> multiChannelDisplaysInOrder) {
		this.displayLayers=multiChannelDisplaysInOrder;
		
		for(ImageDisplayLayer display1: displayLayers) {
			boolean makeDialog=display1.getMultiChannelImage().nFrames()>1||display1.getMultiChannelImage().nSlices()>1;
			if (makeDialog) 
				{
				SingleInstructionDialog part1 = new SingleInstructionDialog(display1);
				this.addSubordinateDialog(display1.getName(), part1);
				
				empty=false;
				}
		}	
		
		JTabbedPane tabs = this.getOptionDisplayTabs();
		if (!empty)tabs.setSelectedIndex(1);
	
	}

	/**returns true if no selection tabs have been added. If the images given do not
	 * have more than one slice or frame, this will be true*/
	public boolean isEmpty() {
		return empty;
	}

	/**
	if this will be included in a larger dialog
	 */
	public void setParentDialog(PanelStackDisplayOptions panelStackDisplayOptions) {
		parentDialog=panelStackDisplayOptions;
		
	}
	
	private static final long serialVersionUID = 1L;
	
	public class SingleInstructionDialog extends GraphicItemOptionsDialog {

		
		private ImageDisplayLayer display1;
		private boolean frames;
		private boolean slices;
		private ChannelUseInstructions instructions;

		public SingleInstructionDialog(ImageDisplayLayer display1) {
			this.display1=display1;
			frames=display1.getMultiChannelImage().nFrames()>1;
			slices=display1.getMultiChannelImage().nSlices()>1;
			instructions=display1.getPanelList().getChannelUseInstructions();
			
			if (slices) {
				StringInputPanel sliceInput = new StringInputPanel("Slices Chosen", instructions.getSliceUseInstructions().selectedString());
				this.add("Slice", sliceInput);
			}
			
			if (frames) {
				StringInputPanel frameInput = new StringInputPanel("Frames Chosen", instructions.getFrameUseInstructions().selectedString());
				this.add("Frame", frameInput);
			}
			
			addNumerRangeExamples();
			
		}

		/**
		adds text explaining how to input the numbers
		 */
		public void addNumerRangeExamples() {
			InfoDisplayPanel tip = new InfoDisplayPanel("Input the desired stack indices" , "in field above");
			InfoDisplayPanel examples = new InfoDisplayPanel("For Example" , "  '1, 4-6'= [1,4,5,6] and '2x4'= [2, 4, 6, 8]");
			
			this.add("t1", tip);
			this.add("t2", examples);
		}
		
		protected void setItemsToDiaog() {
			if (slices) {
				String a = this.getString("Slice");
				ArrayList<Integer> newSlices = NumberUse.integersFromString(a);
				SliceUseInstructions n = SubStackSelectionInstructions.createSliceUseInstructions(newSlices );
				if (0<n.estimateNUsed(display1.getMultiChannelImage()))
					instructions.getSliceUseInstructions().resetSelectedIndex(newSlices);
				
				
			}
			if (frames) {
				String b = this.getString("Frame");
				ArrayList<Integer> newFrame = NumberUse.integersFromString(b);
				FrameUseInstructions n = SubStackSelectionInstructions.createFrameUseInstructions(newFrame);
				if (0<n.estimateNUsed(display1.getMultiChannelImage()))
					instructions.getFrameUseInstructions().resetSelectedIndex(newFrame);
			}
			if (parentDialog!=null) 
				parentDialog.afterEachItemChange();
			display1.updatePanels();
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}



}
