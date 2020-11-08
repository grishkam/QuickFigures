package channelLabels;

import java.awt.GridBagConstraints;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JTabbedPane;

import channelMerging.ChannelEntry;
import channelMerging.MultiChannelWrapper;
import fLexibleUIKit.MenuItemMethod;
import genericMontageKit.PanelList;
import genericMontageKit.PanelListElement;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import objectDialogs.ChannelLabelDialog;
import objectDialogs.ChannelLabelPropertiesDialog;
import objectDialogs.ChannelSliceAndFrameSelectionDialog;
import objectDialogs.MultiTextGraphicSwingDialog;
import objectDialogs.TextLineDialogForChenLabel;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialog;
import standardDialog.SwingDialogListener;
import undo.CompoundEdit2;
import undo.UndoAbleEditForRemoveItem;
import utilityClassesForObjects.SnappingPosition;

/**A class for adding, accessing and editing the channel labels*/
public class ChannelLabelManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelList stack;
	private GraphicLayer layer;
	private ChannelLabelProperties channelLabelProp;
	private static TextGraphic fossilLabel;
	private transient MultiTextGraphicSwingDialog mt;
	private MultichannelDisplayLayer source;

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
											cltg.setSnappingBehaviour(getFossilLabel().getSnappingBehaviour());
											} else {
												cltg.setSnappingBehaviour(SnappingPosition.defaultPanelLabel());
											}
		slice.setChannelLabelDisplay(cltg);
		if (getLayer()!=null)getLayer().add(cltg);
		if (slice.getImageDisplayObject() instanceof ImagePanelGraphic) {
			ImagePanelGraphic panel=(ImagePanelGraphic) slice.getImageDisplayObject();
			panel.addLockedItem(cltg);
		}
		
		return cltg;
		
	}

	public TextGraphic getFossilLabel() {
		return fossilLabel;
	}
	
	/**Removes the channel labels from the layer and returns them as an array*/
	@MenuItemMethod(menuActionCommand = "labelgone", menuText = "Eliminate Channel Labels", subMenuName="Channel Labels")
	public CompoundEdit2 eliminateChanLabels() {
		CompoundEdit2 output = new CompoundEdit2();
		if(stack==null) return output ; 
		ArrayList<ChannelLabelTextGraphic> arr =getStack().getChannelLabels();
	
		for(ChannelLabelTextGraphic g:arr) {
			output.addEditToList((new UndoAbleEditForRemoveItem(getLayer(), g)));
			if (this.getLayer()!=null)getLayer().remove(g);
			fossilLabel=g;
		}
		return output;
		
	}
	
	void generateSingleChannelPanelLabel(int channel, int slice, int frame) {
		PanelListElement panel = getStack().getOrCreateChannelPanel(getMultiChannel(),channel, slice, frame);
		 if (panel!=null)this.generateChanelLabel(panel);
	}
	
	void generateSingleChannelMergeLabel( int slice, int frame) {
		PanelListElement panel =  getStack().getOrCreateMergePanel(getMultiChannel(), slice, frame);
		 if (panel!=null)this.generateChanelLabel(panel);
	}
	
	@MenuItemMethod(menuActionCommand = "channeoLabels", menuText = "Generate Channel Labels", subMenuName="Channel Labels")
	public ArrayList<ChannelLabelTextGraphic> generateChannelLabels() {
		ArrayList<ChannelLabelTextGraphic> output=new ArrayList<ChannelLabelTextGraphic>();
		for(PanelListElement slice: getStack().getPanels()) {
			output.add(generateChanelLabel(slice));
		}
		return output;
	}
	
	/**Creates channel labels but only for the first stack slice or frame*/
	@MenuItemMethod(menuActionCommand = "channeoLabels2", menuText = "Generate Channel Labels (For first slice only)", subMenuName="Channel Labels")
	public ArrayList<ChannelLabelTextGraphic> generateChannelLabels2() {
		ArrayList<ChannelLabelTextGraphic> output=new ArrayList<ChannelLabelTextGraphic>();
		for(PanelListElement slice: getStack().getPanels()) {
			if(slice.originalFrameNum>1) continue;
			if(slice.originalSliceNum>1) continue;
			output.add(generateChanelLabel(slice));
		}
		return output;
	}
	
	@MenuItemMethod(menuActionCommand = "1mergeL", menuText = "Create 1 Merge Panel Label", subMenuName="Channel Labels")
	public void addSingleMergeLabel() {
		ChannelSliceAndFrameSelectionDialog dia = new ChannelSliceAndFrameSelectionDialog(1,1,1, getMultiChannel());
		dia.show2DimensionDialog();
		
		generateSingleChannelMergeLabel( dia.getSlice(),dia.getFrame());
	}
	
	@MenuItemMethod(menuActionCommand = "1chanL", menuText = "Create 1 Channel Panel Label", subMenuName="Channel Labels")
	public void addSingleChannelLabel() {
		ChannelSliceAndFrameSelectionDialog dia = new ChannelSliceAndFrameSelectionDialog(0,1,1, getMultiChannel());
		dia.show3DimensionDialog();
		generateSingleChannelPanelLabel( dia.getChannel(), dia.getSlice(),dia.getFrame());
	}
	
	@MenuItemMethod(menuActionCommand = "chantype", menuText = "Merge Label Options", subMenuName="Channel Labels")
	public void showChannelLabelPropDialog() {
		ChannelLabelPropertiesDialog dia = new  ChannelLabelPropertiesDialog(this.getChannelLabelProp());
		dia.setLabelItems(getStack().getChannelLabels());
		
		JTabbedPane tabs = dialogForChannelEntries(getMultiChannel().getChannelEntriesInOrder()).getOptionDisplayTabs();
		dia.getOptionDisplayTabs().addTab("Channel Names", tabs);
		dia.showDialog();
	}

	public MultiChannelWrapper getMultiChannel() {
		return source.getMultichanalWrapper();
	}

	public GraphicLayer getLayer() {
		return layer;
	}

	public void setLayer(GraphicLayer layer) {
		this.layer = layer;
	}

	public PanelList getStack() {
		return stack;
	}

	public void setStack(PanelList stack) {
		this.stack = stack;
	}
	
	//@MenuItemMethod(menuActionCommand = "chan names", menuText = "Name Channels", subMenuName="Channel Labels")
	
	public void nameChannels() {
		nameChannels(getMultiChannel().getChannelEntriesInOrder());
	}
	
	public void nameChannels(ArrayList<ChannelEntry> entries) {
	
	
		dialogForChannelEntries(entries).makeVisible();;
		
	}

	public StandardDialog dialogForChannelEntries(ArrayList<ChannelEntry> entries) {
		return TextLineDialogForChenLabel.showMultiTabDialogDialogss(entries, this.getChannelLabelProp(), new SwingDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				getStack().resetChannelEntriesForAll(getMultiChannel());
			
				getStack().updateAllPanelsWithImage(getMultiChannel());
				
			}});
	}

	public void completeMenu() {
		ArrayList<ChannelLabelTextGraphic> labels = this.getStack().getChannelLabels();
		
		mt = new MultiTextGraphicSwingDialog( labels, true);
		
		SwingDialogListener listener1 = new SwingDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				try {
					FigureOrganizingLayerPane f = FigureOrganizingLayerPane.findFigureOrganizer(getLayer());
					if (f!=null)
							{
						for(TextGraphic t:mt.getAllEditedItems()) 
						f.getLayout().getEditor().expandSpacesToInclude(f.getLayout(), t.getBounds());
						//TODO expand label spaces
							}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
		mt.addDialogListener(listener1);
		
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

	private static ChannelLabelTextGraphic getMergeLabel(ArrayList<ChannelLabelTextGraphic> labels) {
		for(ChannelLabelTextGraphic l:labels) {
			if (l.isThisMergeLabel()) return l;
		}
		return null;
	}
	
	public void copyLabelStyleFrom(ChannelLabelManager c) {
		this.fossilLabel=c.fossilLabel;
	}
	
	
}
