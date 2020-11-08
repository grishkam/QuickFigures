package genericMontageKit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

import channelMerging.CSFLocation;
import channelMerging.ChannelEntry;
import channelMerging.MultiChannelWrapper;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import gridLayout.GridIndex;
import logging.IssueLog;
import multiChannelFigureUI.ChannelSwapHandleList;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ScaleInfo;
import utilityClassesForObjects.ScalededItem;
import utilityClassesForObjects.Selectable;
import applicationAdapters.PixelWrapper;
import channelLabels.ChannelLabelTextGraphic;

/**An object containing a set of images and various informtation about them. */
 public class PanelListElement implements Serializable, ScalededItem{
	
	 	/**
	 */
	private static final long serialVersionUID = 1L;
	public static final int MergeImageDes=2, ChannelPanelDes=1;;

		/**The image data. Its pixels. whatever type they may be*/
		transient PixelWrapper image;
		
		/**The graphical object that displays this panel. If there is one. 
		   */
		public Object imageGObject;

		/**The index of the image in its original stack. */
		public Integer originalIndex=0;
		public Integer originalChanNum=0;	
		public Integer originalFrameNum=0;	
		public Integer originalSliceNum=0;	
		
		/**The location of the panel in a grid layout. In both row-column and panel index formats*/
		private GridIndex displayGridIndex=new GridIndex();
		
		/**An integer telling what type of panel this is. channel or merge*/
		public Integer designation=ChannelPanelDes;	
		
		/**the original source image name and id for this panel*/
		public String originalImageName;
		public String originalImagePath;
		public Integer originalImageID;
		public ArrayList<Integer> originalIndices=new ArrayList<Integer>();
		private ChannelLabelTextGraphic channelLabelDisplay;

		private ScaleInfo scaleinfo;

		private ArrayList<ChannelEntry> hashChannel =new  ChannelEntryList();
		
		public PanelListElement createDouble() {
			PanelListElement output=new PanelListElement();
			giveSettingsTo(output);
				
			return output;
		}
		/**simple, sets fields of output*/
		public void giveSettingsTo(PanelListElement output) {
			output.originalChanNum=this.originalChanNum;
			output.originalFrameNum=this.originalFrameNum;
			output.originalSliceNum=this.originalSliceNum;
			output.originalIndex=this.originalIndex;
			output.originalImageID=this.originalImageID;
			output.originalImageName	=this.originalImageName;
			output.originalImagePath=this.originalImagePath;
				
			output.displayGridIndex=this.displayGridIndex;
			output.designation=this.designation;
			output.image=this.image;
			output.hashChannel=this.hashChannel;
		}
		
		public PanelListElement copy() {
			PanelListElement output =new PanelListElement();
			giveObjectsAndSettingsTo(output);
			return output;
		}

		public void giveObjectsAndSettingsTo(PanelListElement output) {
			giveSettingsTo(output);
			giveObjectsTo(output);
		}
		
		private void giveObjectsTo(PanelListElement output) {
			output.setImageDisplayObject(getImageDisplayObject());
			output.setChannelLabelDisplay(getChannelLabelDisplay());
			output.hashChannel=hashChannel;
			output.originalIndices=new ArrayList<Integer>(); output.originalIndices.addAll(originalIndices);
		}
		
		/**Checks for any duplicate Channel Entries, no entry should occur twice in the list*/
		public void purgeDuplicateChannelEntries() {
			ArrayList<ChannelEntry> dd = getDuplicateChannelEntries();
			for(ChannelEntry d:dd) {hashChannel.remove(d);}
		}
		private ArrayList<ChannelEntry> getDuplicateChannelEntries() {
			ArrayList<ChannelEntry> out=new ArrayList<ChannelEntry>();
			for(ChannelEntry e:this.getChannelEntries()) {
				if (e==null) continue;
				for(ChannelEntry e2:this.getChannelEntries()) {
					if (e2==null) continue;
					if (areEntriesDuplicate(e, e2) &&!out.contains(e2)&&!out.contains(e)) out.add(e2);
					
				}
			}
			
			return out;
		}
		private boolean areEntriesDuplicate(ChannelEntry c1, ChannelEntry c2) {
			if (c1!=c2&&c1.getLabel()==c2.getLabel() &&c1.getOriginalChannelIndex()==c2.getOriginalChannelIndex())return true;
			return false;
		}
		
		/**returns a channel entry from the list, returns based on the order they appear in the channel list
		  and not their original channel index*/
		ChannelEntry getChannelEntry(int index) {return getChannelEntries().get(index);}
		
		public boolean hasChannel(int chanIndex) {
			for(ChannelEntry chan: hashChannel) {
				if (chanIndex==chan.getOriginalChannelIndex()) return true;
			}
			return false;
		}
		
		public String getChanSF() {
			return "Channel "+this.originalChanNum+" Slice "+this.originalSliceNum +" Frame " +this.originalFrameNum;
		}
	
		
		
		/**Adds a given channel entry to the list elements Array*/
		public void addChannelEntry(ChannelEntry ce ) {
			hashChannel.add(ce);
			originalIndices.add(ce.getOriginalStackIndex());
		}
		/**Removes*/
		public void removeChannelEntry(ChannelEntry ce) {
			
			try{
				
				this.hashChannel.remove(ce);
				originalIndices.remove(new Integer(ce.getOriginalStackIndex()));//random index out of bounds error here. not clear why
				}
			catch (Throwable t) {t.printStackTrace();}
			
		}
		
		/**Adds a channel entry to the list*/
		public void addChannelDescriptor(String label, Color c, int number, int index) {
			ChannelEntry ce = new ChannelEntry(label,  c, number);
			ce.setOriginalStackIndex(index);
			addChannelEntry(ce);
		}
		
		public void setChannelFrameSlice(int channel,  int frame,int slice) {
			originalChanNum=channel;
			originalSliceNum=slice;
			originalFrameNum=frame;
		}
		
		public void setChannelNumber(int channel) {
			originalChanNum=channel;
		}
		public void setSliceNumber(int slice) {
			originalSliceNum=slice;
		}
	
		public void setFrameNumber(int frame) {
			originalFrameNum=frame;
		}
	
		
		/**resizes the image to fit within a particular size*/
		@Deprecated
		void fit(double width, double height) {
			if (width==getWidth() || height==getHeight()) return;
			double as1 = ((double) height)/((double) width);
			double as2 = ((double) getHeight())/((double) getWidth());
			if (as2>as1) { 
				scale(height/this.getHeight());
				} else {
				scale(width/this.getWidth());
			
			}	
		}
		
		/**Scales the contained image*/
		@Deprecated
		public void scale(double scale) {
			if (scale==1) return;
			resize(getWidth()*scale, getHeight()*scale);
			if (getScaleInfo()!=null) this.getScaleInfo().scaleXY(scale);
		}
		
		/**Scales the contained image. no longer used. obsolete*/
		@Deprecated
		public void scaleBilinear(double scale) {
			if (scale==1) return;
			this.getImageWrapped().scaleBilinear(scale);
			//this.getImageWrapped().scaleBilinear(scale);//resizeBilinear(getWidth()*scale, getHeight()*scale);
			if (getScaleInfo()!=null) this.getScaleInfo().scaleXY(scale);
		}
		
		
		
		/**crops the contained image
		 Does nothing if rectangle is null
		 If there is an angle, rotates the rectangle about
		 its center before crop. uses bilinear interpolation
		 to form rotated image
		 */
		@Deprecated
		public
		void crop(Rectangle r, double angle) {
			if (r==null||r.getHeight()==0||r.getWidth()==0) return;
			if(angle%Math.PI!=0) {image.cropAtAngle(r, angle);} else
			this.image.crop(r);	
		}
		
		
		@Deprecated
		void resize(double width, double height) {
			if(getImageWrapped()==null) return;
			this.getImageWrapped().resize(width, height);
		}
		
		/**void resizeBilinear(double width, double height) {
			this.getImageWrapped().resizeBilinear(width, height);
		} */
		
		public void setImageDisplayObject(Object ob) {
			imageGObject=ob;
		}
		public Object getImageDisplayObject() {
			 return imageGObject;
		}
		public LocatedObject2D getLocatedImageDisplayObject() {
			 return (LocatedObject2D) imageGObject;
		}
		
		public ArrayList<ChannelEntry> getChannelEntries() {
			return hashChannel;
		}
		
		public ChannelEntryList getChannelEntryList() {
			ChannelEntryList ce = new ChannelEntryList();
			ce.addAll(hashChannel);
			return ce;
		}
		
		public void setChannelEntries(ChannelEntryList hashChannel) {
			this.hashChannel = hashChannel;
		}
		

		
		
		public
		int getWidth() {
			return this.getImageWrapped().width();
		}

		
		
		public
		int getHeight() {
			return this.getImageWrapped().height();
		}
		
		public Dimension getDimensions() {
			return new Dimension(getWidth(),getHeight());
		}
		
		public double[] getDimensionsInUnits() {
				return this.getScaleInfo().convertPixelsToUnits( getDimensions() );
		}
		
		public Image getAwtImage() {
			if (this.getImageWrapped()==null) return null;
			return getImageWrapped().image();
		}


		public PixelWrapper getImageWrapped() {
			return image;
		}
		
		public void setImageObjectWrapped(PixelWrapper image) {
			this.image=image;
		}

		@Override
		public ScaleInfo getScaleInfo() {
			// TODO Auto-generated method stub
			return scaleinfo;
		}

		@Override
		public void setScaleInfo(ScaleInfo s) {
			scaleinfo=s;
			
		}

		@Override
		public ScaleInfo getDisplayScaleInfo() {
			// TODO Auto-generated method stub
			return scaleinfo;
		}

		
		public void setChannelLabelDisplay(ChannelLabelTextGraphic cltg) {
			this.channelLabelDisplay=cltg;
			
		}
		
		public ChannelLabelTextGraphic getChannelLabelDisplay() {
			return this.channelLabelDisplay;
		}

		public GridIndex getDisplayGridIndex() {
			return displayGridIndex;
		}

		
		public void setDisplayGridIndex(GridIndex displayGridIndex) {
			this.displayGridIndex = displayGridIndex;
		}
		
		public String getName() {
			if (designation+0==MergeImageDes+0) return "Merge";
			if (this.getChannelEntries().size()==0) return "";
			return getChannelEntries().get(0).getLabel();
		}
	
		
		/**sets the original image of the entry. the method does nothing now but is 
		  overwritten in some subclasses*/
		public void setSourceImage(MultiChannelWrapper imp) {	
			originalImageName=imp.getTitle();//imp.getTitle();
			originalImageID=imp.getID();
			this.setScaleInfo(imp.getScaleInfo());
		
			originalImagePath=imp.getPath();
		}
		
		class ChannelEntryList extends ArrayList<ChannelEntry> {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public boolean hasChannelWithRealName(String realName) {
				for(ChannelEntry chan: this) {
					if (chan.getRealChannelName()==null) continue;
					if (chan.getRealChannelName().equals(realName)) return true;
				}
				
				return false;
			}
			
			
			
			
		}

		void updateImagePanelGraphic() {
			if (getImageDisplayObject() instanceof ImagePanelGraphic) {
				ImagePanelGraphic graphic=(ImagePanelGraphic) getImageDisplayObject();
				graphic.setSourcePanel(this);
				graphic.setImage((BufferedImage) getAwtImage());
			
				graphic.setScaleInfo(getDisplayScaleInfo());
				createChanSwapHandles(graphic);
				graphic.setScaleInfo(getDisplayScaleInfo());
				
			}
		}
		
		
	public BarGraphic getScaleBar() {
		Object imageDisplayObject = getImageDisplayObject();
		if (imageDisplayObject instanceof ImagePanelGraphic) {
			return ((ImagePanelGraphic) imageDisplayObject).getScaleBar();
		}
		return null;
	}
	public void setScaleBar(BarGraphic b) {
		Object imageDisplayObject = getImageDisplayObject();
		if (imageDisplayObject instanceof ImagePanelGraphic) {
			((ImagePanelGraphic) imageDisplayObject).addLockedItem(b);
		}
	}
		
		public ChannelSwapHandleList createChanSwapHandles(ImagePanelGraphic graphic) {
		
			if (graphic.getExtraHandles()==null ) {
				
				ChannelSwapHandleList handles = new ChannelSwapHandleList(null, hashChannel, graphic);
				graphic.setExtraHandles(handles);
				
				
			
			}
			else {
				graphic.getExtraHandles().updateList(hashChannel);
				
			}
			/**
			ChannelLabelTextGraphic clabel = this.getChannelLabelDisplay();
			if(clabel!=null) {
				if (clabel.getExtraHandles()!=null) clabel.getExtraHandles().updateList(hashChannel);
				else
				clabel.setExtraHandles(new ChannelSwapHandleList(null, hashChannel, clabel));
			} 
			*/
			
			return graphic.getExtraHandles();
		}

		public static void main(String[] args) throws CloneNotSupportedException {
			PanelListElement pp = new PanelListElement();
			pp.originalChanNum=4;
			PanelListElement pp2=(PanelListElement) pp.createDouble();
				
		}
		
		
		/**returns the image display object as a panel Grahic*/
		public ImagePanelGraphic getPanelGraphic() {
				if ( getImageDisplayObject() instanceof ImagePanelGraphic)
			return (ImagePanelGraphic) getImageDisplayObject();
				
				return null;
		}
		
		public boolean isSelected() {
			if ( getImageDisplayObject() instanceof Selectable)
				return ((Selectable) getImageDisplayObject()).isSelected();
			
			return false;
		}
		
		/**selects/deselects the display panel, scale bar and label for this panel*/
		public void selectLabelAndPanel(boolean sel) {
			if (!sel){
					if (getPanelGraphic()!=null) {
						getPanelGraphic().deselect();
						if (getPanelGraphic().getScaleBar()!=null)  	getPanelGraphic().getScaleBar().deselect();
					}
					if (getChannelLabelDisplay()!=null)getChannelLabelDisplay().deselect();
					
					}
			else {
				{
					if (getPanelGraphic()!=null) {
						getPanelGraphic().select();
						if (getPanelGraphic().getScaleBar()!=null)  	getPanelGraphic().getScaleBar().select();
					}
					if (getChannelLabelDisplay()!=null)getChannelLabelDisplay().select();
					}
			}
		}
		
		
		public boolean isTheMerge() {
			return this.designation.equals(MergeImageDes);
		}
		
		public boolean changeStackLocation(CSFLocation csf) {
			if(csf.channel>-1) this.originalChanNum=csf.channel;
			if(csf.frame>0) this.originalFrameNum=csf.frame;
			if(csf.slice>0) this.originalSliceNum=csf.slice;
			return false;
		}
		
	}
