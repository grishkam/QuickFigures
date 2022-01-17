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
 * Date Modified: Mar 7, 2021
 * Version: 2022.0
 */
package channelLabels;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import channelMerging.ChannelEntry;
import figureEditDialogs.ChannelLabelDialog;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelListElement;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import handles.miniToolbars.ChannelLabelTextActionButtonHandleList;
import handles.miniToolbars.TextActionButtonHandleList;
import includedToolbars.StatusPanel;
import locatedObject.ColorDimmer;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import objectDialogs.ComplexTextGraphicSwingDialog;
import popupMenusForComplexObjects.MenuForMultiChannelDisplayLayer;
import popupMenusForComplexObjects.TextGraphicMenu;
import popupMenusForComplexObjects.TextSelectionMenu;
import standardDialog.StandardDialog;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;
import textObjectProperties.TextParagraph;
import undo.AbstractUndoableEdit2;
import undo.ChannelLabelPropertiesUndo;
import undo.CombinedEdit;
import undo.EditListener;

/**A special category of label that changes based on the options
  set in channel label properties and the channel colors*/
public class ChannelLabelTextGraphic extends ComplexTextGraphic implements ChannelLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**options related to how channel labels are organized is stored her. 
	 * a channel label properties object is shared between many labels*/
	private ChannelLabelProperties ChannelLabelproperties=new ChannelLabelProperties();
	
	/**a list of channels*/
	private ArrayList<ChannelEntry> channelEntryList;
	private Integer panelType=PanelListElement.CHANNEL_IMAGE_PANEL;

	private PanelListElement panel;//the panel that the channel label is part of 
	boolean coupledToImage=true;

	/**This array helps maintain consistency between the text in the channel label properties
	  and the text of this channel label. */
	private ArrayList<EntryPair> updateMap=new ArrayList<EntryPair>();

	/**creates a label with the given channel label properties*/
	public ChannelLabelTextGraphic(ChannelLabelProperties p) {
		super();
		setChannelLabelproperties(p);
	}
	
	/**sets the panel for this channel label*/
	public void setPanel(PanelListElement panel) {
		this.panel=panel;
			setChanEntries(panel.getChannelEntries());
			setPanelType(panel.designation);
	}
	/**returns the panel for this channel label*/
	public PanelListElement getPanel() {
		return panel;
	}
	
	
	/**Called to update the channel label after changed to channel label properties */
	public void setParaGraphToChannels() {
		if(coupledToImage)
		setParaGraphToChannels2();
	}
	
	
	/**Creates a new paragraph based on the channels, this method is called whenever a channel label is created. 
	 In some contexts, this is called to keep the labels consistent as the channel swaps and channel color changes are done.
	 */
	private void setParaGraphToChannels2() {
		
		TextParagraph paragraph = new TextParagraph(this);
		updateMap.clear();//when paragraph is reset the old text lines no longer have any relationship with the text lined in the channel label properties 
		
		/**The merge label is a complicated entity*/
		MergeLabelStyle mergeLabelType = this.getChannelLabelProperties().getMergeLabelStyle();
		
		if (isThisMergeLabel()) {
			if (mergeLabelType==MergeLabelStyle.SIMPLY_LABEL_AS_MERGE) {
				paragraph.addLineFromCodeString(getChannelLabelProperties().getMergeText(), Color.black);
				this.setParagraph(paragraph);
				return;
			}
			
			if (mergeLabelType==MergeLabelStyle.OVERLAY_THE_COLORS) {
				paragraph.addLineFromCodeString(getChannelLabelProperties().getMergeText(), ChannelLabelProperties.fuseColors(getChanEntries()));
				this.setParagraph(paragraph);
				return;
			}
			
			
			
			if (mergeLabelType==MergeLabelStyle.RAINBOW_STYLE) {
				TextLine lin = paragraph.addLine();	
				lin.removeAllSegments();
				ArrayList<ChannelEntry> entries = this.getChanEntries();
				int size=entries.size();
				String[] mergeLabel=ChannelLabelProperties.split3MergeTexts[this.getChannelLabelProperties().getMergeTextOption()];
				if (size==2) mergeLabel=ChannelLabelProperties.split2MergeTexts[this.getChannelLabelProperties().getMergeTextOption()];
				if (size==1) mergeLabel=new String[]{getChannelLabelProperties().getMergeText()};
				if (size==4)  mergeLabel=ChannelLabelProperties.split4MergeTexts[this.getChannelLabelProperties().getMergeTextOption()];
				for(int i=0; i<size&&i<mergeLabel.length; i++) {
					lin.addSegment(mergeLabel[i], entries.get(i).getColor());
				}
				this.setParagraph(paragraph);
				return;
				
			}
			
		
		}
		
		
		/**What to do if every channel entry must be placed in the same line*/
			if (mergeLabelType==MergeLabelStyle.ONE_LINE_WITH_ALL_CHANNEL_NAMES) {
				TextLine lin = paragraph.addLine();	
				lin.removeAllSegments();
				//lin.addSegment();
				for(int i=0; i<getChanEntries().size(); i++) {
					ChannelEntry en=this.getChanEntries().get(i);
					if (en.getLabel()==null) continue;
					generateLineTextFromChannelName(lin, en, false);
					
					if (i+1!=this.getChanEntries().size())
					lin.addSegment(this.getChannelLabelProperties().getSeparatorText(), Color.black);
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

	/**returns true if the target panel is a merge panel*/
	public boolean isThisMergeLabel() {
		return getPannelType()==PanelListElement.MERGE_IMAGE_PANEL;
	}
	
	
	
	
	/**When given channel entry, determines what text serves as the label for that channel entry 
	  and adds that text to the empty textLine given*/
	public void generateLineTextFromChannelName(TextLine aTextLine, ChannelEntry cc, boolean map) {
		
		Color c=cc.getColor(); 
		
		/**If there is already a text line in the channel label properties, finds that line.
		  that line may have superscripts and other options/*/
		TextLine textLineForChannel = getChannelLabelProperties().getTextLineForChannel(cc);
		if (textLineForChannel==null) IssueLog.log("PROBLEM, failed to get text line for channel");
		
		ArrayList<TextLineSegment> list = aTextLine.copySegmentsFrom(textLineForChannel);
		
		/**generates an object that will help maintain consistency by updateing the text in the channel label
		  properties object to reflect changes in the text line*/
			if(map)updateMap.add(new EntryPair(aTextLine, textLineForChannel,cc));
			else updateMap.add(new EntryPair(list, textLineForChannel, cc));
			
			for(TextLineSegment seg: aTextLine) {
				if (!seg.usesUniqueColor())
				seg.setTextColor(c);//makes sure the color is consistent with the current channel color		
			}
		
	}
	
	/**returns the channel entry list*/
	public ArrayList<ChannelEntry> getChanEntries() {
		return channelEntryList;
	}
	/**sets the channel entry list*/
	public void setChanEntries(ArrayList<ChannelEntry> chanEn) {
		this.channelEntryList = chanEn;
	}


/**returned in indicates whether the label is meant for a channel panel or a merge panel*/
	public Integer getPannelType() {
		return panelType;
	}


	/**sets whether the label is meant for a channel panel or a merge panel*/
	public void setPanelType(Integer isMerge) {
		this.panelType = isMerge;
	}
	
	
	/**modifies the color based on the position of the label.
	  If the color is black and the panel is to be placed above a black panel background
	  the color is changed to white. the opposite occurs if the label is outside the panel on a white background
	  for colored labels, */
	public Color getDimmedColor(Color c) {
		Color output=c;
		boolean dontinvert=true;
		if (this.getAttachmentPosition()!=null && this.getAttachmentPosition().isInternalSnap()) dontinvert=false;
	
		
		
		if (isDimColor()) output= ColorDimmer.modifyColor( c, getDimming(), dontinvert);
		 	  
		 	    else return c;
		
		if (!dontinvert&&getDimming().ordinal()<4) {
			if (c.equals(Color.black)) return Color.white;
			//if (c.equals(Color.white)) return Color.black;
		}
		if (dontinvert&&getDimming().ordinal()<4) {
			//if (c.equals(Color.black)) return Color.white;
			if (c.equals(Color.white)) return Color.black;
		}
		
		return output;
	}
	
	
	/**returns the channel label properties object for this label
	 * Multiple labels will share the same label properties*/
	public ChannelLabelProperties getChannelLabelProperties() {
		return ChannelLabelproperties;
	}

	/**Sets the channel label properties object for this label
	 * Multiple labels will share the same label properties*/
	public void setChannelLabelproperties(ChannelLabelProperties channelLabelproperties) {
		ChannelLabelproperties = channelLabelproperties;
	}
	
	@Override
	public StandardDialog getOptionsDialog() {
		ComplexTextGraphicSwingDialog dia = new ChannelLabelDialog(this);
		return dia;
	}
	
	/**Shows an options dialog*/
	public void showOptionsDialog() {
		getOptionsDialog().showDialog();
		}

	/**replaces the split up segment call of the superclass.  also splits of the text segment but
	  also updates the text stored within the entries*/
	protected TextLineSegment[] splitUpSingleHighLightSegment(TextLineSegment thisSegment) {
		 getParagraph().getLineWithSegment(thisSegment);
		
	TextLineSegment[] newseg = super.splitUpSingleHighLightSegment(thisSegment);
	
		if(newseg==null) return newseg;
		
		//updates the records in the list to match segment split that occurred. That way, the text is the channel label properties object will remain consistent
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
	/**gives a brief message to the user about one caveat of the system. TODO: find a clever solution*/
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

	/**After the user edits the text, this will change the text in the channel label properties to be consistent
	  with this channel label*/
	public void updateChannelLabelPropertiesToLabelText() { 
		/**will update some of the line segments channel label properties*/
		for(EntryPair p: updateMap) {
			if(p==null) continue;
			
			if(p.record!=null&&p.segments.size()>0) {
				p.record.replaceSegmentsWithoutColor(p.segments);
				}
		}
		if (this.isThisMergeLabel()&&ChannelLabelproperties.getMergeLabelStyle()==MergeLabelStyle.SIMPLY_LABEL_AS_MERGE) {
			
			ChannelLabelproperties.setMergeText(this.getParagraph().getText());
		}
	}
	
	/**returns the popup menu for channel labels*/
	public PopupMenuSupplier getMenuSupplier() {
		if(this.isEditMode()) 
			return new TextSelectionMenu(this);
		
		TextGraphicMenu output = new TextGraphicMenu(this);
		
		/**finds the Multichannel display in the hierarchy*/
		GraphicLayer parentLayer = this.getParentLayer();
		
		MenuForMultiChannelDisplayLayer chanLabelMenu=null;
		
		if (parentLayer instanceof MultichannelDisplayLayer) {
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) parentLayer;
			chanLabelMenu = m.getMenuSupplier().createChanLabelMenu();
		}
		else {
			PanelGraphicInsetDefiner ins = PanelGraphicInsetDefiner.findInsetWith(this);
			if(ins!=null) {
				chanLabelMenu = new MenuForMultiChannelDisplayLayer("Channel Labels", ins.getSourceDisplay(), ins.getPanelManager().getPanelList(), ins.getChannelLabelManager());
			}
		}
		
		if (chanLabelMenu!=null) {
			output.add(chanLabelMenu);
			output.insert(chanLabelMenu.createAllLabelMenuItem(),0);
		}
		
		return output;
	}
	

	/**This one is difficult to explain. an object with a list of text segments, the text */
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
	

		public void updateColor() {
			Color c = channel.getColor();
			for(TextLineSegment seg: segments) {
				seg.setTextColor(c);
			}
		}
	}
	
	
	public void setParagraph(TextParagraph paragraph) {
		super.setParagraph(paragraph);
		
		
		/**if an undo is called, the code below replaces the text lines on the update map with new ones
		  but only if the new lines are a copy of the old ones. written as a quick bugfix.
		  just meant to prevent flagrant inconsistencies from being visible after undo
		  is followed by an update of the channel labels from the channel label properties*/
		for(EntryPair p: updateMap) try {
			p.onParagraphReplace(paragraph);
		} catch (Throwable t ) {}
		updateChannelLabelPropertiesToLabelText();//updates the channel label properties
	}


	/**work in progress. this will be needed. added to undoable edits that update the channel*/
	public EditListener createEditListener() {
		return new EditListener() {

			@Override
			public void afterEdit() {
				setParaGraphToChannels();
			}};
	}
	
	/**provides a more complex edit than the superclass*/
	@Override
	public AbstractUndoableEdit2 provideUndoForDialog() {
		CombinedEdit output = new CombinedEdit(super.provideUndoForDialog());
		output.addEditToList(new ChannelLabelPropertiesUndo(this.getChannelLabelProperties()));
		return output;
	}

	/**creates a handle list that appears similar to a mini-toolbar*/
	@Override
	public TextActionButtonHandleList createActionHandleList() {
		return new ChannelLabelTextActionButtonHandleList(this);
	}
	
	public void changeText(String st) {
		this.getParagraph().get(0).get(0).setText(st);
		this.afterSplitUp();
	}
}
