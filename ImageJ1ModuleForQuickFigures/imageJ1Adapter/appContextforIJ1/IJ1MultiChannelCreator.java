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
package appContextforIJ1;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.Opener;
import ij.plugin.FolderOpener;
import ij.process.ColorProcessor;
import logging.IssueLog;
import messages.ShowMessage;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import ultilInputOutput.FileChoiceUtil;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.MultiChannelImage;
import figureEditDialogs.SelectImageDialog;
import figureOrganizer.MultichannelDisplayLayer;

/**implementation of the MultiChannelDisplayCreator interface for ImageJ*/
public class IJ1MultiChannelCreator implements MultiChannelDisplayCreator {
	
	/**Creates a multiChannel Display for the user selected open image or file.
	  if the path is null, this allows the user to select an image*/
	public MultichannelDisplayLayer creatMultiChannelDisplayFromUserSelectedImage(boolean openFile, String path) {
		ImagePlus imp=selectImagePlus( openFile,  path);
		if(imp==null) return null;
		return createDisplayFromImagePlus(imp);
	}

	/**returns an image display layer to display the slices, frames and channels of the
	 * given imageJ image
	 * @see ImagePlus
	 * @see MultichannelDisplayLayer*/
	protected MultichannelDisplayLayer createDisplayFromImagePlus(ImagePlus imp) {
		ImagePlusMultiChannelSlot slot = new ImagePlusMultiChannelSlot();
		MultichannelDisplayLayer display = new MultichannelDisplayLayer(slot);
		
		display.setLaygeneratedPanelsOnGrid(true);
		slot.setAndInnitializeImagePlus(imp);
		return display;
	}
	
	/**Uses the current image to create a display*/
	@Override
	public MultichannelDisplayLayer creatMultiChannelDisplayFromOpenImage() {
		return createDisplayFromImagePlus(WindowManager.getCurrentImage());
	}
	

	/**opens the image at the given path 
	 * or allows the user to select an image
	 * or uses the active image
	 * or if one method fails to return an image, tries the others*/
	private ImagePlus selectImagePlus(boolean openFile, String path) {
		ImagePlus imp=null;
		if (useActiveImage(path)) {
			
			imp=WindowManager.getCurrentImage();
		}
		
		//next method attempt to open a file. Or if there is no active image, opens one
		if ((openFile)|| (imp==null&&useActiveImage(path))) {
			if (MultiChannelDisplayCreator.useActiveImage.equals(path)) {
				path=null;//one string is not a valid path but may be passed as the path
				boolean userSelection = ShowMessage.showOptionalMessage("No image open", true,  "No active image", "please select an image file to continue");
				if(!userSelection) 
					return null;
			
			}
			if (path==null)
						try {
							//imp=IJ.openImage();
							path=FileChoiceUtil.getOpenFile().getAbsolutePath();
							} catch (Exception e) {}
			
			if (path!=null) {
				try {
					imp=IJ.openImage(path);
				
					if (imp!=null) return imp;
				} catch (Exception e) {}
			}
			
			if (imp==null&&path!=null)
				{IssueLog.log("ImageJ failed to return an image after opening  the image "+path+'\n');
				ImagePlus shownImage = null;
				try {
					
					 shownImage =WindowManager.getCurrentImage();//if bioformats import occurs then the new image will be the active one
					 if (shownImage==null||!new ImagePlusWrapper(shownImage).getPath().equals(path))
					 		{
						 IssueLog.log("ImageJ failed to show the image using bioformats so will try again "+path+'\n');
							 
						 shownImage= Opener.openUsingBioFormats(path);
						// this.openBioformats();
					 		}
					 
						if (shownImage!=null&&new ImagePlusWrapper(shownImage).getPath().equals(path))
						{
							shownImage.hide();//this method is not supposed to show the images
								return shownImage;
						}
						
				} catch (Exception e) {
					
				}
			
			
				}
				}
		
		
		if (imp==null&&!openFile)
			imp=showImagePlusChoice() ;
			
		if (imp==null) return null;
		return imp;
	}

	/**
	 * returns true if the path consists of instructions to use the currently active image
	   rather than open the  path
	 */
	boolean useActiveImage(String path) {
		return path!=null&& path.equals(MultiChannelDisplayCreator.useActiveImage);
	}
	
	/**if open multichannel images are present, displays a dialog to choose them*/
	public static ImagePlus showImagePlusChoice() {
		ImagePlus imp=null;
		ArrayList<MultiChannelImage> list=null;
		if (imp==null)
			list=SelectImageDialog.getSelectedMultis(false,1).getList();
			if (list!=null&&list.size()>0) {
				ImagePlusWrapper p=(ImagePlusWrapper) list.get(0);
				imp=p.getImagePlus();
			}
			return imp;
	}
	

	@Override
	public String imageTypeName() {
		return "ImageJ Image";
	}

	/**Creates an image using an AWT image*/
	@Override
	public MultiChannelImage creatMultiChannelFromImage(Image img) {
		ColorProcessor cp = new ColorProcessor(img.getWidth(null), img.getHeight(null));
		
		cp.insert(new ColorProcessor(img), 0, 0);
		return new ImagePlusWrapper(new ImagePlus("no title", new ColorProcessor(img)));
	}
	
	/**creates a multichannel display from an awt image and saves it
	 * @param img the image
	 * @param savePath where to save*/
	@Override
	public MultiChannelImage creatRGBFromImage(Image img, String savePath) {
		File f=new File(savePath);
		ColorProcessor cp = new ColorProcessor(img.getWidth(null), img.getHeight(null));
		
		cp.insert(new ColorProcessor(img), 0, 0);
		ImagePlus image = new ImagePlus(f.getName(), new ColorProcessor(img));
		IJ.saveAsTiff(image, savePath);
		image.show();
		return new ImagePlusWrapper(image);
	}


	/**using a sequence of images, generates an image*/
	@Override
	public MultiChannelImage createFromImageSequence(String path, int[] dims) {
		ImagePlus open = FolderOpener.open(path);
		open.show();
		return new ImagePlusWrapper( open);
	}

	
	
	





	
}
