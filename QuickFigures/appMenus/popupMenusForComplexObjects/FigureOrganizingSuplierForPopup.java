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
 * Date Modified: Feb 18, 2023
 * Version: 2023.1
 */
package popupMenusForComplexObjects;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.undo.UndoableEdit;

import addObjectMenus.FigureAdder;
import advancedChannelUseGUI.AdvancedChannelUseGUI;
import appContext.MakeFigureAfterFileOpen;
import applicationAdapters.CanvasMouseEvent;
import channelMerging.CSFLocation;
import channelMerging.ChannelEntry;
import channelMerging.MultiChannelImage;
import channelMerging.ImageDisplayLayer;
import channelMerging.PreProcessInformation;
import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import figureEditDialogs.ScaleLevelInputDialog;
import figureEditDialogs.WindowLevelDialog;
import figureFormat.TemplateUserMenuAction;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.LabelCreationOptions;
import figureOrganizer.MultichannelDisplayLayer;
import genericTools.NormalToolDragHandler;
import graphicActionToolbar.CurrentFigureSet;
import graphicActionToolbar.CurrentSetInformer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BandMarkers.BandMarkLayer;
import graphicalObjects_BandMarkers.MarkLabelCreationOptions;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import handles.layoutHandles.AddLabelHandle;
import iconGraphicalObjects.ChannelUseIcon;
import iconGraphicalObjects.CropIconGraphic;
import iconGraphicalObjects.IconUtil;
import icons.SourceImageTreeIcon;
import icons.ToolIconWithText;
import imageDisplayApp.CanvasOptions;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageScaling.Interpolation;
import imageScaling.ScaleInformation;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;
import menuUtil.BasicSmartMenuItem;
import menuUtil.PopupMenuSupplier;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import objectDialogs.CroppingDialog;
import objectDialogs.CroppingDialog.CropDialogContext;
import standardDialog.StandardDialog;
import storedValueDialog.StoredValueDilaog;
import ultilInputOutput.FileChoiceUtil;
import undo.AbstractUndoableEdit2;
import undo.CanvasResizeUndo;
import undo.ChannelDisplayUndo;
import undo.CombinedEdit;
import undo.Edit;
import undo.PreprocessChangeUndo;
import undo.UndoAddItem;
import undo.UndoLayoutEdit;
import undo.UndoScalingAndRotation;

/**A menu for a figure organizing layer. This is a rather complex menu with many options*/
public class FigureOrganizingSuplierForPopup implements PopupMenuSupplier, LayoutSpaces, ActionListener {


	FigureOrganizingLayerPane figureOrganizingLayerPane;
	BasicSmartMenuItem addImageFromFileButton;
	private JMenuItem addOpenImageFromList;
	private JMenuItem rowLabelButton;
	private JMenuItem columnLabelButton;
	private JMenuItem recreatePanelsButton;
	private JMenuItem minMaxButton5;
	private JMenuItem windowLevelButton;
	private JMenuItem channelUseOptionsButton;
	private JMenuItem panelLabelButton;
	private JMenuItem recropPanelsButton;
	private JMenuItem reScalePanelsButton;
	private JMenuItem rePanelSizePanelsButton;
	private JMenuItem rePPIPanelsButton;
	
	
	public FigureOrganizingSuplierForPopup(FigureOrganizingLayerPane figureOrganizingLayerPane) {
		this.figureOrganizingLayerPane=figureOrganizingLayerPane;
	}



	@Override
	public JPopupMenu getJPopup() {
		SmartPopupJMenu jj = new SmartPopupJMenu();
		 addMenus(jj);
		return jj;
	}


	/**Adds the menu items from this popup to an arbitrary container*/
	protected void addMenus(Container jj) {
		JMenu imagesMenu = new SmartJMenu("Images", new SourceImageTreeIcon());	
		
		JMenu addImage=new SmartJMenu("Add Image",new SourceImageTreeIcon());
		
		jj.add(addImage);
		addImageFromFileButton = new BasicSmartMenuItem("Images From Files");
		addImage.add(addImageFromFileButton);
		addImageFromFileButton.addActionListener(this);
		
		addOpenImageFromList = new BasicSmartMenuItem("Currently Open Image");
		addImage.add(addOpenImageFromList);
		addOpenImageFromList.addActionListener(this);
		
	

		SmartJMenu labelMenu = new SmartJMenu("Add Labels", ComplexTextGraphic.createImageIcon());
		
			 rowLabelButton = new BasicSmartMenuItem("Generate Row Labels", new ToolIconWithText(0, ROW_OF_PANELS).getMenuVersion());
			 labelMenu.add(rowLabelButton);
				rowLabelButton.addActionListener(this);
				
				 columnLabelButton = new BasicSmartMenuItem("Generate Column Labels", new ToolIconWithText(0, COLUMN_OF_PANELS).getMenuVersion());
				 labelMenu.add(columnLabelButton);
					columnLabelButton.addActionListener(this);
					
					panelLabelButton = new BasicSmartMenuItem("Generate Panel Labels", new ToolIconWithText(0, PANELS).getMenuVersion());
					 labelMenu.add(panelLabelButton);
						panelLabelButton.addActionListener(this);
						
				
						 
						/**Adds more options to the label menu based on annotations on the method calls within this class*/
						new MenuItemExecuter(this).addToJMenu(labelMenu);
					jj.add(labelMenu);
					
					
					
					
				recropPanelsButton= new BasicSmartMenuItem("Re-Crop All Images");
				recropPanelsButton.addActionListener(this);
				recropPanelsButton.setIcon( CropIconGraphic.createsCropIcon());
				imagesMenu.add(recropPanelsButton);
				
				reScalePanelsButton= new BasicSmartMenuItem("Re-Set Scale for All Images");
				reScalePanelsButton.addActionListener(this);
				imagesMenu.add(reScalePanelsButton);
				
				rePanelSizePanelsButton= new BasicSmartMenuItem("Re-size panels without scale Re-Set");
				rePanelSizePanelsButton.addActionListener(this);
				imagesMenu.add(rePanelSizePanelsButton);
				
				
				
				recreatePanelsButton = new BasicSmartMenuItem("Recreate All Panels");
				jj.add(recreatePanelsButton);
							recreatePanelsButton.addActionListener(this);
				jj.add(imagesMenu);
				rePPIPanelsButton=new BasicSmartMenuItem("Re-Set Pixel Density for All Images");
				rePPIPanelsButton.addActionListener(this);
				imagesMenu.add(rePPIPanelsButton);
				
				
			
				
				if (figureOrganizingLayerPane.getMontageLayoutGraphic()!=null) {
					FigureScalerMenu figureScaler = new FigureScalerMenu(figureOrganizingLayerPane.getMontageLayoutGraphic());
					imagesMenu.add(figureScaler.createRescaleMenuItem());
					jj.add(figureScaler);
				}	
				
				
				
							
				JMenu chanMen=new SmartJMenu("All Channels");
					
					
					
					
					 channelUseOptionsButton = new BasicSmartMenuItem("Channel Use", new ChannelUseIcon());
					
					 chanMen.add(channelUseOptionsButton);
						channelUseOptionsButton.addActionListener(this);
						
						
					
						
					 minMaxButton5 = new BasicSmartMenuItem("Min/Max");
					 minMaxButton5.setIcon(IconUtil.createBrightnessIcon());
					 chanMen.add(minMaxButton5);
						minMaxButton5.addActionListener(this);
						
						 windowLevelButton = new BasicSmartMenuItem("Window/Level");
						 chanMen.add(windowLevelButton);
						 windowLevelButton.setIcon(IconUtil.createBrightnessIcon());
							windowLevelButton.addActionListener(this);
							try {addRecolorMenu(chanMen);} catch (Throwable t) {IssueLog.logT(t);};
							jj.add(chanMen);
							SmartJMenu excluders = this.getMenuContext().createChannelMergeMenu(ChannelPanelEditingMenu.EXCLUDED_CHANNEL_AND_DONT_MERGE);
							excluders.setIcon(new ChannelUseIcon());
							jj.add(excluders);
							chanMen.add(new showAdvancedChannelUse2());
							jj.add(TemplateUserMenuAction.createFormatMenu(figureOrganizingLayerPane));
							
							
	}
	
	/**Adds the recolor channels menu*/
	public void addRecolorMenu(JMenu j) {
		
		MultiChannelImage mw = getPrimaryMultichannelWrapper();
		ArrayList<ChannelEntry> iFin = mw.getChannelEntriesInOrder();
		
		ChannelPanelEditingMenu bit = new ChannelPanelEditingMenu(figureOrganizingLayerPane, iFin.get(0).getOriginalChannelIndex());
		
		
		bit.addChenEntryColorMenus( j, iFin);
	}



	public MultiChannelImage getPrimaryMultichannelWrapper() {
		return figureOrganizingLayerPane.getPrincipalMultiChannel().getMultiChannelImage();
	}





	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		CombinedEdit undo=null ;
		if (source==addImageFromFileButton) {
			
			
			
			ArrayList<File> fileList = FileChoiceUtil.getFileArray();
			
			fileList = NormalToolDragHandler.stichFilesIntoMultiChannel(this.figureOrganizingLayerPane, fileList);
			
			undo=new NormalToolDragHandler(null).openFileListAndAddToFigure((ImageWindowAndDisplaySet) this.addImageFromFileButton.getLastMouseEvent().getAsDisplay(), fileList, true, null, this.figureOrganizingLayerPane.getMontageLayoutGraphic(), this.figureOrganizingLayerPane.getMontageLayoutGraphic(), this.figureOrganizingLayerPane);
				
			
		}
		
		if (source==addOpenImageFromList) {
			undo=figureOrganizingLayerPane.nextMultiChannel(false);
		}
		
		if (source==rowLabelButton||source==columnLabelButton||source==panelLabelButton) {
			int type=BasicLayout.ROWS;
			if(source==columnLabelButton)  type=BasicLayout.COLS;
			if(source==panelLabelButton)  type=BasicLayout.PANELS;
			
			CombinedEdit many = figureOrganizingLayerPane.addRowOrColLabel(type);
			
			/**Adds to the undo manager*/
			figureOrganizingLayerPane.getUndoManager().addEdit(many);
		}
		
		
		
		if (source==recreatePanelsButton) {
            
			figureOrganizingLayerPane.recreateFigurePanels();
        }
		
		if (source ==recropPanelsButton) {
			
			undo= recropAll();
		}
		
		if (source ==reScalePanelsButton) {
			undo=showReScaleAll();
		}
		
		if (source ==rePanelSizePanelsButton) {
			undo=showReDoPanelSizeAll();
		}
		
		if (source ==rePPIPanelsButton) {
			undo=showRePPIAll();
		}
		
		ChannelPanelEditingMenu bit = getMenuContext();
		if (source==minMaxButton5) {
			CombinedEdit undoMinMax = ChannelDisplayUndo.createMany(figureOrganizingLayerPane.getAllSourceImages(), bit);
			undo=undoMinMax;
			WindowLevelDialog.showWLDialogs(getPrimaryMultichannelWrapper().getChannelEntriesInOrder(), getPrimaryMultichannelWrapper(), bit, WindowLevelDialog.MIN_MAX, undoMinMax);
			
		}
		if (source==windowLevelButton) {
			CombinedEdit undoMinMax = ChannelDisplayUndo.createMany(figureOrganizingLayerPane.getAllSourceImages(), bit);
			undo=undoMinMax;
			WindowLevelDialog.showWLDialogs(getPrimaryMultichannelWrapper().getChannelEntriesInOrder(), getPrimaryMultichannelWrapper(), bit,  WindowLevelDialog.WINDOW_LEVEL, undoMinMax);
			
		}
		
		if (source==channelUseOptionsButton){
			figureOrganizingLayerPane.showChannelUseOptions();
			
		}
		
		figureOrganizingLayerPane.getUndoManager().addEdit(undo);
	}







	/**
	generates a channel panel editing menu context for this popup menu
	 */
	public ChannelPanelEditingMenu getMenuContext() {
		return new ChannelPanelEditingMenu( figureOrganizingLayerPane, ChannelPanelEditingMenu.ALL_IMAGES_IN_CLICKED_FIGURE);
	}

	

	/**returns a label editor for the given text item*/
	public EditLabels getLabelEditorMenuItemFor(TextGraphic t) {
		int gridSnap = t.getAttachmentPosition().getGridSpaceCode();
		EditLabels output = new EditLabels(gridSnap, figureOrganizingLayerPane.getMontageLayoutGraphic(), t);
	
		if(output.getLabels(t).size()==0) {
			
			return null;
		}
		return output;
	}

/**Opens a dialog to recrop all the panels
 * @return */
	public CombinedEdit recropAll() {
		MultichannelDisplayLayer crop1 = (MultichannelDisplayLayer) figureOrganizingLayerPane.getPrincipalMultiChannel();
		ArrayList<ImageDisplayLayer> all = figureOrganizingLayerPane.getMultiChannelDisplays();
		
		return recropManyImages(crop1, all);
	}


	/**shows a dialog for changing the drop area for many multichannel images within the figure*/
public static CombinedEdit recropManyImages(MultichannelDisplayLayer crop1, ArrayList<? extends ImageDisplayLayer> all) {
	
	
	if(crop1==null) {
		return null;
	}
	CombinedEdit output = new CombinedEdit();
	CropDialogContext context = new CroppingDialog.CropDialogContext(all.size()+1, crop1.getFigureType());
	
	output.addEditToList(
			showRecropDisplayDialog( crop1, null, null, context)
			);
	PreProcessInformation modifications = crop1.getSlot().getModifications();
	Rectangle r1=null;
	Dimension d1;
	Interpolation interpolate=null;
	
	if (modifications!=null) {
		r1= modifications.getRectangle();
		interpolate=modifications.getInterpolationType();
	}
	
	
	
	if (r1==null) {
		d1=crop1.getMultiChannelImage().getDimensions();
	}else d1=new Dimension(r1.width, r1.height);
	
	
	
	for(ImageDisplayLayer cropTarget2: all) {
		if(cropTarget2==crop1) continue;
		output.addEditToList(
				showRecropDisplayDialog( (MultichannelDisplayLayer) cropTarget2, d1, interpolate, context)
		);
	}
	if (CanvasOptions.current.resizeCanvasAfterEdit)
		output.addEditToList(
				CurrentFigureSet.canvasResizeUndoable()
				);
	return output;
}


	/**shows a cropping dialog*/
	public static CombinedEdit showRecropDisplayDialog(MultichannelDisplayLayer display, Dimension dim, Interpolation interpolate, CropDialogContext context) {
		PreProcessInformation original = display.getSlot().getModifications();
		display.getPanelManager().setupViewLocation();
		PreprocessChangeUndo undo1 = new PreprocessChangeUndo(display);
		CSFLocation csfInitial = display.getSlot().getDisplaySlice().duplicate();
		
		
		CroppingDialog cd = CroppingDialog.showCropDialogOfSize(display.getSlot(), dim, context);
		
		onViewLocationChange(display, csfInitial, display.getSlot().getDisplaySlice());
		
		if (display.getSlot().getModifications()!=null&&display.getSlot().getModifications().isSame(original)
				) {
			return null;
		}
		undo1.establishFinalLocations();
		
		
		return new CombinedEdit(cd.additionalUndo,undo1, updateRowColSizesOf(display), cd.additionalUndo);
		
	}


	/**Called if the user switches slices or channels*/
	private static void onViewLocationChange(MultichannelDisplayLayer display, CSFLocation i,
			CSFLocation f) {
		
		if (!display.getPanelManager().selectsSlicesOrFrames()) return;
		
		if (f.changesT(i)  )  {
			display.getPanelManager().performReplaceOfIndex(
					CSFLocation.frameLocation(i.frame), CSFLocation.frameLocation(f.frame)
					);
			
		}
			if (f.changesZ(i)  )  {
				display.getPanelManager().performReplaceOfIndex(
						CSFLocation.sliceLocation(i.slice), CSFLocation.sliceLocation(f.slice)
						);
				}	
	}


	/**called to resize the layout in order to match the dimensions of object within the layout*/
	public static UndoLayoutEdit updateRowColSizesOf(MultichannelDisplayLayer display) {
		
		if (display.getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) display.getParentLayer();
			DefaultLayoutGraphic l = f.getMontageLayoutGraphic();
			if(l!=null)
			{
				l.generateCurrentImageWrapper();
				UndoLayoutEdit undo = new UndoLayoutEdit(l);
				l.getEditor().alterPanelWidthAndHeightToFitContents(l.getPanelLayout());
				undo.establishFinalLocations();
				return undo;
			}
		}
		return null;
	}
	
	/**shows a dialog for changing the scale factor of many multichannel images within the figure*/
	CombinedEdit showReScaleAll() {
		CombinedEdit output     = showReScaleAllDisplayDialog((MultichannelDisplayLayer) figureOrganizingLayerPane.getPrincipalMultiChannel());
		CanvasResizeUndo output2 = CurrentFigureSet.canvasResizeUndoable();
		return new CombinedEdit(output, output2);
	}


	/**shows a dialog for changing the panelSize within the figure*/
	CombinedEdit showReDoPanelSizeAll() {
		CombinedEdit output     = showReDoPanelSizeAllDisplayDialog((MultichannelDisplayLayer) figureOrganizingLayerPane.getPrincipalMultiChannel());
		CanvasResizeUndo output2 = CurrentFigureSet.canvasResizeUndoable();
		return new CombinedEdit(output, output2);
	}


	/**shows a dialog for changing the scale factor of many multichannel images within the figure*/
	 CombinedEdit showReScaleAllDisplayDialog(MultichannelDisplayLayer display) {
		 CombinedEdit output = new CombinedEdit();
		ScaleInformation newScale = showRescaleDialogSingleFor(display);
		
		output.addEditToList(
				applyNewScaleTo(display, newScale)
				);
		
		ArrayList<ImageDisplayLayer> all = figureOrganizingLayerPane.getMultiChannelDisplays();
		
		for(ImageDisplayLayer crop2: all) {
			if(crop2==display) continue;
			
				output.addEditToList(
							applyNewScaleTo((MultichannelDisplayLayer) crop2, newScale));
		}
		
		return output;
	}
	 
	 /**shows a dialog for changing the scale factor of many multichannel images within the figure*/
	 CombinedEdit showReDoPanelSizeAllDisplayDialog(MultichannelDisplayLayer display) {
		 CombinedEdit output = new CombinedEdit();
		double newScale = showDoPanelSizeDialogSingleFor(display);
		
		ArrayList<ImageDisplayLayer> all = figureOrganizingLayerPane.getMultiChannelDisplays();
		
		for(ImageDisplayLayer crop2: all) {
				output.addEditToList(
							applyNewPanelSizeTo((MultichannelDisplayLayer) crop2, newScale, true));
		}
		
		return output;
	}
	 
	


	/**shows a dialog for the user to input a pixel density for all the images*/
	 private CombinedEdit showRePPIAll() {
		 CombinedEdit output = new CombinedEdit();
		 double newPPI = showPPISingleImage(figureOrganizingLayerPane.getPrincipalMultiChannel());
		 ArrayList<ImageDisplayLayer> all = figureOrganizingLayerPane.getMultiChannelDisplays();
		 for(ImageDisplayLayer crop2: all) {
			 output.addEditToList(
				((MultichannelDisplayLayer) crop2).getPanelManager().changePPI(newPPI)
				);
			}
		 return output;
		}



	 /**shows a dialog for the user to input a pixel density for an image*/
	private double showPPISingleImage(ImageDisplayLayer principalMultiChannel) {
		ImagePanelGraphic panel = principalMultiChannel.getPanelManager().getPanelList().getPanels().get(0).getPanelGraphic();
		double ppi = panel.getQuickfiguresPPI();
		double newppi=StandardDialog.getNumberFromUser("Input Pixels per inch ", ppi);
		return newppi;
	}


	/**shows a dialog for changing the scale factor one image*/
	public static ScaleInformation showSingleImageRescale(MultichannelDisplayLayer display) {
		ScaleInformation newScale = showRescaleDialogSingleFor(display);
		
		applyNewScaleTo(display, newScale);
		return newScale;
	}



	/**shows a rescale dialog for the single image*/
	protected static ScaleInformation showRescaleDialogSingleFor(MultichannelDisplayLayer display) {
		PreProcessInformation original = display.getSlot().getModifications();
		
		ScaleInformation oldScale =new ScaleInformation();
		if (original!=null)
				oldScale= original.getScaleInformation();
		ScaleInformation newScale = ScaleLevelInputDialog.showUserTheDialog(oldScale);
		return newScale;
	}
	
	/**a dialog to scale the panel objects only*/
	protected static double showDoPanelSizeDialogSingleFor(MultichannelDisplayLayer display) {
		
		double original = display.getPanelList().getPanelGraphics().get(0).getRelativeScale();
		
		double newScale = StandardDialog.getNumberFromUser("Change Panel Size",1);
		return newScale* original;
	}


/**Sets a new preprocess scale for the image, panels will be resized by this change and
  layout rows and columns will also be resized*/
	public static AbstractUndoableEdit2 applyNewScaleTo(MultichannelDisplayLayer display, ScaleInformation newScale) {
		if (display.getPreprocessScale()==newScale)
			return null;
		PreprocessChangeUndo output1 = new PreprocessChangeUndo(display);
		display.setPreprocessScale(newScale);
		output1.establishFinalLocations();
		UndoLayoutEdit output2 = updateRowColSizesOf(display);
		
		return new CombinedEdit(output1,output2 );
	}
	
	 /**applies a new panel size to all of the images in the the layer
		 * @param layer
		 * @param newScale
		 * @return
		 */
		private UndoableEdit applyNewPanelSizeTo(MultichannelDisplayLayer layer, double newScale, boolean upperLeft) {
			CombinedEdit undoOutPut = new CombinedEdit();
			for(ZoomableGraphic object:layer.getAllGraphics()) {
				if (object instanceof ImagePanelGraphic) {
					ImagePanelGraphic imagepanel=(ImagePanelGraphic) object;
					UndoScalingAndRotation undo = new UndoScalingAndRotation(imagepanel);
					if(upperLeft)
						imagepanel.setLocationType(RectangleEdges.UPPER_LEFT);
					imagepanel.setRelativeScale(newScale);
					undo.establishFinalState();
					undoOutPut.addEditToList(undo);
				}
			}
			
			undoOutPut.addEditToList( updateRowColSizesOf(layer));
			
			return undoOutPut;
		}

	
	/**shows a labeling options dialog*/
	@MenuItemMethod(menuActionCommand = "Label Creation Options", menuText = "Label Creation Options")
	public void changeLabelProperties() {
		new StoredValueDilaog(LabelCreationOptions.current).showDialog();;
	}
	
	/**shows an options dialog for the label positions*/
	@MenuItemMethod(menuActionCommand = "Row/Col Label Positions", menuText = "Row/Column Label Positions", subMenuName="Advanced Options")
	public void changeLabelPositions() {
		DefaultLayoutGraphic layout = figureOrganizingLayerPane.getMontageLayoutGraphic();
		if(layout==null) {
			ShowMessage.showOptionalMessage("No layout found ", true, "There is no layout");
		}
		new StoredValueDilaog("Advanced Label Options", layout).showDialog();;
	}
	
	
	
	/**shows a labeling options dialog
	 * @return */
	@MenuItemMethod(menuActionCommand = "Lane Label", menuText = "Add Lane Labels")
	public CombinedEdit addLaneLabel(CanvasMouseEvent me) {
		Point point = me.getCoordinatePoint();
		DefaultLayoutGraphic thelayout = figureOrganizingLayerPane.getMontageLayoutGraphic();
		int index = thelayout.getPanelLayout().makeAltered(COLS).getPanelIndex(point.getX(), point.getY());
		return AddLabelHandle.createLaneLabelsFor(me, thelayout, index);
		
	}
	
	/**Adds mark labels to the panel in the click location
	 * @return */
	@MenuItemMethod(menuActionCommand = "Band marks", menuText = "Add Band Marks on Left")
	public CombinedEdit addBandMarkLabel(CanvasMouseEvent me) {
		MarkLabelCreationOptions markOptions=new MarkLabelCreationOptions();
		CombinedEdit output=new CombinedEdit();
		//boolean decision = ShowMessage.showOptionalMessage("work in progress ", false, "Band marks are a work in progress. They may be change in later versions.", "They are for gel and blot", "Are you sure you want to try them?");
		//if(!decision)
		//	return null;
		Point point = me.getCoordinatePoint();
		DefaultLayoutGraphic thelayout = figureOrganizingLayerPane.getMontageLayoutGraphic();
		output.addEdit(new UndoLayoutEdit(thelayout));
		Point2D locI = thelayout.getLocationUpperLeft();
		ImagePanelGraphic panel = figureOrganizingLayerPane.getAllPanelLists().getPanelGraphics().get(0);
		for(ImagePanelGraphic panel1:  figureOrganizingLayerPane.getAllPanelLists().getPanelGraphics()) {
			if(panel1.getBounds().contains(point)) {
					panel=panel1;
					}
		}
		BandMarkLayer pa = new BandMarkLayer(panel);
		output.addEditToList(
				Edit.addItem(figureOrganizingLayerPane, pa));
		
		 MarkLabelCreationOptions.showLaneLabelDialog( markOptions);
		pa.createBandMarks(panel.getFrameRect(),  markOptions);
		thelayout .getEditor().expandSpacesToInclude(thelayout.getPanelLayout(), pa.fulloutline());
		
		Point2D locF = thelayout.getLocationUpperLeft();
		try {
			pa.moveObjects(thelayout.getPanelLayout().getPanel(1).getMinX()-pa.fulloutline().getMaxX(),0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}
	
	
	public class showAdvancedChannelUse2 extends BasicSmartMenuItem {
		 /**
		 * 
		 */
		
		public showAdvancedChannelUse2() {
			super("Advanced Channel Use");
		}
		
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent e) {
			try{
				AdvancedChannelUseGUI output = new AdvancedChannelUseGUI(figureOrganizingLayerPane);
				output.setVisible(true);
			} catch (Exception e2) {
				IssueLog.log(e2);
			}
			
			
			
		}
		
	
	}
	

}