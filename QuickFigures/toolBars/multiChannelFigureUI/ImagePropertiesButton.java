package multiChannelFigureUI;

import java.util.ArrayList;

import javax.swing.Icon;

import channelMerging.MultiChannelImage;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import iconGraphicalObjects.ColorModeIcon;
import iconGraphicalObjects.CropIconGraphic;
import iconGraphicalObjects.IconUtil;
import popupMenusForComplexObjects.FigureOrganizingSuplierForPopup;
import selectedItemMenus.BasicMultiSelectionOperator;
import undo.CombinedEdit;
import utilityClassesForObjects.LocatedObject2D;

/**A multi-selection operation that performs one among a few operations 
  that depend on the setting. each options requires that the user have an image panel selected*/
public class ImagePropertiesButton extends BasicMultiSelectionOperator {

	

	public static final int COLOR_MODE = 9, CROP_IMAGE = 3, PIXEL_DENSITY=8;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanelGraphic firstImage;
	int brightcontrast=WindowLevelDialog.MIN_MAX;
	
	public ImagePropertiesButton() {}
	public ImagePropertiesButton(ImagePanelGraphic i, int bc) {
		firstImage=i;
		brightcontrast=bc;
	}

	@Override
	public String getMenuCommand() {
		if(brightcontrast==WindowLevelDialog.ALL) return "Set Display Range";
		if(brightcontrast==WindowLevelDialog.WINDOW_LEVEL) return "Set Window/Level";
		if(brightcontrast==WindowLevelDialog.MIN_MAX) return "Set Brightness/Contrast";
		if(brightcontrast==CROP_IMAGE) return "Recrop Image";
		if(brightcontrast==PIXEL_DENSITY) return "Pixel Density";
		if(brightcontrast==COLOR_MODE) return "Change Color Modes";
		
		return "Set Window/Level";
	}

	@Override
	public void run() {
		ArrayList<LocatedObject2D> items = super.getAllObjects();
		CombinedEdit undo=null;
		
		
		firstImage=null;
		
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
		
		ChannelPanelEditingMenu context = new ChannelPanelEditingMenu(firstImage);
		context.workOn=0;
		context.extraWrappers= foundImages;
		context.extraDisplays=foundDisplays;
		//if (this.brightcontrast<2) undo = ChannelDisplayUndo.createMany(foundImages, context);//
	
		if (doesShowDisplayRange())
			context.showDisplayRangeDialog(brightcontrast);
	
			
		if(this.brightcontrast==CROP_IMAGE) {
			if(foundDisplays.size()<1) return;
			foundDisplays.remove(context.getPrincipalDisplay());
		
			undo = FigureOrganizingSuplierForPopup.recropManyImages(context.getPrincipalDisplay(),foundDisplays);;
		}
		
		if(this.brightcontrast==COLOR_MODE) {
			context.workOn=1;
			undo=context.changeColorModes();
		}
		
		if(undo!=null&&this.getUndoManager()!=null) this.getUndoManager().addEdit(undo);
	}
	/**
	 * @return
	 */
	protected boolean doesShowDisplayRange() {
		return this.brightcontrast<=WindowLevelDialog.ALL;
	}
	
	public Icon getIcon() {
		if(brightcontrast==CROP_IMAGE) return CropIconGraphic.createsCropIcon();
		if(brightcontrast==COLOR_MODE) return new ColorModeIcon(firstImage);
		return IconUtil.createBrightnessIcon(0);
	}

}
