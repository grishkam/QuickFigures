/**
 * Author: Greg Mazo
 * Date Modified: Apr 8, 2021
 * Date Created: Apr 7, 2021
 * Version: 2021.1
 */
package figureFormat;

import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelTextGraphic;
import channelLabels.MergeLabelStyle;
import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import channelMerging.MultiChannelImage;
import figureOrganizer.FigureOrganizingLayerPane;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import layout.basicFigure.BasicLayoutEditor;
import locatedObject.AttachmentPosition;
import locatedObject.ColorDimmer;
import logging.IssueLog;

/**Class is used to generate the options for figure templates
 * Each enum performs a perticular edit used to create a template
This class stores an option for a possible figure template
and maintains an preview of that that figure would look like
 */
public enum MutateFigure   {
	
	
	CHANNEL_LABELS_INSIDE,
	COLOR_CHANNEL_PANELS,
	BORDER_4,
	FONT_12(12),
	BORDER_8,
	TWO_COLUMN, VERTICAL,
	 MERGE_ONLY(true), CHANNEL_LABELS_MERGED_ONLY;
	
	
	boolean forMergedPanels=false;
	Long channelLabelFontSize=null;
	
	
	MutateFigure() {}
	MutateFigure(long f) {channelLabelFontSize=f;}
	MutateFigure(boolean  b) {forMergedPanels=b;}
	

	public static MutateFigure[] getShortList() {return new MutateFigure[] {CHANNEL_LABELS_INSIDE,COLOR_CHANNEL_PANELS};}
	
	/**
	 * @param figure
	 */
	public void mutate(FigureOrganizingLayerPane figure) {
		if(this==CHANNEL_LABELS_INSIDE) {
			ChannelLabelManager channelLabelManager = figure.getPrincipalMultiChannel().getChannelLabelManager();
			for(ChannelLabelTextGraphic label:channelLabelManager.getAllLabels())
			{
				label.getAttachmentPosition().setLocationTypeInternal(AttachmentPosition.UPPER_LEFT);
				label.getAttachmentPosition().setHorizontalOffset(2);
				label.getAttachmentPosition().setVerticalOffset(1);
				label.getAttachmentPosition().setLocationCategory(AttachmentPosition.INTERNAL);
				label.setDimming(ColorDimmer.FULL_BRIGTHNESS);
				label.updateChannelLabelPropertiesToLabelText();
				}
		for(ImagePanelGraphic panel:	figure.getPrincipalMultiChannel().getPanelList().getPanelGraphics())
				{panel.snapLockedItems();}
			
		}
		
		if(this==COLOR_CHANNEL_PANELS) {
			figure.getPrincipalMultiChannel().getPanelList().getChannelUseInstructions().channelColorMode=ChannelUseInstructions.CHANNELS_IN_COLOR;
			figure.updatePanelsAndLabelsFromSource();
		}
		
		if(this==BORDER_4) {
			BasicLayoutEditor edit = figure.getMontageLayoutGraphic().getEditor();
			edit.setVerticalBorder(figure.getLayout(), 4);
			edit.setHorizontalBorder(figure.getLayout(), 4);
		}
		
		if(this==BORDER_8) {
			BasicLayoutEditor edit = figure.getMontageLayoutGraphic().getEditor();
			edit.setVerticalBorder(figure.getLayout(), 8);
			edit.setHorizontalBorder(figure.getLayout(), 8);
		}
		
		if(channelLabelFontSize!=null) {
			ChannelLabelManager channelLabelManager = figure.getPrincipalMultiChannel().getChannelLabelManager();
			for(ChannelLabelTextGraphic label:channelLabelManager.getAllLabels())
			{
				label.setFont(label.getFont().deriveFont((float)channelLabelFontSize));
				label.updateChannelLabelPropertiesToLabelText();
				}
		for(ImagePanelGraphic panel:	figure.getPrincipalMultiChannel().getPanelList().getPanelGraphics())
				{panel.snapLockedItems();}
			
		}
		
		if(this==MERGE_ONLY) { 
			for(ImageDisplayLayer f:figure.getMultiChannelDisplays()) {
				f.getPanelManager().getChannelUseInstructions().MergeHandleing=ChannelUseInstructions.ONLY_MERGE_PANELS;
				f.eliminateAndRecreate();
			}
			figure.fixLabelSpaces();
		}
		
		if(this==TWO_COLUMN) { 
			figure.getPrincipalMultiChannel().getPanelList().getChannelUseInstructions().setIdealNumberOfColumns(2);
			BasicLayoutEditor edit = figure.getMontageLayoutGraphic().getEditor();
			figure.getMontageLayoutGraphic().generateCurrentImageWrapper();
			edit.repackagePanels(figure.getLayout(), 2, 2);
		}
		
		if(this==VERTICAL) { 
			
			
			BasicLayoutEditor edit = figure.getMontageLayoutGraphic().getEditor();
			figure.getMontageLayoutGraphic().generateCurrentImageWrapper();
			edit.invertPanelsAndLabels(figure.getLayout());
			figure.fixLabelSpaces();
		}
		
		
		if(this==CHANNEL_LABELS_MERGED_ONLY) {
			
				ChannelLabelManager channelLabelManager = figure.getPrincipalMultiChannel().getChannelLabelManager();
				for(ChannelLabelTextGraphic label:channelLabelManager.getAllLabels())
				{
					label.getAttachmentPosition().setLocationTypeExternal(AttachmentPosition.ABOVE_AT_LEFT);
					label.getAttachmentPosition().setHorizontalOffset(0);
					label.getAttachmentPosition().setVerticalOffset(1);
					label.getAttachmentPosition().setLocationCategory(AttachmentPosition.EXTERNAL);
					
					label.setDimming(ColorDimmer.NORMAL_DIM);
					label.getChannelLabelProperties().setMergeLabelStyle(MergeLabelStyle.MULTIPLE_LINES);
					label.updateChannelLabelPropertiesToLabelText();
					}
			for(ImagePanelGraphic panel:	figure.getPrincipalMultiChannel().getPanelList().getPanelGraphics())
					{panel.snapLockedItems();}
			figure.fixLabelSpaces();
		}
		
		
	}


	
	
	
	
}
