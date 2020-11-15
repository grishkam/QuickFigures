package graphicalObjects_FigureSpecific;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Icon;

import channelMerging.MultiChannelSlot;
import channelMerging.MultiChannelUpdateListener;
import channelMerging.MultiChannelWrapper;
import channelMerging.PanelStackDisplay;
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

/**Special layer for showing parts of a specific multi-channel image */
public class MultichannelDisplayLayer extends GraphicLayerPane implements ZoomableGraphic, ShowsOptionsDialog, PanelStackDisplay, KnowsParentLayer, Named, HasUniquePopupMenu,  HasTreeLeafIcon, SubFigureOrganizer, MultiChannelUpdateListener,PointsToFile , Serializable{

	/**
	 * 
	 */
	{description= "A layer containing the components of a multichannel image";}
	
private static final long serialVersionUID = 1L;
	

	
	private boolean loadFromFile=false;
	public MultiChannelSlot slot=null;
	protected GraphicLayer parent;
	protected String name="Image ";
	private boolean laygeneratedPanelsOnGrid;

	PanelSetter setter;
	
	/**Creates a display layer, Argument cannot be null*/
	public MultichannelDisplayLayer(MultiChannelSlot slot) {	
		super("");	
		this.setSlot(slot);
		slot.setPanelStackDisplay(this);
	}

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
	
	public PanelList getPanelList() {
		return stack;
	}

	
	public void setPanelList(PanelList stack) {
		this.stack = stack;
	}
	

	@Override
	public PanelList getWorkingPanelList() {
		return stack;
	}
	
	
	public PanelSetter getSetter() {
		if (setter==null) setter=new  PanelSetter();
		return setter;
	}
	
	

	
	
	
	
	
	static boolean showTimes=false;
	

	
	/**returns the bounding box of all the panel graphics in this layer
	  wrote it with the intention of using it to create a way to sort
	  these types of layers by position*/
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
	
	
	
	
	@Override
	public GraphicLayer getParentLayer() {
		return parent;
	}


	@Override
	public void setParentLayer(GraphicLayer parent) {
		this.parent=parent;
		
	}

	public String getName() {
	return name;
	}

	
	@Override
	public void setName(String st) {
		if (st.length()>50) {IssueLog.log("name is excessive");}
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
		panelMan.setMultiChannelWrapper(getMultichanalWrapper());
				
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


	/**returns true if the default way to get the image is to load it from the original file*/
	public boolean loadFromFile() {
		return loadFromFile;
	}



	public void setLoadFromFile(boolean loadFromFile) {
		this.loadFromFile = loadFromFile;
	}



	public boolean isLaygeneratedPanelsOnGrid() {
		return laygeneratedPanelsOnGrid;
	}



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
			
			}catch (Throwable t) {IssueLog.log(t);}
	}

	
	
	@MenuItemMethod(menuActionCommand = "labeldefaults", menuText = "Set Label Options to Saved", subMenuName="Channel Labels")
	public void setLabalPropertiesToSaved() {
		GraphicEncoder ge=new GraphicEncoder(null);
		ZoomableGraphic zz = ge.readFromUserSelectedFile();
		if (zz instanceof TextGraphic) {
			TextGraphic zt=(TextGraphic) zz;
			for (ChannelLabelTextGraphic lab:getPanelList().getChannelLabels()) {
				lab.copyAttributesFrom(zt);
				lab.setSnappingBehaviour(zt.getSnapPosition().copy());
			}
		}
	}

	
	
	public synchronized void updatePanels() {
		this.getPanelManager().updatePanels();
	}
	
	public synchronized void updatePanelsWithChannel(String realChannelName) {
		getPanelManager().updatePanelsWithChannel(realChannelName);
	}
	

	
	public void showStackOptionsDialog() {
		PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(this,this.getPanelList(), null,false);
		dialog.showDialog();
	}
	
/**returns a list of channel names*/
	public static String[] getChannelNames(MultiChannelWrapper impw) {
		
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
	public ArrayList<MultiChannelWrapper> getAllSourceStacks() {
		// TODO Auto-generated method stub
		ArrayList<MultiChannelWrapper> output = new ArrayList<MultiChannelWrapper> ();
		output.add(this.getMultichanalWrapper());
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
		// TODO Auto-generated method stub
		//di.turnOn();
	}


	@Override
	public void supress() {
		// TODO Auto-generated method stub
	//	di.turnOff();
	}
	@Override
	public void onImageUpdated() {
	
		try{
			updatePanels() ;

			if (this.getGraphicSetContainer()!=null)getGraphicSetContainer().updateDisplay();
			if (GraphicItemOptionsDialog.getSetContainer()!=null) GraphicItemOptionsDialog.getSetContainer().updateDisplay();
		} catch (Throwable t) {
			IssueLog.log(t);
		}
	}


	@Override
	public void onImageInitiated() {
		getPanelManager().generatePanelGraphics();
		setName(this.getMultichanalWrapper().getTitle());
		notes=getMultichanalWrapper().getPath();
		this.description=""+getMultichanalWrapper().getPath();
	}

	
	

	
	public CombinedEdit eliminateChanLabels() {
		return getChannelLabelManager().eliminateChanLabels();
	}
	public UndoAddManyItem generateChannelLabels() {
		MultiChannelWrapper mw = getMultichanalWrapper();
		ArrayList<ChannelLabelTextGraphic> labels;
		//Check to determine if this stack is both multichannel and multi-slice. if so, channel labels will be needed only for the top row
		if (mw.nChannels()>1 && (mw.getStackSize()>mw.nChannels()))
			labels=getChannelLabelManager().generateChannelLabels2();
		else if (mw.nChannels()>1) 
			labels=getChannelLabelManager().generateChannelLabels();
		else return null;//channel labels not needed if only one channel present
		
		return new UndoAddManyItem(this, labels);
	}
	

	
	
	

	public MultiChannelSlot getSlot() {
		return slot;
	}
	
	public void setSlot(MultiChannelSlot slot) {
		if (this.slot!=null) {
			slot.removeMultichannelUpdateListener(this);
		}
		this.slot = slot;
		if (slot==null) return;
		slot.setPanelStackDisplay(this);
		slot.addMultichannelUpdateListener(this);
	}


	public void kill() {
		super.kill();
		getSlot().kill();
	}
	
	
	public void closeWindow(boolean save) {
		getSlot().kill();
		getSlot().hideImage();
	}
	
	@Override
	public MultiChannelWrapper getMultichanalWrapper() {
		if(getSlot()==null) {
			IssueLog.log("null slot");
		}
		return this.getSlot().getMultichanalWrapper();
	
	}
	
	@Override
	/**retruns a short title*/
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
		if (slot.getMultichanalWrapper()==null) return null;
		String path= slot.getMultichanalWrapper().getPath();
		return new File(path);
	}
	
	
	/**getter and setter methods for the preprocess scale. experimental*/
	public double getPreprocessScale() {
		PreProcessInformation info = this.getSlot().getModifications();
		if(info==null) return 1;
		return info.getScale();
	}
	
	/**sets the preprocess scale*/
	public MultiChannelWrapper setPreprocessScale(double s) {
		if (s>10) s=10; //does not allow user to scale more than 10 fold. it is never really needed and may cause heap space issues
		PreProcessInformation info = this.getSlot().getModifications();
		if(info!=null&&s==info.getScale()) return this.getMultichanalWrapper();
		PreProcessInformation newscale;
		if (info!=null) newscale= new PreProcessInformation(info.getRectangle(), info.getAngle(), s);
		else  newscale= new PreProcessInformation(null, 0, s);
		slot.applyCropAndScale(newscale);
		return this.getMultichanalWrapper();
	}
	@Override
	public PreProcessInformation getPreProcess() {
		return this.getSlot().getModifications();
	}
	
	public ArrayList<PanelGraphicInsetDef> getInsets() {
		ArrayList<PanelGraphicInsetDef> output = new ArrayList<PanelGraphicInsetDef>();
		for(ZoomableGraphic z: this.getAllGraphics()) {
			if (z instanceof PanelGraphicInsetDef)
				output.add((PanelGraphicInsetDef) z);
		}
		
		return output;
	}
	
	/**should be called after scale reset*/
	public void updateFromOriginal() {
		getSlot().redoCropAndScale();
		this.updatePanelsAndLabelsFromSource();
		this.updatePanels();
	}
	
	public void setFrameSliceUseToViewLocation() {getPanelManager().getPanelList().getChannelUseInstructions().shareViewLocation(getSlot().getDisplaySlice());}
	
}
