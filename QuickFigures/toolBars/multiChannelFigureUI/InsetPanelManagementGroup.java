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
 
 * 
 */
package multiChannelFigureUI;

import java.util.ArrayList;

import channelLabels.ChannelLabelManager;
import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import figureOrganizer.CollectivePanelManagement;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelManager;
import figureOrganizer.PanelOrderCorrector;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner.InsetPanelManager;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import layout.basicFigure.BasicLayout;

/**
 
 * 
 */
public class InsetPanelManagementGroup implements CollectivePanelManagement {

	private PanelGraphicInsetDefiner targetInset;

	/**
	 * @param pressedInset
	 */
	public InsetPanelManagementGroup(PanelGraphicInsetDefiner pressedInset) {
		this.targetInset=pressedInset;
	}

	

	@Override
	public ArrayList<ImageDisplayLayer> getDisplaysInOrder() {
		 ArrayList<ImageDisplayLayer>  output=new  ArrayList<ImageDisplayLayer>  ();
			for(PanelGraphicInsetDefiner inset:targetInset.getInsetDefinersThatShareLayout()) {
				MultichannelDisplayLayer imageDisplayLayer = inset.getPanelManager().getImageDisplayLayer();
				if(output.contains(imageDisplayLayer))
					continue;
				output.add(imageDisplayLayer);
			}
			return output;
	}

	@Override
	public ImageDisplayLayer getMultichannel() {
		return targetInset.getSourceDisplay();
	}

	@Override
	public GraphicLayerPane getTargetLayer() {
		return targetInset.personalLayer;
	}

	@Override
	public ArrayList<ChannelUseInstructions> getChannelUserInformation() {
		 ArrayList<ChannelUseInstructions> output=new  ArrayList<ChannelUseInstructions> ();
		for(PanelGraphicInsetDefiner inset:targetInset.getInsetDefinersThatShareLayout()) {
			output.add(inset.getPanelManager().getChannelUseInstructions());
		}
		return output;
	}

	/**
	 sets the relative scale of the panel manager to match the scale of the panels
	 */
	public void updatePanelLevelScale() {
		for(PanelManager i: this.getPanelManagers()) {
			i.setPanelLevelScaleToPanels();
		}
		
	}

	@Override
	public ArrayList<PanelManager> getPanelManagers() {
		 ArrayList<PanelManager> output=new  ArrayList<PanelManager> ();
			for(PanelGraphicInsetDefiner inset:targetInset.getInsetDefinersThatShareLayout()) {
				output.add(inset.getPanelManager());
			}
			return output;
	}

	/**not yet properly implemented*/
	@Override
	public ArrayList<PanelManager> getPanelManagersInLayoutOrder() {
		return  new PanelOrderCorrector(this).getPanelManagersInLayoutImageOrder();
		// return getPanelManagers() ;
	}

	
	/**returns one channel label manager. specifically, the first one to appear in the layout*/
	@Override
	public ChannelLabelManager getChannelLabelManager() {
		
		ChannelLabelManager channelLabelManager = null;
		ArrayList<PanelManager> managers = this.getPanelManagersInLayoutOrder();
		for(PanelGraphicInsetDefiner p: targetInset.getInsetDefinersThatShareLayout()) {
			InsetPanelManager i=(InsetPanelManager) managers.get(0);
			if(i.getInset()==p)
				 {channelLabelManager = p.getChannelLabelManager();
				
				 }
		}
		return channelLabelManager;
	}

	@Override
	public DefaultLayoutGraphic getTargetLayout() {
		return targetInset.personalLayout;
	}



	@Override
	public BasicLayout getUsedLayout() {
		return this.getTargetLayout().getPanelLayout();
	}

}
