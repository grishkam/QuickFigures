/**
 * Author: Greg Mazo
 * Date Modified: Apr 8, 2021
 * Date Created: Apr 7, 2021
 * Version: 2021.1
 */
package figureFormat;

import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.ChannelUseInstructions;
import figureOrganizer.FigureOrganizingLayerPane;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import layout.basicFigure.BasicLayoutEditor;
import locatedObject.AttachmentPosition;
import locatedObject.ColorDimmer;

/**
This class stores an option for a possible figure template
and maintains an preview of that that figure would look like
 */
public enum MutateFigure   {
	
	
	CHANNEL_LABELS_INSIDE,
	COLOR_CHANNEL_PANELS,BORDER_4,
	FONT_12,
	BORDER_8,
	TWO_COLUMN, VERTICAL;
	

	public static MutateFigure[] getShortList() {return new MutateFigure[] {CHANNEL_LABELS_INSIDE,COLOR_CHANNEL_PANELS,BORDER_4,  BORDER_8, FONT_12,TWO_COLUMN};}
	
	/**
	 * @param figure
	 */
	public void mutate(FigureOrganizingLayerPane figure) {
		if(this==CHANNEL_LABELS_INSIDE||this==TWO_COLUMN) {
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
		
		if(this==FONT_12) {
			ChannelLabelManager channelLabelManager = figure.getPrincipalMultiChannel().getChannelLabelManager();
			for(ChannelLabelTextGraphic label:channelLabelManager.getAllLabels())
			{
				label.setFont(label.getFont().deriveFont((float)12));
				label.updateChannelLabelPropertiesToLabelText();
				}
		for(ImagePanelGraphic panel:	figure.getPrincipalMultiChannel().getPanelList().getPanelGraphics())
				{panel.snapLockedItems();}
			
		}
		
		if(this==TWO_COLUMN) { 
			figure.getPrincipalMultiChannel().getPanelList().getChannelUseInstructions().setIdealNumberOfColumns(2);
			BasicLayoutEditor edit = figure.getMontageLayoutGraphic().getEditor();
			figure.getMontageLayoutGraphic().generateCurrentImageWrapper();
			edit.repackagePanels(figure.getLayout(), 3, 2);
		}
		
		if(this==VERTICAL) { 
			
			
			BasicLayoutEditor edit = figure.getMontageLayoutGraphic().getEditor();
			figure.getMontageLayoutGraphic().generateCurrentImageWrapper();
			edit.invertPanelsAndLabels(figure.getLayout());
			figure.fixLabelSpaces();
		}
		
		
	}


	
	
	
	
}
