/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package channelLabels;

import java.awt.GridBagConstraints;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JTabbedPane;

import channelMerging.ChannelEntry;
import channelMerging.MultiChannelImage;
import fLexibleUIKit.MenuItemMethod;
import figureEditDialogs.ChannelLabelDialog;
import figureEditDialogs.ChannelLabelPropertiesDialog;
import figureEditDialogs.ChannelSliceAndFrameSelectionDialog;
import figureEditDialogs.TextLineDialogForChenLabel;
import genericMontageKit.PanelList;
import genericMontageKit.PanelListElement;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import objectDialogs.MultiTextGraphicSwingDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialog;
import standardDialog.StandardDialogListener;
import undo.CombinedEdit;
import undo.UndoAbleEditForRemoveItem;
import utilityClassesForObjects.AttachmentPosition;

/**A class containing methods for adding, accessing, removing and editing the channel labels to a figure.
 * 
  */
public class ChannelLabelManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**A dialog for editing multiple labels*/
	private transient MultiTextGraphicSwingDialog mt;
	
	private PanelList panelList;//the panels
	private GraphicLayer layer;//the target layer where the labels are kept
	private ChannelLabelProperties channelLabelProp;
	
	/**stores the last label to be removed after a call to eliminate all channel labels
	   the font of this label may then be reused when replacement labels are creates*/
	private static TextGraphic fossilLabel;//
	
	private MultichannelDisplayLayer source;

	/**constructor for the label manager. */
	public ChannelLabelManager( MultichannelDisplayLayer multichannelImageDisplay, PanelList stack, GraphicLayer layer) {
		this.source= multichannelImageDisplay;
		this.setStack(stack);
		this.setLayer(layer);
	}
	
	public ChannelLabelProperties getChannelLabelProp() {
		if (channelLabelProp==null) channelLabelProp=new ChannelLabelProperties();
		return channelLabelProp;
	}
	
	/**creates a channel label and adds it to the current layer*/
	public ChannelLabelTextGraphic generateChanelLabel(PanelListElement slice) {
		ChannelLabelTextGraphic cltg = new ChannelLabelTextGraphic(this.getChannelLabelProp());
		cltg.setPanel(slice);
		cltg.setParaGraphToChannels();
		cltg.setName("channel label");
				if (getFossilLabel()!=null) {cltg.copyAttributesFrom(getFossilLabel());
											cltg.setAttachmentPosition(getFossilLabel().getAttachmentPosition());
											} else {
												cltg.setAttachmentPosition(AttachmentPosition.defaultPanelLabel());
											}
				
		
		slice.setChannelLabelDisplay(cltg);
		if (getLayer()!=null)getLayer().add(cltg);//adds the label to the image
		/**Attaches this label to the panel*/
		if (slice.getImageDisplayObject() instanceof ImagePanelGraphic) {
			ImagePanelGraphic panel=(ImagePanelGraphic) slice.getImageDisplayObject();
			panel.addLockedItem(cltg);
		}
		
		return cltg;
		
	}

	/**The last label to be removed in the previous round of removal. 
	  will be used as a model if labels are immediately recreated*/
	public TextGraphic getFossilLabel() {
		return fossilLabel;
	}
	
	/**Removes the channel labels from the layer and returns them as an array*/
	@MenuItemMethod(menuActionCommand = "labelgone", menuText = "Eliminate Channel Labels", subMenuName="Channel Labels")
	public CombinedEdit eliminateChanLabels() {
		CombinedEdit output = new CombinedEdit();
		if(panelList==null) return output ; 
		ArrayList<ChannelLabelTextGraphic> arr =getPanelList().getChannelLabels();
	
		for(ChannelLabelTextGraphic g:arr) {
			output.addEditToList((new UndoAbleEditForRemoveItem(getLayer(), g)));
			if (this.getLayer()!=null)getLayer().remove(g);
			fossilLabel=g;
		}
		return output;
		
	}
	
	/**if a channel panel with the given channel slice and frame exists, this generates a label for it*/
	void generateSingleChannelPanelLabel(int channel, int slice, int frame) {
		PanelListElement panel = getPanelList().getOrCreateChannelPanel(getMultiChannel(),channel, slice, frame);
		 if (panel!=null)this.generateChanelLabel(panel);
	}
	
	/**if a merge panel with the given slice and frame exists, this generates a label for it*/
	void generateSingleChannelMergeLabel( int slice, int frame) {
		PanelListElement panel =  getPanelList().getOrCreateMergePanel(getMultiChannel(), slice, frame);
		 if (panel!=null)this.generateChanelLabel(panel);
	}
	
	/**generates channel labels for every single panel*/
	@MenuItemMethod(menuActionCommand = "channeoLabels", menuText = "Generate Channel Labels", subMenuName="Channel Labels")
	public ArrayList<ChannelLabelTextGraphic> generateChannelLabels() {
		ArrayList<ChannelLabelTextGraphic> output=new ArrayList<ChannelLabelTextGraphic>();
		for(PanelListElement slice: getPanelList().getPanels()) {
			output.add(generateChanelLabel(slice));
		}
		return output;
	}
	
	/**Creates channel labels but only for panels in the first stack slice or first frame*/
	@MenuItemMethod(menuActionCommand = "channeoLabels2", menuText = "Generate Channel Labels (For first slice only)", subMenuName="Channel Labels")
	public ArrayList<ChannelLabelTextGraphic> generateChannelLabels2() {
		ArrayList<ChannelLabelTextGraphic> output=new ArrayList<ChannelLabelTextGraphic>();
		for(PanelListElement slice: getPanelList().getPanels()) {
			if(slice.targetFrameNumber>firstFrameIndex()
					||
					slice.targetSliceNumber>firstSliceIndex()) continue;
			output.add(generateChanelLabel(slice));
		}
		return output;
	}
	
	

	/**
	
	 */
	public int firstSliceIndex() {
		int first = source.getPanelList().getChannelUseInstructions().getSliceUseInstructions().getFirstIndex();
		return first;
	}

	/**
	 
	 */
	public int firstFrameIndex() {
		int first = source.getPanelList().getChannelUseInstructions().getFrameUseInstructions().getFirstIndex();
		return first;
	}
	
	/**displays a dialog for the addition of a single merge panel*/
	@MenuItemMethod(menuActionCommand = "1mergeL", menuText = "Create 1 Merge Panel Label", subMenuName="Channel Labels")
	public void addSingleMergeLabel() {
		ChannelSliceAndFrameSelectionDialog dia = new ChannelSliceAndFrameSelectionDialog(1,1,1, getMultiChannel());
		dia.show2DimensionDialog();
		generateSingleChannelMergeLabel( dia.getSlice(),dia.getFrame());
	}
	
	/**displays a dialog for the addition of a single channel panel*/
	@MenuItemMethod(menuActionCommand = "1chanL", menuText = "Create 1 Channel Panel Label", subMenuName="Channel Labels")
	public void addSingleChannelLabel() {
		ChannelSliceAndFrameSelectionDialog dia = new ChannelSliceAndFrameSelectionDialog(0,1,1, getMultiChannel());
		dia.show3DimensionDialog();
		generateSingleChannelPanelLabel( dia.getChannel(), dia.getSlice(),dia.getFrame());
	}
	
	/**displays the channel label properties dialog to the user*/
	@MenuItemMethod(menuActionCommand = "chantype", menuText = "Merge Label Options", subMenuName="Channel Labels")
	public void showChannelLabelPropDialog() {
		ChannelLabelPropertiesDialog dia = new  ChannelLabelPropertiesDialog(this.getChannelLabelProp());
		dia.setLabelItems(getPanelList().getChannelLabels());
		
		JTabbedPane tabs = dialogForChannelEntries(getMultiChannel().getChannelEntriesInOrder()).getOptionDisplayTabs();
		dia.getOptionDisplayTabs().addTab("Channel Names", tabs);
		dia.showDialog();
	}

	/**returns the multichannel image being used*/
	public MultiChannelImage getMultiChannel() {
		return source.getMultiChannelImage();
	}

	public GraphicLayer getLayer() {
		return layer;
	}

	public void setLayer(GraphicLayer layer) {
		this.layer = layer;
	}

	public PanelList getPanelList() {
		return panelList;
	}

	public void setStack(PanelList stack) {
		this.panelList = stack;
	}
	
	/**displays the channel naming dialog to the user*/
	public void nameChannels() {
		nameChannels(getMultiChannel().getChannelEntriesInOrder());
	}
	/**displays a channel naming dialog to the user*/
	public void nameChannels(ArrayList<ChannelEntry> entries) {
		dialogForChannelEntries(entries).makeVisible();;
		
	}

	/**method calls a dialog that allows the user to change the text lines of the channel labels*/
	public StandardDialog dialogForChannelEntries(ArrayList<ChannelEntry> entries) {
		return TextLineDialogForChenLabel.showMultiTabDialogDialogss(entries, this.getChannelLabelProp(), new StandardDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				getPanelList().resetChannelEntriesForAll(getMultiChannel());
			
				getPanelList().updateAllPanelsWithImage(getMultiChannel());
				
			}});
	}

	/**displays a dialog that allows the editing of all the channel labels that are 
	 * handled by this channel label manager*/
	public void showEditAllChannelLabelsDialog() {
		ArrayList<ChannelLabelTextGraphic> labels = this.getPanelList().getChannelLabels();
		
		mt = new MultiTextGraphicSwingDialog( labels, true);
		
		addAutomaticLayoutSpaceUpdatesToDialog(mt, getLayer());
		
		
		/**these lines add the label text options to the dialog*/
		ChannelLabelTextGraphic ml = getMergeLabel(labels);
		if (ml==null) ml=labels.get(0);
			ChannelLabelDialog clDialog = new ChannelLabelDialog(ml, true);
			//clDialog.addDialogListener(listener1);
			GridBagConstraints c = new GridBagConstraints();
			c.gridx=1;
			c.gridy=3;
			c.gridheight=4;
			c.gridwidth=6;
			mt.add(clDialog.getTheTabs(), c);
		
			
		mt.showDialog();
	}

	/**
	 given the multi text dialog given, adds a feature that updates the label spaces 
	 in the figure layout after each edit to ensure that the layout fits the labels.
	 * @param graphicLayer 
	 */
	protected void addAutomaticLayoutSpaceUpdatesToDialog(MultiTextGraphicSwingDialog mt, GraphicLayer graphicLayer) {
		StandardDialogListener listener1 = new StandardDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				try {
					FigureOrganizingLayerPane f = FigureOrganizingLayerPane.findFigureOrganizer(graphicLayer);
					if (f!=null)
							{
						for(TextGraphic t:mt.getAllEditedItems()) 
						f.getLayout().getEditor().expandSpacesToInclude(f.getLayout(), t.getBounds());
						
							}
				} catch (Exception e) {
				
					e.printStackTrace();
				}
			}};
		mt.addDialogListener(listener1);
	}

	/**returns all of the merge panel labels in the list*/
	private static ChannelLabelTextGraphic getMergeLabel(ArrayList<ChannelLabelTextGraphic> labels) {
		for(ChannelLabelTextGraphic l:labels) {
			if (l.isThisMergeLabel()) return l;
		}
		return null;
	}

	
	
}
