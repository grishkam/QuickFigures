package popupMenusForComplexObjects;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import channelMerging.CSFLocation;
import channelMerging.ChannelEntry;
import channelMerging.MultiChannelImage;
import channelMerging.ImageDisplayLayer;
import channelMerging.PreProcessInformation;
import figureFormat.TemplateSaver;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import iconGraphicalObjects.CropIconGraphic;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import menuUtil.PopupMenuSupplier;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import multiChannelFigureUI.WindowLevelDialog;
import objectDialogs.CroppingDialog;
import standardDialog.StandardDialog;
import undo.AbstractUndoableEdit2;
import undo.CanvasResizeUndo;
import undo.ChannelDisplayUndo;
import undo.CombinedEdit;
import undo.PreprocessChangeUndo;
import undo.UndoLayoutEdit;

public class FigureOrganizingSuplierForPopup implements PopupMenuSupplier, ActionListener {
	FigureOrganizingLayerPane figureOrganizingLayerPane;
	JMenuItem addImageFromFileButton;
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
		JMenu imagesMenu = new SmartJMenu("Images");	
		
		JMenu addImage=new SmartJMenu("Add Image");
		jj.add(addImage);
		addImageFromFileButton = new JMenuItem("Image From File");
		addImage.add(addImageFromFileButton);
		addImageFromFileButton.addActionListener(this);
		
		addOpenImageFromList = new JMenuItem("Currently Open Image");
		addImage.add(addOpenImageFromList);
		addOpenImageFromList.addActionListener(this);
		
	
	/**	 mi2 = new JMenuItem("Save Source Image Paths");
			jj.add(mi2);
			mi2.addActionListener(this);*/
		
			
		JMenu labelMenu = new SmartJMenu("Add Labels");
		
			 rowLabelButton = new JMenuItem("Generate Row Labels");
			 labelMenu.add(rowLabelButton);
				rowLabelButton.addActionListener(this);
				
				 columnLabelButton = new JMenuItem("Generate Col Labels");
				 labelMenu.add(columnLabelButton);
					columnLabelButton.addActionListener(this);
					
					panelLabelButton = new JMenuItem("Generate Panel Labels");
					 labelMenu.add(panelLabelButton);
						panelLabelButton.addActionListener(this);
					
					jj.add(labelMenu);
					
					
					
				recropPanelsButton= new JMenuItem("Re-Crop All Images");
				recropPanelsButton.addActionListener(this);
				recropPanelsButton.setIcon( CropIconGraphic.createsCropIcon());
				imagesMenu.add(recropPanelsButton);
				
				reScalePanelsButton= new JMenuItem("Reset Scale for All Images");
				reScalePanelsButton.addActionListener(this);
				imagesMenu.add(reScalePanelsButton);
				
				recreatePanelsButton = new JMenuItem("Recreate All Panels");
				jj.add(recreatePanelsButton);
							recreatePanelsButton.addActionListener(this);
				jj.add(imagesMenu);
				rePPIPanelsButton=new JMenuItem("Re-Set PPI for All Images");
				rePPIPanelsButton.addActionListener(this);
				imagesMenu.add(rePPIPanelsButton);
				
				
							
				JMenu chanMen=new JMenu("All Channels");
				
				
					
					 channelUseOptionsButton = new JMenuItem("Channel Use");
					 chanMen.add(channelUseOptionsButton);
						channelUseOptionsButton.addActionListener(this);
					
					 minMaxButton5 = new JMenuItem("Min/Max");
					 chanMen.add(minMaxButton5);
						minMaxButton5.addActionListener(this);
						
						 windowLevelButton = new JMenuItem("Window/Level");
						 chanMen.add(windowLevelButton);
							windowLevelButton.addActionListener(this);
							try {addRecolorMenu(chanMen);} catch (Throwable t) {IssueLog.logT(t);};
							jj.add(chanMen);
							
							jj.add(TemplateSaver.createFormatMenu(figureOrganizingLayerPane));
							
							if (figureOrganizingLayerPane.getMontageLayoutGraphic()!=null)
								jj.add(new FigureScalerMenu(figureOrganizingLayerPane.getMontageLayoutGraphic()));
	}
	
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
			IssueLog.log("about to open image ");
			undo=figureOrganizingLayerPane.nextMultiChannel(true);
		}
		if (source==addOpenImageFromList) {
			undo=figureOrganizingLayerPane.nextMultiChannel(false);
		}
		
		if (source==rowLabelButton||source==columnLabelButton||source==panelLabelButton) {
			int type=BasicMontageLayout.ROWS;
			if(source==columnLabelButton)  type=BasicMontageLayout.COLS;
			if(source==panelLabelButton)  type=BasicMontageLayout.PANELS;
			
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
		
		if (source ==rePPIPanelsButton) {
			undo=showRePPIAll();
		}
		
		ChannelPanelEditingMenu bit = new ChannelPanelEditingMenu( figureOrganizingLayerPane, 1);
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

	


	public EditLabels getLabelEditorMenuItemFor(TextGraphic t) {
		int gridSnap = t.getSnapPosition().getGridSpaceCode();
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


public static CombinedEdit recropManyImages(MultichannelDisplayLayer crop1, ArrayList<? extends ImageDisplayLayer> all) {
	CombinedEdit output = new CombinedEdit();
	output.addEditToList(
			showRecropDisplayDialog( crop1, null)
			);
	PreProcessInformation modifications = crop1.getSlot().getModifications();
	Rectangle r1=null;
	if (modifications!=null)
		r1= modifications.getRectangle();
	Dimension d1;
	if (r1==null) {
		d1=crop1.getMultiChannelImage().getDimensions();
	}else d1=new Dimension(r1.width, r1.height);
	
	
	for(ImageDisplayLayer crop2: all) {
		if(crop2==crop1) continue;
		output.addEditToList(
				showRecropDisplayDialog( (MultichannelDisplayLayer) crop2, d1)
		);
	}
	output.addEditToList(
			CurrentFigureSet.canvasResizeUndoable()
			);
	return output;
}

	public static CombinedEdit showRecropDisplayDialog(MultichannelDisplayLayer display, Dimension dim) {
		PreProcessInformation original = display.getSlot().getModifications();
		display.getPanelManager().setupViewLocation();
		PreprocessChangeUndo undo1 = new PreprocessChangeUndo(display);
		CSFLocation csfInitial = display.getSlot().getDisplaySlice().duplicate();
		
		CroppingDialog.showCropDialogOfSize(display.getSlot(), dim);
		
		onViewLocationChange(display, csfInitial, display.getSlot().getDisplaySlice());
		
		if (display.getSlot().getModifications()!=null&&display.getSlot().getModifications().isSame(original)
				) {
			return null;
		}
		undo1.establishFinalLocations();
		
		
		return new CombinedEdit(undo1, updateRowColSizesOf(display));
		
	}



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
			//display.getStack().getChannelUseInstructions().shareViewLocation(display.getSlot().getDisplaySlice());//experimental
			
		
	}



	public static UndoLayoutEdit updateRowColSizesOf(MultichannelDisplayLayer display) {
		
		if (display.getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) display.getParentLayer();
			MontageLayoutGraphic l = f.getMontageLayoutGraphic();
			l.generateCurrentImageWrapper();
			UndoLayoutEdit undo = new UndoLayoutEdit(l);
			l.getEditor().alterPanelWidthAndHeightToFitContents(l.getPanelLayout());
			undo.establishFinalLocations();
			return undo;
		}
		return null;
	}
	
	CombinedEdit showReScaleAll() {
		CombinedEdit output     = showReScaleAllDisplayDialog((MultichannelDisplayLayer) figureOrganizingLayerPane.getPrincipalMultiChannel());
		CanvasResizeUndo output2 = CurrentFigureSet.canvasResizeUndoable();
		return new CombinedEdit(output, output2);
	}




	
	 CombinedEdit showReScaleAllDisplayDialog(MultichannelDisplayLayer display) {
		 CombinedEdit output = new CombinedEdit();
		double newScale = showRescaleDialogSingleFor(display);
		
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




	private double showPPISingleImage(ImageDisplayLayer principalMultiChannel) {
		ImagePanelGraphic panel = principalMultiChannel.getPanelManager().getPanelList().getPanels().get(0).getPanelGraphic();
		double ppi = panel.getQuickfiguresPPI();
		double newppi=StandardDialog.getNumberFromUser("Input PPI ", ppi);
		return newppi;
	}



	public static double showSingleImageRescale(MultichannelDisplayLayer display) {
		double newScale = showRescaleDialogSingleFor(display);
		
		applyNewScaleTo(display, newScale);
		return newScale;
	}



	protected static double showRescaleDialogSingleFor(MultichannelDisplayLayer display) {
		PreProcessInformation original = display.getSlot().getModifications();
		
		double oldScale =1;
		if (original!=null)
				oldScale= original.getScale();
		
		double newScale = FigureScalerMenu.getScaleFromDialog("Change Image Scale", "Scaling with Bilinear Interpolation is done", oldScale);
		return newScale;
	}


/**Sets a new preprocess scale for the image, panels will be resized by this change and
  layout rows and columns will also be resized*/
	public static AbstractUndoableEdit2 applyNewScaleTo(MultichannelDisplayLayer display, double newScale) {
		if (display.getPreprocessScale()==newScale)
			return null;
		PreprocessChangeUndo output1 = new PreprocessChangeUndo(display);
		display.setPreprocessScale(newScale);
		output1.establishFinalLocations();
		UndoLayoutEdit output2 = updateRowColSizesOf(display);
		
		return new CombinedEdit(output1,output2 );
	}
	
	
}