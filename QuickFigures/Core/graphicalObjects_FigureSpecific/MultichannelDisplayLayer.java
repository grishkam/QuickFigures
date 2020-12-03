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
package graphicalObjects_FigureSpecific;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import channelMerging.MultiChannelSlot;
import channelMerging.MultiChannelUpdateListener;
import channelMerging.MultiChannelImage;
import channelMerging.ImageDisplayLayer;
import channelMerging.PreProcessInformation;
import externalToolBar.IconSet;
import fLexibleUIKit.MenuItemMethod;
import genericMontageKit.PanelList;
import genericMontageKit.PanelListElement;
import genericMontageKit.PanelSetter;
import genericMontageKit.SubFigureOrganizer;
import graphicalObjects.GraphicEncoder;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import layersGUI.HasTreeLeafIcon;
import logging.IssueLog;
import menuUtil.HasUniquePopupMenu;
import objectDialogs.GraphicItemOptionsDialog;
import objectDialogs.PanelStackDisplayOptions;
import popupMenusForComplexObjects.MultiChannelImageDisplayPopup;
import undo.CombinedEdit;
import undo.UndoAddManyItem;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.Named;
import utilityClassesForObjects.PointsToFile;
import utilityClassesForObjects.ShowsOptionsDialog;
import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelProperties;
import channelLabels.ChannelLabelTextGraphic;

/**Special layer for showing parts of a specific multi dimensional image */
public class MultichannelDisplayLayer extends GraphicLayerPane implements ZoomableGraphic, ShowsOptionsDialog, ImageDisplayLayer, KnowsParentLayer, Named, HasUniquePopupMenu,  HasTreeLeafIcon, SubFigureOrganizer, MultiChannelUpdateListener,PointsToFile , Serializable{

	/**
	 * 
	 */
	{description= "A layer containing the components of a multichannel image";}
	
private static final long serialVersionUID = 1L;
	

	public MultiChannelSlot slot=null;//the original image is stored within this variable
	protected String name="Image ";
	private boolean laygeneratedPanelsOnGrid;

	PanelSetter setter;
	
	/**Creates a display layer, Argument cannot be null*/
	public MultichannelDisplayLayer(MultiChannelSlot slot) {	
		super("");	
		if(slot==null) return;//slot given would not be null in most circunstances
		this.setSlot(slot);
		slot.setStackDisplayLayer(this);
	}

	/**creates a new panel list for this ImageDisplay layer*/
	public PanelList createPanelList() {
		return new PanelList();
	}
	
	/**creates a copy with all the same traits but no new image initialized.
	  this copy shares the same image slot as this one*/
	public MultichannelDisplayLayer copy() {
		MultichannelDisplayLayer output = new MultichannelDisplayLayer(getSlot()) ;
		output.copyTraitsFrom(this);
		output.setSlot(getSlot());
		return output;
	}
	
	/**creates a copy with all the same traits but no new image initialized*/
	public MultichannelDisplayLayer similar() {
		MultichannelDisplayLayer output = new MultichannelDisplayLayer(getSlot().copy()) ;
		output.copyTraitsFrom(this);
		return output;
	}
	

	
	/**copies the scaling, frame width and label properties from argument*/
	public void copyTraitsFrom(MultichannelDisplayLayer multi) {
		copyTraitsFrom(multi, true);
	}
	
	/**copies the scaling, frame width and label properties from argument*/
	public void copyTraitsFrom(MultichannelDisplayLayer multi, boolean preprocessToo) {
		if (multi==null) return;
		this.getPanelManager().setDefaultFrameWidth(multi.getPanelManager().getDefaultFrameWidth());
		
		this.getPanelManager().setPanelLevelScale(multi.getPanelManager().getPanelLevelScale());
		
		this.getChannelLabelProp().copyOptionsFrom(multi.getChannelLabelProp().copy());
		
		multi.getPanelList().giveSettingsTo(getPanelList());
		
		
		if (preprocessToo)this.setPreprocessScale(multi.getPreprocessScale());
	}
	
	/**changes the settings of this multi-channel display layer to match the argument. */
	public void partialCopyTraitsFrom(MultichannelDisplayLayer multi, boolean doesPreprocess) {
		if(multi==null) return;
		if (doesPreprocess)this.setPreprocessScale(multi.getPreprocessScale());
		this.getChannelLabelProp().copyOptionsFrom(multi.getChannelLabelProp().copy());
		
		if (getPanelManager()!=null&&multi.getPanelManager()!=null)
		{this.getPanelManager().setDefaultFrameWidth(multi.getPanelManager().getDefaultFrameWidth());
		
		this.getPanelManager().setPanelLevelScale(multi.getPanelManager().getPanelLevelScale());
		}
		
		multi.getPanelList().givePartialSettingsTo(getPanelList());
	
		
	}
	
	protected PanelList stack=createPanelList();//PanelStack(); 

	private ChannelLabelManager channelLabelMan;

	private PanelManager panelMan;
	
	/**returns the list of panels that display this Image*/
	public PanelList getPanelList() {
		return stack;
	}
	/**sets the list of panels that display this Image*/
	public void setPanelList(PanelList stack) {
		this.stack = stack;
	}
	
	
	public PanelSetter getSetter() {
		if (setter==null) setter=new  PanelSetter();
		return setter;
	}
	

	
	static boolean showTimes=false;
	

	
	/**returns the bounding box of all the panel graphics in this layer
	 */
	public Rectangle getBoundOfUsedPanels() {
		ArrayList<ImagePanelGraphic> graphi =this.getPanelList(). getPanelGraphics();
		if (graphi.size()==0) return null;
		if (graphi.size()==1) return graphi.get(0).getBounds();
		
		Area a=new Area(graphi.get(0).getBounds());
		for(int i=1; i<graphi.size(); i++) {
			a.add(new Area(graphi.get(i).getBounds()));
		}
		return a.getBounds();
		
	}
	
	
	public String getName() {
		return name;
	}

	
	@Override
	public void setName(String st) {
		if (st.length()>50) {
			name=st.substring(0, 50);
			return;
			}
		name=st;
		
	}

	public MultiChannelImageDisplayPopup getMenuSupplier() {
		return new MultiChannelImageDisplayPopup(this, stack, null);
	}
	

	
	public ChannelLabelProperties getChannelLabelProp() {
		return getChannelLabelManager().getChannelLabelProp();
	}
	
	public ChannelLabelManager getChannelLabelManager() {
		if (channelLabelMan==null) channelLabelMan=new ChannelLabelManager(this, this.getPanelList(), this);
		return channelLabelMan;
	}
	
	/**returns the panel manager. Also creates and initializes the panel manager
	  if needed*/
	public PanelManager getPanelManager() {
		if (panelMan==null) 
			panelMan=new PanelManager( this, this.getPanelList(), this);
		panelMan.setPanelList(this.getPanelList());
		panelMan.setMultiChannelWrapper(getMultiChannelImage());
				
		return panelMan;
	}

	
	public String toString() {
		return this.getName();
	}
	
	/**returns the name of this image*/
	public String getShortName() {
		return this.getName().split(";")[0];
	}
	
	



/**gets the Panel that one needs to take a scale bar from
 * or put a scale bar into when one recreates the all the panels*/
	ImagePanelGraphic getDefaultPanelForScaleBar() {
		PanelListElement mp = getPanelList().getMergePanelFor( 1,1);
		ImagePanelGraphic MergePanel = null;
		if(mp!=null) MergePanel =(ImagePanelGraphic) mp.getImageDisplayObject();
		
		ArrayList<ImagePanelGraphic> allpan = this.getPanelList().getPanelGraphics();
		
		
		ImagePanelGraphic lastPanel ;
			if (allpan.size()==0)lastPanel = null; else
				lastPanel = allpan.get(allpan.size()-1);
		
		if(MergePanel!=null&& MergePanel.getScaleBar()!=null) return MergePanel;
		if(lastPanel==null||lastPanel.getScaleBar()!=null) return lastPanel;
		
		if (this.getPanelList().getChannelUseInstructions().addsMergePanel())
					return MergePanel;
					else return lastPanel;
	}
	
	ImagePanelGraphic getAnyPanelWithScaleBar() {
		ArrayList<ImagePanelGraphic> allpan = this.getPanelList().getPanelGraphics();
		for(ImagePanelGraphic panel: allpan) {
			if(panel==null) continue;
			if(panel.getScaleBar()!=null)
				return panel;
		}
		return getDefaultPanelForScaleBar();
	}

	/**eliminates old panels and recreates new ones*/
	public void eliminateAndRecreate() {
		eliminateAndRecreate(true, true, true);
	}
	/**eliminates old panels and recreates new ones*/
	public void eliminateAndRecreate(boolean redoDimensions, boolean expandDimensions, boolean labels) {
		CombinedEdit output = new CombinedEdit();
		output.addEditToList(
		eliminateChanLabels()); 
		
		ImagePanelGraphic merpan =getAnyPanelWithScaleBar() ;
		
		BarGraphic bg=null;
		if (merpan!=null) {//removes the scale bar from its panel
			bg=merpan.getScaleBar();
			merpan.removeLockedItem(bg);
		} 
		
		this.getPanelManager().eliminatePanels();
		
	
		getPanelManager().generatePanelGraphics();
		
		merpan = getDefaultPanelForScaleBar();
		if (merpan!=null) {
			
			this.getParentLayer().add(bg);//adds a scale bar
			
			merpan.addLockedItem(bg);
			if(bg!=null&&bg.getBounds().width>merpan.getBounds().getWidth()) {
				bg.changeBarLengthToFit(merpan);//resizes scale bar if too long
			}
		}
		
		/**Lays the newly generated objects on the grid*/
		if (isLaygeneratedPanelsOnGrid()) 
				{
			layObjectsOnGrid(redoDimensions, expandDimensions);
			};
		
		if (labels) generateChannelLabels();
		
	}





	/**If true, newly generated panels should be placed on a grid*/
	public boolean isLaygeneratedPanelsOnGrid() {
		return laygeneratedPanelsOnGrid;
	}


	/**Set to true if newly generated panels should be placed on a grid*/
	public void setLaygeneratedPanelsOnGrid(boolean laygeneratedPanelsOnGrid) {
		this.laygeneratedPanelsOnGrid = laygeneratedPanelsOnGrid;
	}
	
	
	

	

	
	
	@MenuItemMethod(menuActionCommand = "paneloptions", menuText = "Recreate Panels", subMenuName="Image Panels")
	public void showOptionsThenRegeneratePanelGraphics() {
		showRecreatePanelOptions(false);
		
	}
	

	public void showRecreatePanelOptions(boolean modal) {
		PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(this,this.getPanelList(),null, true);
		dialog.setModal(modal);
		dialog.showDialog();
	}
	


	
	
	/**finds the gird layout of the parent layer and places the items in that panel.
	 employs the panel setter object in order to put the panels into a grid.
	 Does not yet consider other competing that may be in the same grid
	 * */
	private void layObjectsOnGrid(boolean fit, boolean expand) {
		MontageLayoutGraphic grid = this.getPanelManager().getGridLayout();
		
		
		BasicMontageLayout layout =  this.getPanelManager().getLayout();
		
		if (grid==null) return;
		
		if (fit)FigureOrganizingLayerPane.setUpRowAndColsToFit(getPanelManager().getMultiChannelWrapper(), this, grid);//added this line on may 12 to handle recreations more smoothly
		
		
		
		getSetter().layDisplayPanelsOfStackOnLayout( getPanelList(), layout,true, fit);
	
		if (getPanelList().getPanels().size()>0)
			{
			if (fit)
				resizeMontagePanelsToFitImage(grid, this.getPanelList().getPanels().get(0));
			if (expand) {
				GenericMontageEditor me =new GenericMontageEditor();
				
				me.alterPanelWidthAndHeightToFitContents(layout);
			}
			}else {
			IssueLog.log("You have asked to rezize the layout to a panel list with length "+0);
		}
	
	}
	
	

	/**Resizes the montage layout panels to fit the first image. changes 
	 * the standard size not the size of unique rows*/
	public void resizeMontagePanelsToFitImage(MontageLayoutGraphic grid, PanelListElement p) {
		try{
			
			GenericMontageEditor me =new GenericMontageEditor();
			
			if (p.getImageDisplayObject() instanceof LocatedObject2D ) {
				LocatedObject2D l=(LocatedObject2D) p.getImageDisplayObject();
				if (l.getBounds().width==0||l.getBounds().height==0) {
					IssueLog.log("invalid size of "+l);
				}
				me.resizePanels(grid.getPanelLayout(), l.getBounds().width, l.getBounds().height);
				
			}
			
			}catch (Throwable t) {IssueLog.logT(t);}
	}

	
	
	@MenuItemMethod(menuActionCommand = "labeldefaults", menuText = "Set Label Options to Saved", subMenuName="Channel Labels")
	public void setLabalPropertiesToSaved() {
		GraphicEncoder ge=new GraphicEncoder(null);
		ZoomableGraphic zz = ge.readFromUserSelectedFile();
		if (zz instanceof TextGraphic) {
			TextGraphic zt=(TextGraphic) zz;
			for (ChannelLabelTextGraphic lab:getPanelList().getChannelLabels()) {
				lab.copyAttributesFrom(zt);
				lab.setAttachmentPosition(zt.getAttachmentPosition().copy());
			}
		}
	}

	
	
	public synchronized void updatePanels() {
		this.getPanelManager().updatePanels();
	}
	
	public synchronized void updateOnlyPanelsWithChannel(String realChannelName) {
		getPanelManager().updatePanelsWithChannel(realChannelName);
	}
	

	
	public void showStackOptionsDialog() {
		PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(this,this.getPanelList(), null,false);
		dialog.showDialog();
	}
	
/**returns a list of channel names*/
	public static String[] getChannelNames(MultiChannelImage impw) {
		
		String[] out=new String[impw.nChannels()];
		for(int i=1; i<=impw.nChannels(); i++) {
			out[i-1]=impw.getGenericChannelName(i);
		}
		return out;
	}
	
	
	
	
	
	/**When given an object, returns the panel which contains that object
	  or null if none found*/
	public PanelListElement getPanelWithDisplay(Object impg) {
		
		/**the fastest way to find the panel is to check if 
		  the field in panel graphic is set to it*/
		if (impg instanceof ImagePanelGraphic) {
			ImagePanelGraphic graphic=(ImagePanelGraphic)impg;
			PanelListElement panelslice = graphic.getSourcePanel();//.setSourcePanel(this);
			if (panelslice!=null) return panelslice;
		}
		
		for(PanelListElement slice: this.getPanelList().getPanels()) {
			if (impg==slice.getImageDisplayObject()) {
				return slice;
			}
		}
		return null;
	}
	
	/**Given an ImagePanel Graphic, looks in layer l to find the multi-channel display that is associated with the panel*/
	public static MultichannelDisplayLayer findMultiChannelForGraphic(GraphicLayer l, ImagePanelGraphic impg) {
		if (impg==null) return null;
		if (impg.getParentLayer() instanceof MultichannelDisplayLayer) return (MultichannelDisplayLayer) impg.getParentLayer();
		
		ArrayList<GraphicLayer> gs = l.getSubLayers();
		ArrayList<GraphicLayer> displays = new ArraySorter<GraphicLayer>().getThoseOfClass(gs, MultichannelDisplayLayer.class);
		for(ZoomableGraphic d: displays) {
			MultichannelDisplayLayer d2=(MultichannelDisplayLayer) d;
			
			if (d2.hasItem(impg)) return d2;//The simplest way is the search the layer
			PanelListElement potentialpanel = d2. getPanelWithDisplay(impg);
			if (d2.getPanelManager().getPanelList().getPanels().contains( potentialpanel )) return d2;
		}
		
		return null;
	}
	
	
	
	/***/
	
	@Override
	public ArrayList<MultiChannelImage> getAllSourceImages() {
		// TODO Auto-generated method stub
		ArrayList<MultiChannelImage> output = new ArrayList<MultiChannelImage> ();
		output.add(this.getMultiChannelImage());
		return output;
	}


	
	
transient static IconSet i;
	
	public static Icon createImageIcon() {
		if (i==null) i=new IconSet("iconsTree/MultiChannelIcon.jpg");
		return i.getIcon(0);//new ImageIcon(i.getIcon(0));
	}

	@Override
	public Icon getTreeIcon() {
		return createImageIcon();
	}
	
	@Override
	public Icon getTreeIcon(boolean open) {
		
		return getTreeIcon();
	/**
		if (open) return defaultLeaf;// TODO Auto-generated method stub
		return defaultLeaf2;*/
	}
	
	public boolean canAccept(ZoomableGraphic z) {
		if (z instanceof FigureOrganizingLayerPane) return false;
		if (z instanceof GraphicLayer) return false;
		if (z instanceof GraphicLayer)  {
			ArrayList<ZoomableGraphic> listed = ((GraphicLayer) z).getObjectsAndSubLayers();
			for(ZoomableGraphic l: listed) {
				if (!this.canAccept(l)) return false;
			}
		}
		
		return super.canAccept(z);
	}
	
	

	@Override
	public void updatePanelsAndLabelsFromSource() {
		updatePanels() ;
		
	}


	@Override
	public void release() {
	}


	@Override
	public void supress() {
	}
	@Override
	public void onImageUpdated() {
	
		try{
			updatePanels() ;

			if (this.getGraphicSetContainer()!=null)getGraphicSetContainer().updateDisplay();
			if (GraphicItemOptionsDialog.getSetContainer()!=null) GraphicItemOptionsDialog.getSetContainer().updateDisplay();
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}


	/**called after a source images is set for this image display layer*/
	@Override
	public void onImageInitiated() {
		getPanelManager().generatePanelGraphics();
		setName(this.getMultiChannelImage().getTitle());
		notes=getMultiChannelImage().getPath();
		this.description=""+getMultiChannelImage().getPath();
	}

	
	

	/**removes the channel labels and returns an undoable edit*/
	public CombinedEdit eliminateChanLabels() {
		return getChannelLabelManager().eliminateChanLabels();
	}
	public UndoAddManyItem generateChannelLabels() {
		MultiChannelImage mw = getMultiChannelImage();
		ArrayList<ChannelLabelTextGraphic> labels;
		//Check to determine if this stack is both multichannel and multi-slice. if so, channel labels will be needed only for the top row
		if (mw.nChannels()>1 && (mw.getStackSize()>mw.nChannels()))
			labels=getChannelLabelManager().generateChannelLabels2();
		else if (mw.nChannels()>1) 
			labels=getChannelLabelManager().generateChannelLabels();
		else return null;//channel labels not needed if only one channel present
		
		return new UndoAddManyItem(this, labels);
	}

	/**returns the MultiChannelSlot that contains the source image*/
	public MultiChannelSlot getSlot() {
		return slot;
	}
	/**sets the MultiChannelSlot that may contain the source image*/
	public void setSlot(MultiChannelSlot slot) {
		if (this.slot!=null) {
			slot.removeMultichannelUpdateListener(this);
		}
		this.slot = slot;
		if (slot==null) return;
		slot.setStackDisplayLayer(this);
		slot.addMultichannelUpdateListener(this);
	}

	/**implements the .kill option from the mortal interface*/
	public void kill() {
		super.kill();
		getSlot().kill();
	}
	
	/**closes the window that showed the source image or the cropped intermediate image*/
	public void closeWindow(boolean save) {
		getSlot().kill();
		getSlot().hideImage();
	}
	
	/**returns the multi channel image object for this image display*/
	@Override
	public MultiChannelImage getMultiChannelImage() {
		if(getSlot()==null) {
			//if there is no slot, then there is no image to return
			return null;
		}
		return this.getSlot().getMultichannelImage();
	
	}
	
	@Override
	/**Returns a short version of the title*/
	public String getTitle() {
		String nam = getName();
		String output = nam.split(";")[0];
		if (output.length()<20)
			return output;
		else return output.substring(0,19);
	}
	
	/**returns the file object for the file uses*/
	@Override
	public File getFile() {
		if (slot.getMultichannelImage()==null) return null;
		String path= slot.getMultichannelImage().getPath();
		return new File(path);
	}
	
	
	/**getter and setter methods for the preprocess scale. experimental*/
	public double getPreprocessScale() {
		MultiChannelSlot slot2 = this.getSlot();
		if(slot2==null) return 1;
		PreProcessInformation info = slot2.getModifications();
		if(info==null) return 1;
		return info.getScale();
	}
	
	/**sets the preprocess scale*/
	public MultiChannelImage setPreprocessScale(double s) {
		if (s>10) s=10; //does not allow user to scale more than 10 fold. it is never really needed and may cause heap space issues
		MultiChannelSlot slot2 = this.getSlot();
		if (slot2==null) return this.getMultiChannelImage();
		PreProcessInformation info = slot2.getModifications();
		if(info!=null&&s==info.getScale()) return this.getMultiChannelImage();
		PreProcessInformation newscale;
		if (info!=null) newscale= new PreProcessInformation(info.getRectangle(), info.getAngle(), s);
		else  newscale= new PreProcessInformation(null, 0, s);
		slot.applyCropAndScale(newscale);
		return this.getMultiChannelImage();
	}
	/**returns the 'pre-process' information object for this image*/
	@Override
	public PreProcessInformation getPreProcess() {
		return this.getSlot().getModifications();
	}
	
	/**returns all of the inset definer objects in the figure*/
	public ArrayList<PanelGraphicInsetDefiner> getInsets() {
		ArrayList<PanelGraphicInsetDefiner> output = new ArrayList<PanelGraphicInsetDefiner>();
		for(ZoomableGraphic z: this.getAllGraphics()) {
			if (z instanceof PanelGraphicInsetDefiner)
				output.add((PanelGraphicInsetDefiner) z);
		}
		
		return output;
	}
	
	/**should be called after scale reset*/
	public void updateFromOriginal() {
		getSlot().redoCropAndScale();
		this.updatePanelsAndLabelsFromSource();
		this.updatePanels();
	}
	
	/**changes the view location of the multichannel slot to match the frame and slice that is selected
	 in the frame and slice use instructions the panel manager */
	public void setFrameSliceUseToViewLocation() {
		getPanelManager().getPanelList().getChannelUseInstructions().shareViewLocation(getSlot().getDisplaySlice());
		}
	
}
