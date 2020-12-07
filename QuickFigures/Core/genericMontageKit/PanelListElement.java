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
/**
 * Author: Greg Mazo
 * Date Modified: Dec 6, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
package genericMontageKit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

import channelMerging.CSFLocation;
import channelMerging.ChannelEntry;
import channelMerging.MultiChannelImage;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import gridLayout.GridIndex;
import multiChannelFigureUI.ChannelSwapHandleList;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ScaleInfo;
import utilityClassesForObjects.ScalededItem;
import utilityClassesForObjects.Selectable;
import applicationAdapters.PixelWrapper;
import channelLabels.ChannelLabelTextGraphic;

/**An object containing a set of images and various information about them. */
 public class PanelListElement implements Serializable, ScalededItem{
	
	 	/**
	 */
	private static final long serialVersionUID = 1L;
	public static final int MERGE_IMAGE_PANEL=2, CHANNEL_IMAGE_PANEL=1;

		/**The image data. Its pixels. whatever type they may be*/
		transient PixelWrapper image;
		
		/**The graphical object that displays this panel. If there is one. 
		   */
		public Object imageGObject;
		/**The channel label used to display the channel name*/
		private ChannelLabelTextGraphic channelLabelDisplay;

		/**The index of the image in its original stack.
		 *  This represents the index used when the element was first created*/
		public Integer innitialStackIndex=0;
		
		/**Specifies the channel slice and frame that the panel is derived from*/
		public Integer targetChannelNumber=0;	
		public Integer targetFrameNumber=0;	
		public Integer targetSliceNumber=0;	
		
		/**The location of the panel in a grid layout. In both row-column and panel index formats*/
		private GridIndex displayGridIndex=new GridIndex();
		
		/**An integer telling what type of panel this is. channel or merge*/
		public Integer designation=CHANNEL_IMAGE_PANEL;	
		
		/**the original source image name and id for this panel. */
		public String originalImageName;
		public String originalImagePath;
		public Integer originalImageID;
		public ArrayList<Integer> originalIndices=new ArrayList<Integer>();
		
		
		private ScaleInfo scaleinfo;//the pixel size is stored here

		/**Maintains a list of the channel entries*/
		private ArrayList<ChannelEntry> hashChannel =new  ChannelEntryList();
		
		/**creates a similar panel targeting the same stack location but 
		  without any Channel Label or Image*/
		public PanelListElement createDouble() {
			PanelListElement output=new PanelListElement();
			giveSettingsTo(output);
				
			return output;
		}
		
		/**simple, sets fields of output*/
		public void giveSettingsTo(PanelListElement output) {
			output.targetChannelNumber=this.targetChannelNumber;
			output.targetFrameNumber=this.targetFrameNumber;
			output.targetSliceNumber=this.targetSliceNumber;
			output.innitialStackIndex=this.innitialStackIndex;
			output.originalImageID=this.originalImageID;
			output.originalImageName	=this.originalImageName;
			output.originalImagePath=this.originalImagePath;
				
			output.displayGridIndex=this.displayGridIndex;
			output.designation=this.designation;
			output.image=this.image;
			output.hashChannel=this.hashChannel;
		}
		
		/**creates a copy. this method is needed for the undo to work*/
		public PanelListElement copy() {
			PanelListElement output =new PanelListElement();
			giveObjectsAndSettingsTo(output);
			return output;
		}

		/**Alters the target panel to be a copy of this panel*/
		public void giveObjectsAndSettingsTo(PanelListElement targetPanel) {
			giveSettingsTo(targetPanel);
			giveObjectsTo(targetPanel);
		}
		
		/**sets the target panels stored objects to those of this object*/
		private void giveObjectsTo(PanelListElement targetPanel) {
			targetPanel.setImageDisplayObject(getImageDisplayObject());
			targetPanel.setChannelLabelDisplay(getChannelLabelDisplay());
			targetPanel.hashChannel=hashChannel;
			targetPanel.originalIndices=new ArrayList<Integer>(); targetPanel.originalIndices.addAll(originalIndices);
		}
		
		/**Checks for any duplicate Channel Entries, no entry should occur twice in the list*/
		public void purgeDuplicateChannelEntries() {
			ArrayList<ChannelEntry> dd = getDuplicateChannelEntries();
			for(ChannelEntry d:dd) {hashChannel.remove(d);}
		}
		
		/**looks for duplicate channel entries in the channel entry list. returns the extras.
		 * Only adds one of the two duplicates to the output*/
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
		
		/**returns true if the two channel entries appear to be identical.*/
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
		
		/**Returns a string describing the channel framd and slice that this panel uses*/
		public String getChanSF() {
			return "Channel "+this.targetChannelNumber+" Slice "+this.targetSliceNumber +" Frame " +this.targetFrameNumber;
		}
	
		
		
		/**Adds a given channel entry to the list elements Array*/
		public void addChannelEntry(ChannelEntry ce ) {
			hashChannel.add(ce);
			originalIndices.add(ce.getOriginalStackIndex());
		}
		/**Removes a channel entry from the list*/
		public void removeChannelEntry(ChannelEntry ce) {
			if (ce==null) return;
			try{
				this.hashChannel.remove(ce);
				if (originalIndices==null) return;
				int index = originalIndices.indexOf(ce.getOriginalStackIndex());
				if (index>0)
				index=originalIndices.remove(index);//random index out of bounds exception would occur if the .remove was done 
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
			targetChannelNumber=channel;
			targetSliceNumber=slice;
			targetFrameNumber=frame;
		}
		
		public void setChannelNumber(int channel) {
			targetChannelNumber=channel;
		}
		public void setSliceNumber(int slice) {
			targetSliceNumber=slice;
		}
	
		public void setFrameNumber(int frame) {
			targetFrameNumber=frame;
		}
	
		
	
	
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
			if (designation+0==MERGE_IMAGE_PANEL+0) return "Merge";
			if (this.getChannelEntries().size()==0) return "";
			return getChannelEntries().get(0).getLabel();
		}
	
		
		/**sets the original image of the entry. the method does nothing now but is 
		  overwritten in some subclasses*/
		public void setSourceImage(MultiChannelImage imp) {	
			originalImageName=imp.getTitle();//imp.getTitle();
			originalImageID=imp.getID();
			this.setScaleInfo(imp.getScaleInfo());
		
			originalImagePath=imp.getPath();
		}
		
		public class ChannelEntryList extends ArrayList<ChannelEntry> {

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
		
	/**crates a set of channel handles for the image panel that */
		public ChannelSwapHandleList createChanSwapHandles(ImagePanelGraphic graphic) {
		
			if (graphic.getExtraHandles()==null ) {
				
				ChannelSwapHandleList handles = new ChannelSwapHandleList(null, hashChannel, graphic);
				graphic.setExtraHandles(handles);
				
				
			
			}
			else {
				graphic.getExtraHandles().updateList(hashChannel);
				
			}
			
			return graphic.getExtraHandles();
		}


		
		
		/**returns the image display object as a panel Grahic*/
		public ImagePanelGraphic getPanelGraphic() {
				if ( getImageDisplayObject() instanceof ImagePanelGraphic)
			return (ImagePanelGraphic) getImageDisplayObject();
				
				return null;
		}
		
		/**returns true if the user has selected this element*/
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
		
		
		/**returns true if this is a merge panel*/
		public boolean isTheMerge() {
			return this.designation.equals(MERGE_IMAGE_PANEL);
		}
		
		
		/**Changes the panel's channel slice and frame based on the argument given*/
		public boolean changeStackLocation(CSFLocation csf) {
			if(csf.channel>-1) this.targetChannelNumber=csf.channel;
			if(csf.frame>0) this.targetFrameNumber=csf.frame;
			if(csf.slice>0) this.targetSliceNumber=csf.slice;
			return false;
		}
		
	}
