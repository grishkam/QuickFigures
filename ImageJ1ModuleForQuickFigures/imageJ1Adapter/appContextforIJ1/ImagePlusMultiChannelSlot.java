package appContextforIJ1;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.MultiChannelSlot;
import channelMerging.MultiChannelUpdateListener;
import channelMerging.MultiChannelWrapper;
import channelMerging.PanelStackDisplay;
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

		
		protected String path;
		
		private transient ImagePlus sourceImagePlus;//the imageplus
		protected byte[] serializedIM;//the serialized version of the image 
		
		
		/**An uncropped backup version used in case user wants to change cropping later*/
		private transient ImagePlus backupUncroppedImagePlus;//the imageplus
		protected byte[] serializedBackup;
		
		transient displayUpdater di;
		transient ImagePlusWrapper multiChannelWrapper;
		
		
		private static final long serialVersionUID = 1L;
		ArrayList<MultiChannelUpdateListener> listens=new ArrayList<MultiChannelUpdateListener>();
		private int retreival=loadEmbeded;

		private PreProcessInformation preprocessRecord;

		private PanelStackDisplay display;

		public ImagePlusMultiChannelSlot() {
			
		}

		public ImagePlusMultiChannelSlot(PanelStackDisplay imagePlusDisplayPanels) {
			display= imagePlusDisplayPanels;
		}

		@Override
		public void addMultichannelUpdateListener(MultiChannelUpdateListener lis) {
			listens.add(lis);
			
		}

		@Override
		public ImagePlusWrapper getMultichanalWrapper() {
			if (getImagePlus()==null) {
				IssueLog.log("Warning. No image in wrapper");
				return null;
			}
			if (multiChannelWrapper!=null && this.multiChannelWrapper.getImagePlus()==this.getImagePlus()) return multiChannelWrapper;
			multiChannelWrapper =new ImagePlusWrapper(getImagePlus());
			return multiChannelWrapper;
		}
		
		void storeImage() {
				this.saveImageEmbed();
			if (this.getRetrieval()==loadFromFile&&getImagePlus()!=null) {
	
				String savePath = getSavePath();
				if(savePath!=null) {//saves the image file if it is used
					IJ.save(this.getImagePlus(), savePath);
					
					this.setPathStringToImagePlus();
				
				}			
				}
		}
		
		/**Before serialization of the object, this will serialize the ImagePlus into its Byte[]*/
		private void writeObject(java.io.ObjectOutputStream out)
			     throws IOException {
		
			storeImage();
			out.defaultWriteObject();
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
		public void setToPath(String path) {
			
			
			if (!imageFileFound() ) return;
			
			sourceImagePlus = IJ.openImage(this.getFileFinder().findExistingFilePath(path));
			if (sourceImagePlus!=null) {
				this.setPathStringToImagePlus();
			}
			ImagePlus.addImageListener(getDisplayUpdater());
		
		}

		private boolean loadFromFile() {
			// TODO Auto-generated method stub
			if (this.getRetrieval()==loadFromFile) return true;
			return false;
		}

		

		/**sets the image and saves it to the byte array. may be set to a null value but do not reccomend*/
		private void setImagePlus(ImagePlus imagePlus) {
			ImagePlusWrapper wrap = new ImagePlusWrapper(imagePlus);
			this.sourceImagePlus = imagePlus;
			if (wrap.containsImage())
			{
			
				path=wrap.getPath();
			
			saveImageEmbed();
			}
		}
		
		public FileFinder getFileFinder() {
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
			if (this.getMultichanalWrapper().containsImage())
			path=this.getMultichanalWrapper().getPath();
		}
		

		/**Turns the image data into a byte array*/
		@MenuItemMethod(menuActionCommand = "saveEm", menuText = "Save Embedded", subMenuName="Image")
		public void saveImageEmbed() {
			if (getImagePlus()==null)return;
			serializedIM=new ij.io.FileSaver(getImagePlus()).serialize();
			if(this.backupUncroppedImagePlus!=null)
				this.serializedBackup=new ij.io.FileSaver(backupUncroppedImagePlus).serialize();
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
			
			/**showing the actual image causes java heap space issues when using the contrast adjuster
			  */
			//this.getUnprocessedVersion().show();
			/**ImagePlus.removeImageListener(this.getDisplayUpdater());
			ImagePlus backup = this.getBackup();
			if (backup!=imp &&backup!=null) {
				backup.show();
				backup.getWindow().setVisible(true);
			}
			else 
			{
				imp.show();
				imp.getWindow().setVisible(true);
			}
			ImagePlus.addImageListener(this.getDisplayUpdater());
			*/
			ImagePlus backup = this.getBackup();
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
				if (arg0==dis.backupUncroppedImagePlus) {
					redoCropandScale(preprocessRecord);
					onImageUpdated();
				}
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
				if (arg0==dis.backupUncroppedImagePlus) {
						storeImage();
						dis.backupUncroppedImagePlus=null;
						ImagePlus.removeImageListener(this);
				}
				
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
			 path=this.getMultichanalWrapper().getPath();
			 
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
			if (backupUncroppedImagePlus!=null ) backupUncroppedImagePlus.hide();
		}
		
		@Override
		public void hideImageWihtoutMessage() {
			ImagePlus.removeImageListener(getDisplayUpdater());
			this.hideImage();
			
			ImagePlus.addImageListener(getDisplayUpdater());
		}
		
		
		private static ImagePlus showImagePlusChoice() {
			ImagePlus imp=null;
			ArrayList<MultiChannelWrapper> list=null;
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
		
	
		
		public void applyCropAndScale(PreProcessInformation process) {
			ImagePlus.removeImageListener(this.getDisplayUpdater());//avoid exceptions during the next thread
			
			if(preprocessRecord!=null) {
				if(areTheySame(process) )
						
				{
					return;
				}
			} 
			
			 redoCropandScale(process);
			 
			 ImagePlus.addImageListener(getDisplayUpdater());
		}
		
		
		public void redoCropAndScale() {
			ImagePlus.removeImageListener(this.getDisplayUpdater());//avoid exceptions during the next thread
			
			 redoCropandScale(this.getModifications());
			 
			 ImagePlus.addImageListener(getDisplayUpdater());
		}

	private void redoCropandScale(PreProcessInformation process) {
			ImagePlusWrapper unprocessedVersion = getUnprocessedVersion();
			 if (unprocessedVersion==null) {
				 IssueLog.log("cannot scale empty image");
				 preprocessRecord=process;
				 return;
			 }
			 if(process==null) {
				 //IssueLog.log("tried to apply a null process");;
				 process=new PreProcessInformation(null, 0, 1);
			 }
			ImagePlusWrapper m =unprocessedVersion.cropAtAngle(process);
			 preprocessRecord=process;
			 this.setImagePlus(m.getImagePlus());
			 getDisplayUpdater().imageUpdated(getImagePlus());
		}

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

		public ImagePlusWrapper getUnprocessedVersion() {
			ImagePlus backup = getBackup();
			ImagePlusWrapper bwrap=null;
			if (backup!=null)bwrap=new ImagePlusWrapper(backup);
			if(backup==null) {
				backupUncroppedImagePlus=this.sourceImagePlus;
				bwrap=this.getMultichanalWrapper();
			}
			return bwrap;
		}
		
		/**Returns the uncropped backup, however it will be returned with any changes to channel order
		 * display range and colors set to the current ones and not the original*/
		ImagePlus getBackup() {
			if(backupUncroppedImagePlus==null &&this.serializedBackup!=null) {
				backupUncroppedImagePlus=	new ij.io.Opener().deserialize(serializedBackup);
			}
			if(backupUncroppedImagePlus!=null) {
				/**Tries to match the channel order and luts. this part is prone to errors so it is in a try catch*/
				try {
				matchOrderAndLuts(new ImagePlusWrapper(backupUncroppedImagePlus));
				} catch (Throwable t) {IssueLog.log(t);}
			}
			return backupUncroppedImagePlus;
		}

		/**changes the channel order, Display range and luts of a to match the main source stack*/
		public void matchOrderAndLuts(MultiChannelWrapper a) {
			ChannelOrderAndLutMatching m = new ChannelOrderAndLutMatching();
			m. matchDisplayRangeLUTandOrder(getMultichanalWrapper(),a);
		}

		@Override
		public ScaleInfo getScaleInfo() {
			return getUnprocessedVersion().getScaleInfo();
		}

		@Override
		public void setScaleInfo(ScaleInfo scaleInfo) {
			getUnprocessedVersion().setScaleInfo(scaleInfo);
			if (preprocessRecord!=null) this.applyCropAndScale(preprocessRecord);
			this.getDisplayUpdater().imageUpdated(this.getImagePlus());
		}

		@Override
		public void setPanelStackDisplay(PanelStackDisplay multichannelDisplayLayer) {
			display=multichannelDisplayLayer;
		}
		
		public PanelStackDisplay getDisplayLayer() {
			return display;
		}

		
	}

