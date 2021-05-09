/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
 * Date Modified: May 9, 2021
 * Version: 2021.1
 */
package figureOrganizer;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Icon;

import appContext.CurrentAppContext;
import applicationAdapters.DisplayedImage;
import channelMerging.ChannelOrderAndLutMatching;
import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import channelMerging.MultiChannelImage;
import channelMerging.PreProcessInformation;
import figureEditDialogs.PanelStackDisplayOptions;
import figureOrganizer.PanelOrderCorrector.ImageOrderComparator;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import iconGraphicalObjects.IconUtil;
import imageDisplayApp.CanvasOptions;
import imageMenu.CanvasAutoResize;
import imageScaling.ScaleInformation;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.TransformFigure;
import locatedObject.AttachmentPosition;
import logging.IssueLog;
import menuUtil.HasUniquePopupMenu;
import objectDialogs.CroppingDialog;
import popupMenusForComplexObjects.FigureOrganizingSuplierForPopup;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoAddManyItem;
import undo.UndoLayoutEdit;

/**A figure organizing layer*/
public class FigureOrganizingLayerPane extends GraphicLayerPane implements SubFigureOrganizer, HasUniquePopupMenu {

	
	/**if crop area is below this value, asks user to re-draw the crop area */
	public static int MIN_WIDTH_FOR_CROP_AREA = 25;
	/**if crop area width/hieght or height/width is above this ratio, will ask user to re-draw*/
	public static double MAX_ASPECT_RATIO_FOR_CROP_AREA = 3.5;




	{description= "A Figure Organizing Layer";}
	
	/**set to true if the crop dialog shoule be skipped*/
	public static boolean suppressCropDialog=false;
	
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

	
	/**combines the panel lists from all the images in the figure into one list
	 * this method does not include inset panels*/
	public PanelList getAllPanelLists() {
		PanelList output = new PanelList();
		for(ImageDisplayLayer d: getMultiChannelDisplays()){
			if (d==null) continue;
			output.add(d.getPanelList());
		}
		return output;
	}
	
	/**user the panel setter to determine grid locations for each panel in the the list of panels
	 * and but them into the layout locations*/
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
	
	/**looks for figure organizing layers within the target layer. updates all the panels*/
	public static void updateAllPanelsFromSource(GraphicLayer l) {
		ArrayList<?> subLayers = l.getSubLayers();
		for (Object layer: subLayers) {
			if (layer instanceof FigureOrganizingLayerPane) {
				((FigureOrganizingLayerPane) layer).updatePanelsAndLabelsFromSource();
			}
		}
	}

	/**not yet implemented*/
	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

	/**not yet implemented*/
	@Override
	public void supress() {
		
		
	}
	
	/**Adds an item to the layer*/
	@Override
	public void addItemToLayer(ZoomableGraphic z) {
		super.addItemToLayer(z);
		if (z instanceof  ImageDisplayLayer) {
			ImageDisplayLayer psdz = (ImageDisplayLayer) z;
			getMultiChannelDisplays().add(psdz);
		}
	}
	
	/**removes an item from the layer*/
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
public BasicLayout getLayout() {
	return getMontageLayout();
}
/**returns the layout */
public BasicLayout getMontageLayout() {
	DefaultLayoutGraphic mm = getMontageLayoutGraphic();
	if (mm!=null) return mm.getPanelLayout();
	return null;
}
/**gets the graphic that displays the layout and allows layout editing by user*/
public DefaultLayoutGraphic getMontageLayoutGraphic() {
	for(ZoomableGraphic a:super.getItemArray()) {
		if (a==null) continue;
		if (a instanceof DefaultLayoutGraphic) {
			DefaultLayoutGraphic m=(DefaultLayoutGraphic) a;
			
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
	
	/**returns a list of all panel managers for the main panels in the figure*/
	public ArrayList< PanelManager> getPanelManagers() {
		ArrayList<PanelManager> output = new ArrayList< PanelManager> ();
		for(ImageDisplayLayer d: displays) {
			output.add(d.getPanelManager());
		}
		
		return output;
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
	
	/**returns the display layers within the figure. their order in the returned list is determined by their
	 * xy positions*/
	public ArrayList< ImageDisplayLayer> getMultiChannelDisplaysInLayoutOrder() {
		this.updateImageOrder();
		return getMultiChannelDisplays();
	}
	

	/**Adds an additional multi-dimensional  image to the figure, creates panels for the new image
	 * and sets up the images properties to fit
	 @param start the panel index where the added item will be placed
	 @param display the added multi-channel image
	 * @return */
	public CombinedEdit addNovelMultiChannel(MultichannelDisplayLayer display, int start) {
		if(display==null) return null;
		int startpoint=this.getAllPanelLists().getlastPanelsGridIndex()+1;
		if(start>0) startpoint=start;
		ImageDisplayLayer principalMultiChannel = getPrincipalMultiChannel();
		boolean hasOne=principalMultiChannel!=null;//true if there is already a multichannel image in figure
		CombinedEdit output = new CombinedEdit();
		
		/**if current is the first multichannel to be added to the figure, sets up an innitial crop area*/
		if(!hasOne&&!display.cropShown) {
			cropIfUserSelectionExists(display);
		}
		
		if (hasOne) {
			
			display.setPreprocessScale(principalMultiChannel.getPreprocessScale());
			principalMultiChannel.getPanelList().giveSettingsTo(display.getPanelList());
			display.getSetter().startPoint=startpoint;
			
			try {
				boolean mustResize=areSizesDifferent(principalMultiChannel, display) ;
			ScaleInformation pScale = principalMultiChannel.getPreprocessScale();
			double w = principalMultiChannel.getMultiChannelImage().getDimensions().getWidth()/pScale.getScale();
			double h = principalMultiChannel.getMultiChannelImage().getDimensions().getHeight()/pScale.getScale();
			
			if ( (mustResize||display.getPanelList().getChannelUseInstructions().selectsSlicesOrFrames(display.getMultiChannelImage())) &&!suppressCropDialog)
				{
					
								CroppingDialog crop = CroppingDialog.showCropDialog(display.getSlot(), new Rectangle(0,0,(int) w,(int) h), 0);
								display.getPanelList().getChannelUseInstructions().shareViewLocation(display.getSlot().getDisplaySlice());
								display.cropShown=true;
							
							if (crop.wasCanceled()) {
								this.remove(display);//the user may chose not to add the image by clicking cancel
								return output;
								}
					
					
				}
			
			} catch (Exception e) {
				IssueLog.logT(e);
			}
		
		
		}
		else {
			
			setUpALayoutGraphicForLayerAndImage(display);
		}
		
		/**Tries to match the channel order and luts. this part is prone to errors so it is in a try catch*/
		if (hasOne) try {new ChannelOrderAndLutMatching().matchChannels(principalMultiChannel.getMultiChannelImage(), display.getMultiChannelImage(), ChannelOrderAndLutMatching.ORDER_AND_COLOR);
				} catch (Throwable t) {IssueLog.logT(t);}
		
		this.add(display);
		output.addEditToList(new UndoAddItem(this, display));
		
		DefaultLayoutGraphic targetLayout = getMontageLayoutGraphic();
		UndoLayoutEdit lUndo = new UndoLayoutEdit(targetLayout);
		
		/**in the specific case of a layout with one panel, ensures that the addition does to the next column and not the next row*/
			if (targetLayout!=null) {	
				BasicLayout pl = targetLayout.getPanelLayout();
					if (pl!=null&&pl.rowmajor&& pl.nColumns()==1&&pl.nRows()==1) {
						pl.setNColumns(2);
						
					} else
					if (pl!=null&&!pl.rowmajor&& pl.nColumns()==1&&pl.nRows()==1) {
						pl.setNRows(2);
						
					}
					
			}
		
		display.eliminateAndRecreate(!hasOne, false, !hasOne);//since this method alters the layout, a layout undo is needed. all other actions done do not need to be undone since the new objects are removed
		output.addEditToList(lUndo);
		
		if (hasOne) {
			display.eliminateChanLabels();//if another multichannel exists, then the new image's channel labels are not needed
		}
		boolean alterLayout = areSizesChangedForLayout(principalMultiChannel, display);
		if(alterLayout) {
			
			this.getMontageLayoutGraphic().generateCurrentImageWrapper();//sets of a list of the contents
			lUndo = new UndoLayoutEdit(targetLayout);
			this.getLayout().getEditor().alterPanelWidthAndHeightToFitContents(getLayout());
			output.addEditToList(lUndo);
		}
		
		return output;
	}

	/**Sometimes, a user or programmer will have set a cropping region
	 Written to crop an initial image based on the roi. 
	 If the crop area is strange, displays a dialog asking the user
	 to change it*/
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
		if(b.height>MAX_ASPECT_RATIO_FOR_CROP_AREA*b.width) valid=false;
		if(b.width>MAX_ASPECT_RATIO_FOR_CROP_AREA*b.height) valid=false;
		if(b.width<MIN_WIDTH_FOR_CROP_AREA||b.height<MIN_WIDTH_FOR_CROP_AREA) valid=false;	
		
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
		
		if(!valid && !suppressCropDialog) {
			
			CroppingDialog.showCropDialog(display.getSlot(), b, 0);
			display.cropShown=true;
			IssueLog.log("Calling crip dialog because of invalid");
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

	public DefaultLayoutGraphic setUpALayoutGraphicForLayerAndImage( MultichannelDisplayLayer display) {
		/**creates a MontageLayout Graphic if the layer has none*/
		DefaultLayoutGraphic p=getMontageLayoutGraphic();
		if (p==null) {
			p=createLayoutForImage(display.getMultiChannelImage(), display);
			add(p);
		} 
		p.generateCurrentImageWrapper();//
		return p;
	}
	
	/**creates a layout that is of the right dimensions for the panel stack display to use to place the image's panels*/
 static DefaultLayoutGraphic createLayoutForImage(MultiChannelImage image, ImageDisplayLayer panelStackDisplay) {
		DefaultLayoutGraphic p = new DefaultLayoutGraphic();
		//p.getPanelLayout().setNColumns(image.nChannels()+1);
		p.getPanelLayout().setHorizontalBorder(10);
		p.getPanelLayout().setVerticalBorder(10);
		setUpRowAndColsToFit(image, panelStackDisplay, p);
		return p;
	}
public static void setUpRowAndColsToFit(MultiChannelImage image, ImageDisplayLayer panelStackDisplay,
		DefaultLayoutGraphic p) {
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
			
			
			/**if the container can be found, there needs to be a canvas resize*/
			if(getGraphicSetContainer()!=null) {
					DisplayedImage disp = getGraphicSetContainer() .getAsWrapper().getImageDisplay();
					if (CanvasOptions.current.resizeCanvasAfterEdit)
						output.addEditToList(	new  CanvasAutoResize(true).performUndoableAction(disp));
			}
		return output;
	}
	
	/**Adds a multi channel image to the figure. returns an undoable edit*/
	public CombinedEdit nextMultiChannel(MultichannelDisplayLayer item) {
		return nextMultiChannel(item,-1);
	}

	/**Attempts to identify the names of the images present in either panels, rows or columns
	  adds label accordingly*/
	public ArrayList<TextGraphic> addLabelsBasedOnImageNames(int type) {
		IssueLog.sytemprint=true;
		
		BasicLayout ml = getMontageLayout();
		ArrayList<TextGraphic> addedItems=new ArrayList<TextGraphic>();
		int limit = ml.nRows();
		if(type==BasicLayout.COLS) limit=ml.nColumns();
		if(type==BasicLayout.PANELS) limit=ml.nPanels();
		AttachmentPosition position=null;
		for(int i=1; i<=limit; i++) {
			TextGraphic item=null;
			ImageDisplayLayer pan = getDisplayLayerForRowindex(i, type);//may return null
			
			boolean useImageNames = (pan!=null) && LabelCreationOptions.current.useImageNames;
			
			if (!useImageNames) {
				item = FigureLabelOrganizer.addLabelOfType(type, i,  this, this.getMontageLayoutGraphic());
			}
			
			if (useImageNames) {
				String text=pan.getMultiChannelImage().getTitle();
				if(text.startsWith("DUP")) text=text.replace("DUP","");//imagej adds dup
				if(text.endsWith(".tiff")) text=text.replace(".tiff","");//imagej tiffs dont need that in their label
				if (text!=null&&text.length()>LabelCreationOptions.current.clipLabels) {
					{text=text.substring(0, (int)LabelCreationOptions.current.clipLabels);}
				}
				
				if (type==BasicLayout.ROWS)
					item=addRowLabel(text, i);
				if (type==BasicLayout.COLS)
					item=addColLabel(text, i);
				if (type==BasicLayout.PANELS)//added on nov 3 thinking that it would fix an issue with generate all labels that I have only been seeing on the Mac
					item=addPanelLabel(text, i);
			}
			
			if (item!=null)
				{
				if(position==null) position = item.getAttachmentPosition(); else item.setAttachmentPosition(position);
				addedItems.add(item);
				}
		}
		return addedItems;
	}


	/**If an entire Multichannel image's set of panels is contained in teh Row, Col, or panel in the layout, returns the image*/
	public ImageDisplayLayer getDisplayLayerForRowindex(int i, int type) {
		BasicLayout rowshapes = this.getMontageLayout().makeAltered(type);
		ArrayList<ImageDisplayLayer> list = this.getMultiChannelDisplaysInOrder();
		for(ImageDisplayLayer l: list) {
			Rectangle boundOfUsedPanels = l.getBoundOfUsedPanels();
			Rectangle2D panel = rowshapes.getPanel(i);
			
			if (panel.contains(boundOfUsedPanels.getCenterX(), boundOfUsedPanels.getCenterY())) {
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
	public void recreateFigurePanels() {PanelStackDisplayOptions.recreateFigurePanels(this, false);}


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
		PanelOrderCorrector panelOrder = new PanelOrderCorrector(this);
		updateImageOrder();
		panelOrder.updateChanOrder(type);
		
	}
	
	/**Determines the order of the multidimensional images within the layout based on the x/y geometry of the layout
	 The list of images within this figure organizer is reordered to store that order.
	  */
	public void updateImageOrder() {
		PanelOrderCorrector panelOrder = new PanelOrderCorrector(this);
		ArrayList<ImageDisplayLayer> layoutOrder = panelOrder.getDisplaysInLayoutImageOrder();
		ImageOrderComparator lorder = new PanelOrderCorrector.ImageOrderComparator(layoutOrder);

		Collections.sort(displays, lorder);
	}


	/**
	creates a new multichannel display layer and adds it to the figure. 
	If preprocess information is not given, then the user will be shown a dialog to select the crop area
	 */
	public static CombinedEdit createSecondView(FigureOrganizingLayerPane f, MultichannelDisplayLayer displayLayer, PreProcessInformation p) {
		MultichannelDisplayLayer secondView = displayLayer.similar();
		secondView.setSlot(secondView.getSlot().createPartner());
		secondView.getSlot().redoCropAndScale();
		secondView.setLaygeneratedPanelsOnGrid(true);
		if (p!=null) {
				secondView.getSlot().applyCropAndScale(p);
			} else {
					PreProcessInformation mods =secondView.getSlot().getModifications();
					Rectangle r=null;
					if(mods==null||mods.getRectangle()==null) {
						int w = displayLayer.getMultiChannelImage().getDimensions().width/2;
						int h = displayLayer.getMultiChannelImage().getDimensions().height/2;
						r=new Rectangle(0,0,w,h);
					} else {
						r=mods.getRectangle();
					}
					
					CroppingDialog.showCropDialog(secondView.getSlot(), r, 0);
					displayLayer.cropShown=true;
					
			}
		
		return f.nextMultiChannel(secondView);
	}


	
	
	/**returns a transform object for this figure*/
	public TransformFigure transform() {
		return new TransformFigure(this,getMontageLayoutGraphic());
	}



	
}
