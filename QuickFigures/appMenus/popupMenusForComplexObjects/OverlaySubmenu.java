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
 * Date Created: Nov 6, 2022
 * Date Modified: Nov 19, 2022
 * Copyright (C) 2022 Gregory Mazo
 * Version: 2023.2
 */

package popupMenusForComplexObjects;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.undo.AbstractUndoableEdit;

import channelMerging.PreProcessInformation;
import fLexibleUIKit.ObjectAction;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.OverlayHolder;
import graphicalObjects_SpecialObjects.OverlayObjectList;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageDisplayApp.OverlayObjectManager;
import imageDisplayApp.StandardWorksheet;
import layout.BasicObjectListHandler;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import messages.ShowMessage;
import multiChannelFigureUI.ImagePropertiesButton;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.Edit;
import utilityClasses1.ArraySorter;

/**
 A submenu for actions related to the overlays above an image panel
 */
public class OverlaySubmenu extends SmartJMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanelGraphic c;

	/**
	 * @param string
	 */
	public OverlaySubmenu(ImagePanelGraphic image) {
		super("Overlay");
		this.c=image;
		addMenuItems();
	}

	/**Adds the meny items*/
	public void addMenuItems() {
	
		SmartJMenu editMenu = new SmartJMenu("Edit");
		
		
		add(new ObjectAction<ImagePanelGraphic>(c) {
		@Override
		public AbstractUndoableEdit2 performAction() {
			AbstractUndoableEdit2 undo = item.provideUndoForDialog();
			item.setShowOverlay(!item.isShowOverlay());
			if(item.getOverlay()==null) {
				ShowMessage.showOptionalMessage("There is no overlay in this image", false, "there are no overlay objects");
				return undo;
				
			} else if(item.getOverlay().getOverlayObjects().size()==0) {
				ShowMessage.showOptionalMessage("There are no overlay objects in this image", false, "there are no overlay objects");
				return undo;
			}
			
			item.updateDisplay();
			return undo;
			}	
		
		public JMenuItem createJMenuItem(String st) {
			
			JMenuItem output = super.createJMenuItem("Show Overlay");
			if(item.isShowOverlay())
				output = super.createJMenuItem("Hide Overlay");
			return output;
		}
	}.createJMenuItem("Show Overlay"));
		
		this.add(editMenu);
		
		editMenu.add(new ObjectAction<ImagePanelGraphic>(c) {
		@Override
		public AbstractUndoableEdit2 performAction() {
			CombinedEdit undo2= item.getOverlay().getUndoForEditWindow(item);
			undo2.addEditToList( item.provideUndoForDialog());
			
			GraphicLayerPane extractOverlay =new GraphicLayerPane("Extracted overlay");
			
			undo2.addEditToList(OverlayHolder.extractOverlay(item, extractOverlay, true, item.getOverlay()));
			if(extractOverlay.getItemArray().size()==0) {
				ShowMessage.showOptionalMessage("No overlay objects were extracted");
				return null;
			}
				
			undo2.addEditToList( Edit.addItem(item.getParentLayer(), extractOverlay));
			item.setShowOverlay(false);
			return  undo2;
			
			}	
		
	}.createJMenuItem("Extract Objects"));
		
		editMenu.add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {
				CombinedEdit undo2= item.getOverlay().getUndoForEditWindow(item);
				undo2.addEditToList( item.provideUndoForDialog());
				ArrayList<LocatedObject2D> itemsAdded = c.getTopLevelContainer().getLocatedObjects();
				ArraySorter.removeNonSelectionItems(itemsAdded);
				switchToEditMode(item);
				return new CombinedEdit( undo2,OverlayHolder.insertIntoOverlay(item, item.getOverlay(),itemsAdded));
				
				}	
			
		}.createJMenuItem("Add selected objects"));
		
		editMenu.add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {
				
				CombinedEdit undo2= item.getOverlay().getUndoForEditWindow(item);
				undo2.addEditToList( item.provideUndoForDialog());
				int n = item.getOverlay().getAllGraphics().size();
				ArrayList<LocatedObject2D> itemsAdded = new BasicObjectListHandler().getContainedObjects(item.getBounds(), item.getTopLevelContainer());
				
				switchToEditMode(item);
				undo2.addEditToList(  OverlayHolder.insertIntoOverlay(item, item.getOverlay(), itemsAdded));
				int dn = item.getOverlay().getAllGraphics().size()-n;
				IssueLog.log("Added "+dn+" objects to overlay");
				return undo2;
				
				}	
			
		}.createJMenuItem("Add shapes"));
		
		addOverlayEditor( editMenu);
		
		
		SmartJMenu share = new SmartJMenu("Sharing");
		this.add(share);
		
		/**Adds an option to make the overlay for this panel unique.*/
		share.add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {
				AbstractUndoableEdit2 undo = item.provideUndoForDialog();
				item.updateOrSetOverlayObjects(item.getOverlay().copy());
				switchToEditMode(item);
				return undo;
				}	
			
		}.createJMenuItem("Make Overlay Unique"));
		
	
		
		
		/**Adds an option to make the overlay for this panel unique.*/
		share.add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {
				CombinedEdit undo = new CombinedEdit();
				undo.addEditToList(item.provideUndoForDialog());
				ArrayList<ZoomableGraphic> panels = item.getParentLayer().getAllGraphics();
				ArraySorter.removeNonSelectionItems(panels);
				ArraySorter.removeThoseNotOfClass(panels, ImagePanelGraphic.class);
				panels.remove(item);
				boolean done=false;
				if(panels.size()==0) {
					ShowMessage.showOptionalMessage("Make sure that you select one of the sister panels");
					return null;
				}
				
				for(Object p: panels) {
					ImagePanelGraphic imagePanelGraphic = (ImagePanelGraphic)p;
					
					if(imagePanelGraphic.getOverlay()==item.getOverlay())
						continue;
					undo.addEditToList(imagePanelGraphic.provideUndoForDialog());
					imagePanelGraphic.getOverlay().setEdited(false);
					imagePanelGraphic.updateOrSetOverlayObjects(item.getOverlay());
					
					done=true;
				}
				if(!done&&panels.size()>0) {
					ShowMessage.showOptionalMessage("It seems that the selected panels already share an overlay");
				}
				
				if(done)switchToEditMode(item);
					return undo;
				}	
			
		}.createJMenuItem("Share Overlay With Selected Sister Panels"));
		
		/**Adds an option to make the overlay for this panel unique.*/
		share.add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {
				return showRecropWithThisOverlay(item);
				
			
			}	
			
		}.createJMenuItem("Show Re-Crop dialog with this overlay"));
		
		
		
		
		/**Adds an option to make the overlay for this panel unique.*/
		if(c.getOverlay().manualEditsMade()) add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {
				return clearcustom(item);
			}

			/**
			 * @return
			 */
		}.createJMenuItem("Clear custom overlay"));
		
		
		/**Adds an option to make the overlay for this panel unique.*/
		if(c.getOverlay().manualEditsMade()) share.add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {
				AbstractUndoableEdit2 clearcustom = clearcustom(item);
				MultichannelDisplayLayer original = MultichannelDisplayLayer.findMultiChannelForGraphic(item.getParentLayer(), item);
				original.getSlot().redoCropAndScale();
				return clearcustom;
			}

			/**
			 * @return
			 */
		}.createJMenuItem("Replace custom overlay"));
		
	}
	public AbstractUndoableEdit2 clearcustom(ImagePanelGraphic item) {
		CombinedEdit undo=new CombinedEdit(item.provideUndoForDialog(), item.getOverlay().getUndoForEditWindow(item));
		boolean proceed = ShowMessage.showOptionalMessage("You sure?", true, "Are you sure you want to clear the overlay");
		
		if(proceed)item.setOverlayObjects(null);
		item.updateDisplay();
		return undo;
	}	
	
	
	/**Adds a menu option to edit the overlay objects in a separate window
	 * @param editMenu 
	 * @param c
	 * @param expert
	 */
	public void addOverlayEditor(SmartJMenu editMenu ) {
		editMenu.add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {
				AbstractUndoableEdit2 undo1 = switchToEditMode(item);
				return new CombinedEdit(undo1,showEditWindowForOverlay(item, null));
				
				}

			
			
		}.createJMenuItem("Edit objects in isolated Window"));
	}
	
	/**
	 * @param c 
	 * @return 
	 * 
	 */
	public AbstractUndoableEdit2 switchToEditMode(ImagePanelGraphic c) {
		AbstractUndoableEdit2 undo = c.provideUndoForDialog();
		int size = c.getOverlay().getAllGraphics().size();
		if(!c.isShowOverlay()&&size>0) {
			ShowMessage.showOptionalMessage("Overlay now visible",true, "Your overlay was hidden", "There were already "+size+" objects inside", "Those and any newly added objects will now be visible", "You can hide the overlay at any time");
		}
		c.setShowOverlay(true);
		c.getOverlay().setEdited(true);
		return undo;
	}

	/**
	 * @return
	 */
	public AbstractUndoableEdit2 showRecropWithThisOverlay(ImagePanelGraphic item) {
		//ShowMessage.showOptionalMessage("Will use a copy of this overlay to update panel overlays", true, "Updated overlay for source image", "You will see ", "There is no undo");
		
		OverlayObjectList o = item.getOverlay();
		PreProcessInformation lastProcess = o.getLastProcess();
		
		OverlayObjectList reversed = OverlayObjectList.cropOverlayAtAngle(o, lastProcess, true);
		MultichannelDisplayLayer original = MultichannelDisplayLayer.findMultiChannelForGraphic(item.getParentLayer(), item);
		AbstractUndoableEdit partialundo = original.getSlot().setOriginalOverlay(reversed);

		CombinedEdit performButtonAction = new ImagePropertiesButton(item, ImagePropertiesButton.CROP_IMAGE).performButtonAction();
		performButtonAction.addEditToList(partialundo);
		original.getSlot().redoCropAndScale();
		
		return performButtonAction;
	}

	/**returns a separate window
	 * @param background2
	 * @param extractOverlay
	 * @return 
	 */
	private static ImageWindowAndDisplaySet createOverlayEditorWindow(ImagePanelGraphic item, GraphicLayerPane extractOverlay) {
		ImagePanelGraphic background2 = item.copy();
		background2.setRelativeScale(1);
		background2.setLocationUpperLeft(0, 0);
		
		
		StandardWorksheet gs = new StandardWorksheet( extractOverlay);
		gs.setTitle(item.getName());
		gs.getBasics().setWidth( item.getUnderlyingImageWidth());
		gs.getBasics().setHeight(item.getUnderlyingImageHeight());
		ImageWindowAndDisplaySet newwindow = ImageWindowAndDisplaySet.show(gs);
		
		
		
		newwindow.getTheCanvas().setBackgroundImage(background2);
		newwindow.getWindow().setVisible(true);
		item.setOverlayEditingWindow(newwindow.getWindow());
		ShowMessage.showOptionalMessage("You may now edit overlay objects", true, "A window to edit overlay objects has appeared", "Note: if you recrop the parent image, this overlay may be replaced");
		newwindow.getWindow().addWindowListener(new WindowListener() {

			
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
				 ArraySorter.deselectItems(item.getOverlay().getAllGraphics());
				
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
				// TODO Auto-generated method stub
				
			}}
		);
		return newwindow;
	}

	/**displays a window to edit the overlay
	 * @return
	 */
	public static AbstractUndoableEdit2 showEditWindowForOverlay(ImagePanelGraphic item, WindowListener w) {
		AbstractUndoableEdit2 undo = item.provideUndoForDialog();
		item.setShowOverlay(true);
		
		OverlayObjectList extractOverlay = item.getOverlay();
		if(extractOverlay==null){
			extractOverlay =new OverlayObjectList();
			item.updateOrSetOverlayObjects(extractOverlay);
			}
		
		ImageWindowAndDisplaySet window = createOverlayEditorWindow(item, extractOverlay);
		
		if(w!=null)window.getWindow().addWindowListener(w);
		
		return new CombinedEdit(undo, extractOverlay.getUndoForEditWindow(item));
	}	
}
