package graphicalObjects_FigureSpecific;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Icon;

import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelImage;
import channelMerging.ImageDisplayLayer;
import channelMerging.PreProcessInformation;
import genericMontageKit.PanelList;
import genericMontageKit.PanelOrder;
import genericMontageKit.PanelOrder.imageOrderComparator;
import graphicActionToolbar.CurrentFigureSet;
import genericMontageKit.PanelSetter;
import genericMontageKit.SubFigureOrganizer;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import iconGraphicalObjects.IconUtil;
import imageMenu.CanvasAutoResize;
import logging.IssueLog;
import objectDialogs.CroppingDialog;
import objectDialogs.PanelStackDisplayOptions;
import popupMenusForComplexObjects.FigureOrganizingSuplierForPopup;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoAddManyItem;
import undo.UndoLayoutEdit;
import utilityClassesForObjects.SnappingPosition;
import menuUtil.HasUniquePopupMenu;
import appContext.CurrentAppContext;
import applicationAdapters.DisplayedImage;

/**meant to check if bugs that existed with previous subclasses are gone. Later adapted to work 
 * with source stacks in a special way*/
public class FigureOrganizingLayerPane extends GraphicLayerPane implements SubFigureOrganizer, HasUniquePopupMenu {

	{description= "A Figure Organizing Layer";}
	
	PanelSetter subfigureSetter=new PanelSetter(); 
	private ArrayList< ImageDisplayLayer> displays=new 	ArrayList<ImageDisplayLayer>();
	
	
	public FigureOrganizingLayerPane(String name) {
		super(name);
	}

	private static final long serialVersionUID = 1L;

	/**Returns the miltidimensional images the are part of the figure*/
	@Override
	public ArrayList<MultiChannelImage> getAllSourceImages() {
		ArrayList<MultiChannelImage> output = new ArrayList<MultiChannelImage>();
		for(ImageDisplayLayer d: getMultiChannelDisplays()){
			if (d==null) continue;
			output.add(d.getMultiChannelImage());
		}

		return output;
	}

	
	/**combines the panel lists from all the images in the figure into one list*/
	public PanelList getAllPanelLists() {
		PanelList output = new PanelList();
		for(ImageDisplayLayer d: getMultiChannelDisplays()){
			if (d==null) continue;
			output.add(d.getPanelList());
		}
		return output;
	}
	/***/
	public void mapAllPanelPlacements() {
		this.subfigureSetter.layDisplayPanelsOfStackOnLayout(getAllPanelLists(), this.getMontageLayoutGraphic().getPanelLayout(), true);
		
	}
	
	
	/**Updates the images panels and labels for every image in the figure from the source images.
	  this is needed in order for the panels to reflect things like color switches, channel order changes, display range adjustments and images panels */
	@Override
	public void updatePanelsAndLabelsFromSource() {
		for(ImageDisplayLayer d: getMultiChannelDisplays()){
			if (d==null) continue;
			d.updatePanels();
		}
	}
	


	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void supress() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addItemToLayer(ZoomableGraphic z) {
		super.addItemToLayer(z);
		if (z instanceof  ImageDisplayLayer) {
			ImageDisplayLayer psdz = (ImageDisplayLayer) z;
			getMultiChannelDisplays().add(psdz);
		}
	}
	
	public void removeItemFromLayer(ZoomableGraphic z) {
		super.removeItemFromLayer(z);
		if (z instanceof  ImageDisplayLayer) {
			ImageDisplayLayer psdz = (ImageDisplayLayer) z;
			getMultiChannelDisplays().remove(psdz);
			this.updateImageOrder();
		}
	}
	
	
	/**in the tree, figure organizing layers are shown in a different color from normal layers*/
	static Color  folderColorForFigureOrganizers= new Color(140,0, 0);
	public static Icon createDefaultTreeIcon2(boolean open) {
		return IconUtil.createFolderIcon(open, folderColorForFigureOrganizers);
	}
	/**returns the tree icon for figure organizering layers*/
	public Icon getTreeIcon(boolean open) {
		return createDefaultTreeIcon2(open);
	}
	
	
/**returns the layout */
public BasicMontageLayout getLayout() {
	return getMontageLayout();
}
/**returns the layout */
public BasicMontageLayout getMontageLayout() {
	MontageLayoutGraphic mm = getMontageLayoutGraphic();
	if (mm!=null) return mm.getPanelLayout();
	return null;
}
/**gets the graphic that displays the layout and allows layout editing by user*/
public MontageLayoutGraphic getMontageLayoutGraphic() {
	for(ZoomableGraphic a:super.getItemArray()) {
		if (a==null) continue;
		if (a instanceof MontageLayoutGraphic) {
			MontageLayoutGraphic m=(MontageLayoutGraphic) a;
			
			m.generateStandardImageWrapper();
			return m;
					};
	}
	return null;
}


/**returns a list of all display layers for all the images in the figure*/
	public ArrayList< ImageDisplayLayer> getMultiChannelDisplays() {
		return displays;
	}
	/**returns the first display layers for the first image in the figure*/
	public  ImageDisplayLayer getPrincipalMultiChannel() {
		for (ImageDisplayLayer p: getMultiChannelDisplays()) {
			if (p!=null) return p;
		}
		return null;
	}
	/**returns all the image display layers */
	public ArrayList< ImageDisplayLayer> getMultiChannelDisplaysInOrder() {
		ArrayList<ZoomableGraphic> ia = this.getItemArray();
		ArrayList<ImageDisplayLayer> output = new ArrayList< ImageDisplayLayer>();
		for(ZoomableGraphic a:ia) {
			if (a instanceof ImageDisplayLayer) {
				output .add((ImageDisplayLayer) a);
			}
		}
		return output;
	}
	
	public ArrayList< ImageDisplayLayer> getMultiChannelDisplaysInLayoutOrder() {
		this.updateImageOrder();
		return getMultiChannelDisplays();
	}
	

	/**Adds an additional multichannel image to the figure, creates panels as needed
	 * @return */
	public CombinedEdit addNovelMultiChannel(MultichannelDisplayLayer display, int start) {
		if(display==null) return null;
		int startpoint=this.getAllPanelLists().getlastPanelsIndex()+1;
		if(start>0) startpoint=start;
		ImageDisplayLayer principalMultiChannel = getPrincipalMultiChannel();
		boolean hasOne=principalMultiChannel!=null;//true if there is already a multichannel image in figure
		CombinedEdit output = new CombinedEdit();
		
		if(!hasOne) {
			cropIfUserSelectionExists(display);
		}
		
		if (hasOne) {
			//IssueLog.log("d scale "+display.getPreprocessScale());
			display.setPreprocessScale(principalMultiChannel.getPreprocessScale());
			//IssueLog.log("d scale "+display.getPreprocessScale());
			principalMultiChannel.getPanelList().giveSettingsTo(display.getPanelList());
			display.getSetter().startPoint=startpoint;
			
			try {
				boolean mustResize=areSizesDifferent(principalMultiChannel, display) ;
			double pScale = principalMultiChannel.getPreprocessScale();
			double w = principalMultiChannel.getMultiChannelImage().getDimensions().getWidth()/pScale;
			double h = principalMultiChannel.getMultiChannelImage().getDimensions().getHeight()/pScale;
			
			if ( mustResize||display.getPanelList().getChannelUseInstructions().selectsSlices(display.getMultiChannelImage()))
				{
				CroppingDialog.showCropDialog(display.getSlot(), new Rectangle(0,0,(int) w,(int) h), 0);
				display.getPanelList().getChannelUseInstructions().shareViewLocation(display.getSlot().getDisplaySlice());
				}
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		}
		else {
			
			setUpALayoutGraphicForLayerAndImage(display);
		}
		
		/**Tries to match the channel order and luts. this part is prone to errors so it is in a try catch*/
		if (hasOne) try {new ChannelOrderAndLutMatching().matchChannels(principalMultiChannel.getMultiChannelImage(), display.getMultiChannelImage(), 2);
				} catch (Throwable t) {IssueLog.logT(t);}
		
		this.add(display);
		output.addEditToList(new UndoAddItem(this, display));
		
		UndoLayoutEdit lUndo = new UndoLayoutEdit(getMontageLayoutGraphic());
		display.eliminateAndRecreate(!hasOne, false, !hasOne);//since this method alters the layout, a layout undo is needed. all other actions done do not need to be undone since the new objects are removed
		output.addEditToList(lUndo);
		
		if (hasOne) {
			display.eliminateChanLabels();
		}
		boolean alterLayout = areSizesChangedForLayout(principalMultiChannel, display);
		if(alterLayout) {
			
			this.getMontageLayoutGraphic().generateCurrentImageWrapper();//sets of a list of the contents
			lUndo = new UndoLayoutEdit(getMontageLayoutGraphic());
			this.getLayout().getEditor().alterPanelWidthAndHeightToFitContents(getLayout());
			output.addEditToList(lUndo);
		}
		
		return output;
	}

	/**Sometimes, a user or programmer will have set a cropping region
	 Written to crop an initial image based on the roi*/
	public static void cropIfUserSelectionExists(MultichannelDisplayLayer display) {
		Rectangle totalImageSize = new Rectangle(display.getMultiChannelImage().getDimensions());
		Rectangle b=totalImageSize;
		
		boolean valid=true;//is the ROI valid
		
		if (display.getMultiChannelImage().getSelectionRectangle(0)!=null) try{
			//if user has region of interest drawn this takes precedent
			b = display.getSlot().getMultichannelImage().getSelectionRectangle(0).getBounds();
			display.getMultiChannelImage().eliminateSelection(0);//eliminates the rectangle so it wont interfere with later steps

		} catch (Throwable t) {
			IssueLog.logT(t);
		} else if (display.getPreProcess()!=null &&display.getPreProcess().getRectangle()!=null) 
				{	
					b=display.getPreProcess().getRectangle();
				//in some contexts, a crop region might already be set
				}
			
		/**for rectangles that are either small or have strange aspect ratios, rectangle is declared invalid*/
		if(b.height>3.5*b.width) valid=false;
		if(b.width>3.5*b.height) valid=false;
		if(b.width<25||b.height<25) valid=false;	
		
		/**if area is very large, rectangle is declared invalid and 
		  a smaller one is set. Asks user to crop anyway*/
		if(b.width>1000||b.height>1000) { 
			valid=false;
			b.width=800;
			b.height=600;
		}
		
		/**if the entire image will be used, this method can return without doing a crop*/
		if (valid&&totalImageSize.equals(b)) 
			return;
		
		if(!valid) {
			CroppingDialog.showCropDialog(display.getSlot(), b, 0);
		} else {
			display.getSlot().applyCropAndScale(new PreProcessInformation(b, 0, display.getPreprocessScale()));
		}
	}
	
	/**returns true if the sizes are different enough to merit a cropping
	   is an incompatibility in the sizes*/
	private boolean areSizesDifferent(ImageDisplayLayer principalMultiChannel, MultichannelDisplayLayer display) {
		Dimension2D p1 = principalMultiChannel.getMultiChannelImage().getDimensions();
		Dimension2D p2 = display.getMultiChannelImage().getDimensions();
		if (this.getLayout()!=null) {
			if(getLayout().rowmajor &&p2.getWidth()>p1.getWidth()) return true;
			if(!getLayout().rowmajor &&p2.getWidth()>2.5*p1.getWidth()) return true;
			
			if(!getLayout().rowmajor &&p2.getHeight()>p1.getHeight()) return true;
			if(getLayout().rowmajor &&p2.getHeight()>2.5*p1.getHeight()) return true;
		}
		else {
		if(p2.getWidth()>1.5*p1.getWidth()) return true;
		if(p2.getHeight()>1.5*p1.getHeight()) return true;
		}
		return false;
	}
	
	/**returns true if the sizes are different enough to merit a layout change*/
	private boolean areSizesChangedForLayout(ImageDisplayLayer principalMultiChannel, MultichannelDisplayLayer display) {
		if (principalMultiChannel==null) return false;
		Dimension2D p1 = principalMultiChannel.getMultiChannelImage().getDimensions();
		Dimension2D p2 = display.getMultiChannelImage().getDimensions();
		if (this.getLayout()!=null) {
			if(getLayout().rowmajor &&p2.getHeight()!=p1.getHeight()) return true;
			if(getLayout().rowmajor &&p2.getWidth()>p1.getWidth()) return true;
			
			if(!getLayout().rowmajor &&p2.getHeight()>p1.getHeight()) return true;
			if(!getLayout().rowmajor &&p2.getWidth()!=p1.getWidth()) return true;
		}
		

		return false;
	}

	public MontageLayoutGraphic setUpALayoutGraphicForLayerAndImage( MultichannelDisplayLayer display) {
		/**creates a MontageLayout Graphic if the layer has none*/
		MontageLayoutGraphic p=getMontageLayoutGraphic();
		if (p==null) {
			p=createLayoutForImage(display.getMultiChannelImage(), display);
			add(p);
		} 
		p.generateCurrentImageWrapper();//
		return p;
	}
	
	/**creates a layout that is of the right dimensions for the panel stack display to use to place the image's panels*/
 static MontageLayoutGraphic createLayoutForImage(MultiChannelImage image, ImageDisplayLayer panelStackDisplay) {
		MontageLayoutGraphic p = new MontageLayoutGraphic();
		//p.getPanelLayout().setNColumns(image.nChannels()+1);
		p.getPanelLayout().setHorizontalBorder(10);
		p.getPanelLayout().setVerticalBorder(10);
		setUpRowAndColsToFit(image, panelStackDisplay, p);
		return p;
	}
public static void setUpRowAndColsToFit(MultiChannelImage image, ImageDisplayLayer panelStackDisplay,
		MontageLayoutGraphic p) {
	if (panelStackDisplay!=null) {
	int[] dims =  panelStackDisplay.getPanelList().getChannelUseInstructions().estimateBestMontageDims(image);
	
		//int col=panelStackDisplay.getStack().getChannelUseInstructions().estimateNPanelsNeeded(image);
		int col=dims[1];
		int row=dims[0];
		if(p.getPanelLayout().rowmajor) {
			p.getPanelLayout().setNColumns(col);
			p.getPanelLayout().setNRows(row);
		}
		else {
			p.getPanelLayout().setNColumns(row);
			p.getPanelLayout().setNRows(col);
		}
			}
}
	
	

	@Override
	public FigureOrganizingSuplierForPopup getMenuSupplier() {
		return new FigureOrganizingSuplierForPopup(this);
	}
	
	/**Adds another multi-channel image to the figure
	 * @return */
	public CombinedEdit nextMultiChannel(boolean openFile) {
		
		MultichannelDisplayLayer item = CurrentAppContext.getMultichannelContext().getMultichannelOpener().creatMultiChannelDisplayFromUserSelectedImage(openFile, null);
		
		CombinedEdit output = nextMultiChannel(item);
		
		return output;
	}
	
	/**Adds another multi-channel image to the figure using the file in the path given
	 * @return */
	public CombinedEdit nextMultiChannel(String path, PreProcessInformation p) {
		
		MultichannelDisplayLayer item = CurrentAppContext.getMultichannelContext().getMultichannelOpener().creatMultiChannelDisplayFromUserSelectedImage(true, path);
		item.getSlot().applyCropAndScale(p);
		CombinedEdit output = nextMultiChannel(item);
		
		
		return output;
	}
	
	
	/**Adds another multichannel image to the figure. returns an undoable edit
	 */
	public CombinedEdit nextMultiChannel(MultichannelDisplayLayer item, int start) {
		CombinedEdit output = new CombinedEdit();
	
			output.addEditToList(	
					addNovelMultiChannel(item, start)
					);
		DisplayedImage disp = getGraphicSetContainer() .getAsWrapper().getImageDisplay();
	
		
		output.addEditToList(	new  CanvasAutoResize().performUndoableAction(disp));
				
		return output;
	}
	
	/**Adds a multi channel image to the figure. returns an undoable edit*/
	public CombinedEdit nextMultiChannel(MultichannelDisplayLayer item) {
		return nextMultiChannel(item,-1);
	}

	/**Attempts to identify the names of the images present in either panels, rows or columns
	  adds label accordingly*/
	public ArrayList<TextGraphic> addLabelsBasedOnImageNames(int type) {
		BasicMontageLayout ml = getMontageLayout();
		ArrayList<TextGraphic> addedItems=new ArrayList<TextGraphic>();
		int limit = ml.nRows();
		if(type==BasicMontageLayout.COLS) limit=ml.nColumns();
		if(type==BasicMontageLayout.PANELS) limit=ml.nPanels();
		SnappingPosition position=null;
		for(int i=1; i<=limit; i++) {
			TextGraphic item=null;
			ImageDisplayLayer pan = getPanelForRowindex(i, type);//may return null
			
			if (pan==null) {
				item = FigureLabelOrganizer.addLabelOfType(type, i,  this, this.getMontageLayoutGraphic());
			}
			
			if (pan!=null) {
				String text=pan.getTitle();
				if (type==BasicMontageLayout.ROWS)
					item=addRowLabel(text, i);
				if (type==BasicMontageLayout.COLS)
					item=addColLabel(text, i);
				if (type==BasicMontageLayout.PANELS)//added on nov 3 thinking that it would fix an issue with generate all labels that I have only been seeing on the Mac
					item=addPanelLabel(text, i);
			}
			
			if (item!=null)
				{
				if(position==null) position = item.getSnapPosition(); else item.setSnapPosition(position);
				addedItems.add(item);
				}
		}
		return addedItems;
	}


	/**If an entire Multichannel image's set of panels is contained in teh Row, Col, or panel in the layout, returns the image*/
	public ImageDisplayLayer getPanelForRowindex(int i, int type) {
		BasicMontageLayout rowshapes = this.getMontageLayout().makeAltered(type);
		ArrayList<ImageDisplayLayer> list = this.getMultiChannelDisplaysInOrder();
		for(ImageDisplayLayer l: list) {
			if (rowshapes.getPanel(i).contains(l.getBoundOfUsedPanels().getCenterX(), l.getBoundOfUsedPanels().getCenterY())) {
				return l;
			}
		}
		return null;
	}
	
	/**displays the channel use options dialog to the user*/
	public void showChannelUseOptions() {
		ImageDisplayLayer image1 = getPrincipalMultiChannel();
		PanelStackDisplayOptions dialog = new PanelStackDisplayOptions((MultichannelDisplayLayer)image1,image1.getPanelList(), null,false);
		/**adds a list of all the channel displays that are relevant*/
		ArrayList<ImageDisplayLayer> all = getMultiChannelDisplaysInOrder();
		all.remove(image1);
		dialog.addAditionalDisplays(all);
		dialog.showDialog();
	}


	/**shows dialog to recreate figure panels*/
	public void recreateFigurePanels() {recreateFigurePanels(false);}
	public PanelStackDisplayOptions recreateFigurePanels(boolean cropToo) {
		
		ArrayList<ImageDisplayLayer> d1 = getMultiChannelDisplaysInLayoutOrder();
		MultichannelDisplayLayer in = (MultichannelDisplayLayer)getPrincipalMultiChannel();
		PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(in, in.getPanelList(),null, true);
		
		dialog.addAditionalDisplays(d1);
		dialog.setCurrentImageDisplay(CurrentFigureSet. getCurrentActiveDisplayGroup());
		dialog.setModal(false);
		
		dialog.showDialog();
		this.fixLabelSpaces();
		return dialog;
	}


	/**Adds row labels based on names*/
	public CombinedEdit addRowOrColLabel(int type) {
		CombinedEdit edit = new CombinedEdit();
		ArrayList<TextGraphic> output = addLabelsBasedOnImageNames(type);
		UndoAddManyItem many = new UndoAddManyItem(this, output);
		edit.addEditToList(many);
		UndoLayoutEdit many2 = new UndoLayoutEdit(getMontageLayout());
		fixLabelSpaces();
		many2.establishFinalLocations(); 
		edit.addEditToList(many2);
		DisplayedImage disp = getGraphicSetContainer() .getAsWrapper().getImageDisplay();

		disp.updateDisplay();
		return edit;
	}

	/**Expends the label spaces of the layout to fit the current labels*/
	public void fixLabelSpaces() {
		this.getMontageLayoutGraphic().generateCurrentImageWrapper();
		getMontageLayout().getEditor().fitLabelSpacesToContents(getMontageLayout());
	}
	
	/**Adds a row label to the figure*/
	public ComplexTextGraphic addRowLabel(String st, int rowNum) {
		return FigureLabelOrganizer.addRowLabel(st, rowNum, this, this.getMontageLayoutGraphic());
	}
	
	/**Adds a column label to the figure*/
	public ComplexTextGraphic addColLabel(String st, int colNum) {
		return FigureLabelOrganizer.addColLabel(st, colNum, this, this.getMontageLayoutGraphic());
	}
	
	
	/**adds a panel label to the figure at the index*/
	public ComplexTextGraphic addPanelLabel(String st, int theIndex) {
		return FigureLabelOrganizer.addPanelLabel(st, theIndex, this, this.getMontageLayoutGraphic());
	}
	
	
	/** returns false if the layer should not contain the proposed object. depending on the result of this method
	  the user may not be allowed to drag a particular item into this layer.
	  Figure organizing layers are not allowed to contain other figure organizing layers.
	  */
	public boolean canAccept(ZoomableGraphic z) {
		if (z instanceof FigureOrganizingLayerPane) return false;
		if (z instanceof GraphicLayer)  {
			ArrayList<ZoomableGraphic> listed = ((GraphicLayer) z).getObjectsAndSubLayers();
			for(ZoomableGraphic l: listed) {
				if (!this.canAccept(l)) return false;
			}
		}
		
		return  super.canAccept(z);
	}
	
	/**given an object, moves up the layer tree until it find a figure organizer to return*/
	public static FigureOrganizingLayerPane findFigureOrganizer(KnowsParentLayer k) {
		GraphicLayer output = k.getParentLayer();
		
		while (output!=null &&!(output instanceof FigureOrganizingLayerPane)) {
			 output=output.getParentLayer();
			 if (output==null) return null;
		}
		if (output instanceof FigureOrganizingLayerPane)
			return (FigureOrganizingLayerPane) output;
		return null;
	}

	/**returns the channel use instructions for every image in the figure*/
	public ArrayList<ChannelUseInstructions> getChannelUseInfo() {
		ArrayList<ChannelUseInstructions> onList=new ArrayList<ChannelUseInstructions>();
		for (ImageDisplayLayer d: getMultiChannelDisplaysInOrder()) {
			ChannelUseInstructions ins = d.getPanelManager().getPanelList().getChannelUseInstructions();
			if(onList.contains(ins)) continue;
			onList.add(ins);
		}
		return onList;
	}

	/**hides any open images that are source images for this*/
	public void hideImages() {
		for(ImageDisplayLayer m :getMultiChannelDisplaysInOrder())
			m.getSlot().hideImageWihtoutMessage();
	}

	/**called after a panel, column or row swap on the layout is done. 
	 * updates the image order within the figure and channel order to reflect the change*/
	public void updateChannelOrder(int type) {
		PanelOrder panelOrder = new PanelOrder(this);
		updateImageOrder();
		panelOrder.updateChanOrder(type);
	}
	
	/**Determines the order of the multidimensional images within the layout based on the x/y geometry of the layout
	 The list of images within this figure organizer is reordered to store that order.
	  */
	public void updateImageOrder() {
		PanelOrder panelOrder = new PanelOrder(this);
		ArrayList<ImageDisplayLayer> layoutOrder = panelOrder.getDisplaysInLayoutImageOrder();
		imageOrderComparator lorder = new PanelOrder.imageOrderComparator(layoutOrder);

		Collections.sort(displays, lorder);
	}
	
	
	
}
