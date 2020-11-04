package graphicalObjects_FigureSpecific;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Icon;

import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelWrapper;
import channelMerging.PanelStackDisplay;
import channelMerging.PreProcessInformation;
import genericMontageKit.PanelList;
import genericMontageKit.PanelOrder;
import genericMontageKit.PanelOrder.imageOrderComparator;
import genericMontageKit.PanelSetter;
import genericMontageKit.SubFigureOrganizer;
import graphicActionToombar.CurrentSetInformerBasic;
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
import undo.CompoundEdit2;
import undo.UndoAddItem;
import undo.UndoAddManyItem;
import undo.UndoLayoutEdit;
import utilityClassesForObjects.SnappingPosition;
import menuUtil.HasUniquePopupMenu;
import appContext.CurrentAppContext;
import applicationAdapters.DisplayedImageWrapper;

/**meant to check if bugs that existed with previous subclasses are gone. Later adapted to work 
 * with source stacks in a special way*/
public class FigureOrganizingLayerPane extends GraphicLayerPane implements SubFigureOrganizer, HasUniquePopupMenu {

	{description= "A Figure Organizing Layer";}
	
	PanelSetter subfigureSetter=new PanelSetter(); 
	private ArrayList< PanelStackDisplay> displays=new 	ArrayList<PanelStackDisplay>();
	
	
	public FigureOrganizingLayerPane(String name) {
		super(name);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public ArrayList<MultiChannelWrapper> getAllSourceStacks() {
		ArrayList<MultiChannelWrapper> output = new ArrayList<MultiChannelWrapper>();
		for(PanelStackDisplay d: getMultiChannelDisplays()){
			if (d==null) continue;
			output.add(d.getMultichanalWrapper());
		}

		return output;
	}

	/**combines the panel lists*/
	@Override
	public PanelList getWorkingStack() {
		PanelList output = new PanelList();
		for(PanelStackDisplay d: getMultiChannelDisplays()){
			if (d==null) continue;
			output.add(d.getStack());
		}
		// TODO Auto-generated method stub
		return output;
	}
	public void mapAllPanelPlacements() {
		this.subfigureSetter.layDisplayPanelsOfStackOnLayout(getWorkingStack(), this.getMontageLayoutGraphic().getPanelLayout(), true);
		
	}

	@Override
	public void updatePanelsAndLabelsFromSource() {
		for(PanelStackDisplay d: getMultiChannelDisplays()){
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
		if (z instanceof  PanelStackDisplay) {
			PanelStackDisplay psdz = (PanelStackDisplay) z;
			getMultiChannelDisplays().add(psdz);
		}
	}
	
	public void removeItemFromLayer(ZoomableGraphic z) {
		super.removeItemFromLayer(z);
		if (z instanceof  PanelStackDisplay) {
			PanelStackDisplay psdz = (PanelStackDisplay) z;
			getMultiChannelDisplays().remove(psdz);
			this.updateImageOrder();
		}
	}
	
	
	static Color  folderColor2= new Color(140,0, 0);
	public static Icon createDefaultTreeIcon2(boolean open) {
		return IconUtil.createFolderIcon(open, folderColor2);
	}
	
	@Override
	public Icon getTreeIcon(boolean open) {
		
		return createDefaultTreeIcon2(open);
	
	}
	

public BasicMontageLayout getLayout() {
	return getMontageLayout();
}

/**gets the layout graphic*/
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

public BasicMontageLayout getMontageLayout() {
	MontageLayoutGraphic mm = getMontageLayoutGraphic();
	if (mm!=null) return mm.getPanelLayout();
	
	return null;
}

	public ArrayList< PanelStackDisplay> getMultiChannelDisplays() {
		return displays;
	}
	
	public  PanelStackDisplay getPrincipalMultiChannel() {
		for (PanelStackDisplay p: getMultiChannelDisplays()) {
			if (p!=null) return p;
		}

		return null;
	}
	
	public ArrayList< PanelStackDisplay> getMultiChannelDisplaysInOrder() {
		ArrayList<ZoomableGraphic> ia = this.getItemArray();
		ArrayList<PanelStackDisplay> output = new ArrayList< PanelStackDisplay>();
		for(ZoomableGraphic a:ia) {
			if (a instanceof PanelStackDisplay) {
				output .add((PanelStackDisplay) a);
			}
		}
		return output;
	}
	
	public ArrayList< PanelStackDisplay> getMultiChannelDisplaysInLayoutOrder() {
		this.updateImageOrder();
		return getMultiChannelDisplays();
	}
	

	/**Adds an additional multichannel image to the figure, creates panels as needed
	 * @return */
	public CompoundEdit2 addNovelMultiChannel(MultichannelDisplayLayer display, int start) {
		if(display==null) return null;
		int startpoint=this.getWorkingStack().getlastPanelsIndex()+1;
		if(start>0) startpoint=start;
		PanelStackDisplay principalMultiChannel = getPrincipalMultiChannel();
		boolean hasOne=principalMultiChannel!=null;//true if there is already a multichannel image in figure
		CompoundEdit2 output = new CompoundEdit2();
		
		if(!hasOne) {
			cropIfUserSelectionExists(display);
		}
		
		if (hasOne) {
			//IssueLog.log("d scale "+display.getPreprocessScale());
			display.setPreprocessScale(principalMultiChannel.getPreprocessScale());
			//IssueLog.log("d scale "+display.getPreprocessScale());
			principalMultiChannel.getStack().giveSettingsTo(display.getStack());
			display.getSetter().startPoint=startpoint;
			
			try {
				boolean mustResize=areSizesDifferent(principalMultiChannel, display) ;
			double pScale = principalMultiChannel.getPreprocessScale();
			double w = principalMultiChannel.getMultichanalWrapper().getDimensions().getWidth()/pScale;
			double h = principalMultiChannel.getMultichanalWrapper().getDimensions().getHeight()/pScale;
			
			if ( mustResize)CroppingDialog.showCropDialog(display.getSlot(), new Rectangle(0,0,(int) w,(int) h), 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		}
		else {
			
			setUpALayoutGraphicForLayerAndImage(display);
		}
		
		/**Tries to match the channel order and luts. this part is prone to errors so it is in a try catch*/
		if (hasOne) try {new ChannelOrderAndLutMatching().matchOrder(principalMultiChannel.getMultichanalWrapper(), display.getMultichanalWrapper(), 2);
				} catch (Throwable t) {IssueLog.log(t);}
		
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

	/**Written to crop an initial image based on the roi*/
	public static void cropIfUserSelectionExists(MultichannelDisplayLayer display) {
		if (display.getMultichanalWrapper().getSelectionRectangle(0)!=null) try{
			Rectangle b = display.getSlot().getMultichanalWrapper().getSelectionRectangle(0).getBounds();
			display.getMultichanalWrapper().eliminateSelection(0);
			boolean valid=true;//is the ROI valid
			if(b.height>3.5*b.width) valid=false;
			if(b.width>3.5*b.height) valid=false;
			if(b.width<25||b.height<25) valid=false;
			
			
			if(!valid) {
				CroppingDialog.showCropDialog(display.getSlot(), b, 0);
			} else {
				display.getSlot().applyCropAndScale(new PreProcessInformation(b, 0, display.getPreprocessScale()));
			}
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**returns true if the sizes are different enough to merit a cropping
	   is an incompatibility in the sizes*/
	private boolean areSizesDifferent(PanelStackDisplay principalMultiChannel, MultichannelDisplayLayer display) {
		Dimension2D p1 = principalMultiChannel.getMultichanalWrapper().getDimensions();
		Dimension2D p2 = display.getMultichanalWrapper().getDimensions();
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
	private boolean areSizesChangedForLayout(PanelStackDisplay principalMultiChannel, MultichannelDisplayLayer display) {
		if (principalMultiChannel==null) return false;
		Dimension2D p1 = principalMultiChannel.getMultichanalWrapper().getDimensions();
		Dimension2D p2 = display.getMultichanalWrapper().getDimensions();
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
			p=createLayoutForImage(display.getMultichanalWrapper(), display);
			add(p);
		} 
		p.generateCurrentImageWrapper();//
		return p;
	}
	
	/**creates a layout that is of the right dimensions for the panel stack display to use to place the image's panels*/
 static MontageLayoutGraphic createLayoutForImage(MultiChannelWrapper image, PanelStackDisplay panelStackDisplay) {
		MontageLayoutGraphic p = new MontageLayoutGraphic();
		//p.getPanelLayout().setNColumns(image.nChannels()+1);
		p.getPanelLayout().setHorizontalBorder(10);
		p.getPanelLayout().setVerticalBorder(10);
		setUpRowAndColsToFit(image, panelStackDisplay, p);
		return p;
	}
public static void setUpRowAndColsToFit(MultiChannelWrapper image, PanelStackDisplay panelStackDisplay,
		MontageLayoutGraphic p) {
	if (panelStackDisplay!=null) {
	int[] dims =  panelStackDisplay.getStack().getChannelUseInstructions().estimateBestMontageDims(image);
	
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
	
	/**Adds another multichannel image
	 * @return */
	public CompoundEdit2 nextMultiChannel(boolean openFile) {
		
		MultichannelDisplayLayer item = CurrentAppContext.getMultichannelContext().createMultichannelDisplay().creatMultiChannelDisplayFromUserSelectedImage(openFile, null);
		
		CompoundEdit2 output = nextMultiChannel(item);
		
		return output;
	}
	
	
	/**Adds another multichannel image
	 * @return */
	public CompoundEdit2 nextMultiChannel(MultichannelDisplayLayer item, int start) {
		CompoundEdit2 output = new CompoundEdit2();
	
			output.addEditToList(	
					addNovelMultiChannel(item, start)
					);
		DisplayedImageWrapper disp = getGraphicSetContainer() .getAsWrapper().getImageDisplay();
		
		
		
		
		output.addEditToList(	new  CanvasAutoResize().performUndoableAction(disp));
				
		return output;
	}
	public CompoundEdit2 nextMultiChannel(MultichannelDisplayLayer item) {
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
			PanelStackDisplay pan = getPanelForRowindex(i, type);//may return null
			
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
				if(position==null) position = item.getSnappingBehaviour(); else item.setSnappingBehaviour(position);
				addedItems.add(item);
				}
		}
		return addedItems;
	}


	/**If an entire Multichannel image's set of panels is contained in teh Row, Col, or panel in the layout, returns the image*/
	public PanelStackDisplay getPanelForRowindex(int i, int type) {
		BasicMontageLayout rowshapes = this.getMontageLayout().makeAltered(type);
		ArrayList<PanelStackDisplay> list = this.getMultiChannelDisplaysInOrder();
		for(PanelStackDisplay l: list) {
			if (rowshapes.getPanel(i).contains(l.getBoundOfUsedPanels().getCenterX(), l.getBoundOfUsedPanels().getCenterY())) {
				return l;
			}
		}
		return null;
	}
	
	public void showChannelUseOptions() {
		PanelStackDisplayOptions dialog = new PanelStackDisplayOptions((MultichannelDisplayLayer)getPrincipalMultiChannel(),getPrincipalMultiChannel().getStack(), null,false);
		/**adds a list of all the channel displays that are relevant*/
		ArrayList<PanelStackDisplay> all = getMultiChannelDisplaysInOrder();
		all.remove(getPrincipalMultiChannel());
		dialog.addAditionalDisplays(all);
		dialog.showDialog();
	}


	/**shows dialog to recreate figure panels*/
	public void recreateFigurePanels() {recreateFigurePanels(false);}
	public PanelStackDisplayOptions recreateFigurePanels(boolean cropToo) {
		
		ArrayList<PanelStackDisplay> d1 = getMultiChannelDisplaysInLayoutOrder();
		MultichannelDisplayLayer in = (MultichannelDisplayLayer)getPrincipalMultiChannel();
		PanelStackDisplayOptions dialog = new PanelStackDisplayOptions(in, in.getStack(),null, true);
		
		dialog.addAditionalDisplays(d1);
		dialog.setCurrentImageDisplay(CurrentSetInformerBasic. getCurrentActiveDisplayGroup());
		dialog.setModal(false);
		
		dialog.showDialog();
		this.fixLabelSpaces();
		return dialog;
	}
	/** obsolete code
	public void minMaxSet(int chan, double min, double max) {
		// TODO Auto-generated method stub
		
		ArrayList<MultiChannelWrapper> wraps = getAllSourceStacks() ;
		
		The real channel name will be checked against the channel names in each image
		  in the for loop. display ranges will be changed in either those with a match
		  or (if no match), those with the same number
		//String realName=getPressedWrapper().getRealChannelName(chan);
		
		StatusPanel.updateStatus("Setting Display Range "+min+", "+max);
		for(MultiChannelWrapper w: wraps) {
	
			w.setChannalMin(chan, min);
			w.setChannalMax(chan, max);
			StatusPanel.updateStatus("working on image "+w.getTitle());
		}
		
		updateMontageFromSource();
		//updateAllDisplaysWithRealChannel( realName);
		//presseddisplay.updateMontageFromSource();
		
		updateDisplay();
	}
	 * @return 
	*/


	/**Adds row labels based on names*/
	public CompoundEdit2 addRowOrColLabel(int type) {
		CompoundEdit2 edit = new CompoundEdit2();
		ArrayList<TextGraphic> output = addLabelsBasedOnImageNames(type);
		UndoAddManyItem many = new UndoAddManyItem(this, output);
		edit.addEditToList(many);
		UndoLayoutEdit many2 = new UndoLayoutEdit(getMontageLayout());
		fixLabelSpaces();
		many2.establishFinalLocations(); 
		edit.addEditToList(many2);
		DisplayedImageWrapper disp = getGraphicSetContainer() .getAsWrapper().getImageDisplay();

		disp.updateDisplay();
		return edit;
	}

	public void fixLabelSpaces() {
		this.getMontageLayoutGraphic().generateCurrentImageWrapper();
		getMontageLayout().getEditor().fitLabelSpacesToContents(getMontageLayout());
	}
	
	/**Adds a row label to the figure*/
	public ComplexTextGraphic addRowLabel(String st, int rowNum) {
		return FigureLabelOrganizer.addRowLabel(st, rowNum, this, this.getMontageLayoutGraphic());
	}
	
	public ComplexTextGraphic addColLabel(String st, int colNum) {
		return FigureLabelOrganizer.addColLabel(st, colNum, this, this.getMontageLayoutGraphic());
	}
	
	
	
	public ComplexTextGraphic addPanelLabel(String st, int colNum) {
		return FigureLabelOrganizer.addPanelLabel(st, colNum, this, this.getMontageLayoutGraphic());
	}
	
	
	
	
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
	
	/**given an object, moves up the layer tree untill it find a figure organizer to return*/
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

	public ArrayList<ChannelUseInstructions> getChannelUseInfo() {
		ArrayList<ChannelUseInstructions> alreadySwapped=new ArrayList<ChannelUseInstructions>();
		for (PanelStackDisplay d: getMultiChannelDisplaysInOrder()) {
			ChannelUseInstructions ins = d.getPanelManager().getStack().getChannelUseInstructions();
			if(alreadySwapped.contains(ins)) continue;
			alreadySwapped.add(ins);
		}
		return alreadySwapped;
	}

	/**hides any open images that are source images for this*/
	public void hideImages() {
		for(PanelStackDisplay m :getMultiChannelDisplaysInOrder())
			m.getSlot().hideImageWihtoutMessage();
	}

	public void updateChannelOrder(int type) {
		PanelOrder panelOrder = new PanelOrder(this);
		updateImageOrder();
		panelOrder.updateChanOrder(type);
	}
	
	public void updateImageOrder() {
		PanelOrder panelOrder = new PanelOrder(this);
		ArrayList<PanelStackDisplay> layoutOrder = panelOrder.getDisplaysInLayoutImageOrder();
		imageOrderComparator lorder = new PanelOrder.imageOrderComparator(layoutOrder);

		Collections.sort(displays, lorder);
	}
	
	
	
}
