/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package graphicalObjects_FigureSpecific;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import channelLabels.ChannelLabelManager;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import objectDialogs.PanelStackDisplayOptions;
import popupMenusForComplexObjects.MenuForChannelLabelMultiChannel;
import popupMenusForComplexObjects.PanelMenuForMultiChannel;

/**handles menus and dialogs
 */
class PanelAndLayoutManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ChannelLabelManager labels;
	private MontageLayoutGraphic layout;
	private PanelManager panelManager;
	private GraphicLayer layer;
	
	
	

	PanelAndLayoutManager(ChannelLabelManager labelsMan, PanelManager stack,
			GraphicLayer dumpingLayer, MontageLayoutGraphic layout) {
		this.labels=labelsMan;
			this.layout=layout;
			this.panelManager=stack;
			this.layer=dumpingLayer;
		
	}
	
	
	void showChannelUseOption() {
		new PanelStackDisplayOptions(panelManager.getDisplay(),panelManager.getPanelList(), panelManager, false);
		
	}
	
	
	
	ArrayList<JMenuItem> getPopupItems() {
		ArrayList<JMenuItem> output=new ArrayList<JMenuItem>();
		output.add(new PanelMenuForMultiChannel("Image Panels",  panelManager.getDisplay(), panelManager.getPanelList(),panelManager));
		output.add(new MenuForChannelLabelMultiChannel("Channel Labels", panelManager.getDisplay(),  panelManager.getPanelList(), labels));
		
		return output;
	}

	

}
