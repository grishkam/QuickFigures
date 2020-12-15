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
 * Date Modified: Dec 8, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
package graphicalObjects_FigureSpecific;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import channelMerging.CSFLocation;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelImage;
import fLexibleUIKit.MenuItemMethod;
import figureEditDialogs.ChannelSliceAndFrameSelectionDialog;
import genericMontageKit.PanelList;
import genericMontageKit.PanelListElement;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import logging.IssueLog;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.Edit;
import undo.EditListener;
import undo.PreprocessChangeUndo;
import undo.UndoAbleEditForRemoveItem;
import undo.UndoMoveItems;
import undo.UndoScalingAndRotation;
import utilityClassesForObjects.RectangleEdges;

/**handles the adding and removing of channel display panels
  to and from figures.
 */
public class PanelManager implements Serializable, EditListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int NORMAL_CHANNEL_USE=0, ADVANCED_CHANNEL_USE = 1;
	
	int channelUseMode=NORMAL_CHANNEL_USE;
	
	private GraphicLayer layer;
	private PanelList panelList;
	private MultichannelDisplayLayer display;
	
	private transient MultiChannelImage multi;
	private int defaultFrameWidth;

	public PanelManager(MultichannelDisplayLayer multichannelImageDisplay, PanelList stack,
			GraphicLayer multichannelImageDisplay2) {
		this.display=multichannelImageDisplay;
		this.setPanelList(stack);
		this.layer=multichannelImageDisplay2;
		if(layer==null) layer=multichannelImageDisplay;
		setMultiChannelWrapper(multichannelImageDisplay.getMultiChannelImage());
		
	}
	
	/**Creates a panel Graphic for the panel, adds that panel to the layer*/
	protected ImagePanelGraphic generatePanelGraphicFor(PanelListElement panel) {
		if (panel==null) return null;
		ImagePanelGraphic panelgraphic = new ImagePanelGraphic();
		panelgraphic.setSourcePanel(panel);
			
			panelgraphic.setScaleInfo(panel.getDisplayScaleInfo());//sets the scale info
			panelgraphic.setRelativeScale(getPanelLevelScale());
			panelgraphic.setFrameWidthH(getDefaultFrameWidth());
			//logTime("done setting image and scale");
			panelgraphic.setEmbed(true);
			//logTime("done embedding image and scale");
			panel.setImageDisplayObject(panelgraphic);
			String name=panel.getName();
			if(name==null) name="";
			
			
			try {
				
				String indexname="";
				if (display.getMultiChannelImage().nFrames()>1)
					indexname+="t:"+panel.targetFrameNumber+" "; 
				if (display.getMultiChannelImage().nSlices()>1)
					indexname+="s:"+panel.targetSliceNumber;
				
				if (panel.isTheMerge()&&indexname.length()>1) {
					name="Merge "+indexname;
				}
				if (!panel.isTheMerge()&&"".equals(name)) {
					name="c:"+panel.targetChannelNumber+" "+indexname;
				}
			} catch (Throwable t) {}
			
			panelgraphic.setName(name);
			
			
			layer.add(panelgraphic);
			
			panel.createChanSwapHandles(panelgraphic).setDisplayLayer(display);
			 
			//
			
			return panelgraphic;
	}
	
	public ImagePanelGraphic getImagePanelFor(PanelListElement panel) {
		if (panel==null) return null;
		if (panel.getImageDisplayObject() instanceof ImagePanelGraphic) {
			return (ImagePanelGraphic) panel.getImageDisplayObject();
		}
		return null;
	}
	
	/**returns the panel list element that contins the given image*/
	public PanelListElement  getListElementFor(ImagePanelGraphic image) {
		if (image==null) return null;
		if (panelList==null) return null;
		for(PanelListElement panel: panelList.getPanels()) {
			if (panel.getImageDisplayObject() ==image) {
				return panel;
			}
		}
		return null;
	}
	
	/**returns the pixel density ratio that has been set of the panel list*/
	public double getPanelLevelScale() {
		return panelList.getPixelDensityRatio();
	}
	
	/**sets the pixel density ratio for the panel list*/
	public void setPanelLevelScale(double panelLevelScale) {
		panelList.setPixelDensityRatio(panelLevelScale);
	}
	

	/**generates the panel graphics and adds then all to the parent layer*/
	@MenuItemMethod(menuActionCommand = "paneloptions2", menuText = "Generate New Panels", subMenuName="Image Panels")
	public void generatePanelGraphics() {
		getPanelList().addAllCandF(display.getMultiChannelImage());
		generatePanelGraphicsFor(getPanelList());
	}
	
	/**Creates a panelGraphic for the list elements and adds them
	 to the current layer for this panel Manager. Also returns those items
	 * @return */
	public ArrayList<ImagePanelGraphic> generatePanelGraphicsFor(PanelList p) {
		ArrayList<ImagePanelGraphic> items=new ArrayList<ImagePanelGraphic> ();
			if (p!=null) for(PanelListElement panel: p.getPanels()) {
			items.add(generatePanelGraphicFor(panel));
		}
			return items;
	}
	
	/**empties the panel list and removes all the objects from the layer
	*/
	@MenuItemMethod(menuActionCommand = "panelgone", menuText = "Eliminate Panels", subMenuName="Image Panels")
	public CombinedEdit eliminatePanels() {
		return eliminatePanels(getPanelList());
	}
	public CombinedEdit eliminatePanels(PanelList stack) {
		if (stack==null) return null;
		CombinedEdit output = new CombinedEdit();
		ArrayList<ImagePanelGraphic> arr = stack.getPanelGraphics();
		for(ImagePanelGraphic g:arr) {
			output.addEditToList(
			Edit.removeItem(layer,g));
			if(g!=null&&g.getScaleBar()!=null) {
				output.addEditToList(
						Edit.removeItem(g.getScaleBar().getParentLayer(),g.getScaleBar()));
			}
		}
		stack.eliminateAllPanels();
		return null; //undo is incomplete and unfinished
 	}
	
	
	/**removes all objects associated with the panels including imagePanelGraphics,
		channelLabels and scale bars. returns an undo for the action*/
	public CombinedEdit removeDisplayObjectsForAll() {
		CombinedEdit output = new CombinedEdit();
		if (panelList==null) return  output;
		
		PanelList arr = panelList;
		
		for(PanelListElement  g:arr.getPanels()) {
			 output.addEditToList(
					removeDisplayObjectsFor(g)
			);
		}
		return output;
	}
	
	/**removes the display objects for the given panel list element from the layer.
	  */
	public CombinedEdit removeDisplayObjectsFor(PanelListElement g) {
		CombinedEdit itemsTaken=new CombinedEdit();
		
			itemsTaken.addEditToList(new UndoAbleEditForRemoveItem(layer, g.getPanelGraphic()));
			layer.remove(g.getPanelGraphic()); 
			
		BarGraphic bar = g.getPanelGraphic().getScaleBar();
		if (bar!=null) {
			itemsTaken.addEditToList(new UndoAbleEditForRemoveItem(layer, bar));
			layer.remove(bar); 
			if (layer.getParentLayer()!=null)
				layer.getParentLayer().remove(bar);
			// itemsTaken.add(bar);
		}
		
		itemsTaken.addEditToList(new UndoAbleEditForRemoveItem(layer, g.getChannelLabelDisplay()));
		layer.remove(g.getChannelLabelDisplay());
		
		 
		 return itemsTaken;
	}
	
	
	/**returns a channel panel at the given slice and frame.
	  If the list does not already contain such a panel, this will create one*/
	public ArrayList<PanelListElement> generateManyChannelPanels( int channel) {
		ArrayList<PanelListElement> output=new ArrayList<PanelListElement>();
		
		int frames= this.getMultiChannelWrapper().nFrames();
		int slices= this.getMultiChannelWrapper().nSlices();
		for(int f=1; f<=frames; f++) {
			for(int s=1; s<=slices; s++) {
				if(this.getChannelUseInstructions().isFrameExcluded(f)) continue;
				if(this.getChannelUseInstructions().isSliceExcluded(s)) continue;
		output.add( generateSingleChannelPanel(getPanelList(),channel, s, f));
			}
			}
		return output;
	}
	
	
	/**
	 * @return
	 */
	public ChannelUseInstructions getChannelUseInstructions() {
		return this.getPanelList().getChannelUseInstructions();
	}

	/**returns a channel panel at the given slice and frame.
	  If the list does not already contain such a panel, this will create one*/
	private PanelListElement generateSingleChannelPanel(PanelList stack, int channel, int slice, int frame) {
		PanelListElement panel = stack.getOrCreateChannelPanel(getMultiChannelWrapper(),channel, slice, frame);
		
		 if (panel!=null)this.generatePanelGraphicFor(panel);
		 else IssueLog.log("null for requested panel"); 
		return panel;
	}
	
	/**returns a panel containing a merged image at the given slice and frame.
	  If the list does not already contain such a panel, this will create one*/
	private PanelListElement generateSingleMergePanel(PanelList stack, int slice, int frame) {
		PanelListElement panel =stack.getOrCreateMergePanel( display.getMultiChannelImage(), slice, frame);
	
		 if (panel!=null)this.generatePanelGraphicFor(panel);
		 else IssueLog.log("null for requested panel"); 
		 return panel;
	}
	
	/**Displays a dialog then adds a merge panel to the figure*/
	@MenuItemMethod(menuActionCommand = "1merge", menuText = "Create 1 Merge Panel", subMenuName="Image Panels")
	public void addSingleMergePanel(PanelList stack) {
		ChannelSliceAndFrameSelectionDialog dia = new ChannelSliceAndFrameSelectionDialog(1,1,1,display.getMultiChannelImage());
		dia.show2DimensionDialog();
		
		generateSingleMergePanel(stack, dia.getSlice(),dia.getFrame());
	}
	
	
	/**Displays a dialog then adds a channel panel to the figure*/
	@MenuItemMethod(menuActionCommand = "1chan", menuText = "Create New Panel", subMenuName="Image Panels")
	public PanelListElement addSingleChannelPanel(PanelList stack) {
		ChannelSliceAndFrameSelectionDialog dia = new ChannelSliceAndFrameSelectionDialog(1,1,1, display.getMultiChannelImage());
		dia.show3DimensionDialog();
		boolean b = dia.getChannel()==0;
		if (b) { 
			return generateSingleMergePanel(stack, dia.getSlice(),dia.getFrame());} else
		return generateSingleChannelPanel(stack, dia.getChannel(), dia.getSlice(),dia.getFrame());
	}
	
	
	/**getter method for the initial frame width used for newly created image panels*/
	public int getDefaultFrameWidth() {
		return defaultFrameWidth;
	}
	public void setDefaultFrameWidth(int defaultFrameWidth) {
		this.defaultFrameWidth = defaultFrameWidth;
	}

	/**getter method for the panel list*/
	public PanelList getPanelList() {
		return panelList;
	}
	/**setter method for the panel list*/
	public void setPanelList(PanelList stack) {
		this.panelList = stack;
	}

	/**getter method for multi-channel*/
	public MultiChannelImage getMultiChannelWrapper() {
		return multi;
	}
	/**setter method for multi-channel*/
	 void setMultiChannelWrapper(MultiChannelImage multi) {
		this.multi = multi;
	}
	
	/**Based on the current source image, channel use instructions
	  and other options, updates the ImagePanel objects with 
	   the buffered images or appropriate color/contrast
	    scale information */
	public synchronized void updatePanels() {
		MultiChannelImage impw =multi;
		getPanelList().resetChannelEntriesForAll(impw);
		getPanelList().updateAllPanelsWithImage(impw);
	}
	
	/**updates the panels that include the given channel from the source
	  multichannel stack. This is primarily used after display ranges are changed.
	  Also after cropping, or flipping of the source image. And used 
	  after creation of the image panels.
	 * */
	public synchronized void updatePanelsWithChannel(String realChannelName) {
		getPanelList().updateAllPanelsWithImage(multi, realChannelName);
	}
	
	public MultichannelDisplayLayer getDisplay() {
		return display;
	}
	
	
	/**Given an array of panels, returns them in an altered order. Ones with 
	 * display panels in the first rectangle go first, second to the second and so on.
	 * All leftover panels are put in the end*/
	public static ArrayList<PanelListElement> getPanelsInLayoutOrder(ArrayList<PanelListElement> input, Rectangle2D[] r) {
		ArrayList<PanelListElement> output = new ArrayList<PanelListElement>();
		
		
		for(int i=0; i<r.length; i++) {
				Rectangle2D rect = r[i];
				
				/**Checks whether each element is inside the rectangle, if so adds it to the list if not already in it*/
				for(PanelListElement panel1: input) {
					if (output.contains(panel1)) continue;
					
					ImagePanelGraphic item = panel1. getPanelGraphic();
					if(item.doesIntersect(rect)) {
						output.add(panel1); 
						continue;}
					if(item.isInside(rect)) {
						output.add(panel1); continue;
						}
				}
				
		}
		
		/**adds all remaining panels that have not yet been added to the output*/
		for(PanelListElement panel1: input) {
			if (output.contains(panel1)) continue;
			
			 {output.add(panel1);}
		}
		
		
		return output;
		
	}

	
	/**When given a single panel list element, finds an empty spot in the layout
	   and puts the panel in that location. If no empty spot exists, it creates one*/
	public void putSingleElementOntoGrid(PanelListElement p, boolean addIfNeeded) {
		BasicMontageLayout layout = getLayout();
		int index = layout.getEditor().indexOfFirstEmptyPanel(layout, 1, 0);
		
		if ((index>layout.nPanels()||index==0)&&addIfNeeded) {
			if (layout.rowmajor)
			layout.getEditor().addCols(layout, 1);
			else 
				layout.getEditor().addRows(layout, 1);
			
		}
		
		if (index>0&&index<=layout.nPanels()) {
			this.getDisplay().getSetter().putDisplayObjectForPanelInRect(p, layout.getPanel(index));
			}
	}
	
	
	/**returns the layout that is used for the given layer*/
	public static MontageLayoutGraphic getGridLayout(GraphicLayer layer) {
		if (layer==null) return null;
		ArrayList<ZoomableGraphic> arr = layer.getItemArray();
		if (arr==null) return null;
		for(ZoomableGraphic a:arr) {
			if (a==null) continue;
			if (a instanceof MontageLayoutGraphic) {
				MontageLayoutGraphic m=(MontageLayoutGraphic) a;
				m.generateStandardImageWrapper();
				return m;
						};
		}
		/**recursively looks for layouts in the parent layers. Added on 11/8/20. If the user moves the layout to a layer above*/
		if (layer.getParentLayer()!=null) 
			return getGridLayout(layer.getParentLayer());
		return null;
	}
	
	/**returns the graphical object meant for displaying and editing (via handles) of the figure layout*/
	public MontageLayoutGraphic getGridLayout() {
		MontageLayoutGraphic layoutGraphic=getGridLayout(layer);
		if (layoutGraphic==null &&layer.getParentLayer()!=null) 
				layoutGraphic=getGridLayout(layer.getParentLayer());
		if (layoutGraphic==null) return null;
		return layoutGraphic;
	}
	
	/**Returns the figure layout*/
	public BasicMontageLayout getLayout() {
		MontageLayoutGraphic layoutGraphic=getGridLayout();
		if (layoutGraphic==null) return null;
		return layoutGraphic.getPanelLayout();
	}

	/**Updates the Image display canvas*/
	public void updateDisplay() {
		display.getGraphicSetContainer().updateDisplay();
	}

	/**returns that layer that this panel manager adds/removes panels to and from*/
	public GraphicLayer getLayer() {
		return layer;
	}
	
	/**alters the pixel density of the figure.
	 */
	public CombinedEdit changePPI(double newppi) {
		ImagePanelGraphic panel = getPanelList().getPanels().get(0).getPanelGraphic();
		double ppi = panel.getQuickfiguresPPI();
		double newPanelScale=panel.getScale()*ppi/newppi;
		double newScale=getDisplay().getPreprocessScale()*newppi/ppi;
		
		CombinedEdit output = new CombinedEdit();
		
		output.addEditToList(
				imposePanelLevelScale(newPanelScale));
		
		output.addEditToList(new PreprocessChangeUndo(getDisplay()));
		getDisplay().setPreprocessScale(newScale);
		
		
		updatePanels();
		output.addEditListener(this);
		
		output.establishFinalState();
		return output;
	}

	/**Changed the panel level scale and returns a CompoundEdit edit*/
	protected CombinedEdit imposePanelLevelScale(double newPanelScale) {
		CombinedEdit output=new CombinedEdit();
		
		for(PanelListElement panel2: getPanelList().getPanels()) {
		
			ImagePanelGraphic panelGraphic = panel2.getPanelGraphic();
			
			output.addEditToList(new UndoScalingAndRotation(panelGraphic));
			
			panelGraphic.setLocationType(RectangleEdges.UPPER_LEFT);
			panelGraphic.setRelativeScale(newPanelScale);
		}
		output.addEditToList(new PanelManagerScaleUndo(this));
		this.setPanelLevelScale(newPanelScale);
		output.establishFinalState();
		return output;
	}
	

	

	
	/**sets the initial view location of the image
	   to match the selected slice and frame*/
	public void setupViewLocation() {
		CSFLocation out =getDisplay().getSlot().getDisplaySlice();
		if(out==null) {
			out=new CSFLocation();
			getDisplay().getSlot().setDisplaySlice(out);
		}
		this.getPanelList().setupViewLocation(out);
	}

	/**returns true if the panel manager can switch the panel locations (slice, frame or channel) from one setting to another
	  this will be the case if there are panels with location 1 but none with location 2*/
	boolean canReplace(CSFLocation f1, CSFLocation f2) {
		if (this.getMultiChannelWrapper().nSlices()==1&&this.getMultiChannelWrapper().nFrames()==1) return false;
		if (getMultiChannelWrapper().nFrames()<f2.frame) return false;
		if (getMultiChannelWrapper().nSlices()<f2.slice) return false;
		
		int s1 = getPanelList().getPanelsWith(f1).size();
		int s2 = getPanelList().getPanelsWith(f2).size();
		if(s2==0&&s1>0) return true;
		return false;
	}
	/**when given two stack location objects, this edits the panels
	 such that every panel with the first location is switched to the second.
	 if there are already panels in the second location this will do nothing and return false*/
	public boolean performReplaceOfIndex(CSFLocation f1, CSFLocation f2) {
		if(!canReplace(f1, f2)) return false;
		ArrayList<PanelListElement> list = this.getPanelList().getPanelsWith(f1);
		boolean bf = getPanelList().getChannelUseInstructions().getFrameUseInstructions().replaceIndex( f1, f2);
		boolean bs= getPanelList().getChannelUseInstructions().getSliceUseInstructions().replaceIndex( f1, f2);
		if (!bs&&!bf) return false;
		for(PanelListElement l: list) {
			l.changeStackLocation(f2);
		}
		this.updatePanels();
		return true;
	}
	
	/**returns the panels that actually have images in the layer*/
	public ArrayList<PanelListElement> listPanelsWithVisibleGraphic() {
		ArrayList<PanelListElement> output=new ArrayList<PanelListElement>();
		for(PanelListElement panel: panelList.getPanels()) {
			ZoomableGraphic z = panel.getImageDisplayObject();
			if (this.getLayer().hasItem(z))
				output.add(panel);
		}
		return output;
	}
	
	/**Alters the channel order, also swaps the channel panel locations.
	  Channel labels will also be updated. However*/
	public void performChannelSwap(int i, int i2) {
		this.getPanelList().getChannelUseInstructions().onChannelSwap(i, i2);
		reorderImagePanels();
		this.updatePanels();
		this.updateDisplay();
	}
	
	/**reorders the image panels and their display items, 
	 * assuming that changes to channel order have been made, the new order will be different and channel
	 * panels will move*/
private AbstractUndoableEdit2 reorderImagePanels() {
		ArrayList<PanelListElement> list = listPanelsWithVisibleGraphic();
		CombinedEdit undo = new CombinedEdit();
		ArrayList<Point2D> locations=new ArrayList<Point2D>();
		
		for(PanelListElement panel:list) {
			ImagePanelGraphic graphic = panel.getImageDisplayObject();
			undo.addEdit(new UndoMoveItems(graphic));
			locations.add(graphic.getLocationUpperLeft());
		}
		this.getPanelList().sortThese(list);
		this.getPanelList().sortPanels();
		for(int i=0; i<locations.size(); i++) {
			list.get(i).getImageDisplayObject().setLocationUpperLeft(locations.get(i));
		}
		undo.establishFinalState();
		return undo;
	}
	
	/**returns true if the panel manager is using only a subset of the Z slices or T frames*/
	public boolean selectsSlicesOrFrames() {
		return (
				!getPanelList().getChannelUseInstructions().getFrameUseInstructions().selectsAll()
				||
				!getPanelList().getChannelUseInstructions().getSliceUseInstructions().selectsAll()
				)
				;
	}


	/**An undo that can be added to a compound edit. does not actually undo anything but
	  only updates the panels to display the edit before it in the compound edit*/
	@Override
	public void afterEdit() {
		this.updatePanels();
		
	}

	/**
	Sets whether the given channel is visible in the merged image.
	If successful, returns true.
	If the merged image is down to one channel, displays a message and returns false.
	 */
	public boolean setMergeExcluded(int originalChannelIndex, boolean b, boolean showWarning) {
		ChannelUseInstructions ins = this.getPanelList().getChannelUseInstructions();
		
		/**counts the number of channel that are in the merge*/
		int totalChannels=0;
		for(int i=1; i<=getMultiChannelWrapper().nChannels(); i++) {
			if (!ins.isMergeExcluded(i)) totalChannels++;
		}
		if (totalChannels==1 && b && !ins.isMergeExcluded(originalChannelIndex)) {
			if (showWarning)IssueLog.showMessage("At least one channel must be present in the image");
			return false;
		}
		
		ins.setMergeExcluded(originalChannelIndex, b);
		this.updatePanels();
		this.updateDisplay();
		return true;
	}

	/**
	  Sets the channel use mode
	 */
	public void setChannelUseMode(int a) {
		this.channelUseMode=a;
		if (channelUseMode==ADVANCED_CHANNEL_USE) this.getPanelList().channelUpdateMode=true; else
			getPanelList().channelUpdateMode=false;
	}
	
	public boolean isAdvancedChannelUse() {
		return channelUseMode==ADVANCED_CHANNEL_USE;
	}
	
	/**An undo for changes to the panel level scale (what determines pixel density of the panels)*/
	class PanelManagerScaleUndo extends AbstractUndoableEdit2 {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		double iScale;
		double fScale;
		private PanelManager pm;
		
		public PanelManagerScaleUndo(PanelManager pm) {
			this.pm=pm;
			iScale=pm.getPanelLevelScale();
		}
		public void undo() {pm.setPanelLevelScale(iScale);}
		public void redo() {pm.setPanelLevelScale(fScale);}
		public void establishFinalState() {
			fScale=pm.getPanelLevelScale();
		}
	}

	/**
	 * 
	 */
	public void setPanelLevelScaleToPanels() {
		for(ImagePanelGraphic p: panelList.getPanelGraphics()) {
			this.setPanelLevelScale(p.getScale());
		}
	}


	

}
