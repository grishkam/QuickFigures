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
 * Version: 2022.2
 */
package graphicalObjects_SpecialObjects;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;

import fLexibleUIKit.MenuItemMethod;
import illustratorScripts.IllustratorObjectConvertable;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.ScalededItem;
import logging.IssueLog;
import objectDialogs.CroppingDialog;
import objectDialogs.ImageGraphicOptionsDialog;

/**A special case of image panel. used for buffered images of specific types
 * Includes multiple options related to the color model
 * 
 * */
public class BufferedImageGraphic extends ImagePanelGraphic implements IllustratorObjectConvertable, ScalededItem {




	transient int[] lasizeused;
	
	/**how to manipulate the channels of an RGB before display*/
	boolean[] takeout=new boolean[] {false,false, false, false};
	private int forceGrayChannel=0;

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public ImagePanelGraphic copy() {
		return duplicate();
	}
	
	protected BufferedImageGraphic() {
			setImage(null);
		}
	
	 public BufferedImageGraphic(BufferedImage bi) {
			setImage(bi);
		}
	 
	 public BufferedImageGraphic(BufferedImage bi, double x, double y) {
			setImage(bi);
			setLocation(x, y);
		}
	 
	 public BufferedImageGraphic(File f) {
			super(f);
		}
	
	private BufferedImageGraphic duplicate() {
		BufferedImageGraphic copy = new BufferedImageGraphic(getBufferedImage());
		copy.copyAttributesFrom(this);
		//copy.setRectangle(getBounds());
		copy.setForceGrayChannel(getForceGrayChannel());
		return copy;
	}



	
	/**returns true if the entire array of boolean is all false*/
	boolean areAllFalse(boolean[] colors) {
		for(boolean b: colors) {
			if (b) return false;
		}
		return true;
	}
	
	@Override
	public void showOptionsDialog() {
		ImageGraphicOptionsDialog ig = new ImageGraphicOptionsDialog(this);
		ig.showDialog();
		//new BufferedImageGraphicDialog(this);
	}
	
	/**Given a buffered image and color model, generates another buffered image with the same raster data but
	 * with the given color model*/
	private  BufferedImage createImageWithColorModel(BufferedImage image, DirectColorModel ARGB_COLOR_MODEL) {
		SampleModel sampleModel = ARGB_COLOR_MODEL.createCompatibleWritableRaster( 1, 1 ).getSampleModel() .createCompatibleSampleModel(image.getWidth(),image.getHeight() );
        
		/**gets teh data buffer of the underling buffered image*/
        DataBuffer dataBuffer =  image.getRaster().getDataBuffer();
        
        /***/
        WritableRaster rgbRaster = Raster.createWritableRaster( sampleModel, dataBuffer, null );
       
        BufferedImage output = new BufferedImage( ARGB_COLOR_MODEL, rgbRaster, false, null );
        return output;
	}
	
	
	
	/**Removed unneeded channels and crops image*/
	public BufferedImage filterCrop(BufferedImage image) {
		boolean[] colors=this.getRemovedChannels();
		int fg=this.getForceGrayChannel();
		Rectangle rect = this.getCroppingRect();
		if(colors.length<4&&fg<1&&!isCroppintRectValid()) return image;
		//if(areAllFalse(colors)&&fg<1) return image;
		BufferedImage output;//=new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		try {
			DirectColorModel ARGB_COLOR_MODEL = generateDirectColorModel();
	    
	        output=createImageWithColorModel(image, ARGB_COLOR_MODEL);
	        
	        
	        if (this.isCroppintRectValid()) return output.getSubimage((int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
	       
	        else return output;
	        
			} catch (Throwable r) {
				IssueLog.logT(r);
				return null;
			}
		/**
		if (this.isCroppintRectValid()) {
			
			output=new BufferedImage(rect.width, rect.height, image.getType());//output must be a new buffered image. for some reason subimage returns the same object
			image=image.getSubimage(rect.x, rect.y, rect.width, rect.height);
		//IssueLog.log("cropping done ", rect.toString());
		}
		else {
		output=new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		image=image.getSubimage(0, 0, image.getWidth(), image.getHeight());//for some reason the code changes the underlying source image unless i have this. it should not do so at all
		
		}
		
		
	
		
		int red=0;
		int green=0;
		int blue=0;
		int alpha=0;
		
		
		for (int y = 0; y < image.getHeight(); ++y) {
		    for (int x = 0; x < image.getWidth(); ++x) {
		        
		        
		         int rgb = image.getRGB(x,y);
		         alpha = rgb>>24 & 0xff;
		         red = rgb >>16 & 0xff;
		         green = rgb>>8 & 0xff;
		         blue = rgb & 0xff;

		        if(fg==1) {green=red;blue=red;}
		        if(fg==2) {red=green;blue=green;}
		        if(fg==3) {red=blue;green=blue;}

		         
		         if (colors[0]) red=0;
		         if (colors[1]) green=0;
		         if (colors[2]) blue=0;
		         if (colors[3]) alpha=0;
		         
		       
		         
		         int color = alpha<<24 | red<<16 | green<<8 | blue;
		        output.setRGB(x, y, color);
		    }
		}
	//	IssueLog.log("output filtered image code is "+img.toString());
		return output;*/
	}
	
	
	/**Returns and argb image*/
	private DirectColorModel getDefaultArgbColorModel() {
		return  new DirectColorModel(32, 0xff0000, 0xff00, 0xff, 0xff000000);
	}
	
	/**Returns true if no colors need to be taken from the RGB color model*/
	private boolean noExcludedColors() {
		return this.areAllFalse(getRemovedChannels());
	}
	
	/**generates the color model that will be used to display the colors of the image
	  depending on the options used, this will return a different color model*/
	public DirectColorModel generateDirectColorModel() {
		boolean[] colors=this.getRemovedChannels();
		int fg=this.getForceGrayChannel();
		
		DirectColorModel ARGB_COLOR_MODEL = getDefaultArgbColorModel();
		if (fg==0&&noExcludedColors()) return ARGB_COLOR_MODEL ;

		int r = 0xff0000;
		int g = 0xff00;
		int b = 0xff;
		int a = 0xff000000;
		
		/**if one of the channels will simply be displayed in gray, this sets the other model components accordingly*/
		  if(fg==1) {g=r; b=r;}
		  if(fg==2) {r=g; b=g;}
		  if(fg==3) {r=b; g=b;}
		  
		  /**this alters the color model to exclude a given channel*/
		  if (colors.length>0&&colors[0]) r=0;
		  if (colors.length>1&&colors[1]) g=0;
		  if (colors.length>2&&colors[2]) b=0;
		  
		  ARGB_COLOR_MODEL = new DirectColorModel(32,r, g,  b, a);
      
        return (DirectColorModel) ARGB_COLOR_MODEL;
	}
	
	

	public void setRemovedChannels(boolean[] array) {
		if (array.equals(takeout)) return;
		for (int i=0; i<takeout.length&&i<array.length;i++) {
			takeout[i]=array[i];
		}
		ensureDisplayedImage();
	}
	public boolean[] getRemovedChannels() {
		return takeout;
	}
	
	/**returns the image after any and all filter and cropping operation*/
	public BufferedImage getProcessedImageForDisplay() {
		try {BufferedImage output = filterCrop(this.getBufferedImage());
		
		return output;}
		catch (Throwable t) {
			IssueLog.logT(t);
		//	return super.getProcessedImageForDisplay();
			return this.getBufferedImage();
		}
	}
	
	
	/**returns a version that can be saved as a PNG and opened*/
	public BufferedImage getPNGExportImage() {
		if (noExcludedColors() )
		return getProcessedImageForDisplay();
		else {
			IssueLog.log("may have some difficulty making channels look identical between original and export see getPNGExportImage() method call");
			
			BufferedImage pid = getProcessedImageForDisplay();
			DirectColorModel cm = this.getDefaultArgbColorModel();
			int width=pid.getWidth();
			int height=pid.getHeight();
			
			BufferedImage bi = new BufferedImage(cm, cm.createCompatibleWritableRaster(pid.getWidth(), pid.getHeight()), false, null);
			bi.getGraphics().drawImage(pid,0, 0, width, height, 0, 0, width, height, null);
			return bi;
		}
	}
	


	public int getForceGrayChannel() {
		return forceGrayChannel;
	}

	public void setForceGrayChannel(int forceGrayChannel) {
		if (this.forceGrayChannel==forceGrayChannel) return;
		this.forceGrayChannel = forceGrayChannel;
		ensureDisplayedImage();
	}
	

	


	@MenuItemMethod(menuActionCommand = "snapLock", menuText = "Snap Locked Items")
	public void snapLockedItems() {
		for(LocatedObject2D o: getLockedItems()) {
			if(o==null) continue;
			AttachmentPosition sb = o.getAttachmentPosition();
			if (sb==null) {
				o.setAttachmentPosition(AttachmentPosition.defaultInternal());
				sb=o.getAttachmentPosition();
				}
			
			sb.snapLocatedObjects(o, this);
		}
	}

	@MenuItemMethod(menuActionCommand = "emptyLock", menuText = "Release Locked Items")
	public void detachLockedItems() {
		this.getLockedItems().clear();
	}
	
	@MenuItemMethod(menuActionCommand = "cropping", menuText = "Set Crop")
	public void showCroppingDialog() {
		CroppingDialog cd = new CroppingDialog();
		try{cd.showDialog(this);} catch (Throwable t) {
			IssueLog.logT(t);
		}
		//IssueLog.log("The cropping rect is valid? "+this.isCroppintRectValid(), this.getCroppingrect().toString());
	}
	


	
}
