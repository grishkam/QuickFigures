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
package graphicalObjectHandles;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ImagePanelGraphic;
import journalCriteria.PPIOption;
import multiChannelFigureUI.ImagePropertiesButton;
import multiChannelFigureUI.WindowLevelDialog;
import objectDialogs.DialogIcon;
import selectedItemMenus.ImageGraphicOptionsSyncer;
import selectedItemMenus.SelectAllButton;
import selectedItemMenus.SnappingSyncer;

/**this handle list contains a set of handles that act as a minitoolbar for an image panel*/
public class ImagePanelActionHandleList extends ActionButtonHandleList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanelGraphic theImage;
	
	public ImagePanelActionHandleList(ImagePanelGraphic t) {
	
		this.theImage=t;
		
			createMultiChannelSourceImageOptions(t);
		
		add(new ImageSyncHandle(1100));
		createGeneralButton(new SelectAllButton(t));
		if(t.getAttachmentPosition()!=null) {
			add(new GeneralActionHandle(new SnappingSyncer(true,t), 741905));
		}
	}

	protected void createMultiChannelSourceImageOptions(ImagePanelGraphic t) {
		ImagePropertiesButton winlevelButton = new  ImagePropertiesButton(t, WindowLevelDialog.ALL);
		this.add(new GeneralActionHandle(winlevelButton, 550));
	
			 winlevelButton = new  ImagePropertiesButton(t, ImagePropertiesButton.COLOR_MODE);
			this.add(new GeneralActionHandle(winlevelButton, 289));
		
		 winlevelButton = new  ImagePropertiesButton(t, ImagePropertiesButton.CROP_IMAGE);
		this.add(new GeneralActionHandle(winlevelButton, 584));
	
		
		 PPIOption ppiO = new  PPIOption();
			this.add(new GeneralActionHandle(ppiO, 8325));
	}
	
	public void updateLocation() {
		
		Rectangle bounds = theImage.getOutline().getBounds();
		super.setLocation(new Point2D.Double(bounds.getX()+5, bounds.getMaxY()+15));
	
	}
	public void updateHandleLocations(double magnify) {
		 
		super.updateHandleLocations(magnify);
	}

	public void draw(Graphics2D g, CordinateConverter<?> cords) {
		
		
		super.draw(g, cords);
	}
	
	
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
	
	
}
