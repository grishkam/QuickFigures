/**
 * Author: Greg Mazo
 * Date Modified: Apr 25, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
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
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
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

	@Override
	public ArrayList<PanelManager> getPanelManagersInLayoutOrder() {
		 return getPanelManagers() ;
	}

	@Override
	public ChannelLabelManager getChannelLabelManager() {
		return targetInset.getChannelLabelManager();
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
