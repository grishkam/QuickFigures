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
 * Date Modified: Jan 5, 2021
 * Version: 2021.2
 */
package handles.miniToolbars;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.Icon;

import applicationAdapters.CanvasMouseEvent;
import channelMerging.ChannelEntry;
import figureEditDialogs.WindowLevelDialog;
import figureOrganizer.PanelListElement;
import graphicalObjects.CordinateConverter;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import handles.IconHandle;
import iconGraphicalObjects.ChannelUseIcon;
import iconGraphicalObjects.DialogIcon;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import multiChannelFigureUI.ChannelPanelEditingMenu.ChannelMergeMenuItem;
import multiChannelFigureUI.ImagePropertiesButton;
import selectedItemMenus.FrameColorButton;
import selectedItemMenus.ImageGraphicOptionsSyncer;
import selectedItemMenus.MultiSelectionOperator;
import selectedItemMenus.SelectAllButton;
import selectedItemMenus.AttachmentPositionAdjuster;

/**this handle list contains a set of handles that act as a minitoolbar for an image panel*/
public class ImagePanelActionHandleList extends ActionButtonHandleList {

	



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanelGraphic theImage;
	private ChannelPanelEditingMenu context;//the menu context for this list
	
	public ImagePanelActionHandleList(ImagePanelGraphic t) {
	
		this.theImage=t;
		
			createMultiChannelSourceImageOptions(t);
		
		add(new ImageSyncHandle(1100));
		createGeneralButton(new SelectAllButton(t));
		
		if(t.getAttachmentPosition()!=null) {
			add(new GeneralActionHandle(new AttachmentPositionAdjuster(true,t), 741905));
		}
		
		addFrameColorButton(t);
	}

	/**
	 * creates the toolbar item for the frame color
	 * @param t
	 */
	void addFrameColorButton(ImagePanelGraphic t) {
		FrameColorButton itemForIcon = new FrameColorButton(t);
		GeneralActionListHandle hf = new FrameListHandle(itemForIcon, 438254, new FrameColorButton[] {});
		hf.setAlternativePopup(new ColoringButton(itemForIcon, 452345324));
		add(hf);
	}

	/**creates a series of handles for image panels that are part of an image display layer*/
	protected void createMultiChannelSourceImageOptions(ImagePanelGraphic t) {
		try {
			context = new ChannelPanelEditingMenu(t);
			ImagePropertiesButton winlevelButton = new ImagePropertiesButton(t, WindowLevelDialog.ALL);
			this.add(new GeneralActionHandle(winlevelButton, 550));
			winlevelButton = new ImagePropertiesButton(t, ImagePropertiesButton.COLOR_MODE);
			this.add(new GeneralActionHandle(winlevelButton, 289));
			this.add(new ChannelsIconHandle());
			winlevelButton = new ImagePropertiesButton(t, ImagePropertiesButton.CROP_IMAGE);
			this.add(new GeneralActionHandle(winlevelButton, 584));
		} catch (Exception e) {
			IssueLog.logT(e);
		}

	}
	
	public void updateLocation() {
		
		Rectangle bounds = theImage.getExtendedBounds().getBounds();
		super.setLocation(new Point2D.Double(bounds.getX()+5, bounds.getMaxY()+15));
	
	}
	public void updateHandleLocations(double magnify) {
		 
		super.updateHandleLocations(magnify);
	}

	public void draw(Graphics2D g, CordinateConverter cords) {
		
		
		super.draw(g, cords);
	}
	
	public ChannelPanelEditingMenu getMenuContext() {
		if(context==null)
			context= new ChannelPanelEditingMenu(theImage);
		return context;
	}

	/**a handle that generates a multi image dialog */
	public class ImageSyncHandle extends GeneralActionHandle {

		public  ImageSyncHandle( int num) {
			super(new ImageGraphicOptionsSyncer(), num);
			super.setIcon(DialogIcon.getIcon());
		}
		
		public void updateIcon() {
			super.setIcon(DialogIcon.getIcon());
		}
		
		@Override
		public boolean isHidden() {
			
			return false;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}
	
	
	/**
	 A handle that allows to select which channels are to be included in the merged image via a popup
	 */
public class ChannelsIconHandle extends IconHandle {

		/**
		 */
		public ChannelsIconHandle() {
			super(new ChannelIcon2(null), new Point(0,0));
			this.setHandleNumber(236651);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) { 
			SmartPopupJMenu p = getThePopup( canvasMouseEventWrapper);
			 p.showForMouseEvent(canvasMouseEventWrapper);
			canvasMouseEventWrapper.getAsDisplay().updateDisplay();
		}
		
		/**Generates a popup menu for the mouse event*/
		public SmartPopupJMenu getThePopup(CanvasMouseEvent canvasMouseEventWrapper) {
			SmartPopupJMenu o = new SmartPopupJMenu();
			ImagePropertiesButton ss = new ImagePropertiesButton(theImage, canvasMouseEventWrapper.getSelectionSystem());
			ss.setSelection(canvasMouseEventWrapper.getSelectionSystem().getSelecteditems());
			ChannelPanelEditingMenu local = ss.prepareContext();
			local.setScope(ChannelPanelEditingMenu.ALL_IMAGES_IN_CLICKED_FIGURE);
			
			ArrayList<ChannelMergeMenuItem> items =new ArrayList<ChannelMergeMenuItem>();
			
			if(this.isMergePanel())
				 items.addAll( local.createChannelMergeMenuItems(ChannelPanelEditingMenu.NO_MERGE_CHANNEL_MENU));
			else {
				o.add(local.createChannelMergeMenu(ChannelPanelEditingMenu.MERGE_WITH_EACH_MENU));
				o.add(local.createChannelMergeMenu(ChannelPanelEditingMenu.EXCLUDED_CHANNEL_MENU));
				
			}
			for(ChannelMergeMenuItem i:items) {o.add(i);}
			return o;
		}
		
		/**
		 * @return
		 */
		private boolean isMergePanel() {
			PanelListElement source = theImage.getSourcePanel();
			if (source!=null && !source.isTheMerge()) return false;
			return true;
		}

		/**if the image panel is not a merged image or if there is only one channel available
		  then this handle is hidden. When the panels are in advanced channel use mode,
		  there is not need for this item*/
	public boolean isHidden() {
		if (getMenuContext()!=null &&getMenuContext().getPressedMultichannel()!=null&&getMenuContext().getPressedMultichannel().nChannels()==1) return true;
		if (getMenuContext()!=null &&getMenuContext().getPrincipalDisplay()!=null&&getMenuContext().getPrincipalDisplay().getPanelManager().isAdvancedChannelUse()) return true;
		return false;
	}

}
	
/**A channel use icon for the image panel includes colors that depend on the source */
	public class ChannelIcon2 extends ChannelUseIcon  implements Icon {

		/**
		 * @param c
		 */
		public ChannelIcon2(ArrayList<ChannelEntry> c) {
			super(c);
		}

		@Override
		public ArrayList<ChannelEntry> getAllColors() {
			if (getMenuContext()==null||getMenuContext().getPrincipalDisplay()==null||getMenuContext().getPrincipalDisplay().getMultiChannelImage()==null) return super.getAllColors();
			return getMenuContext().getPrincipalDisplay().getMultiChannelImage().getChannelEntriesInOrder();
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		

	}
	
	
	/**
	A frame color handle that is hidden if the frame width is 0
	 */
public class FrameListHandle extends GeneralActionListHandle {

	/**
	 * @param i
	 * @param num
	 * @param items
	 */
	public FrameListHandle(MultiSelectionOperator i, int num, MultiSelectionOperator[] items) {
		super(i, num, items);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean isHidden() {if (theImage.getFrameWidthH()==0)  return true; return false;}

}
}
