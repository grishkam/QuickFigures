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
 * Date Modified: May 9, 2021
 * Version: 2021.1
 */
package addObjectMenus;

import channelMerging.ChannelUseInstructions;
import channelMerging.PreProcessInformation;
import figureFormat.FigureTemplate;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import messages.ShowMessage;
/**
 Adds a special kind of figure adder, assumes the input is a blot.
 row labels will be added on the right and additional images 
 will be added below. No scale bar or channel labels are created.
 */
public class BlotFigureAdder extends FigureAdder {

	/**
	 * @param fromFile
	 */
	public BlotFigureAdder(boolean fromFile) {
		super(fromFile);
		mergeOnly=MERGE_PANELS_ONLY;
		useSingleFrame=true;
		useSingleSlice=true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getMenuCommand() {
		return "Blot "+super.getMenuCommand();
	}
	
	
	/**when given a normal parent layer and a multidimensional image display layer,
	 places the multichannel display layer into a figure organizing layer (which is created if the ordinary layer is not inside of an existing figure organizer). 
	 that figure organizer will contain the multidimensional image inside of it  
	 * @param p */
	public FigureOrganizingLayerPane addFigureOrganizerFor(GraphicLayer ordinaryLayer, MultichannelDisplayLayer multiDimensionalImage, PreProcessInformation p) {

		FigureOrganizingLayerPane output = super.addFigureOrganizerFor(ordinaryLayer, multiDimensionalImage, p);
		output.getMontageLayoutGraphic().rowLabelsOnRight=true;
		output.getMontageLayoutGraphic().getPanelLayout().rowmajor=false;
		ShowMessage.showOptionalMessage("Lane labels", false, "To add a series of labels for blot lanes", "first select an image panel, then", "go to the Add menu and choose 'to selected panels->Lane Labels' ");
		output.transform().move(0, 200);
		return output;
	}
	
	
	/**returns the figure template to be used by this adder. 
	 since blots do not need scale bars, this returns a template without one*/
	protected FigureTemplate getUsedTemplate(MultichannelDisplayLayer display) {
		FigureTemplate output = super.getUsedTemplate(display);
		output.getScaleBar().setModelItem(null);
		output.getMultiChannelPicker().getModelItem().getPanelManager().getChannelUseInstructions().MergeHandleing=ChannelUseInstructions.ONLY_MERGE_PANELS;
		return output;
	}
	
	
	/**
	 Using the figure template given, adds a figure, unlike the superclass, this version does not produce 
	 * @param p 
	 */
	protected void addImageToFigureUsingTemplate(FigureOrganizingLayerPane currentFigureOrganizer, MultichannelDisplayLayer multiDimensionalImage, FigureTemplate temp, PreProcessInformation p) {
		multiDimensionalImage.channelLabelsEnabled=false;
		super.addImageToFigureUsingTemplate(currentFigureOrganizer, multiDimensionalImage, temp, p);
		
	}
	

	

}
