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
 * Date Modified: Dec 6, 2020
 * Version: 2021.1
 */
package figureEditDialogs;

import java.util.ArrayList;

import appContext.ImageDPIHandler;
import channelMerging.ChannelEntry;
import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import channelMerging.MultiChannelImage;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import figureOrganizer.PanelManager;
import graphicActionToolbar.CurrentFigureSet;
import imageDisplayApp.CanvasOptions;
import imageMenu.CanvasAutoResize;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.StandardDialog;
import standardDialog.channels.ChannelEntryBox;
import standardDialog.channels.ChannelListChoiceInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.InfoDisplayPanel;
import storedValueDialog.StoredValueDilaog;
import ultilInputOutput.FileChoiceUtil;
import undo.CanvasResizeUndo;
import undo.ChannelUseChangeUndo;
import undo.CombinedEdit;
import undo.PanelManagerUndo;
import applicationAdapters.DisplayedImage;

/**A dialog box used for two purposes. First, works as a set of options when 
creating a split channel figure. Second, displays channel use options regarding how the channels are
colored and combined in the channel panels and merge.*/
public class PanelStackDisplayOptions extends GraphicItemOptionsDialog {

	

	


	/**
	 * 
	 */
	public static final String FRAMES_AND_SLICES_TAB_NAME = "Frames and Slices";


	/**
	 * 
	 */
	public static final String 
			IDEAL_LAYOUT_SIZE_KEY = "mWidth", 
			PANEL_SIZE_KEY = "Panel Level Scale", 
			PREPROCESS_SCALE_KEY = "preScale",
			EXCLUDE_CHANNEL_KEY = "exclude channel panel ",
			MERGE_PANEL_POSITION_KEY = "merge",
			DONT_MERGE_CHANNELS_KEY = "don't merge",
			COLOR_MODE_KEY = "grey",
					MERGE_TO_EACH_CHANNEL_PANEL_KEY = "mergeeach",
					CHANNEL_ORDER_KEY = "channel order";

	
	private static final long serialVersionUID = 1L;
	
	/**set to true if the panel creation options will be included in this dialog*/
	boolean panelCreationIncluded=true;
	
	/**The initial settings for the dialog are based on a particular group of objects*/
	/**the primary target of the dialog. normally this is the first multichannel display in the figure*/
	private MultichannelDisplayLayer principalMultiChannel;
	/**the panel list that the dialog is based on*/
	private PanelList panelList;
	/**the panel manager that the dialog is based on*/
	private PanelManager panMan;
	
	/**additional targets of the dialog*/
	private ArrayList<MultichannelDisplayLayer> displural=new ArrayList<MultichannelDisplayLayer>();
	
	
	private DisplayedImage currentImageDisplay;

	private boolean canvasResizeUndoIncluded;
	
	/**The text for the combo */
	static final String[] MergePositions=new String[] {"Merged Panel Last", "Merged Panel First",  "Merge Panel Only (No Channel Panels)", "No Merge Panel (Channel Panels Only)"};


	
	
	public PanelStackDisplayOptions(MultichannelDisplayLayer display, PanelList stack, PanelManager panMan, boolean panelCreationIncluded) {
		stack.setChannelUpdateMode(false);//need to be set to false if using this dialog. otherwise the channels will not be updated according to the instructions
		
		this.panelCreationIncluded=panelCreationIncluded;
		this.principalMultiChannel=display;
		this.panMan=panMan;
		this.panelList=stack;
		if (this.panelList==null) this.panelList=display.getPanelList();
		
		if(this.panelCreationIncluded) this.setTitle("Recreate Panels");
			else this.setTitle("Channel Use");
		
		if (panMan==null) this.panMan=display.getPanelManager();
		this.addButton(recropButton());
		addOptionsToDialog();
		
		if(panelCreationIncluded)  {
			
			undo=PanelManagerUndo.createFor(display);
		} else
		undo=new CombinedEdit(new ChannelUseChangeUndo(principalMultiChannel));
	}

	public RecropButton recropButton() {
		return new RecropButton(this);
	}
	
	public ArrayList<MultichannelDisplayLayer> getAllDisplays() {
		ArrayList<MultichannelDisplayLayer> output=new ArrayList<MultichannelDisplayLayer>();
		output.add(this.getMainDisplayItem());
		output.addAll(getAdditionalDisplayLayers());
		return output;
		
	}
	
	public void addAditionalDisplays(Iterable<?> items) {
		for(Object i: items) {
			if (i==principalMultiChannel) continue;
			if (i instanceof MultichannelDisplayLayer && !displural.contains(i) ) {
				displural.add((MultichannelDisplayLayer) i);
			}
		}
		
		if(this.panelCreationIncluded) {
			CombinedEdit undo2 = PanelManagerUndo.createFor(principalMultiChannel);
			undo2.addEditToList(PanelManagerUndo.createForMany(displural));
			undo=undo2;
		} else
		{
			CombinedEdit undo2 = ChannelUseChangeUndo.createForMany(displural);
			undo2.addEditToList(new ChannelUseChangeUndo(principalMultiChannel) );
			undo=undo2;
		}
	}
	
	/**creates a string array with 'none' at the 0 index and channel names from afterwards*/
	private String[] createListWithNoChannelOption() {
		String[] old = MultichannelDisplayLayer.getChannelNames(principalMultiChannel.getMultiChannelImage());
		String[] output=new String[old.length+1];
		output[0]="none";
		for(int i=0; i<old.length; i++) {
			output[i+1]=old[i];
		}
		
		return output;
	}
	
	public void addOptionsToDialog() {
		String[] chanList=createListWithNoChannelOption();
		
		channelMerging.ChannelUseInstructions ins =panelList.getChannelUseInstructions();
		ArrayList<ChannelEntry> entries = principalMultiChannel.getMultiChannelImage().getChannelEntriesInOrder();
		
		if (this.panelCreationIncluded) {
			
			addMergeHandlingToDialog(this, ins);
			
			ArrayList<Integer> ex = ins.getExcludedChannelPanels();
				
			ChannelListChoiceInputPanel p=new ChannelListChoiceInputPanel("Exclude These Channel Panel(s)", entries, ex, "none selected");
			this.add(EXCLUDE_CHANNEL_KEY, p);
			this.add(CHANNEL_ORDER_KEY,  new InfoDisplayPanel("Order",ins.getChanPanelReorder().toString() ));
			
		
		}
		
		
		addChannelUseInstructionsToDialog(this, chanList, ins, entries);
		
		if (this.panelCreationIncluded)
			{
			this.add(PREPROCESS_SCALE_KEY, new NumberInputPanel("Scale Factor (Bilinear)", principalMultiChannel.getPreprocessScale(),3));
			
			
			}
		if (this.panelCreationIncluded) {
			this.add(PANEL_SIZE_KEY, new NumberInputPanel("Panel Pixel Density",ImageDPIHandler.getInchDefinition()/ principalMultiChannel.getPanelManager().getPanelLevelScale(),3));
			this.add(IDEAL_LAYOUT_SIZE_KEY,  new NumberInputPanel("Ideal Number Columns", ins.getIdealNumberOfColumns() ));
			
		}
	}

	/**
	Adds a combo box designed to select whether merged panel is included in creation
	 */
	static void addMergeHandlingToDialog(StandardDialog t, ChannelUseInstructions ins) {
		ChoiceInputPanel mergeh = new ChoiceInputPanel("Created Panels", MergePositions, ins.MergeHandleing);
		t.add(MERGE_PANEL_POSITION_KEY,mergeh );
	}

	/**
	adds the dialog items for the channel use instructions to the dialog
	 */
	protected static void addChannelUseInstructionsToDialog(StandardDialog t, String[] chanList, channelMerging.ChannelUseInstructions ins,
			ArrayList<ChannelEntry> entries) {
		
		
		ArrayList<Integer> nome = ins.getNoMergeChannels();
		
		ChannelListChoiceInputPanel p=new ChannelListChoiceInputPanel("Don't Merge These Channel(s)", entries, nome, "none selected");
		t.add(DONT_MERGE_CHANNELS_KEY, p);
		
	
		ChoiceInputPanel grey =new  standardDialog.choices.ChoiceInputPanel("Channel Color Mode ", new String[] {"Colors", "Greyscale"}, ins.channelColorMode);
		t.add(COLOR_MODE_KEY, grey);
		addIgnoreAfterBox(t, chanList, ins, entries);
		

		ChoiceInputPanel mergeCombo2;
		if (entries.size()>ins.eachMergeChannel){
			 mergeCombo2 = new standardDialog.choices.ChoiceInputPanel("Merge Each Channel Panel with", new ChannelEntryBox(ins.eachMergeChannel , entries));
		} else mergeCombo2 = new standardDialog.choices.ChoiceInputPanel("Merge Each Channel Panel with", chanList, ins.eachMergeChannel);
		t.add(MERGE_TO_EACH_CHANNEL_PANEL_KEY, mergeCombo2);
	}

	/**
	 * @param t
	 * @param chanList
	 * @param ins
	 * @param entries
	 */
	private static void addIgnoreAfterBox(StandardDialog t, String[] chanList,
			channelMerging.ChannelUseInstructions ins, ArrayList<ChannelEntry> entries) {
		ChoiceInputPanel mergeCombo;
		if (entries.size()>ins.ignoreAfterChannel){
			 mergeCombo = new standardDialog.choices.ChoiceInputPanel("Go up to channel", new ChannelEntryBox(ins.ignoreAfterChannel , entries));
		} else mergeCombo = new standardDialog.choices.ChoiceInputPanel("Go up to channel", chanList,ins.ignoreAfterChannel );
		t.add("ignoreAfter", mergeCombo);
	}
	
	/**based on the dialog, changes the options*/
	public void setItemsToDiaog()  {
		setItemstoDialog(panMan.getImageDisplayLayer(), panMan.getPanelList().getChannelUseInstructions(), false, true);

		for(MultichannelDisplayLayer p: displural) {
			if (p!=panMan.getImageDisplayLayer())
			setItemstoDialog(p, p.getPanelManager().getPanelList().getChannelUseInstructions(), true, false);
		}
	}
	
	/**changes the options based on the dialog */
	void setItemstoDialog(MultichannelDisplayLayer dis, ChannelUseInstructions ins, boolean eliminateChanLabel, boolean firstImage) {
	
		setChannelUseOptionsToDialog(this, ins);
		
		if (this.panelCreationIncluded) {
			
			setPanelCreationOptionsToDialog(dis, ins);
			
			recreateGraphicsFor(dis, ins, eliminateChanLabel, firstImage);
			
			
		}
		
		dis.getPanelManager().updatePanels();
		panMan.updatePanels();
		
		resizeCanvasToFit(dis);
	}

	/**
	 * @param dis
	 */
	protected void resizeCanvasToFit(MultichannelDisplayLayer dis) {
		if (getCurrentImageDisplay()!=null) {
			if (CanvasOptions.current.resizeCanvasAfterEdit)
							{
				CanvasResizeUndo undo4 = new CanvasAutoResize(false).makeAllVisible(getCurrentImageDisplay());
				if (this.undo instanceof CombinedEdit && !canvasResizeUndoIncluded) {
					((CombinedEdit) undo).addEditToList(undo4);
					canvasResizeUndoIncluded=true;
				}
							}
							FigureOrganizingLayerPane fo = FigureOrganizingLayerPane.findFigureOrganizer(dis);
							if (fo!=null) {
								 fo.fixLabelSpaces();
							}
							//new CanvasAutoTrim().trimCanvas(getCurrentImageDisplay());
							}
	}

	/**
	Changes the settings of the given display layer and its channel use instructions
	 */
	protected void setPanelCreationOptionsToDialog(MultichannelDisplayLayer dis, ChannelUseInstructions ins) {
		
		double preScale=this.getNumber(PREPROCESS_SCALE_KEY); 
		if (preScale<=0) preScale=dis.getPreprocessScale();
		
		if (preScale>0.01)dis.setPreprocessScale(preScale);
		
		setMergeHandlingToDialog(this, ins);
		ArrayList<Integer> noChan =this.getChannelChoices(EXCLUDE_CHANNEL_KEY) ; /**=new ArrayList<Integer>();
		for(int i=0; i<3; i++) {
			
			noChan.add(this.getChoiceIndex("exclude channel panel "+i));
		}*/
		
		ins.setExcludedChannelPanels(noChan);
		ins.setIdealNumberOfColumns(this.getNumberInt(IDEAL_LAYOUT_SIZE_KEY)); 
		
		
		double theScale = this.getNumber(PANEL_SIZE_KEY);
		if(theScale<1) theScale=ImageDPIHandler.getInchDefinition();//the pixels per inch must be an integer above 0
		double panelLevelScale = ImageDPIHandler.getInchDefinition()/theScale;
		if (panelLevelScale<=0) 
			{panelLevelScale=1;			}
		
		if ( panelLevelScale>0.01)
			dis.getPanelManager().setPanelLevelScale(panelLevelScale);
		
		for(MultichannelDisplayLayer addedDiaply: displural) {
			if ( panelLevelScale>0.01)addedDiaply.getPanelManager().setPanelLevelScale(panelLevelScale);
			
			if (preScale>0.01)addedDiaply.setPreprocessScale(preScale);
		}
	}

	/**
	
	 */
	 static void setMergeHandlingToDialog(StandardDialog t, ChannelUseInstructions ins) {
		ins.MergeHandleing=t.getChoiceIndex(MERGE_PANEL_POSITION_KEY);
	}

	/**
	
	 */
	 static void setChannelUseOptionsToDialog(StandardDialog t, ChannelUseInstructions ins) {
		ins.channelColorMode=t.getChoiceIndex(COLOR_MODE_KEY);
		ArrayList<Integer> noMerge=t.getChannelChoices(DONT_MERGE_CHANNELS_KEY);/**new ArrayList<Integer>();
		
		for(int i=0; i<3; i++) {
			noMerge.add(t.getChoiceIndex("don't merge"+i));
		}*/
		ins.setNoMergeChannels(noMerge);
		ins.eachMergeChannel=t.getChoiceIndex(MERGE_TO_EACH_CHANNEL_PANEL_KEY);
		ins.ignoreAfterChannel=t.getChoiceIndex("ignoreAfter");
	}

	/**
	this methods removes the old panels and channel labels. 
	Subsequently creates new panels and channel labels.
	During the process, it also alter the figure layout to fit the new objects
	 */
	protected void recreateGraphicsFor(MultichannelDisplayLayer dis, ChannelUseInstructions ins,
			boolean eliminateChanLabel, boolean firstImage) {
		ins.setDimensionForPanels(panMan.getLayout(), allWrappers());
		dis.eliminateAndRecreate(firstImage, !firstImage, firstImage);//only want to redo the dimensions if it is the first one being recreated
		if (eliminateChanLabel) {dis.eliminateChanLabels();}
		MultichannelDisplayLayer lastone=dis;
		
		/** does*/
		for(MultichannelDisplayLayer addedDiaply: displural) {
			addedDiaply.getSetter().startPoint=lastone.getPanelList().getlastPanelsGridIndex()+1;
			lastone=addedDiaply;
			if (firstImage&&dis==addedDiaply)  {addedDiaply.getSetter().startPoint=0;}
		}
	}

	public DisplayedImage getCurrentImageDisplay() {
		return currentImageDisplay;
	}

	public void setCurrentImageDisplay(DisplayedImage currentImageDisplay) {
		this.currentImageDisplay = currentImageDisplay;
	}
	
	public ArrayList<MultiChannelImage> allWrappers() {
		ArrayList<MultiChannelImage> array1=new ArrayList<MultiChannelImage>();
		array1.add(principalMultiChannel.getMultiChannelImage());
		for(MultichannelDisplayLayer adisplay: displural) {array1.add(adisplay.getMultiChannelImage());};
		return array1;
	}
	
	
	public MultichannelDisplayLayer getMainDisplayItem() {return principalMultiChannel;}

	public ArrayList<MultichannelDisplayLayer> getAdditionalDisplayLayers() {
		return displural;
	}
	
	/**Before showing this dialog, checks whether any of the panel managers use advanced channel use
	  If they do, asks user if user wants to proceed*/
	public void showDialog() {
		boolean b=hasAdvancedChannelUse(false, this.getAllDisplays());
		if (b) 
			{
			boolean okToProceed = FileChoiceUtil.yesOrNo("This will turn off advanced channel use. Is that OK");
			if ( okToProceed ) {hasAdvancedChannelUse(true, this.getAllDisplays());};
			
			if (!okToProceed)
				return;
			
		}
		
		/**Adds tabs for the Z and T */
		if (this.panelCreationIncluded) {
			SubStackDialog sf = new SubStackDialog(this.getAllDisplays());
			sf.setParentDialog(this);
			if (!sf.isEmpty())
			{
				getOptionDisplayTabs().setTitleAt(0, "Channels and Panels");
				this.addSubordinateDialog(FRAMES_AND_SLICES_TAB_NAME,sf);
				
				if (singleChannel()) {
					this.getOptionDisplayTabs().setSelectedIndex(1);
				}
				
				}
			addSubordinateDialog("Other Options",  new StoredValueDilaog(CanvasOptions.current)  );
			
			
		}
		
		
		super.showDialog();
	}

	/**
	 returns true if all the displays have only a single channel
	 */
	private boolean singleChannel() {
		for(MultichannelDisplayLayer d:this.getAllDisplays()) {
			if(d.getMultiChannelImage().nChannels()>1) return false;
		}
		return true;
	}

	/**
	returns true if one of the panel mangers uses advanced channel use.
	If the argument is set to true, switches all of them to normal channel use
	 */
	private static boolean hasAdvancedChannelUse(boolean turnOff, Iterable<MultichannelDisplayLayer> dis) {
		for(MultichannelDisplayLayer d: dis) {
			if (d.getPanelManager().isAdvancedChannelUse()) {
				if (turnOff) d.getPanelManager().setChannelUseMode(PanelManager.NORMAL_CHANNEL_USE); else
				return true;
				}
		}
		return false;
	}
	
	/**Displays a recreate panels dialog for the given figure*/
	public static PanelStackDisplayOptions recreateFigurePanels(FigureOrganizingLayerPane f, boolean cropToo) {
		
		ArrayList<ImageDisplayLayer> d1 = f.getMultiChannelDisplaysInLayoutOrder();
		MultichannelDisplayLayer in = (MultichannelDisplayLayer)f.getPrincipalMultiChannel();
		PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(in, in.getPanelList(),null, true);
		
		dialog.addAditionalDisplays(d1);
		dialog.setCurrentImageDisplay(CurrentFigureSet. getCurrentActiveDisplayGroup());
		dialog.setModal(false);
		
		dialog.showDialog();
		f.fixLabelSpaces();
		return dialog;
	}
	
}
