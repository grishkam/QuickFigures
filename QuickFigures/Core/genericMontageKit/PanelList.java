package genericMontageKit;


import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import channelMerging.ChannelEntry;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelWrapper;
import graphicalObjects.ImagePanelGraphic;
import logging.IssueLog;
import utilityClasses1.ArraySorter;
import appContext.ImageDPIHandler;
import channelLabels.ChannelLabelTextGraphic;

/**This class is critical for the storage of image that are to 
   be displayed as panels. Each item on the list contains information
   for a channel or it has methods for merging */
public class PanelList implements Serializable{
	/**
	 * 
	 */
	
	private ChannelUseInstructions instructions=new ChannelUseInstructions() ;
	boolean channelUpdateMode=false;//why not update the channel entries in each panel? I dont remember why I set this to false
	 /**The Bilinear Scale that will be applied to the panels*/
    private double scaleBilinear=1;//will make this no longer accessible by user. obsolete
    /**scaling of display image*/
    private double displayPanelScale=ImageDPIHandler.ratioFor300DPI();//
	 public Rectangle cropper=null;//no longer accessible by user. obsolete
	 private double cropAngle=0;//no longer accessible by user. obsolete
	
	 /**never returns null*/
	public ChannelUseInstructions getChannelUseInstructions() {
		if (getChannelUstInstructions()==null) {
			setChannelUstInstructions(new ChannelUseInstructions()) ;
		}
		return getChannelUstInstructions();
	} 
	
	/**needed for functioning of the channel merging. it must be a non-null value for the class to work*/
	
	
	public PanelList() {
		
	} 
	
	public PanelList createDouble() {
		PanelList output = new  PanelList() ;
		output.setChannelUstInstructions(this.getChannelUseInstructions());
		output.scaleBilinear=this.scaleBilinear;
		output.cropper=this.cropper;
		for(PanelListElement e: panels) {
			output.add(e.createDouble());
		}
		return output;
	}
	
	 
	
	private static final long serialVersionUID = 1L;
	
	 

	private ArrayList<PanelListElement> panels=new ArrayList<PanelListElement>();
	
	
	/**creates a panel entry object*/
	protected PanelListElement createEntry() {
		return new PanelListElement();
	}	
	
	
	
	/**returns the channel panel with a given slice and frame*/
	public PanelListElement getChannelPanelFor(int channel, int slice, int frame) {
		for(PanelListElement p: this.getPanels()) {
			if (p==null) continue;
			if (p.originalChanNum==channel&&p.originalFrameNum==frame&&p.originalSliceNum==slice&&p.designation+0!=PanelListElement.MergeImageDes+0) return p;
		}
		return null;
	}
	
	/**returns the merge panel with the given slice and frame*/
	public PanelListElement getMergePanelFor(int slice, int frame) {
		for(PanelListElement p: this.getPanels()) {
			if (p==null) continue;
			if (p.originalFrameNum==frame&&p.originalSliceNum==slice&&p.designation+0==PanelListElement.MergeImageDes+0) return p;
		}
		return null;
	}
	
	/**returns the merge panel with the given slice and frame*/
	public PanelListElement getMergePanel() {
		for(PanelListElement p: this.getPanels()) {
			if (p==null) continue;
			if (p.designation+0==PanelListElement.MergeImageDes+0) return p;
		}
		return null;
	}
	
	/**removes all the panels*/
	public void eliminateAllPanels() {
		if (panels!=null)
		panels.clear();
	}
	
	/***/
	public void add(PanelListElement element) {
		if (element!=null)  getPanels().add(element);
	}
	
	/***/
	public void addAll(Iterable<PanelListElement> element) {
		for(PanelListElement i: element) {add(i);}
	}
	
	/***/
	public void remove(PanelListElement element) {
		if (element!=null)  getPanels().remove(element);
	}
	
	
	/**
	public ImageDataType getImage(int index) {
		return getPanels().get(index).getImageObject();
	}
	*/
	
	public String getImageName(int index) {
		return getPanels().get(index).originalImageName;
	}
	
	
	
	
	public PanelListElement getMontageIndexPanel(int index) {
		for(PanelListElement panel:getPanels()) {
			if (panel.getDisplayGridIndex().getPanelindex()==index) return panel;
		}
		return null;
	}
	
	
	
	/**returns the number of different original image names present in the list*/
	public int nDistinctNames() {
		int output=0;
		String name=null;
		for(PanelListElement panel:getPanels()) {
			if (!panel.originalImageName.equals(name)) {output++;
			name=panel.originalImageName;
			}
		}	
		return output;	
	}
	
	/**returns the number of entries in the list with the orignal image name 'name'*/
	public int nWithName(String name) {
		int output=0;
		for(PanelListElement panel:getPanels()) {
			if (panel.originalImageName.equals(name)) output++;
		}
		return output;
	}
	
	public void add( PanelList b ) {
		getPanels().addAll(b.getPanels());
	}
	
	/**
	public ArrayList<ImageDataType> getImages() {
		ArrayList<ImageDataType> output=new ArrayList<ImageDataType>();
		for (AbstractpanelListElement p:getPanels()) {output.add(p.getImageWrapped().getPixels());}
		return output;
	}*/
	
	public ArrayList<Image> getAwtImages() {
		ArrayList<Image> output=new ArrayList<Image>();
		for (PanelListElement p:getPanels()) {output.add(p.getImageWrapped().image());}
		return output;
	}
	
	

	public  PanelList createList(PanelList[] array) {
		PanelList list=createList();
		for(PanelList list2: array) {add(list2);}
		return list;
	}
	
	public  PanelList createList(ArrayList<PanelList> array) {
		PanelList list=createList();
		for(PanelList list2: array) {add(list2);}
		return list;
	}
	
	public PanelList combine(PanelList[] all) {
		PanelList i = createList();
		for (PanelList one: all) {i.add(one);}
		return i;
	}
	
	
	

	
	/**public abstract AbstractpanelListElement createChannelPanelEntry(
			MultiChannelWrapper imp, int channel, int frame, int slice);
	*/


	 
	public void addChannelPanel(MultiChannelWrapper imp, int channel, int frame, int slice) {
		if (imp==null) {IssueLog.log("panel stack can do nothing with a null image"); return;}
		try{add(createChannelPanelEntry(imp, channel, frame, slice) );}
		
		catch (Throwable t) {IssueLog.log(t);}
	}
	
	
	
	public void addMergePanel(MultiChannelWrapper imp, int frame, int slice) {
		if (imp==null) {IssueLog.log("panel stack can do nothing with a null image"); return;}
		try{add(createMergePanelEntry(imp, frame, slice));	} catch (Throwable t) {IssueLog.log(t);}
	}
	
	
	

	
	 /**Adds creates channel and merge panels for a given frame and slice of the image
	   and add them to this list*/
	 public void addChannelPanelsAndMerge(MultiChannelWrapper imp, int frame, int slice) {
		 
		int chanNum=imp.nChannels();
		boolean singleChannel=   chanNum==1;
		
		 
		if (
				(getChannelUseInstructions().mergePanelFirst()
			||  this.getChannelUseInstructions().onlyMergePanel())
			  &&!singleChannel &&getChannelUseInstructions().addsMergePanel()
							) 
			addMergePanel(imp, frame, slice);
		
		for (int c=1; c<=chanNum; c++ ) {
			if ( singleChannel || !getChannelUseInstructions().isChanPanExcluded(c)  ) 
						addChannelPanel( imp, c, frame, slice);
		}
		
		if (getChannelUseInstructions().mergePanelLast() &&!singleChannel &&getChannelUseInstructions().addsMergePanel()) addMergePanel(imp, frame, slice);
	}
	
	/**Iterates through the frames and slices of an image and calls the "addChannelPanelsAndMerge method"*/
	public void addAllCandF(MultiChannelWrapper imp) {
		if (imp==null)
			{IssueLog.log("Source Multichannel display is missing. Cannot add channel panels to list");
			return;
			}
		int frames= imp.nFrames();//nFrames( imp);
		int slices= imp.nSlices();//nSlices( imp);
		for(int f=1; f<=frames; f++) {
			for(int s=1; s<=slices; s++) {
				
				addChannelPanelsAndMerge(imp, f, s) ;
			}
		}
		
		
		try {
			Collections.sort(panels, new PanelCompare());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

	
	
	
	/**
	ArrayList<Integer> getIncludedChans(MultiChannelWrapper imp) {
		ArrayList<Integer> out=new ArrayList<Integer>();
		for(int c=1; c<=imp.nChannels(); c++) {
			if (!isMergeExcluded(c)) out.add(c);
		}
		return out;
	}*/
	
	
	
	/**sets the image for panel i to be image
	void resetImage(ImageDataType image, int i){
		panels.get(i).image=makeWrapper(image);
	}*/
	
	//public abstract PixelWrapper<ImageDataType> makeWrapper(ImageDataType image);
	

	final int mergeDesig=2;
	
	

	
	/**Used to get labels for each slice. */
	public String[] getSliceLabels() {
		String[] output=new String[getPanels().size()] ;
		
		if (getPanels().size()==0) {
			IssueLog.log("Cannot retreive slice labels as panel"); return output;
			}
		for(int i=0; i<output.length; i++) try {
			if (getPanels().size()==0) continue;
			
			PanelListElement panel = getPanels().get(i);
			if (panel.getChannelEntries().size()==0){IssueLog.log("no channel entry");; ;continue;}
			
			output[i]=panel.getChannelEntries().get(0).getLabel();
			if (panel.designation==mergeDesig) output[i]="Merge";
			if (output[i]==null) output[i]="";
		}
		catch (Throwable t) {
			IssueLog.log("difficulty retriving slice labels");
		}
		return output;
	}
	
	public int getHeight() {
		if (getPanels().size()==0) return 0;
		return getPanels().get(0).getHeight();
	}
	public int getWidth() {
		if (getPanels().size()==0) return 0;
		return getPanels().get(0).getWidth();
	}
	
	/**performs cropping on all the panel images.
	   Needed if there is a cropping rectangle of any kind.*/
	public void cropAll(Rectangle r) {
		for (PanelListElement p:getPanels()) {p.crop(r, cropAngle);}
	}
	
	/**creates a panel list with the same settings as this one*/
	public PanelList createList() {	
		PanelList output = new PanelList();
		output.giveSettingsTo(output);
		return output;
	}
	

	public ArrayList<PanelListElement> getPanels() {
		return panels;
	}

	public void setPanels(ArrayList<PanelListElement> panels) {
		this.panels = panels;
	}

	/**uses the input multichannel to update the image panels.
	  Updates both the image stored in each list element and the 
	  display panel image*/
	public void updateAllPanelsWithImage(MultiChannelWrapper imp) {
		for(PanelListElement panelp: getPanels()) try {
			updateImageForPanel(imp, panelp);
		} catch (Throwable t) {
			IssueLog.log(t);
		}
	}
	
	/**uses the input multichannel to update the image panel with the given name.
	  Does NOT reset the channel entries of each list element so wont
	  reflect changes in channel order.*/
	public void updateAllPanelsWithImage(MultiChannelWrapper imp, String realChannelName) {
		for(PanelListElement panelp: getPanels()) try {
			if (panelp.getChannelEntryList().hasChannelWithRealName(realChannelName))this.updateImageForPanel(imp, panelp);
		} catch (Throwable t) {
			IssueLog.log(t);
		}
	}
	
	
	/**Resets the channel entries for all. This is needed if 
	 * the channel order, channel names or colors of the
	 * source image have been changed.*/
	public void resetChannelEntriesForAll(MultiChannelWrapper  imp) {
		for(PanelListElement entry: this.getPanels()) {
			if (this.channelUpdateMode==true) this.updateChannelEntries(imp, entry);
			else this.resetChannelEntriesForPanel(imp, entry);
		}
	}

	
	
	private void resetChannelEntriesForPanel(MultiChannelWrapper  impw, PanelListElement entry) {
		//ImagePlusWrapper impw = new ImagePlusWrapper(imp);
		
		if (entry.designation+0==PanelListElement.MergeImageDes+0) {
			this.setUpChannelEntryForMerge(impw, entry, entry.originalFrameNum, entry.originalSliceNum);
		}
		else {
			setUpChannelEntriesForPanel(impw, entry, entry.originalChanNum, entry.originalFrameNum, entry.originalSliceNum);
			
		}
		entry.purgeDuplicateChannelEntries();
		if (entry.getChannelLabelDisplay()!=null) entry.getChannelLabelDisplay().setParaGraphToChannels();
	}
	
	
	/**updates the channel entry object. This changes the label and color of the entries but does
	 * not replace them with fresh objects*/
	public void updateChannelEntries(MultiChannelWrapper impw, PanelListElement entry) {
		ArrayList<ChannelEntry> origEntry = impw.getChannelEntriesInOrder();
		
		for(ChannelEntry c: entry.getChannelEntries()) {
			ChannelEntry cnew = findEquivalent(c, origEntry);
			if (cnew!=null) c.updateFrom(cnew);
			if (entry.getChannelLabelDisplay()!=null) {
				entry.getChannelLabelDisplay().setParaGraphToChannels();
				
				};
		}
		
	}
	

	public void setUpChannelEntriesForPanel(MultiChannelWrapper impw, PanelListElement entry, int channel, int frame, int slice) {
		// MultiChannelWrapper impw=new ImagePlusWrapper(imp);
		
		entry.getChannelEntries().clear();
		entry.originalIndices.clear();
		
		if (!impw.containsImage()) {
				IssueLog.log("Cannot complete channel panel without data. No image OR empty ImageStack OR no imagestack");
				return ;
				}
		
		String title=impw.getSliceName(channel, slice, frame);
		
		if (impw.containsSplitedChannels()) {	
			
			entry.addChannelEntry(impw.getSliceChannelEntry(channel, slice, frame));
			
			int eachMergeChannel = this.getChannelUseInstructions().eachMergeChannel;
			
			if (eachMergeChannel>0 && eachMergeChannel<=impw.nChannels()) {		
				entry.addChannelEntry(impw.getSliceChannelEntry(eachMergeChannel, slice, frame));
			} 
			
		} else {
			//entry.originalIndex=imp.getStackIndex(channel, slice, frame);
			//String title=imp.getStack().getSliceLabel(entry.originalIndex);
			entry.addChannelDescriptor(title, Color.BLACK, channel, entry.originalIndex);	
		}
	}
	
	
	
	
	/**sets up the properties and fields of what would be the merged panel's channel entries*/
	public void setUpChannelEntryForMerge(MultiChannelWrapper ipw, PanelListElement entry, int frame, int slice) {
		
		int nChannel=ipw.nChannels();
	
		entry.getChannelEntries().clear();
		entry.originalIndices.clear();
		
		String[] eachChanNameH=new String[nChannel]; 
	
		Color[] eachColor=new Color[nChannel];
		
		for (int c=1; c<=ipw.nChannels(); c++) {
			eachChanNameH[c-1]=ipw.channelName(c);
			eachColor[c-1]=ipw.getChannelColor(c);
		} 

		
		
		for(int c=0; c<nChannel; c++) {
			if (!getChannelUseInstructions().isMergeExcluded(c+1)){
				entry.addChannelEntry(ipw.getSliceChannelEntry(c+1, slice, frame));
			}
		}
	}
	
	
	

	
	
	/**Given a source image and a panel, this sets the panels image to the correct one.
	 * This updates both the display panel, and the image that is stored in the panel 
	 * list element*/
	public void updateImageForPanel(MultiChannelWrapper impw,
			PanelListElement entry){
		
	
		entry.setImageObjectWrapped(impw.getChannelMerger().generateMergedRGB(entry, this.getChannelUseInstructions().ChannelsInGrayScale));
		
		entry.setScaleInfo(impw.getScaleInfo());
		
		processEntryImage(entry);
		
		/**needed to change the image when there are updates but not for initial creation*/
		entry.updateImagePanelGraphic();
	}
	
	
	/**crops and scales the image. needed during each update from the source multichannel
	  so the appropriately cropped image is kept.
	   */
	public void processEntryImage(PanelListElement entry) {
		if (getCropper()!=null) entry.crop(getCropper(), cropAngle);
		if (getScaleBilinear()>0) {
			entry.scaleBilinear(getScaleBilinear());
		}
	}
	
	/**returns the unrotated cropping rectangle*/
	Rectangle getCropper() {
		return cropper;
	}
	
	
	
	/**Creates a panel with an RGB of the merged channels in a given slice and frame of the image*/
	public PanelListElement createMergePanelEntry(MultiChannelWrapper impw, int frame, int slice) {
		//ImageTypeWrapper impw = new ImageTypeWrapper(imp);
		
		PanelListElement entry=createEntry();
		if (!impw.containsImage()) {IssueLog.log("Cannot create meged image for null or empty entry"); return entry;}
		entry.designation=2;
		entry.originalImageName=impw.getTitle();
		entry.originalImageID=impw.getID();
		entry.originalChanNum=1;//not truly relevant as all the channels are included but the number must be set
		entry.originalIndex=impw.getStackIndex(1, slice, frame);
		entry.originalFrameNum=frame;
		entry.originalSliceNum=slice;
		
		
		
		/**the concept of merge panels does not exist when there are not separate channels. As such, this method will return null.*/
		if (!impw.containsSplitedChannels()) {
			IssueLog.log("Warning Channel Data is not Split"); 
			//return null;
		} else {
		
				/**creates the channel entries for the merged image*/
				setUpChannelEntryForMerge(impw, entry, frame, slice);
				}
		
		updateImageForPanel(impw, entry);
		return entry;
	}
	
	/**This sets a channel panel entry */
	public PanelListElement createChannelPanelEntry( MultiChannelWrapper impw, int channel, int frame, int slice) {
		PanelListElement entry=createEntry();
		// MultiChannelWrapper impw=new ImageTypeWrapper(imp);
		entry.setChannelFrameSlice(channel, frame, slice);
		entry.setSourceImage(impw);
		entry.originalIndex=impw.getStackIndex(channel, slice, frame);
		
		setUpChannelEntriesForPanel(impw, entry, channel, frame, slice);
	
		updateImageForPanel(impw, entry);
		
		return entry;
	}
	

	public int getSize() {
		
		return getPanels().size();
		}



	
	


	public void setCropper(Rectangle selectionRectangle) {
		if (selectionRectangle!=null)
			cropper=selectionRectangle.getBounds();
		else cropper=null;
		
	}


/**copies the settings to another list*/
	public void giveSettingsTo(PanelList stack) {
		getChannelUseInstructions().makeMatching(stack.getChannelUseInstructions());
		stack.cropper=this.cropper;
		stack.setScaleBilinear(getScaleBilinear());
		stack.setPanelLevelScale(getPanelLevelScale());
	}
	
	public void givePartialSettingsTo(PanelList stack) {
		stack.setScaleBilinear(getScaleBilinear());
		stack.setPanelLevelScale(getPanelLevelScale());
		getChannelUseInstructions().makePartialMatching(stack.getChannelUseInstructions());
		
	}
	
	
/**gets the level of bilinear scale to use. 1 for no scale*/
	public double getScaleBilinear() {
		return scaleBilinear;
	}

	/**sets the level of bilinear scale to use. 1 for no scale*/
	public void setScaleBilinear(double scaleBilinear) {
		this.scaleBilinear = scaleBilinear;
	}
	
	public int getlastPanelsIndex() {
		
		int out=0;
		for(PanelListElement s:getPanels()) {
			int j=s.getDisplayGridIndex().getPanelindex();
			if (j>out) out=j;
		}
		
		return out;
	}

	public ChannelUseInstructions getChannelUstInstructions() {
		return instructions;
	}

	public void setChannelUstInstructions(ChannelUseInstructions instructions) {
		this.instructions = instructions;
	}
	
	public ArrayList<ChannelLabelTextGraphic> getChannelLabels() {
		ArrayList<ChannelLabelTextGraphic> out=new ArrayList<ChannelLabelTextGraphic>();
		for(PanelListElement panel: getPanels()) {
			if (panel.getChannelLabelDisplay() instanceof ChannelLabelTextGraphic) out.add(panel.getChannelLabelDisplay());
		}
		
		return out;
	}
	
	/**gets or creates a merge panel. normally invoked by menu item to add a panel
	  that is not part of the default list*/
	public PanelListElement getOrCreateMergePanel( MultiChannelWrapper mw, int slice, int frame) {
		PanelListElement panel = getMergePanelFor( slice, frame);
		if (panel==null)addMergePanel(mw,  frame, slice);
		panel = getMergePanelFor( slice, frame);
		 return panel;
	}
	/**gets or creates a channel panel. normally invoked by menu item to add a panel
	  that is not part of the default listl*/
	public PanelListElement getOrCreateChannelPanel(MultiChannelWrapper mw, int channel, int slice, int frame) {
		PanelListElement panel =getChannelPanelFor(channel, slice, frame);
		if (panel==null)addChannelPanel(mw, channel, frame, slice);
		 panel = getChannelPanelFor(channel, slice, frame);
		 return panel;
	}
	
	/**returns all the panel graphics that are both in this image and supported by the panel list*/
	public ArrayList<ImagePanelGraphic> getPanelGraphics() {
		ArrayList<ImagePanelGraphic> out=new ArrayList<ImagePanelGraphic>();
		for(PanelListElement panel: getPanels()) {
							out.add(panel.getPanelGraphic());
		}
		
		
		return out;
	}
	
	

	public double getPanelLevelScale() {
		return displayPanelScale;
	}

	public void setPanelLevelScale(double panelLevelScale) {
		displayPanelScale=panelLevelScale;
	}

	public void setCropperAngle(double angle) {
		cropAngle=angle;
		
	}
	
	public void swapPanelLocations(PanelListElement p1, PanelListElement p2) {
		new ArraySorter<PanelListElement>().swapObjectPositionsInArray(p1, p2, panels);
		
		Point2D l1 = p1.getPanelGraphic().getLocation();
		Point2D l2 = p2.getPanelGraphic().getLocation();
		p1.getPanelGraphic().setLocation(l2);
		p2.getPanelGraphic().setLocation(l1);
		
	}
	
	
	/**changes the channel update mode of this panel list*/
	public void setChannelUpdateMode(boolean mode) {
		channelUpdateMode=mode;
	}
	
	/**looks through the list to find the equivalent channel entry*/
	public static ChannelEntry findEquivalent(ChannelEntry ce, ArrayList<ChannelEntry>  list) {
		for(ChannelEntry chan: list) {
			if(ce.getOriginalChannelIndex()==chan.getOriginalChannelIndex()) return chan;
			//if (ce.getRealChannelName().equals(chan.getRealChannelName())) return chan;
		}
		
		return null;
	}
	
	
	/**A comparator for sorting the panel lists*/
	class PanelCompare implements Comparator<PanelListElement>  {

		@Override
		public int compare(PanelListElement o1, PanelListElement o2) {
			try {
				if (o1.originalFrameNum>o2.originalFrameNum) return 1;
				if (o2.originalFrameNum>o1.originalFrameNum) return -1;
				if (o1.originalSliceNum>o2.originalSliceNum) return 1;
				if (o2.originalSliceNum>o1.originalSliceNum) return -1;
				/**proceeds beyond this point only if the slice and frame are equal*/
				
				if (instructions.mergePanelFirst() &&o1.isTheMerge()&&!o2.isTheMerge())  {
					return -1;
				}
				if (instructions.mergePanelFirst() &&!o1.isTheMerge()&&o2.isTheMerge())  {
					return 1;
				}
				
				if (!instructions.mergePanelFirst() &&o1.isTheMerge()&&!o2.isTheMerge())  {
					return 1;
				}
				if (!instructions.mergePanelFirst() &&!o1.isTheMerge()&&o2.isTheMerge())  {
					return -1;
				}
				
				/**proceeds beyond this point only if neither item is a merge panel*/
				int c1 = instructions.getChanPanelReorder().index(o1.originalChanNum);
				int c2 = instructions.getChanPanelReorder().index(o2.originalChanNum);
				if (c1>c2) return 1;
				if (c2>c1) return -1;
			} catch (Exception e) {
				IssueLog.log(e);
			}
			
			return 0;
		}
		
	}


	
	
	
	
}
