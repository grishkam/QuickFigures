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

import java.util.ArrayList;

import javax.swing.JTabbedPane;

import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import channelMerging.SubStackSelectionInstructions;
import channelMerging.SubStackSelectionInstructions.FrameUseInstructions;
import channelMerging.SubStackSelectionInstructions.SliceUseInstructions;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.StandardDialog;
import standardDialog.strings.InfoDisplayPanel;
import standardDialog.strings.StringInputPanel;
import utilityClasses1.NumberUse;

/**
 This dialog allows a user to input which frames and slices 
 from a multidimensional image will be used. 
 */
public class SubStackDialog extends StandardDialog {

	private ArrayList<? extends ImageDisplayLayer> displayLayers;
	private boolean empty=true;
	private PanelStackDisplayOptions parentDialog;
	private boolean showChannelC=false;

	/**
	 constructor that creates a dialog for many display layers
	 */
	public SubStackDialog(ArrayList<? extends ImageDisplayLayer> multiChannelDisplaysInOrder) {
		setup(multiChannelDisplaysInOrder);
	
	}
	
	/**
	 constructor that creates a dialog for many display layers
	 * @param string 
	 */
	public SubStackDialog(ImageDisplayLayer item, boolean m, String string) {
		ArrayList<ImageDisplayLayer> displayLayers=new ArrayList<ImageDisplayLayer>();
		displayLayers.add(item);
		this.setTitle(string);
		if (m) {
			this.setWindowCentered(m);
			this.setModal(m);
		}
		
		
		this.setup(displayLayers);
		
		
	}

	/**creates tabs for multiple multi-dimensional images
	 * @param multiChannelDisplaysInOrder
	 */
	private void setup(ArrayList<? extends ImageDisplayLayer> multiChannelDisplaysInOrder) {
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
	
	/**A dialog for selecting frames and slices for a single multidimensional image*/
	public class SingleInstructionDialog extends GraphicItemOptionsDialog {

		
		private ImageDisplayLayer display1;
		private boolean frames;
		private boolean slices;
		private ChannelUseInstructions instructions;
		
		/**Panels to display the result of the users input formula*/
		private InfoDisplayPanel framesWanted;
		private InfoDisplayPanel slicesWanted;

		public SingleInstructionDialog(ImageDisplayLayer display1) {
			this.display1=display1;
			frames=display1.getMultiChannelImage().nFrames()>1;
			slices=display1.getMultiChannelImage().nSlices()>1;
			instructions=display1.getPanelList().getChannelUseInstructions();
			
			
			if (showChannelC) {
				PanelStackDisplayOptions.addMergeHandlingToDialog(this, instructions);
			}
			if (slices) {
				StringInputPanel sliceInput = new StringInputPanel("Slices", instructions.getSliceUseInstructions().selectedString());
				this.add("Slice", sliceInput);
				slicesWanted= new InfoDisplayPanel("You want?", instructions.getSliceUseInstructions().selectedString());
				this.add("UserS", slicesWanted);
			}
			
			if (frames) {
				StringInputPanel frameInput = new StringInputPanel("Frames", instructions.getFrameUseInstructions().selectedString());
				this.add("Frame", frameInput);
				framesWanted = new InfoDisplayPanel("You want?", instructions.getSliceUseInstructions().selectedString());
				this.add("UserF", framesWanted);
			}
			
			
			
			addNumerRangeExamples();
			
		}

		/**
		adds text explaining how to input the numbers
		 */
		public void addNumerRangeExamples() {
			InfoDisplayPanel tip = new InfoDisplayPanel("Choose which slices to use" , "in field above");
			InfoDisplayPanel examples = new InfoDisplayPanel("For Example" , "  '1, 4-6'= [1,4,5,6] and '2x4'= [2, 4, 6, 8]");
			InfoDisplayPanel examples2 = new InfoDisplayPanel("For Example" , "  '1 x 3 x 3'= [1,4,7] and '10 x 2 x 3'= [10,12,14]");
			
			this.add("t1", tip);
			this.add("t2", examples);
			this.add("t2", examples2);
		}
		
		protected void setItemsToDiaog() {
			if (slices) {
				String a = this.getString("Slice");
				ArrayList<Integer> newSlices = NumberUse.integersFromString(a);
				SliceUseInstructions n = SubStackSelectionInstructions.createSliceUseInstructions(newSlices );
				slicesWanted.setContentText(n.selectedString());
				if (0<n.estimateNUsed(display1.getMultiChannelImage()))
					instructions.getSliceUseInstructions().resetSelectedIndex(newSlices);	
			}
			
			if (frames) {
				String b = this.getString("Frame");
				ArrayList<Integer> newFrame = NumberUse.integersFromString(b);
				FrameUseInstructions n = SubStackSelectionInstructions.createFrameUseInstructions(newFrame);
				framesWanted.setContentText(n.selectedString());
				if (0<n.estimateNUsed(display1.getMultiChannelImage()))
					instructions.getFrameUseInstructions().resetSelectedIndex(newFrame);
			}
			
			if (showChannelC) {
				PanelStackDisplayOptions.setMergeHandlingToDialog(this, instructions);
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
