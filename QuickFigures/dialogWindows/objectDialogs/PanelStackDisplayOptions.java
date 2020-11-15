package objectDialogs;

import java.util.ArrayList;

import appContext.ImageDPIHandler;
import channelMerging.ChannelEntry;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelWrapper;
import genericMontageKit.PanelList;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_FigureSpecific.PanelManager;
import imageMenu.CanvasAutoResize;
import standardDialog.ChannelEntryBox;
import standardDialog.ComboBoxPanel;
import standardDialog.NumberInputPanel;
import undo.ChannelUseChangeUndo;
import undo.CombinedEdit;
import undo.PanelManagerUndo;
import applicationAdapters.DisplayedImage;

/**A dialog box used for two purposes. First, works as a set of options when 
creating a channel figure. Second, allow you to change how the channels are
colored and combined in the channel panels*/
public class PanelStackDisplayOptions extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MultichannelDisplayLayer principalMultiChannel;
	private ArrayList<MultichannelDisplayLayer> displural=new ArrayList<MultichannelDisplayLayer>();
	boolean panelCreationIncluded=true;
	private DisplayedImage currentImageDisplay;
	private PanelList stack;
	private PanelManager panMan;
	static String[] MergePositions=new String[] {"Merged Image Last", "Merged Image First", "No Merge", "Only Merge (no channels)"};
	boolean includeBilScale=false;
	
	
	public PanelStackDisplayOptions(MultichannelDisplayLayer display, PanelList stack, PanelManager panMan, boolean panelCreationIncluded) {
		stack.setChannelUpdateMode(false);//need to be set to false, otherwise the channels will not be updated according to the instructions
		
		this.panelCreationIncluded=panelCreationIncluded;
		this.principalMultiChannel=display;
		this.panMan=panMan;
		this.stack=stack;
		if (this.stack==null) this.stack=display.getPanelList();
		
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
	/***/
	public String[] createListWithNoChannelOption() {
		String[] old = MultichannelDisplayLayer.getChannelNames(principalMultiChannel.getMultichanalWrapper());
		String[] output=new String[old.length+1];
		output[0]="none";
		for(int i=0; i<old.length; i++) {
			output[i+1]=old[i];
		}
		
		return output;
	}
	
	public void addOptionsToDialog() {
		String[] chanList=createListWithNoChannelOption();
		
		channelMerging.ChannelUseInstructions ins =stack.getChannelUseInstructions();
		ArrayList<ChannelEntry> entries = principalMultiChannel.getMultichanalWrapper().getChannelEntriesInOrder();
		
		if (this.panelCreationIncluded) {
			ComboBoxPanel mergeh = new standardDialog.ComboBoxPanel("Merge Image", MergePositions, ins.MergeHandleing);
			this.add("merge",mergeh );
		ArrayList<Integer> ex = ins.excludedChannelPanels;
			
			
		for(int i=0; i<3; i++) {
			int mergeChannelIndex= ex.size()>i?  ex.get(i):0;
			if (mergeChannelIndex>=chanList.length) { mergeChannelIndex=0;}
			ComboBoxPanel mergeCombo;
			if (entries.size()>i){
				 mergeCombo = new standardDialog.ComboBoxPanel("don't include channel", new ChannelEntryBox(mergeChannelIndex, entries));
			} else mergeCombo = new standardDialog.ComboBoxPanel("don't include channel", chanList, mergeChannelIndex );
		this.add("exclude channel panel "+i, mergeCombo);
		}
		
		}
		
		
		ComboBoxPanel grey =new  standardDialog.ComboBoxPanel("Channels To Grey ", new String[] {"Color of LUTs", "Greyscale"}, ins.channelColorMode);
		this.add("grey", grey);
		
		ArrayList<Integer> nome = ins.noMergeChannels;
		for(int i=0; i<3; i++) {
			int noMergeIndex=nome.size()>i? nome.get(i):0;
			if (noMergeIndex>=chanList.length) noMergeIndex=0;
			ComboBoxPanel mergeCombo;
			if (entries.size()>i){
				 mergeCombo = new standardDialog.ComboBoxPanel("dont't merge channel", new ChannelEntryBox(noMergeIndex , entries));
			} else mergeCombo = new standardDialog.ComboBoxPanel("dont't merge channel", chanList,noMergeIndex );
		this.add("don't merge"+i, mergeCombo);

		}

		ComboBoxPanel mergeCombo;
		if (entries.size()>ins.ignoreAfterChannel){
			 mergeCombo = new standardDialog.ComboBoxPanel("Go upto channel", new ChannelEntryBox(ins.ignoreAfterChannel , entries));
		} else mergeCombo = new standardDialog.ComboBoxPanel("Go upto channel", chanList,ins.ignoreAfterChannel );
		this.add("ignoreAfter", mergeCombo);
		

		ComboBoxPanel mergeCombo2;
		if (entries.size()>ins.eachMergeChannel){
			 mergeCombo2 = new standardDialog.ComboBoxPanel("merge each channel with", new ChannelEntryBox(ins.eachMergeChannel , entries));
		} else mergeCombo2 = new standardDialog.ComboBoxPanel("merge each channel with", chanList, ins.eachMergeChannel);
		this.add("mergeeach", mergeCombo2);
		
		if (this.panelCreationIncluded)
			{
			this.add("preScale", new NumberInputPanel("Scale (Bilinear Interpolation)", principalMultiChannel.getPreprocessScale(),3));
			
			if (this.includeBilScale) this.add("Source Image Level Scale", new NumberInputPanel("Bilinear Scale 2", stack.getScaleBilinear(),3));
			
			}
		if (this.panelCreationIncluded) {
			this.add("Panel Level Scale", new NumberInputPanel("PPI",ImageDPIHandler.getStandardDPI()/ principalMultiChannel.getPanelManager().getPanelLevelScale(),3));
			this.add("mWidth",  new NumberInputPanel("Ideal Number Columns", ins.idealColNum ));
			}
	}
	
	
	public void setItemsToDiaog()  {
		setItemstoDialog(panMan.getDisplay(), panMan.getPanelList().getChannelUseInstructions(), false, true);
		
		//displural.remove(panMan.getDisplay());
		for(MultichannelDisplayLayer p: displural) {
			if (p!=panMan.getDisplay())
			setItemstoDialog(p, p.getPanelManager().getPanelList().getChannelUseInstructions(), true, false);
		}
	}
	
	void setItemstoDialog(MultichannelDisplayLayer dis, ChannelUseInstructions ins, boolean eliminateChanLabel, boolean first) {
		//ChannelUseInstructions ins = dis.getStack().getChannelUseInstructions();

		ins.channelColorMode=this.getChoiceIndex("grey");
		ArrayList<Integer> noMerge=new ArrayList<Integer>();
		for(int i=0; i<3; i++) {
			noMerge.add(this.getChoiceIndex("don't merge"+i));
		}
		ins.noMergeChannels=noMerge;
		ins.eachMergeChannel=getChoiceIndex("mergeeach");
		ins.ignoreAfterChannel=getChoiceIndex("ignoreAfter");
		
		
		if (this.panelCreationIncluded) {
			if (includeBilScale) {
				double bilScale = this.getNumber("Source Image Level Scale");
				if (bilScale>0)stack.setScaleBilinear(bilScale);
			}
			
			double preScale=this.getNumber("preScale");
			if (preScale>0.01)dis.setPreprocessScale(preScale);
			
			ins.MergeHandleing=this.getChoiceIndex("merge");
			ArrayList<Integer> noChan=new ArrayList<Integer>();
			for(int i=0; i<3; i++) {
				
				noChan.add(this.getChoiceIndex("exclude channel panel "+i));
			}
			ins.excludedChannelPanels=noChan;
			ins.idealColNum=this.getNumberInt("mWidth"); 
			
			
			double panelLevelScale = ImageDPIHandler.getStandardDPI()/this.getNumber("Panel Level Scale");
			if ( panelLevelScale>0.01)dis.getPanelManager().setPanelLevelScale(panelLevelScale);
			
			for(MultichannelDisplayLayer addedDiaply: displural) {
				addedDiaply.getPanelManager().setPanelLevelScale(panelLevelScale);
				if (includeBilScale) {
					double bilScale = this.getNumber("Source Image Level Scale");
					if (bilScale >0)addedDiaply.getPanelManager().getPanelList().setScaleBilinear(bilScale);
				}
				if (preScale>0.01)addedDiaply.setPreprocessScale(preScale);
			}
			
			ins.setDimensionForPanels(panMan.getLayout(), allWrappers());
			dis.eliminateAndRecreate(first, !first, first);//only want to redo the dimensions if it is the first one being recreated
			if (eliminateChanLabel) {dis.eliminateChanLabels();}
			MultichannelDisplayLayer lastone=dis;
			
			/**flawed code. does*/
			for(MultichannelDisplayLayer addedDiaply: displural) {
				addedDiaply.getSetter().startPoint=lastone.getPanelList().getlastPanelsIndex()+1;
				lastone=addedDiaply;
				if (first&&dis==addedDiaply)  {addedDiaply.getSetter().startPoint=0;}
			}
			
			
		}
		
		dis.getPanelManager().updatePanels();
		panMan.updatePanels();
		if (getCurrentImageDisplay()!=null) {
				
							new CanvasAutoResize().makeAllVisible(getCurrentImageDisplay());
							FigureOrganizingLayerPane fo = FigureOrganizingLayerPane.findFigureOrganizer(dis);
							if (fo!=null) {
								 fo.fixLabelSpaces();
							}
							//new CanvasAutoTrim().trimCanvas(getCurrentImageDisplay());
							}
	}

	public DisplayedImage getCurrentImageDisplay() {
		return currentImageDisplay;
	}

	public void setCurrentImageDisplay(DisplayedImage currentImageDisplay) {
		this.currentImageDisplay = currentImageDisplay;
	}
	
	public ArrayList<MultiChannelWrapper> allWrappers() {
		ArrayList<MultiChannelWrapper> array1=new ArrayList<MultiChannelWrapper>();
		array1.add(principalMultiChannel.getMultichanalWrapper());
		for(MultichannelDisplayLayer adisplay: displural) {array1.add(adisplay.getMultichanalWrapper());};
		return array1;
	}
	
	
	public MultichannelDisplayLayer getMainDisplayItem() {return principalMultiChannel;}

	public ArrayList<MultichannelDisplayLayer> getAdditionalDisplayLayers() {
		return displural;
	}
	
	
}
