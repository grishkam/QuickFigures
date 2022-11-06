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
 * Version: 2022.1
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import channelLabels.ChannelLabelManager;
import channelMerging.CSFLocation;
import channelMerging.MultiChannelImage;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.OverlayObjectList;
import objectDialogs.CroppingDialog;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.PanelManagerUndo;

public class MenuForMultiChannelDisplayLayer extends JMenu {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected MultichannelDisplayLayer display;
	protected PanelList stack;
	protected ChannelLabelManager labelManager;
	
	

	public MenuForMultiChannelDisplayLayer(String name, MultichannelDisplayLayer  panel, PanelList list, ChannelLabelManager man) {
		this.setText(name);
		this.setName(name);
		display=panel;
		stack=list;
		this.labelManager=man;
		generateIncludedMenuItems();
	}
	
	public void generateIncludedMenuItems() {
		createMergeMenuItem() ;
	
		createAllLabelMenuItem();
		JMenu add=new JMenu("Add/Replace");
		JMenu rem=new JMenu("Remove");
		create1ChannelLabelItem(add);
		create1MergeLabelItem(add) ;
		createGenerateChannelLabelItem(add);
		createGenerateChannelLabelItem2(add);
	;
		createEliminateChannelLabelItem(rem) ;
		
		
		//createCopySavedChannelLabelItem();
		JMenu expert = new JMenu("Expert Options");
		createResetNameItem(expert);
		createNameChannelLabelItem(expert);
		expert.add(add);
		expert.add(rem);
		this.add(expert);
	}
	
	public SmartMenuItem2 createAllLabelMenuItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Edit All Channel Labels") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				labelManager.showEditAllChannelLabelsDialog();
			}
			
		};
		this.add(out);
		return out;
	}
	
	
	
	SmartMenuItem2 createMergeMenuItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Merge Label Menu") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				
				labelManager.showChannelLabelPropDialog();
				
			}
			
		};
		this.add(out);
		return out;
	}
	
	SmartMenuItem2 create1MergeLabelItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Add 1 Merge Label") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(display);
				labelManager.addSingleMergeLabel();
			}
			
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 create1ChannelLabelItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Add 1 Channel Label") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(display);
				labelManager.addSingleChannelLabel();
			}
			
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createEliminateChannelLabelItem(JMenu j) {
		SmartMenuItem2 out=new SmartMenuItem2("Eliminate Channel Labels") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onAction(){
			undo=labelManager.eliminateChanLabels();
			}
		};
		j.add(out);
		return out;
	}
	
	SmartMenuItem2 createCopySavedChannelLabelItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Load Label Properties From Saved") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				display.setLabalPropertiesToSaved();
			}
			
		};
		this.add(out);
		return out;
	}
	
	SmartMenuItem2 createGenerateChannelLabelItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Generate New Channel Labels") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(display);
				labelManager.generateChannelLabels();
			}
			
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createGenerateChannelLabelItem2(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Generate New Channel Labels (first slice only)") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(display);
				labelManager.generateChannelLabels2();
			}
			
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createNameChannelLabelItem(JMenu th) {
		SmartMenuItem2 out=new SmartMenuItem2("See Channel Labels") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				labelManager.nameChannels();
			}
			
		};
		th.add(out);
		return out;
	}
	
	SmartMenuItem2 createResetNameItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Reset Channel Names") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				display.getMultiChannelImage().renameBasedOnRealChannelName();;
				display.updatePanelsAndLabelsFromSource();
			}
			
		};
		thi.add(out);
		return out;
	}
	

	 protected abstract class SmartMenuItem2 extends JMenuItem implements ActionListener {
		 
		 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected CombinedEdit undo;

		public SmartMenuItem2(String name) {
			 super(name);
			 this.addActionListener(this);
			 
		 }
		public void addUndo(AbstractUndoableEdit2 e) {
			new CurrentFigureSet().addUndo(e);
		}
		
		public void actionPerformed(ActionEvent e) {
			this.onAction();
			if(undo==null) return;
			undo.establishFinalState();
			addUndo(undo);
		}
		abstract void onAction() ;
		 
	 }
	 
	 SmartMenuItem2 createCropOption() {
			SmartMenuItem2 out=new SmartMenuItem2("Re-Crop") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onAction() {
					addUndo(
							FigureOrganizingSuplierForPopup.showRecropDisplayDialog(display, null, null, null)
							);
				}
				
			};
			
			
			addOverlayEditOption();
			
			this.add(out);
			
			return out;
		}

	/**
	 * Adds a menu option for editing the overlay for the original source image
	 */
	public void addOverlayEditOption() {
		SmartMenuItem2 outoverlay = new SmartMenuItem2("Edit Overlay") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				MultiChannelImage unprocessedVersion = display.getSlot().getUnprocessedVersion(false);
				OverlayObjectList objects = unprocessedVersion.getOverlayObjects("editor window");
				ImagePanelGraphic imagepanel = new ImagePanelGraphic(CroppingDialog.createDisplayImage(new CSFLocation(),unprocessedVersion, 0, 1, 1));
				imagepanel.setOverlayObjects(objects);
				addUndo(OverlaySubmenu.showEditWindowForOverlay(imagepanel, new WindowListener() {

					@Override
					public void windowOpened(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowClosing(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowClosed(WindowEvent e) {
						display.updateFromOriginal();
						
					}

					@Override
					public void windowIconified(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowDeiconified(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowActivated(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowDeactivated(WindowEvent e) {
						display.updateFromOriginal();
						
					}
					
				}));
			}
			
		};
		this.add(outoverlay);
	}
		
	 
		protected SmartMenuItem2 createSetScaleItem() {
			SmartMenuItem2 out=new SmartMenuItem2("Set Pixel Size (Set Scale...)") {
				private static final long serialVersionUID = 1L;
				@Override
				public void onAction() {
					new SetImagePixelSize(display). usePixelSizeSetDialog(null);
				}
			};
			this.add(out);
			return out;
			
		}

}
