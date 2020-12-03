package genericMontageKit;


import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import channelMerging.CSFLocation;
import channelMerging.ChannelEntry;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelImage;
import graphicalObjects.ImagePanelGraphic;
import logging.IssueLog;
import utilityClasses1.ArraySorter;
import appContext.ImageDPIHandler;
import channelLabels.ChannelLabelTextGraphic;

/**This class stores a list of the panels that are part of a figure
  A figure may contain many such lists (one for each source image)
  Panel list elements contain information about a panel, the channels it contains
  the object that displays the image, the object that displays the channel label and more.
  Each item on the list contains information relating to how to update
  the image displayed (which channels to merge)*/
public class PanelList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private ChannelUseInstructions instructions=new ChannelUseInstructions() ;
	
	//If channelUpdateMode=true The colors of all the channel entires are updated to account for changes in the channel colors of the original image. 
	//TODO: determine which method is superior. the channel update mode variable seems to have little effect on the user experience
	//if channelUpdateMode=false, this will create new channel entries which at the moment dont always update to the channel handles and color mode button. need to figure out why
	/**Determines the method of updating the channel entries*/
	boolean channelUpdateMode=false;
	
    /**Determines the pixel density of newly creates image panels*/
    private double pixelDensityRatio=ImageDPIHandler.ratioFor300DPI();//
    
    
    /**The Bilinear Scale that will be applied to the panels*/
    @Deprecated
    private double scaleBilinear=1;//will make this no longer accessible by user. obsolete
    @Deprecated
	 public Rectangle cropper=null;//no longer accessible by user. will be obsolete in future versions. may be retained for use by programmers
    @Deprecated
    private double cropAngle=0;//no longer accessible by user. will be obsolete in future versions. may be retained for use by programmers

		private ArrayList<PanelListElement> panels=new ArrayList<PanelListElement>();
	
	 /**The method used by this list to select which channels and panels are created 
	  never returns null*/
	public ChannelUseInstructions getChannelUseInstructions() {
		if (instructions==null) {
			instructions=new ChannelUseInstructions() ;
		}
		return instructions;
	} 
	
	
	public PanelList() {
	} 
	
	/**Creates a non-identical duplicate of this list
	   duplicate will lack objects but have all the same settings, channel indices and so on*/
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
	
	 
	
	/**creates a panel entry object*/
	protected PanelListElement createEntry() {
		return new PanelListElement();
	}	
	
	
	/**returns the channel panel with a given slice and frame*/
	public PanelListElement getChannelPanelFor(int channel, int slice, int frame) {
		for(PanelListElement p: this.getPanels()) {
			if (p==null) continue;
			if (p.targetChannelNumber==channel&&p.targetFrameNumber==frame&&p.targetSlideNumber==slice&&p.designation+0!=PanelListElement.MERGE_IMAGE_PANEL+0) return p;
		}
		return null;
	}
	
	/**returns the merge panel with the given slice and frame*/
	public PanelListElement getMergePanelFor(int slice, int frame) {
		for(PanelListElement p: this.getPanels()) {
			if (p==null) continue;
			if (p.targetFrameNumber==frame&&p.targetSlideNumber==slice&&p.designation+0==PanelListElement.MERGE_IMAGE_PANEL+0) return p;
		}
		return null;
	}
	
	/**returns the merge panel with the given slice and frame*/
	public PanelListElement getMergePanel() {
		for(PanelListElement p: this.getPanels()) {
			if (p==null) continue;
			if (p.designation+0==PanelListElement.MERGE_IMAGE_PANEL+0) return p;
		}
		return null;
	}
	
	/**removes all the panels*/
	public void eliminateAllPanels() {
		if (panels!=null)
		panels.clear();
	}
	
	/**adds a panel to the list*/
	public void add(PanelListElement element) {
		if (element!=null)  getPanels().add(element);
	}
	
	/**Add many panels to the list*/
	public void addAll(Iterable<PanelListElement> element) {
		for(PanelListElement i: element) {add(i);}
	}
	
	/**removed a panel from the list*/
	public void remove(PanelListElement element) {
		if (element!=null)  getPanels().remove(element);
	}
	
	
	/**returns the name of the image that the panel came from*/
	public String getImageName(int index) {
		return getPanels().get(index).originalImageName;
	}
	
	
	/**In some situations, the location of a panel within the layout (the panel index)
	  will be stored. this method returns the panel at a given index. */
	//TODO: determine if this is obsolete
	public PanelListElement getPanelAtLayoutLocation(int index) {
		for(PanelListElement panel:getPanels()) {
			if (panel.getDisplayGridIndex().getPanelindex()==index) return panel;
		}
		return null;
	}

	
	/**returns the number of different original image names present in the list
	  In most cases this will consist of a single image.*/
	//TODO: determine if this is obsolete
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
	//TODO: determine if this is obsolete
	public int nWithName(String name) {
		int output=0;
		for(PanelListElement panel:getPanels()) {
			if (panel.originalImageName.equals(name)) output++;
		}
		return output;
	}
	
	/**Adds all the panels within list b to this list*/
	//TODO: determine if this is obsolete
	public void add( PanelList b ) {
		getPanels().addAll(b.getPanels());
	}
	
	/**moved through the list. pulls out the images from the panels and returns them*/
	//TODO: determine if this is obsolete
	public ArrayList<Image> getAwtImages() {
		ArrayList<Image> output=new ArrayList<Image>();
		for (PanelListElement p:getPanels()) {output.add(p.getImageWrapped().image());}
		return output;
	}
	
	
	/**creates a new list from an array of lists*/
	//TODO: determine if this is obsolete
	public  PanelList createList(PanelList[] array) {
		PanelList list=createList();
		for(PanelList list2: array) {add(list2);}
		return list;
	}
	
		/**creates a new list from an array of lists*/
	//TODO: determine if this is obsolete
	public PanelList combine(PanelList[] all) {
		PanelList i = createList();
		for (PanelList one: all) {i.add(one);}
		return i;
	}
	/**creates a new list containing the same panels as the given arrayList*/
	//TODO: determine if this is obsolete
	public  PanelList createList(Iterable<PanelList> arrayList) {
		PanelList list=createList();
		for(PanelList list2: arrayList) {add(list2);}
		return list;
	}
	

	 /**Adds a channel panel with the given channel frame and slice to the list 
	 and returns it*/
	public PanelListElement addChannelPanel(MultiChannelImage imp, int channel, int frame, int slice) {
		if (imp==null) {IssueLog.log("panel stack can do nothing with a null image"); return null;}
		try{
			PanelListElement createChannelPanelEntry = createChannelPanelEntry(imp, channel, frame, slice);
			add(
					createChannelPanelEntry );
			return createChannelPanelEntry ;
			}
	
		
		catch (Throwable t) {IssueLog.logT(t); return null;}
	}
	
	
	 /**Adds a merge panel with the given frame and slice to the list 
	 and returns it*/
	public PanelListElement addMergePanel(MultiChannelImage imp, int frame, int slice) {
		if (imp==null) {IssueLog.log("panel stack can do nothing with a null image"); return null;}
		try{
			PanelListElement createMergePanelEntry = createMergePanelEntry(imp, frame, slice);
			add(createMergePanelEntry);	
			return createMergePanelEntry;
			} catch (Throwable t) {IssueLog.logT(t); return null;}
	}
	
	
	

	
	 /**Adds creates channel and merge panels for a given frame and slice of the image
	   and add them to this list*/
	 public void addChannelPanelsAndMerge(MultiChannelImage imp, int frame, int slice) {
		 
		int chanNum=imp.nChannels();
		boolean singleChannel=   chanNum==1;
		if(this.getChannelUseInstructions().isFrameExcluded(frame)) return;
		if(this.getChannelUseInstructions().isSliceExcluded(slice)) return;
		 
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
	public void addAllCandF(MultiChannelImage imp) {
		if (imp==null)
			{IssueLog.log("Source Multichannel display is missing. Cannot add channel panels to list");
			return;
			}
		int frames= imp.nFrames();
		int slices= imp.nSlices();
		for(int f=1; f<=frames; f++) {
			for(int s=1; s<=slices; s++) {
				addChannelPanelsAndMerge(imp, f, s) ;
			}
		}
		
		
		try {
			// sorts the list so that the set channel order is followed. Also ensures that slices and frames are in order
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
	   Needed if there is a cropping rectangle added to the list.
	   Although a programmer can add a rectangle here for an additional crop operation
	   the user */
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
	public void updateAllPanelsWithImage(MultiChannelImage imp) {
		for(PanelListElement panelp: getPanels()) try {
			updateImageForPanel(imp, panelp);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
	/**uses the input multichannel to update the image panel with the given name.
	  Does NOT reset the channel entries of each list element so wont
	  reflect changes in channel order.*/
	public void updateAllPanelsWithImage(MultiChannelImage imp, String realChannelName) {
		for(PanelListElement panelp: getPanels()) try {
			if (panelp.getChannelEntryList().hasChannelWithRealName(realChannelName))this.updateImageForPanel(imp, panelp);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
	
	/**Resets the channel entries for all. This is needed if 
	 * the channel order, channel names or colors of the
	 * source image have been changed.*/
	public void resetChannelEntriesForAll(MultiChannelImage  imp) {
		for(PanelListElement entry: this.getPanels()) {
			if (this.channelUpdateMode==true) this.updateChannelEntries(imp, entry);
			else this.resetChannelEntriesForPanel(imp, entry);
			
			updateChannelColors(imp, entry);
		}
	}

	
	
	private void resetChannelEntriesForPanel(MultiChannelImage  impw, PanelListElement entry) {
		//ImagePlusWrapper impw = new ImagePlusWrapper(imp);
		
		if (entry.designation+0==PanelListElement.MERGE_IMAGE_PANEL+0) {
			this.setUpChannelEntryForMerge(impw, entry, entry.targetFrameNumber, entry.targetSlideNumber);
		}
		else {
			setUpChannelEntriesForPanel(impw, entry, entry.targetChannelNumber, entry.targetFrameNumber, entry.targetSlideNumber);
			
		}
		entry.purgeDuplicateChannelEntries();
		if (entry.getChannelLabelDisplay()!=null) entry.getChannelLabelDisplay().setParaGraphToChannels();
	}
	
	
	/**updates the channel entry object. This changes the label and color of the entries but does
	 * not replace them with fresh objects*/
	public void updateChannelEntries(MultiChannelImage impw, PanelListElement entry) {
		ArrayList<ChannelEntry> origEntry = impw.getChannelEntriesInOrder();
		
		for(ChannelEntry c: entry.getChannelEntries()) {
			ChannelEntry cnew = findEquivalent(c, origEntry);
			if (cnew!=null) c.updateFrom(cnew);
			if (entry.getChannelLabelDisplay()!=null) {
				entry.getChannelLabelDisplay().setParaGraphToChannels();
				
				};
		}
		
	}
	
	/**updates the channel entry object. This changes the label and color of the entries but does
	 * not replace them with fresh objects*/
	public void updateChannelColors(MultiChannelImage impw, PanelListElement entry) {
		ArrayList<ChannelEntry> origEntry = impw.getChannelEntriesInOrder();
		
		for(ChannelEntry c: entry.getChannelEntries()) {
			ChannelEntry cnew = findEquivalent(c, origEntry);
			if (cnew!=null) c.setColor(cnew.getColor());
		}
		
	}
	
	/**sets up the properties and fields for the split channel panel's channel entries. called when entries are
	  created or updated*/
	public void setUpChannelEntriesForPanel(MultiChannelImage impw, PanelListElement entry, int channel, int frame, int slice) {
		
		entry.getChannelEntries().clear();
		entry.originalIndices.clear();
		
		if (!impw.containsImage()) {
				IssueLog.log("Cannot complete channel panel without data. No image OR empty ImageStack OR no imagestack");
				return ;
				}
		
		String title=impw.getSliceName(channel, slice, frame);
		
		if (impw.containsSplitedChannels()) {	
			
				entry.addChannelEntry(impw.getSliceChannelEntry(channel, slice, frame));
				
				/**if a second channel is not be added to the panel*/
				int eachMergeChannel = this.getChannelUseInstructions().eachMergeChannel;
				if (eachMergeChannel>ChannelUseInstructions.NONE_SELECTED && eachMergeChannel<=impw.nChannels()) {		
					entry.addChannelEntry(impw.getSliceChannelEntry(eachMergeChannel, slice, frame));
				} 
			
		} else {
			/**if the target image channels are not truly split*/
			entry.addChannelDescriptor(title, Color.BLACK, channel, entry.innitialStackIndex);	
		}
	}
	
	
	
	
	/**sets up the properties and fields of what would be the merged panel's channel entries*/
	public void setUpChannelEntryForMerge(MultiChannelImage ipw, PanelListElement entry, int frame, int slice) {
		
		int nChannel=ipw.nChannels();
	
		entry.getChannelEntries().clear();
		entry.originalIndices.clear();
		
		String[] eachChanNameH=new String[nChannel]; 
	
		Color[] eachColor=new Color[nChannel];
		
		for (int c=1; c<=ipw.nChannels(); c++) {
			eachChanNameH[c-1]=ipw.getGenericChannelName(c);
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
	public void updateImageForPanel(MultiChannelImage impw,
			PanelListElement entry){
		
	
		entry.setImageObjectWrapped(impw.getChannelMerger().generateMergedRGB(entry, this.getChannelUseInstructions().channelColorMode));
		
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
	public PanelListElement createMergePanelEntry(MultiChannelImage impw, int frame, int slice) {
		//ImageTypeWrapper impw = new ImageTypeWrapper(imp);
		
		PanelListElement entry=createEntry();
		if (!impw.containsImage()) {IssueLog.log("Cannot create meged image for null or empty entry"); return entry;}
		entry.designation=2;
		entry.originalImageName=impw.getTitle();
		entry.originalImageID=impw.getID();
		entry.targetChannelNumber=1;//not truly relevant as all the channels are included but the number must be set
		entry.innitialStackIndex=impw.getStackIndex(1, slice, frame);
		entry.targetFrameNumber=frame;
		entry.targetSlideNumber=slice;
		
		
		
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
	public PanelListElement createChannelPanelEntry( MultiChannelImage impw, int channel, int frame, int slice) {
		PanelListElement entry=createEntry();
		// MultiChannelWrapper impw=new ImageTypeWrapper(imp);
		entry.setChannelFrameSlice(channel, frame, slice);
		entry.setSourceImage(impw);
		entry.innitialStackIndex=impw.getStackIndex(channel, slice, frame);
		
		setUpChannelEntriesForPanel(impw, entry, channel, frame, slice);
	
		updateImageForPanel(impw, entry);
		
		return entry;
	}
	

	public int getSize() {
		
		return getPanels().size();
		}



	
	
	/**Sets and internal cropping rectangle*/
	@Deprecated
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
		stack.setPixelDensityRatio(getPixelDensityRatio());
	}
	
	public void givePartialSettingsTo(PanelList stack) {
		stack.setScaleBilinear(getScaleBilinear());
		stack.setPixelDensityRatio(getPixelDensityRatio());
		getChannelUseInstructions().makePartialMatching(stack.getChannelUseInstructions());
		
	}
	
	
/**gets the level of bilinear scale to use. 1 for no scale*/
	@Deprecated
	public double getScaleBilinear() {
		return scaleBilinear;
	}

	/**sets the level of bilinear scale to use. 1 for no scale*/
	@Deprecated
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

	

	public void setChannelUstInstructions(ChannelUseInstructions instructions) {
		this.instructions = instructions;
	}
	
	/**returns all the channel labels*/
	public ArrayList<ChannelLabelTextGraphic> getChannelLabels() {
		return getChannelLabelsFrom(getPanels());
	}
	private ArrayList<ChannelLabelTextGraphic> getChannelLabelsFrom(ArrayList<PanelListElement> eachPanel) {
		ArrayList<ChannelLabelTextGraphic> out=new ArrayList<ChannelLabelTextGraphic>();
		for(PanelListElement panel: eachPanel) {
			if (panel.getChannelLabelDisplay() instanceof ChannelLabelTextGraphic) out.add(panel.getChannelLabelDisplay());
		}
		return out;
	}
	
	/**gets or creates a merge panel. normally invoked by menu item to add a panel
	  that is not part of the default list*/
	public PanelListElement getOrCreateMergePanel( MultiChannelImage mw, int slice, int frame) {
		PanelListElement panel = getMergePanelFor( slice, frame);
		if (panel==null)addMergePanel(mw,  frame, slice);
		panel = getMergePanelFor( slice, frame);
		 return panel;
	}
	/**gets or creates a channel panel. normally invoked by menu item to add a panel
	  that is not part of the default listl*/
	public PanelListElement getOrCreateChannelPanel(MultiChannelImage mw, int channel, int slice, int frame) {
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
	
	/**Given a channel, frame and slice location, returns that panel at that location
	 if one value in the argument (c.frame for example) is set to -1, the frame is unspecified and 
	 every panel with that frame will be returned*/
	public 	ArrayList<PanelListElement> getPanelsWith(CSFLocation c) {
		ArrayList<PanelListElement> output = new ArrayList<PanelListElement>();
		for(PanelListElement p:panels) {
			if(c.channel>-1 &&p.targetChannelNumber!=c.channel) continue;
			if(c.frame>-1 &&p.targetFrameNumber!=c.frame) continue;
			if(c.slice>-1 &&p.targetSlideNumber!=c.slice) continue;
			output.add(p);
		}
		return output;
	}
	
/**gets the ratio that determines the panel's pixel density*/
	public double getPixelDensityRatio() {
		return pixelDensityRatio;
	}

	public void setPixelDensityRatio(double panelLevelScale) {
		pixelDensityRatio=panelLevelScale;
	}
	
	@Deprecated
	public void setCropperAngle(double angle) {
		cropAngle=angle;
		
	}
	
	/**swaps the locations of two elements in the list. Also swaps the locations of the
	 * objects which show the image to the user (which is visible)*/
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
		}
		
		return null;
	}
	
	
	/**A comparator for sorting the panel lists. takes into account the channel
	  order from the channel use instructions*/
	class PanelCompare implements Comparator<PanelListElement>  {

		@Override
		public int compare(PanelListElement o1, PanelListElement o2) {
			try {
				if (o1.targetFrameNumber>o2.targetFrameNumber) return 1;
				if (o2.targetFrameNumber>o1.targetFrameNumber) return -1;
				if (o1.targetSlideNumber>o2.targetSlideNumber) return 1;
				if (o2.targetSlideNumber>o1.targetSlideNumber) return -1;
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
				int c1 = instructions.getChanPanelReorder().index(o1.targetChannelNumber);
				int c2 = instructions.getChanPanelReorder().index(o2.targetChannelNumber);
				if (c1>c2) return 1;
				if (c2>c1) return -1;
			} catch (Exception e) {
				IssueLog.logT(e);
			}
			
			return 0;
		}
		
	}

	/**Called after the user changes the z section that the user is */
	public void setupViewLocation(CSFLocation d) {
		d.channel=CSFLocation.MERGE_SELECTED;//allows for merge
		if(this.getChannelUseInstructions().getFrameUseInstructions().selectsSingle()) getChannelUseInstructions().getFrameUseInstructions().setupLocation(d);
		if(this.getChannelUseInstructions().getSliceUseInstructions().selectsSingle()) getChannelUseInstructions().getSliceUseInstructions().setupLocation(d);
	}
	
	
	
}
