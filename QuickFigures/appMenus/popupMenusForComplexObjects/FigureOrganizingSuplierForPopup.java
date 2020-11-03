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

import channelMerging.ChannelEntry;
import channelMerging.MultiChannelWrapper;
import channelMerging.PanelStackDisplay;
import channelMerging.PreProcessInformation;
import figureTemplates.TemplateSaver;
import graphicActionToombar.CurrentSetInformerBasic;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import menuUtil.PopupMenuSupplier;
import multiChannelFigureUI.ChannelSwapperToolBit2;
import multiChannelFigureUI.WindowLevelDialog;
import objectDialogs.CroppingDialog;
import standardDialog.StandardDialog;
import undo.AbstractUndoableEdit2;
import undo.CanvasResizeUndo;
import undo.ChannelDisplayUndo;
import undo.CompoundEdit2;
import undo.PreprocessChangeUndo;
import undo.UndoLayoutEdit;
import undo.UndoManagerPlus;

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
				imagesMenu.add(recropPanelsButton);
				
				reScalePanelsButton= new JMenuItem("Re-Scale All Images");
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
							try {addRecolorMenu(chanMen);} catch (Throwable t) {IssueLog.log(t);};
							jj.add(chanMen);
							
							jj.add(TemplateSaver.createFormatMenu(figureOrganizingLayerPane));
							
							if (figureOrganizingLayerPane.getMontageLayoutGraphic()!=null)
								jj.add(new FigureScalerMenu(figureOrganizingLayerPane.getMontageLayoutGraphic()));
	}
	
	public void addRecolorMenu(JMenu j) {
		
		MultiChannelWrapper mw = getPrimaryMultichannelWrapper();
		ArrayList<ChannelEntry> iFin = mw.getChannelEntriesInOrder();
		
		ChannelSwapperToolBit2 bit = new ChannelSwapperToolBit2(figureOrganizingLayerPane, iFin.get(0).getOriginalChannelIndex());
		
		
		bit.addChenEntryColorMenus( j, iFin);
	}



	public MultiChannelWrapper getPrimaryMultichannelWrapper() {
		return figureOrganizingLayerPane.getPrincipalMultiChannel().getMultichanalWrapper();
	}





	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		CompoundEdit2 undo=null ;
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
			
			CompoundEdit2 many = figureOrganizingLayerPane.addRowOrColLabel(type);
			
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
		
		ChannelSwapperToolBit2 bit = new ChannelSwapperToolBit2( figureOrganizingLayerPane, 1);
		if (source==minMaxButton5) {
			CompoundEdit2 undoMinMax = ChannelDisplayUndo.createMany(figureOrganizingLayerPane.getAllSourceStacks(), bit);
			undo=undoMinMax;
			WindowLevelDialog.showWLDialogs(getPrimaryMultichannelWrapper().getChannelEntriesInOrder(), getPrimaryMultichannelWrapper(), bit, WindowLevelDialog.MIN_MAX, undoMinMax);
			
		}
		if (source==windowLevelButton) {
			CompoundEdit2 undoMinMax = ChannelDisplayUndo.createMany(figureOrganizingLayerPane.getAllSourceStacks(), bit);
			undo=undoMinMax;
			WindowLevelDialog.showWLDialogs(getPrimaryMultichannelWrapper().getChannelEntriesInOrder(), getPrimaryMultichannelWrapper(), bit,  WindowLevelDialog.WINDOW_LEVEL, undoMinMax);
			
		}
		
		if (source==channelUseOptionsButton){
			figureOrganizingLayerPane.showChannelUseOptions();
			
		}
		
		figureOrganizingLayerPane.getUndoManager().addEdit(undo);
	}

	


	public EditLabels getLabelEditorMenuItemFor(TextGraphic t) {
		int gridSnap = t.getSnappingBehaviour().getGridChoiceNumbers();
		EditLabels output = new EditLabels(gridSnap, figureOrganizingLayerPane.getMontageLayoutGraphic(), t);
	
		if(output.getLabels(t).size()==0) {
			
			return null;
		}
		return output;
	}

/**Opens a dialog to recrop all the panels
 * @return */
	public CompoundEdit2 recropAll() {
		MultichannelDisplayLayer crop1 = (MultichannelDisplayLayer) figureOrganizingLayerPane.getPrincipalMultiChannel();
		ArrayList<PanelStackDisplay> all = figureOrganizingLayerPane.getMultiChannelDisplays();
		
		return recropManyImages(crop1, all);
	}


public static CompoundEdit2 recropManyImages(MultichannelDisplayLayer crop1, ArrayList<? extends PanelStackDisplay> all) {
	CompoundEdit2 output = new CompoundEdit2();
	output.addEditToList(
			showRecropDisplayDialog( crop1, null)
			);
	PreProcessInformation modifications = crop1.getSlot().getModifications();
	Rectangle r1=null;
	if (modifications!=null)
		r1= modifications.getRectangle();
	Dimension d1;
	if (r1==null) {
		d1=crop1.getMultichanalWrapper().getDimensions();
	}else d1=new Dimension(r1.width, r1.height);
	
	
	for(PanelStackDisplay crop2: all) {
		if(crop2==crop1) continue;
		output.addEditToList(
				showRecropDisplayDialog( (MultichannelDisplayLayer) crop2, d1)
		);
	}
	output.addEditToList(
			CurrentSetInformerBasic.canvasResizeUndoable()
			);
	return output;
}

	public static CompoundEdit2 showRecropDisplayDialog(MultichannelDisplayLayer display, Dimension dim) {
		PreProcessInformation original = display.getSlot().getModifications();
		PreprocessChangeUndo undo1 = new PreprocessChangeUndo(display);
		CroppingDialog.showCropDialogOfSize(display.getSlot(), dim);
		if (display.getSlot().getModifications()!=null&&display.getSlot().getModifications().isSame(original)) {
			return null;
		}
		undo1.establishFinalLocations();
		return new CompoundEdit2(undo1, updateRowColSizesOf(display));
		
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
	
	CompoundEdit2 showReScaleAll() {
		CompoundEdit2 output     = showReScaleAllDisplayDialog((MultichannelDisplayLayer) figureOrganizingLayerPane.getPrincipalMultiChannel());
		CanvasResizeUndo output2 = CurrentSetInformerBasic.canvasResizeUndoable();
		return new CompoundEdit2(output, output2);
	}




	
	 CompoundEdit2 showReScaleAllDisplayDialog(MultichannelDisplayLayer display) {
		 CompoundEdit2 output = new CompoundEdit2();
		double newScale = showRescaleDialogSingleFor(display);
		
		output.addEditToList(
				applyNewScaleTo(display, newScale)
				);
		
		ArrayList<PanelStackDisplay> all = figureOrganizingLayerPane.getMultiChannelDisplays();
		
		for(PanelStackDisplay crop2: all) {
			if(crop2==display) continue;
			
				output.addEditToList(
							applyNewScaleTo((MultichannelDisplayLayer) crop2, newScale));
		}
		
		return output;
	}
	 
	 private CompoundEdit2 showRePPIAll() {
		 CompoundEdit2 output = new CompoundEdit2();
		 double newPPI = showPPISingleImage(figureOrganizingLayerPane.getPrincipalMultiChannel());
		 ArrayList<PanelStackDisplay> all = figureOrganizingLayerPane.getMultiChannelDisplays();
		 for(PanelStackDisplay crop2: all) {
			 output.addEditToList(
				((MultichannelDisplayLayer) crop2).getPanelManager().changePPI(newPPI)
				);
			}
		 return output;
		}




	private double showPPISingleImage(PanelStackDisplay principalMultiChannel) {
		ImagePanelGraphic panel = principalMultiChannel.getPanelManager().getStack().getPanels().get(0).getPanelGraphic();
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
		
		return new CompoundEdit2(output1,output2 );
	}
	
	
}