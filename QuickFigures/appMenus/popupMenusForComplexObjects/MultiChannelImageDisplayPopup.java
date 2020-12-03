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
package popupMenusForComplexObjects;


import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import channelMerging.PreProcessInformation;
import fLexibleUIKit.ObjectAction;
import genericMontageKit.PanelList;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import menuUtil.PopupMenuSupplier;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import objectDialogs.CroppingDialog;
import ultilInputOutput.FileChoiceUtil;
import undo.Edit;

public class MultiChannelImageDisplayPopup extends SmartPopupJMenu implements
		PopupMenuSupplier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelList list;
	private MultichannelDisplayLayer panel;
	private ImagePanelGraphic clickedPanel;

	public MultiChannelImageDisplayPopup(MultichannelDisplayLayer panel, PanelList list, ImagePanelGraphic img) {
		this.list=list;
		this.panel=panel;
			this.clickedPanel=img;	
		addMenus(this, panel, list);
		
	}

	public void addMenus(SmartPopupJMenu thi, MultichannelDisplayLayer panel, PanelList list) {
		thi.add(new ImageMenuForMultiChannel("Source Image", panel, list) );
		thi.add(new PanelMenuForMultiChannel("Image Panels", panel, list, panel.getPanelManager()));
		thi.add(new MenuForChannelLabelMultiChannel("Channel Labels", panel, list, panel.getChannelLabelManager()));
		addChannelMenu(thi);
	}
	
	public JMenu[] addMenus(JMenu thi) {
		JMenu[] output=new JMenu[3];
		output[0]=new ImageMenuForMultiChannel("Source Image", panel, list) ;
		thi.add(output[0]);
		output[1]=new PanelMenuForMultiChannel("Image Panels", panel, list, panel.getPanelManager());
		thi.add(output[1]);
		addChannelLabelMenu(thi);
		
		addChannelMenu(thi);
		addRemoveImage(thi);
		addView(thi);
		return output;
		
	}

	/**creates a menu item to remove the image*/
	protected void addRemoveImage(JMenu thi) {
		ObjectAction<MultichannelDisplayLayer> act = new ObjectAction<MultichannelDisplayLayer>(panel) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(panel.getParentLayer() instanceof FigureOrganizingLayerPane) {
					FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) panel.getParentLayer() ;
					if(f.getAllSourceImages().size()==1) {
						FileChoiceUtil.yesOrNo("Figure must contain at least one image. Will not remove last image. Understood?");
						return;
					}
				}
				boolean b=FileChoiceUtil.yesOrNo("Are you sure you want to remove this image from figure?");
				
				if (b)
					{
					
					addUndo(Edit.removeItem(panel.getParentLayer(), panel)
							);
					
					
					};
				
			}};
		thi.add(act.createJMenuItem("Remove From Figure"));
	}
	
	/**creates a menu item to remove the image*/
	protected void addView(JMenu thi) {
		ObjectAction<MultichannelDisplayLayer> act = new ObjectAction<MultichannelDisplayLayer>(panel) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(panel.getParentLayer() instanceof FigureOrganizingLayerPane) {
					FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) panel.getParentLayer() ;
					MultichannelDisplayLayer co = panel.similar();
					
					co.setSlot(co.getSlot().copy());
					co.getSlot().redoCropAndScale();
					co.setLaygeneratedPanelsOnGrid(true);
					PreProcessInformation mods =co.getSlot().getModifications();
					Rectangle r=null;
					if(mods==null||mods.getRectangle()==null) {
						int w = panel.getMultiChannelImage().getDimensions().width/2;
						int h = panel.getMultiChannelImage().getDimensions().height/2;
						r=new Rectangle(0,0,w,h);
					} else {
						r=mods.getRectangle();
					}
					//CompoundEdit2 undo = new CompoundEdit2();
					CroppingDialog.showCropDialog(co.getSlot(), r, 0);
					addUndo(
							f.nextMultiChannel(co));
					
				} else return;
				
				
				
			}};
		thi.add(act.createJMenuItem("Copy with new cropping"));
	}

	public void addChannelMenu(Container thi) {
		JMenu oneMore = new SmartJMenu("Channels");
		ChannelPanelEditingMenu b = new ChannelPanelEditingMenu(panel, clickedPanel);
		b.workOn=0;
		b.addChannelRelevantMenuItems(oneMore, true);
		thi.add(oneMore);
	}

	public void addChannelLabelMenu(JMenu thi) {
		thi.add(createChanLabelMenu());
	}

	public MenuForChannelLabelMultiChannel createChanLabelMenu() {
		return new MenuForChannelLabelMultiChannel("Channel Labels", panel, list, panel.getChannelLabelManager());
	}
	
	@Override
	public JPopupMenu getJPopup() {
		return this;
	}



}
