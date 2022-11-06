/**
 * Author: Greg Mazo
 * Date Modified: Nov 6, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package popupMenusForComplexObjects;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JMenuItem;

import fLexibleUIKit.ObjectAction;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.OverlayObjectList;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageDisplayApp.StandardWorksheet;
import menuUtil.SmartJMenu;
import messages.ShowMessage;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.Edit;

/**
 
 * 
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

		add(new ObjectAction<ImagePanelGraphic>(c) {
		@Override
		public AbstractUndoableEdit2 performAction() {
			AbstractUndoableEdit2 undo = item.provideUndoForDialog();
			
			GraphicLayerPane extractOverlay = item.extractOverlay();
			if(extractOverlay.getItemArray().size()==0) {
				ShowMessage.showOptionalMessage("No overlay objects were extracted");
				return null;
			}
				
			AbstractUndoableEdit2 addItem = Edit.addItem(item.getParentLayer(), extractOverlay);
			item.setShowOverlay(false);
			return new CombinedEdit( undo,addItem);
			
			}	
		
	}.createJMenuItem("Extract Overlay"));
		
		addOverlayEditor( );
	}
	
	/**Adds a menu option to edit the overlay objects in a separate window
	 * @param c
	 * @param expert
	 */
	public void addOverlayEditor( ) {
		 add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public AbstractUndoableEdit2 performAction() {
				AbstractUndoableEdit2 undo = item.provideUndoForDialog();
				item.setShowOverlay(true);
				
				OverlayObjectList extractOverlay = item.getOverlay();
				
				createOverlayEditorWindow(item, extractOverlay);
				return new CombinedEdit(undo, extractOverlay.getUndoForEditWindow());
				
				}

			
			
		}.createJMenuItem("Edit Overlay Objects"));
	}
	
	/**returns a separate window
	 * @param background2
	 * @param extractOverlay
	 * @return 
	 */
	private ImageWindowAndDisplaySet createOverlayEditorWindow(ImagePanelGraphic item, GraphicLayerPane extractOverlay) {
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
}
