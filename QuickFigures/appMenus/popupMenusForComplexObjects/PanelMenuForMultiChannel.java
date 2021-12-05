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
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;

import advancedChannelUseGUI.AdvancedChannelUseGUI;
import fLexibleUIKit.ObjectAction;
import figureEditDialogs.PanelStackDisplayOptions;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import figureOrganizer.PanelListElement;
import figureOrganizer.PanelManager;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import iconGraphicalObjects.ChannelUseIcon;
import standardDialog.StandardDialog;
import undo.AbstractUndoableEdit2;
import undo.PanelManagerUndo;

public class PanelMenuForMultiChannel extends MenuForMultiChannelDisplayLayer {

	
	private PanelManager panelManager;

	public PanelMenuForMultiChannel(String name, MultichannelDisplayLayer panel,
			PanelList list, PanelManager panMan) {
		super(name, panel, list,  panel.getChannelLabelManager());
		panelManager=panMan;
		if(panMan==null) {panMan=panel.getPanelManager();}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void generateIncludedMenuItems() {
		
		recreateChannelUseMenuItem();
		showPanelListItem() ;
		addChangePPIMenuItem();
		
		
		JMenu expert=new JMenu("Expert Options");
		createImagePanelMenuItem(expert);
		create1ChannelPanelMenuItem(expert);
		createEliminatePanelMenuItem(expert) ;
	//	create1MergePanelMenuItem();
		recreatePanelsMenuItem(expert);
		this.add(expert);
		
		
	}

	public void addChangePPIMenuItem() {
		add(new ObjectAction<PanelManager>(panelManager) {
			@Override
			public AbstractUndoableEdit2  performAction() {
				ImagePanelGraphic panel = panelManager.getPanelList().getPanels().get(0).getPanelGraphic();
			double ppi = panel.getQuickfiguresPPI();
			double newppi=StandardDialog.getNumberFromUser("Input PPI ", ppi);
			return panelManager.changePPI(newppi);
			}
			
		}.createJMenuItem("Re-Set Pixel Density"));
	}
	
	SmartMenuItem2 createImagePanelMenuItem(JMenu  thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Generate New Image Panels") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(panelManager);
				stack.addAllCandF(display.getMultiChannelImage());
				panelManager.generatePanelGraphicsFor(stack);
			}
			
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createEliminatePanelMenuItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Eliminate Image Panels") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(){
				undo=PanelManagerUndo.createFor(panelManager);
				panelManager.eliminatePanels(stack);
				panelManager.updateDisplay();
			}
			
		};
		thi.add(out);
		return out;
	}
	
	
	SmartMenuItem2 create1MergePanelMenuItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Create 1 Merge Panel") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(panelManager);
				panelManager.addSingleMergePanel(stack);
				panelManager.updateDisplay();
			}
			
		};
		this.add(out);
		return out;
	}
	
	SmartMenuItem2 create1ChannelPanelMenuItem(JMenu t) {
		SmartMenuItem2 out=new SmartMenuItem2("Create New Image Panel") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(panelManager);
				PanelListElement panelp = panelManager.addSingleChannelPanel(stack);
				panelManager.putSingleElementOntoGrid(panelp, true);
				panelManager.updateDisplay();
			}
			
		};
		t.add(out);
		return out;
	}
	
	
	public SmartMenuItem2 recreateChannelUseMenuItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Channel Use") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				
				new PanelStackDisplayOptions(display, stack, panelManager,false).showDialog();;
				
			}
			
		};
		out.setIcon(new ChannelUseIcon(display.getMultiChannelImage().getChannelEntriesInOrder()));
		this.add(out);
		return out;
	}
	
	SmartMenuItem2 recreatePanelsMenuItem(JMenu j) {
		SmartMenuItem2 out=new SmartMenuItem2("Separately Recreate Panels") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				display.showOptionsThenRegeneratePanelGraphics();
			}
			
		};
		j.add(out);
		return out;
	}
	
	SmartMenuItem2 showPanelListItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Advanced Channel and Frame Use") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				showAdvancedChannelUse();
			}
			
		};
		
			out.setIcon(new ChannelUseIcon(null, ChannelUseIcon.ADVANCED, false));
		
			this.add(out);
			return out;
	}

	/**
	shows the advanced channel use dialog
	 */
	public void showAdvancedChannelUse() {
		AdvancedChannelUseGUI distpla = new AdvancedChannelUseGUI( panelManager, labelManager);
		
		distpla.setVisible(true);
	}
	
}
