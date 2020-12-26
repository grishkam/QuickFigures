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
package appContextforIJ1;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.Opener;
import ij.plugin.FolderOpener;
import ij.process.ColorProcessor;
import logging.IssueLog;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import standardDialog.SelectImageDialog;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.MultiChannelImage;
import figureOrganizer.MultichannelDisplayLayer;

public class IJ1MultiChannelCreator implements MultiChannelDisplayCreator {
	
	/**Creates a multiChannel Display for the user selected open image or file.
	  if the path is null, this allows the user to select an image*/
	public MultichannelDisplayLayer creatMultiChannelDisplayFromUserSelectedImage(boolean openFile, String path) {
		ImagePlus imp=selectImagePlus( openFile,  path);
		if(imp==null) return null;
		return createDisplayFromImagePlus(imp);
	}

	protected MultichannelDisplayLayer createDisplayFromImagePlus(ImagePlus imp) {
		ImagePlusMultiChannelSlot slot = new ImagePlusMultiChannelSlot();
		MultichannelDisplayLayer display = new MultichannelDisplayLayer(slot);
		
		display.setLaygeneratedPanelsOnGrid(true);
		//slot = new ImagePlusMultiChannelSlot(display);
		//display.setSlot(slot);
		slot.setAndInnitializeImagePlus(imp);
		return display;
	}
	
	/**the current image*/
	@Override
	public MultichannelDisplayLayer creatMultiChannelDisplayFromOpenImage() {
		return createDisplayFromImagePlus(WindowManager.getCurrentImage());
	}
	
	private ImagePlus selectImagePlus(boolean openFile, String path) {
		ImagePlus imp=null;
		if ((path!=null&& path.equals(MultiChannelDisplayCreator.useActiveImage))) {
			
			imp=WindowManager.getCurrentImage();
		}
		else
		if (openFile) {
			if (path==null)
				{
				try {imp=IJ.openImage();} catch (Exception e) {}
				}
			else {
				try {
					imp=IJ.openImage(path);
					if (imp!=null) return imp;
				} catch (Exception e) {}
			}
			
			if (imp==null&&path!=null)
				{IssueLog.log("ImageJ failed to return an image after opening  the image "+path+'\n');
				ImagePlus shownImage = null;
				try {
					
					 shownImage =WindowManager.getCurrentImage();//if bioformats import occurs then the new image will be the imported on
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

	@Override
	public MultiChannelImage creatMultiChannelFromImage(Image img) {

		
		ColorProcessor cp = new ColorProcessor(img.getWidth(null), img.getHeight(null));
		
		cp.insert(new ColorProcessor(img), 0, 0);
		return new ImagePlusWrapper(new ImagePlus("no title", new ColorProcessor(img)));
	}
	
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


	@Override
	public MultiChannelImage createFromImageSequence(String path, int[] dims) {
		ImagePlus open = FolderOpener.open(path);
		open.show();
		return new ImagePlusWrapper( open);
	}

	







	
}
