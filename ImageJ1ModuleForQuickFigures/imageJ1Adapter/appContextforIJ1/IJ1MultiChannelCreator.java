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
 * Date Modified: Feb 18, 2023
 * Version: 2023.2
 */
package appContextforIJ1;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.io.Opener;
import ij.plugin.FolderOpener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import infoStorage.BasicMetaDataHandler;
import infoStorage.MetaInfoWrapper;
import logging.IssueLog;
import messages.ShowMessage;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import ultilInputOutput.FileChoiceUtil;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.MultiChannelImage;
import figureEditDialogs.SelectImageDialog;
import figureOrganizer.MultichannelDisplayLayer;

/**implementation of the MultiChannelDisplayCreator interface for ImageJ*/
public class IJ1MultiChannelCreator implements MultiChannelDisplayCreator {
	
	/**
	 * 
	 */
	
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
		//openFile=true;//temporarily removed this variable for testing purposes
		progress("opeining "+path);
		ImagePlus imp=null;
		
		/**If "'use active image, is the path"*/
		if (useActiveImage(path)) {
			progress("Mandaroy use active image");
			imp=WindowManager.getCurrentImage();
			if(imp==null) {
				path=null;//one string is not a valid path but may be passed as the path
				boolean userSelection = ShowMessage.showOptionalMessage("No image open", true,  "No active image", "please select an image file to continue");
				if(!userSelection) 
					return null;
			} else return imp;
		}
		
		/**if there is still a failure to return an image and the instructions suggest not to open a new file*/
		if (imp==null&&!openFile)
			{
				imp=showImagePlusChoice() ;
				if(imp!=null)
					return imp;
			}
		
		//next method attempt to open a file. Or if there is no active image, opens one
		if (true/**(openFile)|| (imp==null&&useActiveImage(path))*/) {
			progress("Image J will attempt to open "+path);
			//if (MultiChannelDisplayCreator.useActiveImage.equals(path)) {
				
			
			//}
			if (path==null)
						try {
							//imp=IJ.openImage();
							path=FileChoiceUtil.getOpenFile().getAbsolutePath();
							} catch (Exception e) {}
			
			if (path!=null) {
				try {
					progress("Image J will open "+path);
					imp=IJ.openImage(path);
				
					if (imp!=null) return imp;
				} catch (Exception e) {}
			}
			
			if (imp==null&&path!=null)
				{progress("ImageJ failed to return an image after opening  the image "+path+'\n');
				ImagePlus shownImage = null;
				try {
					
					 shownImage =WindowManager.getCurrentImage();//if bioformats import occurs then the new image will be the active one
					 if (shownImage==null||!new ImagePlusWrapper(shownImage).getPath().equals(path))
					 		{
						 progress("ImageJ failed to show the image using bioformats so will try again "+path+'\n');
							 
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

	/**Assuming each file represents a different channel, this will open them as a multichannel
	 * If none of the given files are single channel greyscales, this will return null
	 * */
	@Override
	public String createMultichannelFromImageSequence(Iterable<File> input, int[] dims, String path, boolean show) {
		if(input==null)
			input= FileChoiceUtil.showMultipleFileDialog();
		ArrayList<File> list=new ArrayList<File> ();
		for(File l:input) {list.add(l);}
		ImagePlus createImageFromSeriesOfFiles = createImageFromSeriesOfFiles(list);
		if(createImageFromSeriesOfFiles==null)
			return null;
		if(path==null){
				path=list.get(0).getAbsolutePath().replace(list.get(0).getName(), createImageFromSeriesOfFiles.getTitle());

				if(path.endsWith(".png") ) {
					path =path.replace(".png", ".tif");
					}
				if(path.endsWith(".PNG") ) {
					path =path.replace(".PNG", ".tif");
					}
				if(!path.toLowerCase().endsWith(".tif") &&!path.toLowerCase().endsWith(".tiff")) {
					path =path+".tif";
					}
			}
		IJ.saveAsTiff(createImageFromSeriesOfFiles, path);
		if(show)
			createImageFromSeriesOfFiles.show();
		return path;
	}

	
	public static void main(String[] args) {
		ImageDisplayTester.main(null);
		ArrayList<File> dd = FileChoiceUtil.showMultipleFileDialog();
		String image =new IJ1MultiChannelCreator().createMultichannelFromImageSequence(dd, null, null, true);
		
	}

	/**creats a multichannel image from a list of single channel greyscale files.
	 * May return null if the files given consists of multichannel image file
	 * @param dd
	 * @return
	 */
	public static ImagePlus createImageFromSeriesOfFiles(ArrayList<File> dd) {
		String start = getStartOfName(dd,  true);
		String end   = getStartOfName(dd, false);
		HashMap<String, ImageProcessor> map=new HashMap<String, ImageProcessor> ();
		
		ImageProcessor ip;
		ImagePlus image = new ImagePlus();
		ImageStack stack = null;
		ImagePlus openImage = null;
		
		for(File f: dd) {
			String n = f.getName();
			String n2=n;
			n2=n.substring(start.length(), n.length()-end.length());
			openImage = IJ.openImage(f.getAbsolutePath());
			if(openImage.getStackSize()>1) {
				progress("this option only accepts single channel greyscales ");
				progress("will skip "+n);
				continue;
			}
			ip = openImage.getStack().getProcessor(1);
			
			map.put(n2, ip);
			if(stack==null) {
				stack=new ImageStack(ip.getWidth(), ip.getHeight());
			}
			stack.addSlice(n2, ip);
		}
		if(stack==null)
			return null;
		image.setStack(stack);
		image=new CompositeImage(image);
		
		image.setDimensions(stack.getSize(), 1, 1);
		image.setTitle(start+"combined"+end);
		ImagePlusWrapper imagePlusWrapper = new ImagePlusWrapper(image);
		
		MetaInfoWrapper data = imagePlusWrapper.getMetadataWrapper();
		for(int i=0; i<stack.getSize();i++) {
			BasicMetaDataHandler.setCustomChannelColor(data, i, stack.getSliceLabel(i+1));
		}
		data.setEntry(MADE_BY_STITCHING, "T");
		imagePlusWrapper = new ImagePlusWrapper(image);
		imagePlusWrapper.colorBasedOnRealChannelName();
		image.setCalibration(openImage.getCalibration());
		
		return image;
	}

	/**progress reports for the process ofopening images
	 * @param string
	 */
	private static void progress(String... string) {
		//IssueLog.log(string);
		
	}

	/**
	 * @param dd
	 * @return
	 */
	private static String getStartOfName(ArrayList<File> dd, boolean start) {
		int l = 1;
		String name = dd.get(0).getName();
		String s;
		if(start)
			s = name.substring(0, l);
		else 
			s = name.substring(name.length()-l, name.length());
		
		boolean mustcontinue=allNamesStartOrEndWith(dd, s, start);
		
		if(!mustcontinue)
			return "";
		String olds=s;
		while (l<name.length() && mustcontinue) {
			 olds=s;
			l++;
			if(start)
				s = name.substring(0, l);
			else 
				s = name.substring(name.length()-l, name.length());
			mustcontinue=allNamesStartOrEndWith(dd,s, start);
			
		}
		return olds;
	}

	/**returns true if all file anmes tart of 
	 * @param dd
	 * @param s
	 * @param startWith
	 * @return
	 */
	private static boolean allNamesStartOrEndWith(ArrayList<File> dd, String s, boolean startWith) {
		for(File d: dd) {
			
			if(!d.getName().startsWith(s)&startWith)
				return false;
			
			if(!d.getName().endsWith(s)&!startWith)
				return false;
		}
		return true;
	}

	
	





	
}
