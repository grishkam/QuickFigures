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

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.CSFLocation;
import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.MultiChannelSlot;
import channelMerging.MultiChannelUpdateListener;
import channelMerging.MultiChannelImage;
import channelMerging.ImageDisplayLayer;
import channelMerging.PreProcessInformation;
import fLexibleUIKit.MenuItemMethod;
import ultilInputOutput.FileChoiceUtil;
import ultilInputOutput.FileFinder;
import utilityClassesForObjects.ScaleInfo;
import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import logging.IssueLog;
import standardDialog.SelectImageDialog;


/**All the what is needed to keep track of a multichannel source stack, save and retrieve it.
  Before this object is serialized, it saves its images as serialized arrays.
  */
	public class ImagePlusMultiChannelSlot implements MultiChannelSlot {

		
		/**An object that keeps track of the original image*/
		private SubSlot original=new SubSlot();
		
		
		/**The path where the working image is stored*/
		protected String path;
		/**The image that is displayed. Compared to the original, this version may be cropped or scaled*/
		private transient ImagePlus sourceImagePlus;//the imageplus
		protected byte[] serializedIM;//the serialized version of the image 
		
		
	
		
		transient displayUpdater di;
		transient ImagePlusWrapper multiChannelWrapper;
		
		
		private static final long serialVersionUID = 1L;
		ArrayList<MultiChannelUpdateListener> listens=new ArrayList<MultiChannelUpdateListener>();
		private int retreival=LOAD_EMBEDDED_IMAGE;

		private PreProcessInformation preprocessRecord;

		private ImageDisplayLayer display;


		private CSFLocation displayLocation;

		public ImagePlusMultiChannelSlot() {
			
		}

		public ImagePlusMultiChannelSlot(ImageDisplayLayer imagePlusDisplayPanels) {
			display= imagePlusDisplayPanels;
		}

		@Override
		public void addMultichannelUpdateListener(MultiChannelUpdateListener lis) {
			listens.add(lis);
			
		}

		@Override
		public ImagePlusWrapper getMultichannelImage() {
			if (getImagePlus()==null) {
				IssueLog.log("Warning. No image in wrapper");
				return null;
			}
			if (multiChannelWrapper!=null && this.multiChannelWrapper.getImagePlus()==this.getImagePlus()) return multiChannelWrapper;
			multiChannelWrapper =new ImagePlusWrapper(getImagePlus());
			return multiChannelWrapper;
		}
		
		/**stores the working image, */
		void storeImage() {
				this.saveImageEmbed();
			if (this.getRetrieval()==lOAD_FROM_SAVED_FILE&&getImagePlus()!=null) {
	
				String savePath = getSavePath();
				if(savePath!=null) {//saves the image file if it is used
					IJ.save(this.getImagePlus(), savePath);
					
					if (this.getImagePlus()==original.backupUncroppedImagePlus) {
						original.setSavePath(savePath);
					}
					
					this.setPathStringToImagePlus();
				
				}			
				}
		}
		
		
		
		

		/**gets the image. if needed, opens the image file. If
		  the image file is not found it reconstitutes
		  the image from a byte array inside this object. 
		 */
		public ImagePlus getImagePlus() {
			
			if (sourceImagePlus!=null){
				return sourceImagePlus;
			}
			try{
			
			if (loadFromFile()) 
					this.setToPath(path);
				
			
			if (sourceImagePlus==null) {
				setToByteArray();
			}
			
			if (sourceImagePlus==null) {
				setToPath(path);
			}
			
			}
			
			catch (Throwable t) {
				t.printStackTrace();
			}
			return sourceImagePlus;
			
		}
		
		public void setToByteArray() {
			if (serializedIM==null) return;
			sourceImagePlus=	new ij.io.Opener().deserialize(serializedIM);
			ImagePlus.addImageListener( getDisplayUpdater());
		}
		
		/**Sets the image to the given path string.
		 If it cant find the file in the full path, it search
		 for it elsewhere .
		 */
		private void setToPath(String path) {
			
			
			if (!imageFileFound() ) return;
			
			sourceImagePlus = getImageInPath(path);
			if (sourceImagePlus!=null) {
				this.setPathStringToImagePlus();
			}
			ImagePlus.addImageListener(getDisplayUpdater());
		
		}

		/**
		opens an image
		 */
		protected static ImagePlus getImageInPath(String path) {
			return IJ.openImage(getFileFinder().findExistingFilePath(path));
		}

		private boolean loadFromFile() {
			if (this.getRetrieval()==lOAD_FROM_SAVED_FILE) return true;
			return false;
		}

		

		/**sets the image and saves it to the byte array. may be set to a null value but do not reccomend*/
		private void setImagePlus(ImagePlus imagePlus) {
			ImagePlusWrapper wrap = new ImagePlusWrapper(imagePlus);
			this.sourceImagePlus = imagePlus;
			if (wrap.containsImage())
			{
			
				path=wrap.getPath();
			
				
			//saveImageEmbed();//commented out for testing
			}
		}
		
		public static FileFinder getFileFinder() {
			FileFinder finder = new FileFinder();
			return finder;
		}
		
		
		
		protected boolean imageFileFound() {
			if (path==null){
				IssueLog.log("Has looked for image with null path");
				IssueLog.log("This normally means the multichannel image was either not saved in a file or empty");
				; 
				return false;
			
			}
			File file=new File(this.path);
			if (file.exists()) return true;
			
			
			file=getFileFinder().findFile(file);
			if (file.exists()) return true;
			
			return false;
		}
		
		/**Sets the internal String path to its current multichannel wrapper*/
		public void setPathStringToImagePlus() {
			if (this.getMultichannelImage().containsImage())
			path=this.getMultichannelImage().getPath();
		}
		

		/**Turns the image data into a byte array*/
		@MenuItemMethod(menuActionCommand = "saveEm", menuText = "Save Embedded", subMenuName="Image")
		public void saveImageEmbed() {
			if (getImagePlus()==null)return;
			
			serializedIM=new ij.io.FileSaver(getImagePlus()).serialize();
			
			/**needed to keep the original around in case imageJ disposes of it*/
			original.saveImage();
			
		}
		

		

		@MenuItemMethod(menuActionCommand = "setImage", menuText = "Set Image", subMenuName="Image")
		public void setImageDialog() {
			this.setImagePlus(showImagePlusChoice());
			this.setAndInnitializeImagePlus(getImagePlus());
		}


		
		
		
		@MenuItemMethod(menuActionCommand = "showim", menuText = "Show Image", subMenuName="Image")
		public void showImage() {
			ImagePlus imp = this.getImagePlus();
			if (imp==null) {
				IssueLog.log("Image is null");
				return;
			}
			
			
			ImagePlus backup = this.getBackup(true);
			if (backup==null)
			imp.duplicate().show();
			else backup.duplicate().show();
		}
		
		
		
		public displayUpdater getDisplayUpdater() {
			if (di==null)di=new displayUpdater(this);
			return di;
		}
		
		
		public class displayUpdater implements ImageListener {
			private ImagePlusMultiChannelSlot dis;

			public displayUpdater(ImagePlusMultiChannelSlot imagePlusMultiChannelSlot) {
				this.dis=imagePlusMultiChannelSlot;
			}
			
			@Override
			public void imageUpdated(ImagePlus arg0) {
				turnOff();
				if (arg0==dis.sourceImagePlus)onImageUpdated();
				
				/**this next part would not be needed anymore as the user is no longer given the option */
				//if (arg0==dis.original.backupUncroppedImagePlus) {
					/**commented out this to see if it would fix a bug that cased infinite loop for small images*/
					/**redoCropandScale(preprocessRecord);//this part might create an infinite loop
					*/
					//onImageUpdated();}
				
				turnOn();
			}
			
			public synchronized void onImageUpdated() {
				
				//IssueLog.log("will update image");
				setPathStringToImagePlus() ;
				turnOff();
				for(MultiChannelUpdateListener lis: listens) {
					lis.onImageUpdated();
					
				}
			
				turnOn();
			}
			
			@Override
			public void imageClosed(ImagePlus arg0) {
				/**if (arg0==dis.backupUncroppedImagePlus) {
						storeImage();
						dis.backupUncroppedImagePlus=null;
						ImagePlus.removeImageListener(this);
				}*/
				
				if (arg0==dis.sourceImagePlus) 
					{
					
					saveImageEmbed();
					boolean save=FileChoiceUtil.yesOrNo("The image is being used in a figure  "+'\n'+"Do you want to save it?");
					if (save) storeImage();
					
					
					dis.setImagePlus(null);
				ImagePlus.removeImageListener(this);
				}
			}

			@Override
			public void imageOpened(ImagePlus arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void turnOff() {
				ImagePlus.removeImageListener(this);
			}
			public void turnOn() {
			ImagePlus.addImageListener(this);
			}
			
			
		}
		
		
		/**Sets the imagePlus to imp. does nothing if imp is null*/
		public void setAndInnitializeImagePlus(ImagePlus imp) {
			if (imp==null){
				IssueLog.log("no image plus was given");
				
				return;};
				
				
			if (imp==this.sourceImagePlus) return;
				
			this.setImagePlus(imp);
			
			
			ImagePlus.addImageListener(getDisplayUpdater());
			
			for(MultiChannelUpdateListener lis: listens) {
				lis.onImageInitiated();
			}
		
		}

		@Override
		public void removeMultichannelUpdateListener(
				MultiChannelUpdateListener lis) {
			listens.remove(lis);
			
		}

		@Override
		public void setRetrival(int i) {
			this.retreival=i;
			
		}
		
		public int getRetrieval() {
			// TODO Auto-generated method stub
			return retreival;
		}
		
		private String getSavePath() {
			// TODO Auto-generated method stub
			 path=this.getMultichannelImage().getPath();
			 
			if (path!=null&&!path.toLowerCase().endsWith(".tif")) {
				path+=".tif";
			}
			return path;
		}

		@Override
		public void kill() {
			/**called so this object does not needlessly ask the user if he wants to save the image
			  before closing it*/
			getDisplayUpdater().turnOff();
			
			
			
		}

		@Override
		public void hideImage() {
			
			if (sourceImagePlus!=null) sourceImagePlus.hide();
			if (getUncroppedOriginal()!=null ) getUncroppedOriginal().hide();
		}
		
		@Override
		public void hideImageWihtoutMessage() {
			ImagePlus.removeImageListener(getDisplayUpdater());
			this.hideImage();
			
			ImagePlus.addImageListener(getDisplayUpdater());
		}
		
		
		private static ImagePlus showImagePlusChoice() {
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
		public PreProcessInformation getModifications() {
			return preprocessRecord;
		}
		
	
		/**if the argument if different from the previous processing information,
		 * then creates a new working image with the new crop and scale
		  */
		public void applyCropAndScale(PreProcessInformation process) {
			ImagePlus.removeImageListener(this.getDisplayUpdater());//avoid exceptions during the next thread
			
			/**does not need to do anything if they are already the same*/
			if(preprocessRecord!=null) {
				if(areTheySame(process) )
					return;
			} 
			
			 redoCropandScale(process);
			 
			 ImagePlus.addImageListener(getDisplayUpdater());
		}
		
		
		public void redoCropAndScale() {
			ImagePlus.removeImageListener(this.getDisplayUpdater());//avoid exceptions during the next thread
			
			 redoCropandScale(this.getModifications());
			 
			 ImagePlus.addImageListener(getDisplayUpdater());
		}

	/**takes the original image (full size, unscaled) and uses it to create a new
	  working image that has been cropped and scaled with the same object 
	  (@see PreProcessInformation) 
	  */
	private void redoCropandScale(PreProcessInformation process) {
			ImagePlusWrapper unprocessedVersion = getUnprocessedVersion(true);
			 if (unprocessedVersion==null||!unprocessedVersion.containsImage()) {
				 IssueLog.log("cannot scale empty image");
				 //preprocessRecord=process;
				 return;
			 }
			 if(process==null) {
				 IssueLog.log("tried to apply a null process");;
				 process=new PreProcessInformation(null, 0, 1);
			 }
			ImagePlusWrapper m =unprocessedVersion.cropAtAngle(process);
			 preprocessRecord=process;
			 
			 setImagePlus(m.getImagePlus());
			
			getDisplayUpdater().imageUpdated(getImagePlus());
		}

	/**returns true if the two modifications are the same*/
		private boolean areTheySame(PreProcessInformation process) {
			if(process==null&&preprocessRecord!=null) return false;
			if(process!=null&&preprocessRecord==null) return false;
			
			if (preprocessRecord.getAngle()!=process.getAngle()) return false;
			if (preprocessRecord.getScale()!=process.getScale()) return false;
			Rectangle r1 = preprocessRecord.getRectangle();
			Rectangle r2 =process.getRectangle();
			if(r1==null && r2!=null) return false;
			if(r2==null && r1!=null) return false;
			if(r1==r2) return true;
			return r1.equals(r2);
		}

		/**returns the original image*/
		public ImagePlusWrapper getUnprocessedVersion(boolean b) {
			ImagePlus backup = getBackup(b);
			ImagePlusWrapper bwrap=null;
			if (backup!=null)bwrap=new ImagePlusWrapper(backup);
			if(backup==null) {
				/**If the backup once existed will ask user to reopen*/
				if (original.innitialized) 
					{
					boolean reopen = FileChoiceUtil.yesOrNo("It appears the the original image was closed or disposed of. Do you want to open it from a saved tiff ?");
					if (reopen) {
						original.setStoredImage(IJ.openImage());
						if (original.getStoredImage()!=null) 
							return new ImagePlusWrapper(original.getStoredImage());
					}
					return null;
					}
					else  {
						/**if the original image has never been innitialized*/
						original.setStoredImage(this.sourceImagePlus);
						bwrap=this.getMultichannelImage();
				}
			}
			return bwrap;
		}
		
		/**Returns the uncropped backup. If the parameter is set to true, it will be returned with any changes to channel order
		 * display range and colors set to the current ones */
		private ImagePlus getBackup(boolean matchColors) {
			
			ImagePlus backup=original.getOrCreateImagePlus();
			
			if(backup!=null) {
				/**Tries to match the channel order and luts. this part is prone to errors so it is in a try catch*/
				try {
				if (matchColors)matchOrderAndLuts(new ImagePlusWrapper(getUncroppedOriginal()));
				} catch (Throwable t) {IssueLog.logT(t);}
			}
			return backup;
		}

		/**changes the channel order, Display range and luts of a to match the main source stack*/
		public void matchOrderAndLuts(MultiChannelImage a) {
			ChannelOrderAndLutMatching m = new ChannelOrderAndLutMatching();
			m. matchDisplayRangeLUTandOrder(getMultichannelImage(),a);
		}

		@Override
		public ScaleInfo getScaleInfo() {
			return getUnprocessedVersion(false).getScaleInfo();
		}

		@Override
		public void setScaleInfo(ScaleInfo scaleInfo) {
			getUnprocessedVersion(false).setScaleInfo(scaleInfo);
			if (preprocessRecord!=null) this.applyCropAndScale(preprocessRecord);
			this.getDisplayUpdater().imageUpdated(this.getImagePlus());
		}

		@Override
		public void setStackDisplayLayer(ImageDisplayLayer multichannelDisplayLayer) {
			display=multichannelDisplayLayer;
		}
		
		public ImageDisplayLayer getDisplayLayer() {
			return display;
		}

		@Override
		public ImagePlusMultiChannelSlot copy() {
			 ImagePlusMultiChannelSlot output = new ImagePlusMultiChannelSlot();
			 output.path=path;
			 output.setAndInnitializeImagePlus(getUncroppedOriginal());
			if(getModifications()!=null)
			output.applyCropAndScale(getModifications());
			return output;
		}

		public ImagePlus getUncroppedOriginal() {
			return original.getStoredImage();
		}


		@Override
		public void setDisplaySlice(CSFLocation display) {
			this.displayLocation=display;
			
		}
		
		@Override
		public CSFLocation getDisplaySlice() {
			return this.displayLocation;
			
		}

		/**a subcompartment for storing the original version of the image*/
		static class SubSlot implements Serializable {
			
			static final int STORE_IN_ARRAY_ALWAYS=0, STORE_IN_ARRAY_ONLY_WHEN_SAVING=1, STORE_IN_EXTERNAL_FILE=2;
			int storage=STORE_IN_ARRAY_ALWAYS;
			
			/**An uncropped backup version used in case user wants to change scale or cropping later
			  Current working on a way for multiple sets of panels to share this one*/
			private transient ImagePlus backupUncroppedImagePlus;//the imageplus
			protected byte[] serializedBackup;
			boolean innitialized=false;
			private String originalSavePath;
			private String lastSavePath=null;
			private int estimatedFileSize;
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**Before serialization of the object, this will serialize the ImagePlus into its Byte[]*/
			private void writeObject(java.io.ObjectOutputStream out)
				     throws IOException {
			
				
				saveImage();
				out.defaultWriteObject();
			}

			/**
			sets the save path
			 */
			public void setSavePath(String savePath) {
				this.lastSavePath=savePath;
				IssueLog.log("Save path for original image was stored", savePath, " may need to load from this path if missing");
			}

			/**
			 returns the stored image. if there is not image plus object, attempts to re-create it
			 */
			public ImagePlus getOrCreateImagePlus() {
				if(getStoredImage()==null &&serializedBackup!=null) {
					setStoredImage(new ij.io.Opener().deserialize(serializedBackup));
					if (storage!=STORE_IN_ARRAY_ALWAYS) serializedBackup=null;
				}
				String p = lastSavePath;
				if (getStoredImage()==null && p!=null) {
					IssueLog.log("Will re-open original image at path ... ", p);
					backupUncroppedImagePlus = getImageInPath(p);
				}
				return getStoredImage();
			}

			/**
			saves the image inside of this object into an array that can be serialized
			 */
			public void saveImage() {
				if(getStoredImage()!=null&& storage!=STORE_IN_EXTERNAL_FILE)
					this.serializedBackup=new ij.io.FileSaver(getStoredImage()).serialize();
				else {
					askUserToSave();
				}
			}

			/**
			 Not yet implemented
			 */
			private void askUserToSave() {
			}

			public ImagePlus getStoredImage() {
				return backupUncroppedImagePlus;
			}

			public void setStoredImage(ImagePlus backupUncroppedImagePlus) {
				this.backupUncroppedImagePlus = backupUncroppedImagePlus;
				if (backupUncroppedImagePlus!=null) {
					this.innitialized=true;
					this.originalSavePath=new ImagePlusWrapper(backupUncroppedImagePlus).getPath();
					this.lastSavePath=originalSavePath;
					this.estimatedFileSize=backupUncroppedImagePlus.getBitDepth()*backupUncroppedImagePlus.getWidth()*backupUncroppedImagePlus.getHeight()*backupUncroppedImagePlus.getStackSize();
				}
			}

			public String getOriginalSavePath() {
				return originalSavePath;
			}

			public int getEstimatedFileSize() {
				return estimatedFileSize;
			}
		}


		/**returns a slot that uses the same source image as this one*/
		@Override
		public MultiChannelSlot createPartner() {
			ImagePlusMultiChannelSlot c = this.copy();
			c.original=original;
			return c;
		}

		@Override
		public int getEstimatedSizeOriginal() {
			return original.getEstimatedFileSize();
		}
		
		
	}

