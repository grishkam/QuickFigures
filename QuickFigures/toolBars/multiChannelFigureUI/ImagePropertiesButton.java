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
 * Version: 2022.0
 */

package multiChannelFigureUI;

import java.util.ArrayList;

import javax.swing.Icon;

import channelMerging.MultiChannelImage;
import figureEditDialogs.WindowLevelDialog;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import iconGraphicalObjects.ColorModeIcon;
import iconGraphicalObjects.CropIconGraphic;
import iconGraphicalObjects.IconUtil;
import locatedObject.LocatedObject2D;
import popupMenusForComplexObjects.FigureOrganizingSuplierForPopup;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelectionSystem;
import undo.CombinedEdit;

/**A multi-selection operation that performs one among a few operations 
  that depend on the setting. each options requires that the user have an image panel selected*/
public class ImagePropertiesButton extends BasicMultiSelectionOperator {

	

	public static final int COLOR_MODE = 9, CROP_IMAGE = 3, PIXEL_DENSITY=8;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanelGraphic firstImage;
	int actionType=WindowLevelDialog.MIN_MAX;
	private ChannelPanelEditingMenu context;
	
	
	public ImagePropertiesButton(ImagePanelGraphic i, LayerSelectionSystem l) {
		super.setSelector(l);
		
		firstImage=i;
		context= new ChannelPanelEditingMenu(i);
	}
	
	public ImagePropertiesButton(int action) {
		actionType=action;
	}
	public ImagePropertiesButton(ImagePanelGraphic i, int bc) {
		this(bc);
		actionType=bc;
		firstImage=i;
		context= new ChannelPanelEditingMenu(i);
	}

	@Override
	public String getMenuCommand() {
		if(actionType==WindowLevelDialog.ALL) return "Set Display Range";
		if(actionType==WindowLevelDialog.WINDOW_LEVEL) return "Set Window/Level";
		if(actionType==WindowLevelDialog.MIN_MAX) return "Set Brightness/Contrast";
		if(actionType==CROP_IMAGE) return "Recrop Image";
		if(actionType==PIXEL_DENSITY) return "Pixel Density";
		if(actionType==COLOR_MODE) return "Change Color Modes";
		
		return "Set Window/Level";
	}

	@Override
	public void run() {
		
		CombinedEdit undo=null;
		
		
		//firstImage=null;
		prepareContext();
		
		if (doesShowDisplayRange())
			context.showDisplayRangeDialog(actionType);
	
			
		if(this.actionType==CROP_IMAGE) {
			if(context.getExtraDisplays().size()<1) return;
			context.getExtraDisplays().remove(context.getPrincipalDisplay());
		
			undo = FigureOrganizingSuplierForPopup.recropManyImages(context.getPrincipalDisplay(),context.getExtraDisplays());;
		}
		
		if(this.actionType==COLOR_MODE ) {
			context.setScope(ChannelPanelEditingMenu.ALL_IMAGES_IN_CLICKED_FIGURE);
			undo=context.changeColorModes();
		}
		
		if(undo!=null&&this.getUndoManager()!=null) this.getUndoManager().addEdit(undo);
	}
	/**
	 * @return 
	 * 
	 */
	public ChannelPanelEditingMenu prepareContext() {
		ArrayList<LocatedObject2D> items = super.getAllObjects();
		ArrayList<MultichannelDisplayLayer> foundDisplays=new ArrayList<MultichannelDisplayLayer>();
		ArrayList<MultiChannelImage> foundImages=new ArrayList<MultiChannelImage>();
		
		  
		for(LocatedObject2D i: items) {
			if(i instanceof ImagePanelGraphic)  {
				
				if(firstImage==null)firstImage=(ImagePanelGraphic) i;
				MultichannelDisplayLayer nextone = new ChannelPanelEditingMenu((ImagePanelGraphic) i).getPrincipalDisplay();
				
				if (nextone!=null&&!foundDisplays.contains(nextone))
					{
					foundDisplays.add(nextone);
					foundImages.add(nextone.getMultiChannelImage());
					}
			}
				
		}
		
		context = new ChannelPanelEditingMenu(firstImage);
		context.setScope(ChannelPanelEditingMenu.CLICKED_IMAGES_ONLY);
		context.setExtraWrappers(foundImages);
		context.setExtraDisplays(foundDisplays);
		return context;
	}
	/**
	 * @return
	 */
	protected boolean doesShowDisplayRange() {
		return this.actionType<=WindowLevelDialog.ALL;
	}
	
	public Icon getIcon() {
		if(actionType==CROP_IMAGE) return CropIconGraphic.createsCropIcon();
		if(actionType==COLOR_MODE) return new ColorModeIcon(firstImage);
		
		return IconUtil.createBrightnessIcon();
	}
	
	public BasicMultiSelectionOperator[] createMergeContentItems() {
		 BasicMultiSelectionOperator[] output=new  BasicMultiSelectionOperator[context.getPrincipalMultiChannel().nChannels()];;
		for(int i=0; i<output.length; i++) {
			output[i] =new ChannelInclusionMenuItem(i+1);
		}
		 
		 return output;
	}
	
	class ChannelInclusionMenuItem extends BasicMultiSelectionOperator {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int channel=1;
		/**
		 * @param i
		 */
		public ChannelInclusionMenuItem(int c) {
			channel=c;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getMenuCommand() {
			return context.getPrincipalDisplay().getMultiChannelImage().getGenericChannelName(channel);
			
		}}
	
	

}
