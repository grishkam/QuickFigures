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
 * Date Modified: Apr 8, 2021
 * Date Created: Apr 7, 2021
 * Version: 2022.2
 */
package figureFormat;

import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelTextGraphic;
import channelLabels.MergeLabelStyle;
import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import figureOrganizer.FigureOrganizingLayerPane;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import layout.basicFigure.BasicLayoutEditor;
import locatedObject.AttachmentPosition;
import locatedObject.ColorDimmer;

/**Class is used to generate the options for figure templates
 * Each enum performs a perticular edit used to create a template
This class stores an option for a possible figure template
and maintains an preview of that that figure would look like
 */
public enum MutateFigure   {
	
	
	CHANNEL_LABELS_INSIDE,
	COLOR_CHANNEL_PANELS,
	BORDER_4,
	FONT_12(12,10),
	FONT_10(10,8),
	BORDER_8,
	TWO_COLUMN, VERTICAL,
	 MERGE_ONLY( true, ChannelUseInstructions.ONLY_MERGE_PANELS, true), 
	 MERGE_FIRST( true, ChannelUseInstructions.MERGE_FIRST, false), 
	 CHANNEL_LABELS_MERGED_ONLY;
	
	
	/**
	 * 
	 */

	boolean forMergedPanels=false;
	Long channelLabelFontSize=null;
	Integer scaleBarFont=null;
	
	private Integer mergePanelStatus=null;
	public boolean needsSecondImage;
	
	
	
	MutateFigure() {}
	MutateFigure(long f, int scaleBarFont) {channelLabelFontSize=f; this.scaleBarFont=scaleBarFont;}
	MutateFigure(boolean  b, int megreP, boolean needsSecondRow) {forMergedPanels=b; this.mergePanelStatus=megreP; this.needsSecondImage=needsSecondRow;}
	

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
		
		if(scaleBarFont!=null) {
			for(ZoomableGraphic f: figure.getAllGraphics()) {
				if(f instanceof BarGraphic.BarTextGraphic) {
					((BarGraphic.BarTextGraphic) f).setFontSize(scaleBarFont);}
			
		}
		}
		
		if(mergePanelStatus!=null) { 
			for(ImageDisplayLayer f:figure.getMultiChannelDisplays()) {
				if(mergePanelStatus!=null)
					f.getPanelManager().getChannelUseInstructions().MergeHandleing=mergePanelStatus;
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

