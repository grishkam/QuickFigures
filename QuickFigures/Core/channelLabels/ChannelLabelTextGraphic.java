package channelLabels;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import channelMerging.ChannelEntry;
import genericMontageKit.PanelListElement;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDef;
import graphicalObjects_LayerTypes.GraphicLayer;
import includedToolbars.StatusPanel;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import multiChannelFigureUI.ChannelSwapHandleList;
import objectDialogs.ChannelLabelDialog;
import objectDialogs.ComplexTextGraphicSwingDialog;
import popupMenusForComplexObjects.MenuForChannelLabelMultiChannel;
import popupMenusForComplexObjects.TextGraphicMenu;
import popupMenusForComplexObjects.TextSelectionMenu;
import standardDialog.StandardDialog;
import utilityClassesForObjects.ColorDimmer;
import utilityClassesForObjects.TextLine;
import utilityClassesForObjects.TextLineSegment;
import utilityClassesForObjects.TextParagraph;

/**A special category of channel label that changes based on the options
  set in channel label properties and the channel colors*/
public class ChannelLabelTextGraphic extends ComplexTextGraphic implements ChannelLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<ChannelEntry> chanEn;
	private Integer isMerge;
	private ChannelLabelProperties ChannelLabelproperties=new ChannelLabelProperties();
	private PanelListElement panel;
	boolean coupledToImage=true;
	
	private ArrayList<EntryPair> updateMap=new ArrayList<EntryPair>();
	private ChannelSwapHandleList eHan;
	
	public ChannelLabelTextGraphic(ChannelLabelProperties p) {
		super();
		setChannelLabelproperties(p);
	}
	
	public void setPanel(PanelListElement panel) {
		this.panel=panel;
			setChanEntries(panel.getChannelEntries());
			setIsMerge(panel.designation);
	}
	public PanelListElement getPanel() {
		return panel;
	}
	
	
	/**Called to update the channel label after changed to channel label properties */
	public void setParaGraphToChannels() {
		if(coupledToImage)
		setParaGraphToChannels2();
	}
	
	
	/**Creates a new paragraph based on the channels, this method is called whenever channel label is created. 
	 Another method is called to keep the labels consistent as the channel swapper and channel color changes are done*/
	private void setParaGraphToChannels2() {
		
		TextParagraph paragraph = new TextParagraph(this);
		updateMap.clear();
		/**The merge label is a complicated entity*/
		if (isThisMergeLabel()) {
			if (this.getChannelLabelproperties().getMergeLabelType()==0) {
				paragraph.addLineFromCodeString(getChannelLabelproperties().getMergeText(), Color.black);
				this.setParagraph(paragraph);
				return;
			}
			
			if (this.getChannelLabelproperties().getMergeLabelType()==ChannelLabelProperties.Merge_Style) {
				paragraph.addLineFromCodeString(getChannelLabelproperties().getMergeText(), ChannelLabelProperties.fuseColors(getChanEntries()));
				this.setParagraph(paragraph);
				return;
			}
			
			
			if (this.getChannelLabelproperties().getMergeLabelType()==ChannelLabelProperties.noMergeLabel) {return;}
		
			if (this.getChannelLabelproperties().getMergeLabelType()==ChannelLabelProperties.SoniStyle) {
				TextLine lin = paragraph.addLine();	
				lin.removeAllSegments();
				ArrayList<ChannelEntry> entries = this.getChanEntries();
				int size=entries.size();
				String[] mergeLabel=ChannelLabelProperties.split3MergeTexts[this.getChannelLabelproperties().getMergeTextOption()];
				if (size==2) mergeLabel=ChannelLabelProperties.split2MergeTexts[this.getChannelLabelproperties().getMergeTextOption()];
				if (size==1) mergeLabel=new String[]{getChannelLabelproperties().getMergeText()};
				if (size==4)  mergeLabel=ChannelLabelProperties.split4MergeTexts[this.getChannelLabelproperties().getMergeTextOption()];
				for(int i=0; i<size&&i<mergeLabel.length; i++) {
					lin.addSegment(mergeLabel[i], entries.get(i).getColor());
				}
				this.setParagraph(paragraph);
				return;
				
			}
			
		
		}
		
		
		/**What to do if every channel entry must be placed in the same line*/
			if (this.getChannelLabelproperties().getMergeLabelType()==ChannelLabelProperties.Singleline_Labels) {
				TextLine lin = paragraph.addLine();	
				lin.removeAllSegments();
				//lin.addSegment();
				for(int i=0; i<getChanEntries().size(); i++) {
					ChannelEntry en=this.getChanEntries().get(i);
					if (en.getLabel()==null) continue;
					generateLineTextFromChannelName(lin, en, false);
					
					if (i+1!=this.getChanEntries().size())
					lin.addSegment(this.getChannelLabelproperties().getSeparatorText(), Color.black);
				}
				this.setParagraph(paragraph);
				return;
			}

		/**What to do when there is a multiline label*/
		for(ChannelEntry en: this.getChanEntries()) {
			TextLine lin = paragraph.addLine();
			lin.removeAllSegments();
			if (en.getLabel()==null) continue;
			
			generateLineTextFromChannelName(lin, en, true);
		}
		this.setParagraph(paragraph);
	}

	public boolean isThisMergeLabel() {
		return getIsMerge()==PanelListElement.MergeImageDes;
	}
	
	
	
	
	/**When given channel of name label, this generates text for the line from it*/
	public void generateLineTextFromChannelName(TextLine lin, ChannelEntry cc, boolean map) {
		
		Color c=cc.getColor(); 
		
		/**If there is already a text line in the channel label properties, finds that line*/
		TextLine potential = getChannelLabelproperties().getTextLineForChannel(cc);
		if (potential==null) IssueLog.log("PROBLEM, failed to get text line for channel");
		
		ArrayList<TextLineSegment> list = lin.copySegmentsFrom(potential);
		
			if(map)updateMap.add(new EntryPair(lin, potential,cc));
			else updateMap.add(new EntryPair(list, potential, cc));
			
			for(TextLineSegment seg: lin) {
				if (!seg.usesUniqueColor())
				seg.setTextColor(c);//makes sure the color is consitent with the current channel color			
				
			}
		
	}
	
	
	
	
	


	public ArrayList<ChannelEntry> getChanEntries() {
		return chanEn;
	}



	public void setChanEntries(ArrayList<ChannelEntry> chanEn) {
		this.chanEn = chanEn;
	}



	public Integer getIsMerge() {
		return isMerge;
	}



	public void setIsMerge(Integer isMerge) {
		this.isMerge = isMerge;
	}
	
	public Color getDimmedColor(Color c) {
		Color output=c;
		boolean dontinvert=true;
	//	boolean inside=false;
		if (this.getSnapPosition()!=null && this.getSnapPosition().isInternalSnap()) dontinvert=false;
	
		
		
		if (this.isDimColor()) output= ColorDimmer.modifyColor( c, colordimming, dontinvert);
		 	  
		 	    else return c;
		
		if (!dontinvert&&colordimming<4) {
			if (c.equals(Color.black)) return Color.white;
			//if (c.equals(Color.white)) return Color.black;
		}
		if (dontinvert&&colordimming<4) {
			//if (c.equals(Color.black)) return Color.white;
			if (c.equals(Color.white)) return Color.black;
		}
		
		return output;
	}
	
	

	public ChannelLabelProperties getChannelLabelproperties() {
		return ChannelLabelproperties;
	}

	public void setChannelLabelproperties(ChannelLabelProperties channelLabelproperties) {
		ChannelLabelproperties = channelLabelproperties;
	}
	
	@Override
	public StandardDialog getOptionsDialog() {
		ComplexTextGraphicSwingDialog dia = new ChannelLabelDialog(this);
		return dia;
	}
	
	public void showOptionsDialog() {
		getOptionsDialog().showDialog();
		//TextLineDialogForChenLabel.showMultiTabDialogDialogss(chanEn, ChannelLabelproperties, null).showDialog();;
	}

	protected TextLineSegment[] splitUpSingleHighLightSegment(TextLineSegment thisSegment) {
		TextLine line = getParagraph().getLineWithSegment(thisSegment);
		/**for some reason, splitting lines changes their hashcodes and messes up the update hashmap
		  overiding the line splitting method in this way corrects the problem*/
		//TextLine record = updateMap.get(line);
		
		//updates the records in the channel use properties to match segment split that occur
		TextLineSegment[] newseg = super.splitUpSingleHighLightSegment(thisSegment);
		if(newseg==null) return newseg;
		for(EntryPair i: updateMap) {
			/**needed when the merge label consists of many bits of text in a single line*/
			if(i.segments instanceof TextLine) continue;//this update code is only for the array lists not text line objects
			if(i.segments.contains(thisSegment)) {
				i.segments.add(i.segments.indexOf(thisSegment)+1, newseg[1]);
				if(newseg.length==3) i.segments.add(i.segments.indexOf(thisSegment)+2, newseg[2]);
				
			}
		}
		
		return newseg;
	}
	
	
	static boolean warned=false;
	public void handleKeyPressEvent(KeyEvent arg0) {
		super.handleKeyPressEvent(arg0);
		updateChannelLabelPropertiesToLabelText();
		StatusPanel.updateStatus("Warning: Alterations to channel labels may be made as one edits the image");
		
		if(!warned)
		{
			StatusPanel.updateStatus("Warning: Alterations to channel labels may be made as one edits the image");
			//IssueLog.log("Warning: Alterations to channel labels may be made as one edits the image");
		 warned=true;
		 }
	}
	
	/**After operations that splits up the highlight segments and edits the paragraph this is called*/
	protected void afterSplitUp() {
		try {updateChannelLabelPropertiesToLabelText() ;} catch (Exception e) {e.printStackTrace();}
	}

	/**After the user edits the text, this will update the information in channel label properties to be consistent*/
	public void updateChannelLabelPropertiesToLabelText() { 
		/**will update some of the line segments channel label properties*/
		for(EntryPair p: updateMap) {
			if(p==null) continue;
			
			if(p.record!=null&&p.segments.size()>0) {
				
				p.record.replaceSegmentsWithoutColor(p.segments);
				
				}
		}
		if (this.isThisMergeLabel()&&ChannelLabelproperties.getMergeLabelType()==0) {
			
			ChannelLabelproperties.setMergeText(this.getParagraph().getText());
		}
	}
	
	public PopupMenuSupplier getMenuSupplier() {
		if(this.isEditMode()) 
			return new TextSelectionMenu(this);
		
		TextGraphicMenu output = new TextGraphicMenu(this);
		
		/**finds the Multichannel display in the hierarchy*/
		GraphicLayer parentLayer = this.getParentLayer();
		
		MenuForChannelLabelMultiChannel chanLabelMenu=null;
		
		if (parentLayer instanceof MultichannelDisplayLayer) {
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) parentLayer;
			chanLabelMenu = m.getMenuSupplier().createChanLabelMenu();
		}
		else {
			PanelGraphicInsetDef ins = PanelGraphicInsetDef.findInsetWith(this);
			if(ins!=null) {
				chanLabelMenu = new MenuForChannelLabelMultiChannel("Channel Labels", ins.getSourceDisplay(), ins.getPanelManager().getPanelList(), ins.getChannelLabelManager());
			}
		}
		
		if (chanLabelMenu!=null) {
			output.add(chanLabelMenu);
			output.insert(chanLabelMenu.createAllLabelMenuItem(),0);
		}
		
		return output;
	}
	
	/**makes it harder for the user to directly edit the texts of a channel label. needed because the channel labels 
	  are constantly being updated as the channel label properties, channel order and channel colors are changed.
	@Override
	public void handleMouseEvent(int handlenum, int button, int clickcount, int type,
			int... other) {
		
		
		if(clickcount==2)showOptionsDialog();
		super.handleMouseEvent(handlenum, button, clickcount, type, other);
	}*/
	
	class EntryPair implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<TextLineSegment> segments;
		private TextLine record;
		private ChannelEntry channel;

		public EntryPair(ArrayList<TextLineSegment> segments, TextLine record, ChannelEntry cc) {
			this.segments=segments;
			this.record=record;
			this.channel=cc;
		}
		
		/**If the paragraph is replaced, pair is updates.
		   This normally occurs when an undo is called.
		   this was written as a quick bugfix*/
		void onParagraphReplace(ArrayList<TextLine> here) {
			for(TextLine i: here) {
				if(segments instanceof TextLine) {
					 TextLine s=(TextLine) segments;
					 if (i.copyOf==s.id) {
						 segments=i;
					 }
				} else {
					//not written for the case in which there are multiple channel labels per line
				}
			}
		}
		
		private void updateColor(ChannelEntry c2) {
			if (c2.getRealChannelName().equals(channel.getRealChannelName())) {
				channel.setColor(c2.getColor());
			}
					updateColor();
		}

		public void updateColor() {
			Color c = channel.getColor();
			for(TextLineSegment seg: segments) {
				seg.setTextColor(c);
			}
		}
	}
	
	
	public void setParagraph(TextParagraph paragraph) {
		super.setParagraph(paragraph);
		
		
		/**if an undo is called, replaces the text lines on the update map with new ones
		  but only if the new lines are a copy of the old ones. written as a quick bugfix
		  not meant to be flawless
		  just meant to prevent flagrant inconsistencies from being visible after undo
		  is followed by an update of the channel labels from the channel label properties*/
		for(EntryPair p: updateMap) try {
			p.onParagraphReplace(paragraph);
		} catch (Throwable t ) {}
		updateChannelLabelPropertiesToLabelText();//updates the channel label properties
	}

	public void setExtraHandles(ChannelSwapHandleList channelSwapHandleList) {
		eHan=channelSwapHandleList;
		
	}

	public ChannelSwapHandleList getExtraHandles() {
		return eHan;
	}
	
	
	
	
}
