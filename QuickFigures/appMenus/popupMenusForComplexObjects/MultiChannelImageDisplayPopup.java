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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package popupMenusForComplexObjects;


import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import fLexibleUIKit.ObjectAction;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import menuUtil.PopupMenuSupplier;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import ultilInputOutput.FileChoiceUtil;
import undo.CombinedEdit;
import undo.Edit;

/**A popup menu for multichannel display layers*/
public class MultiChannelImageDisplayPopup extends SmartPopupJMenu implements
		PopupMenuSupplier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelList list;
	private MultichannelDisplayLayer displayLayer;
	private ImagePanelGraphic clickedPanel;

	public MultiChannelImageDisplayPopup(MultichannelDisplayLayer panel, PanelList list, ImagePanelGraphic img) {
		this.list=list;
		this.displayLayer=panel;
			this.clickedPanel=img;	
		addMenus(this, panel, list);
		
	}

	/**Adds each submenu*/
	public void addMenus(SmartPopupJMenu thi, MultichannelDisplayLayer panel, PanelList list) {
		thi.add(new ImageMenuForMultiChannel("Source Image", panel, list) );
		thi.add(new PanelMenuForMultiChannel("Image Panels", panel, list, panel.getPanelManager()));
		thi.add(new MenuForMultiChannelDisplayLayer("Channel Labels", panel, list, panel.getChannelLabelManager()));
		addChannelMenu(thi);
	}
	
	/**returns an array of the menus*/
	public JMenu[] addMenus(JMenu thi) {
		JMenu[] output=new JMenu[3];
		output[0]=new ImageMenuForMultiChannel("Source Image", displayLayer, list) ;
		thi.add(output[0]);
		output[1]=generatePanelMenu();
		thi.add(output[1]);
		addChannelLabelMenu(thi);
		
		addChannelMenu(thi);
		addRemoveImage(thi);
		addView(thi);
		return output;
		
	}

	/**
	 * @return
	 */
	private PanelMenuForMultiChannel generatePanelMenu() {
		return new PanelMenuForMultiChannel("Image Panels", displayLayer, list, displayLayer.getPanelManager());
	}

	/**creates a menu item to remove the image*/
	protected void addRemoveImage(JMenu thi) {
		ObjectAction<MultichannelDisplayLayer> act = new ObjectAction<MultichannelDisplayLayer>(displayLayer) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(displayLayer.getParentLayer() instanceof FigureOrganizingLayerPane) {
					FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) displayLayer.getParentLayer() ;
					if(f.getAllSourceImages().size()==1) {
						FileChoiceUtil.yesOrNo("Figure must contain at least one image. Will not remove last image. Understood?");
						return;
					}
				}
				boolean b=FileChoiceUtil.yesOrNo("Are you sure you want to remove this image from figure?");
				
				if (b)
					{
					
					addUndo(Edit.removeItem(displayLayer.getParentLayer(), displayLayer)
							);
					
					
					};
				
			}};
		thi.add(act.createJMenuItem("Remove From Figure"));
	}
	
	
	/**
	crates a new multichannel display layer with a new set of panels
	 */
	public CombinedEdit createSecondView(MultichannelDisplayLayer displayLayer) {
		if(displayLayer.getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) displayLayer.getParentLayer() ;
			
			return FigureOrganizingLayerPane.createSecondView(f, displayLayer, null);
			
		} else return null;
	}

	/**creates a menu item to add another display layer, using the same source image as this one*/
	protected void addView(JMenu thi) {
		ObjectAction<MultichannelDisplayLayer> act = new ObjectAction<MultichannelDisplayLayer>(displayLayer) {
			@Override
			public void actionPerformed(ActionEvent e) {
				this.addUndo(
						createSecondView(displayLayer));
				
				
				
			}

			
			};
		thi.add(act.createJMenuItem("Create second view"));
	}

	/**Adds a channel menu that is limited to this display layer*/
	public void addChannelMenu(Container thi) {
		JMenu oneMore = new SmartJMenu("Channels");
		ChannelPanelEditingMenu b = new ChannelPanelEditingMenu(displayLayer, clickedPanel);
		b.setScope(0);
		b.addChannelRelevantMenuItems(oneMore, true);
		thi.add(oneMore);
	}

	public void addChannelLabelMenu(JMenu thi) {
		thi.add(createChanLabelMenu());
	}

	public MenuForMultiChannelDisplayLayer createChanLabelMenu() {
		return new MenuForMultiChannelDisplayLayer("Channel Labels", displayLayer, list, displayLayer.getChannelLabelManager());
	}
	
	@Override
	public JPopupMenu getJPopup() {
		return this;
	}



}
