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
		if(t.getSnapPosition()!=null) {
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
