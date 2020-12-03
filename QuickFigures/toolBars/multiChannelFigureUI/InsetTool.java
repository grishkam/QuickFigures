package multiChannelFigureUI;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import applicationAdapters.ImageWrapper;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelImage;
import genericMontageKit.PanelList;
import genericMontageKit.PanelListElement;
import genericMontageKit.OverlayObjectManager;
import graphicTools.GraphicTool;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_FigureSpecific.InsetLayoutDialog;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner.InsetGraphicLayer;
import graphicalObjects_FigureSpecific.PanelManager;
import graphicalObjects_FigureSpecific.InsetLayout;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.LayoutSpaces;
import logging.IssueLog;
import standardDialog.BooleanInputPanel;
import standardDialog.ComboBoxPanel;
import standardDialog.NumberInputPanel;
import standardDialog.SnappingPanel;
import standardDialog.StandardDialog;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoAddManyItem;
import undo.UndoInsetDefinerGraphic;
import undo.UndoLayoutEdit;
import undo.UndoScalingAndRotation;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.AttachmentPosition;

public class InsetTool extends GraphicTool implements LayoutSpaces {
	
	static boolean locksItems=false;
	{createIconSet("icons2/InsetIcon.jpg","icons2/InsetIconPressed.jpg","icons2/InsetIcon.jpg");};


	static final int free=5, outsideLR=0, 
			useSnapping=1,
			fill=2,
					onSides=3,
							onOuterSides=4,
			
			fillRight=11, 
			fillbottom=12,
					insideLR=13, 
			
			verticalRight=7,
			rightTop=8,
			rightBottom=9, 
			montageBelowPanel=10;
			
			
	
	
	
	
	ImagePanelGraphic SourceImageforInset=null;
	PanelGraphicInsetDefiner inset=null;
	PanelGraphicInsetDefiner preExisting=null;
	
	int arrangement=useSnapping;//How to arrange the many panel insets
	//int montageOrientation=0;//0 is horizontal, 1 is vertical
	public int border=2;//The width of the frames around the newly created insets
	public double scale=2;//The width of the frames around the newly created insets
	boolean avoidDapi=false;
	int createMultiChannel=1;
	AttachmentPosition sb=AttachmentPosition.partnerExternal() ;//.defaultInternalPanel();
	boolean sizeDefining=true;



	public boolean horizontal=true;






	public boolean addToExisting=true;






	private CombinedEdit undo;
	
	
	
public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
	undo=new CombinedEdit();
	//undo.addEditToList(new UndoWarning());
	
	if (roi2 instanceof PanelGraphicInsetDefiner) {
		
		inset= (PanelGraphicInsetDefiner) roi2;
		SourceImageforInset=inset.getSourcePanel();
		sizeDefining=false;
		return;
	} else sizeDefining=true;
	
	inset=null;
		if (roi2 instanceof ImagePanelGraphic) {
			
			SourceImageforInset=(ImagePanelGraphic) roi2;
		}
	}
	
	public void onRelease(ImageWrapper imageWrapper, LocatedObject2D roi2) {
		
		if (inset==null) return;
		if (!inset.isValid()) {
			inset.removeInsetAndPanels();
			
		}
		
		/**
		if (inset.getSourceDisplay() instanceof MultichannelImageDisplay) {
			inset.getChannelLabelManager().eliminateChanLabels();
			inset.removePanels();
			createInsets(inset);
			
		}*/
		
		resizeCanvas();
		
	}
	
	/**returns true if a newly added set of inset panels is to be
	 * added to the layout of an existing one. returns false
	 * if there is no pre-exisiting one*/
	private boolean usePreexisting(PanelGraphicInsetDefiner inset) {
		if (!addToExisting) return false;
		ArrayList<PanelGraphicInsetDefiner> old =PanelGraphicInsetDefiner.getInsetDefinersFromLayer(inset.getParentLayer());// new ArraySorter<ZoomableGraphic>().getThoseOfClass(inset.getParentLayer().getAllGraphics(), PanelGraphicInsetDef.class);
		
		if (inset!=old.get(0)) {
			this.preExisting=(PanelGraphicInsetDefiner) old.get(0);
			if (preExisting==null) return false;
			return true;
			
		}  
		return false;
	
	}
	
	
	
	
	
	private CombinedEdit createInsets(PanelGraphicInsetDefiner inset) {
		CombinedEdit undo = new CombinedEdit();
		if(!inset.isValid()) return undo;
		
		
		
		MultichannelDisplayLayer display=inset.getSourceDisplay();
		//MultichannelImageDisplay display=(MultichannelImageDisplay) inset.getParentLayer();
		
		
	PanelList list = new PanelList();
	 setUpChannelUse(list,display);

	inset.setBilinearScale(scale);
	inset.multiChannelStackofInsets=list;
	inset.setUpListToMakeInset(list, display.getPanelList());
	
	
	if (createMultiChannel==1)list.addAllCandF(display.getMultiChannelImage());
	if (createMultiChannel==0) {
		PanelListElement p = inset.getSourcePanel().getSourcePanel();
		list.add(p.createDouble());
	}
	
	InsetGraphicLayer pane;
	
	PanelManager pm ;
	
	UndoInsetDefinerGraphic undoLayerSet = new UndoInsetDefinerGraphic(inset);
	if (usePreexisting(inset)) {
		//Called if the panels for this inset simply need to be added to ther layer for another one
		inset.personalGraphic=preExisting.personalGraphic;
		inset.personalLayer=preExisting.personalLayer;
		pm=new PanelManager(display, list, this.preExisting.personalLayer);
		
	} else {
		pane=inset.createPersonalLayer("Insets");//new InsetGraphicLayer("Insets");
		inset.personalLayer=pane;
		inset.getParentLayer().add(pane);
		inset.getParentLayer().swapItemPositions(inset, pane);//ensures that the inset is in front of the other items
		undo.addEditToList(new UndoAddItem(inset.getParentLayer(), pane));
		pm = new PanelManager(display, list, pane);
	}
	
	undoLayerSet.establishFinalState();
	undo.addEditToList(undoLayerSet);
	
	ArrayList<ImagePanelGraphic> newpanels = pm.generatePanelGraphicsFor(list);
	undo.addEditToList(new UndoAddManyItem(pm.getLayer(), newpanels));
	inset.updateImagePanels();
	
	
	/**for inexplicable reasons the list might not be set up at this point*/
	
	//BarGraphic bar = BarGraphicTool.createBar(getImageWrapperClick(), mergeImage);
	// mergeImage.addLockedItem(bar);


	if (!usePreexisting(inset)) { 
		makeInsetLayout().applyInsetLayout(list, inset);
		lockPanelsOntoLayout(list, pm);

	}
	if (list.getSize()>0) {
		ImagePanelGraphic mergeImage = (ImagePanelGraphic) list.getPanels().get(0).getImageDisplayObject();
		
		BarGraphic bar = new BarGraphic();
		bar.setFillColor(Color.white);
		bar.setStrokeColor(Color.white);
		bar.setProjectionType(2);
		BarGraphic.optimizeBar(bar, mergeImage);
		
		GraphicLayer layerforbar = inset.getParentLayer();
		if(inset.personalLayer!=null)layerforbar =inset.personalLayer;
		layerforbar.add(bar);
		undo.addEditToList(new UndoAddItem( layerforbar, bar));
		mergeImage.addLockedItem(bar);
		mergeImage.snapLockedItems();
		BarGraphic.optimizeBar(bar, mergeImage);
	}
	
	ArrayList<ChannelLabelTextGraphic> newlabels = inset.getChannelLabelManager().generateChannelLabels();
	undo.addEditToList(new UndoAddManyItem(pm.getLayer(), newlabels));
	ArrayList<ChannelLabelTextGraphic> labels = inset.multiChannelStackofInsets.getChannelLabels();
	
	
	ArrayList<ImagePanelGraphic> g = list.getPanelGraphics();
	int heightofPanel = g.get(0).getBounds().height;
	float fontsize = heightofPanel/4;
	if(fontsize<6) fontsize=6;
	if(fontsize>12) fontsize=12;
	
	for(ChannelLabelTextGraphic l: labels) {
		l.setFont(l.getFont().deriveFont(fontsize));
		
		ArrayList<LocatedObject2D> itemsInway = getObjecthandler().getAllClickedRoi(this.getImageWrapperClick(), l.getBounds().getCenterX(), l.getBounds().getCenterY(),this.onlySelectThoseOfClass);
		itemsInway.remove(l);
		itemsInway.remove(inset.personalGraphic);
		if (itemsInway.size()>0&&l.getAttachmentPosition().isExternalSnap()) {
			//IssueLog.log("limited space puts makes  col label impossible");
			l.setAttachmentPosition(AttachmentPosition.defaultPanelLabel());
		} else
		if(fontsize>heightofPanel/3.5) {
			l.setAttachmentPosition(AttachmentPosition.defaultColLabel());	
		}
		
		if(fontsize<heightofPanel/4) {
			l.setAttachmentPosition(AttachmentPosition.defaultPanelLabel());
		}
		if  (haveChanLabelsOnTop() ) {
			MontageLayoutGraphic layout = inset.personalGraphic;
			double height = layout.getBounds().getHeight();
			if(height>l.getFont().getSize2D() &&layout.getPanelLayout().nRows()==1) l.setAttachmentPosition(AttachmentPosition.defaultColLabel());	
		}
		
	}
	
	inset.getParentLayer().remove(inset.getImageInset());//removes the direct inset

		return undo;
	
	}

	public static void lockPanelsOntoLayout(PanelList list, PanelManager pm) {
		for(PanelListElement panel: list.getPanels())
		if (locksItems ) {
			pm.getGridLayout().addLockedItem(panel.getPanelGraphic());
			panel.getPanelGraphic().setAttachmentPosition(AttachmentPosition.defaultInternalPanel());
			};
	}
	
	 InsetLayout makeInsetLayout() {
		
		 return new InsetLayout(border,arrangement,horizontal, sb);
	 }
	
	boolean haveChanLabelsOnTop() {
		if (arrangement==rightBottom) return true;
		
		return false;
	}
	
	/**given the source multichannel display and a panel list, sets up the channel use instructions for
	  the inset panels*/
	private void setUpChannelUse(PanelList list, MultichannelDisplayLayer display) {
		
		
		ChannelUseInstructions ins = ChannelUseInstructions.getChannelInstructionsForInsets();
		list.setChannelUstInstructions(ins);
		ArrayList<Integer> noMChan = display.getPanelList().getChannelUseInstructions().noMergeChannels;
		ins.excludedChannelPanels=new ArrayList<Integer>();
		ins.excludedChannelPanels.addAll(noMChan);
		ins.noMergeChannels=new ArrayList<Integer>();;
		ins.noMergeChannels.addAll(noMChan);
		
		if (avoidDapi) {
			String excludedChanName = getExcludedChanName();
			setExcludedChannel(display, ins, excludedChanName);
							
		}
	}

	/**When given the name of a channel, sets the excluded channel to be the one of that same*/
	public static void setExcludedChannel(MultichannelDisplayLayer display, ChannelUseInstructions ins,
			String excludedChanName) {
		MultiChannelImage multichanalWrapper = display.getMultiChannelImage();
		setExcludedChannel(excludedChanName, ins, multichanalWrapper);
	}

	public static void setExcludedChannel(String excludedChanName, ChannelUseInstructions ins,
			MultiChannelImage multichanalWrapper) {
		int indexDapi=multichanalWrapper.getIndexOfChannel(excludedChanName);
		
		int in0 = ins.excludedChannelPanels.indexOf(0);
						if(in0==-1) {in0=0;}
						ins.excludedChannelPanels.add(in0,indexDapi);
						in0 = ins.noMergeChannels.indexOf(0);
						if(in0==-1) {in0=0;}
						ins.noMergeChannels.add(in0,indexDapi);
	}

	public String getExcludedChanName() {
		return "DAPI";
	}
	
	
	public void mouseDragged() {
		if (!getImageDisplayWrapperClick().getUndoManager().hasUndo(undo)){
				this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
		}
		UndoScalingAndRotation scalingUndo = new UndoScalingAndRotation(inset);
		Rectangle2D r = OverlayObjectManager.createRectangleFrom2Points(this.clickedCord(), this.draggedCord());
		undo.addEditToList(scalingUndo);
		boolean isRectValid=validRect(r);
		
		if (sizeDefining==true) {
							if (inset==null) {
								if (SourceImageforInset==null||!isRectValid) return;
								
								inset = new PanelGraphicInsetDefiner(SourceImageforInset,r.getBounds());
								
								scalingUndo = new UndoScalingAndRotation(inset);undo.addEditToList(scalingUndo);
								SourceImageforInset.getParentLayer().add(inset);
								undo.addEditToList(new UndoAddItem(SourceImageforInset.getParentLayer(), inset));
								inset.setDashes(new float[] {});
							} else  {
								if (isRectValid)inset.setRectangle(r);
								}
			} else {
				super.mouseDragged();
				undo.addEditToList(super.currentUndo);
				}
		
		if (SourceImageforInset==null||!isRectValid) return;
		
		
		/**what to do if the panels of the inset are simply added to an existing layout*/
		if (usePreexisting(inset)) {
			
			CombinedEdit undo2 = 
					inset.getPanelManager().removeDisplayObjectsForAll();
					
			undo.addEditToList(undo2);
			CombinedEdit undo3 = inset.getChannelLabelManager().eliminateChanLabels();
			undo.addEditToList(undo3);
			
			/**Sets the layout and layer to those of the preexisting*/
			UndoInsetDefinerGraphic undo5 = new UndoInsetDefinerGraphic(inset);
			inset.personalLayer=preExisting.personalLayer;
			inset.personalGraphic=preExisting.personalGraphic;
			undo5.establishFinalState();
			undo.addEditToList(undo5);
			
			/**Actually create the little figure*/
			undo.addEditToList(
					createInsets(inset)
			);
			;
			
			PanelList currentstack = inset.getPanelManager().getPanelList();
			
			if (currentstack!=null) {
				undo.addEditToList(
						addExtraInsetPanelsToLayout(currentstack, needsFrames()? border: 0, inset.getPanelManager(), true));
				
				undo.addEditToList(
						inset.getChannelLabelManager().eliminateChanLabels()
						);
			}
			
			scalingUndo.establishFinalState();
			inset.resizeMontageLayoutPanels();
			
			return;
		}
		if(inset==null) return;
		undo.addEditToList(
				inset.getChannelLabelManager().eliminateChanLabels()
				);
		undo.addEditToList(
				inset.removePanels());
		
		undo.addEditToList( 
				createInsets(inset));
		
		this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
	}
	
	/**returns true if the panels should be created with frames*/
	private boolean needsFrames() {
		
		if (arrangement==useSnapping &&this.sb.isExternalSnap()) {
			return false;
		}
		return true;
	}

	private boolean validRect(Rectangle2D r) {
		if(r.getWidth()<4) return false;
		if(r.getHeight()<4) return false;
		return true;
	}

	public static CombinedEdit addExtraInsetPanelsToLayout(PanelList currentstack, double border, PanelManager pm, boolean alterlayout) {
		CombinedEdit undoOutput = new CombinedEdit();
		
		/**Adds space if there is none*/
		BasicMontageLayout layout = pm.getLayout();
		UndoLayoutEdit layoutUndo = new UndoLayoutEdit(layout);
		undoOutput.addEditToList(layoutUndo );
		
		int index = layout .getEditor().indexOfFirstEmptyPanel(layout , currentstack.getSize(),0);
		
		/**if row major does not make sense*/
		if (layout.nRows()==1) layout.rowmajor=true;
		if (layout.nColumns()==1) layout.rowmajor=false;
		
		if ((index>layout.nPanels()||index==0)&&alterlayout) {
			if (layout.nColumns()>layout.nRows())
				{layout.getEditor().addRows(layout, 1);}
			else
				layout.getEditor().addCols(layout, 1);
			layout.resetPtsPanels();
		}
		
		layoutUndo.establishFinalLocations();
		
	for(PanelListElement panel: currentstack.getPanels()) {
		setPanelFrames(border, panel);
		pm.putSingleElementOntoGrid(panel, false);
		}
		lockPanelsOntoLayout(currentstack, pm );
	
	return  undoOutput;
	}

	public static void setPanelFrames(double border, PanelListElement panel) {
		
		panel.getPanelGraphic().setFrameWidthH(border);
		panel.getPanelGraphic().setFrameWidthV(border);
	}
	
	
	


	
	@Override
	public void showOptionsDialog() {
		
		 try {
			 InsetToolDialog md = new InsetToolDialog(this);
			md.showDialog();
			md.afterEachItemChange();
		} catch (Throwable e) {
			IssueLog.logT(e);
		}
		 
	}
	
	public class InsetToolDialog extends StandardDialog {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private InsetTool mover;
		private SnappingPanel snappanel;
		
		public InsetToolDialog(InsetTool mover) {
			//setModal(true);
			this.mover=mover;
			
			//String[] options = new String[] {"Left and Right", "Inside Left and Right", "Vertical Montage (If fits at right)",  "Fill Right Side (If fits in single col)", "Horizontal Montage Right Top", "Horizontal Montage Right Bottom", "Montage Below", "Fill Bottom Side (If fits in single row)", "Montage Inside (see tab)",  "Free nearby"};
			add("arrangementClass", new ComboBoxPanel("Select arrangement", InsetLayoutDialog.arrangements, mover.arrangement));
			
			String[] options2 = new String[] {"Only Single Image", "Multiple Channel Panels"};
			add("panelType", new ComboBoxPanel("Select Panel Type", options2, mover.createMultiChannel));
			
			
			//String[] options3 = new String[] {"Horizontal Montage", "Vertical Montage", "Fit at side"};
			//rowadd("orType", new ComboBoxPanel("Select Panel Type", options3, mover.montageOrientation));
			//String[] groupops=new String[] {"Don't", "Do", "1 Level Down", "2 Level Down"};
			add("border", new NumberInputPanel("Border Width", mover.border, 3));
			add("scale", new NumberInputPanel("Scale", mover.scale, 3));
			add("horizon", new BooleanInputPanel("Prefer Horizontal Panels", mover.horizontal));
			add("add2", new BooleanInputPanel("Add to existing layout", mover.addToExisting));
			add("aDAPI", new BooleanInputPanel("Exclude "+mover.getExcludedChanName(), mover.avoidDapi));
			
			this.snappanel=new SnappingPanel(sb , "placement of internal; Montage");
			snappanel.addObjectEditListener(this);
			
			GridBagConstraints gc = new GridBagConstraints();
			gc.gridwidth=4;
			gc.gridheight=2;
			gc.gridx=0;
			gc.anchor=GridBagConstraints.WEST;
			
			gc.gridy=7;
			gy+=2;
			// snappanel.getSnapBox().setToMontageMode();
			add( snappanel.getSnapBox(), gc);
			
			
			
		}
		
		
		protected void afterEachItemChange() {
			mover.border=(int) this.getNumber("border");
			mover.arrangement=this.getChoiceIndex("arrangementClass");
			mover.scale=this.getNumber("scale");
			mover.createMultiChannel=this.getChoiceIndex("panelType");
			mover.horizontal=this.getBoolean("horizon");
			mover.avoidDapi=this.getBoolean("aDAPI");
			sb= snappanel.getSnappingBehaviour();
			int npanels=3;
			 mover.addToExisting=this.getBoolean("add2");
			
			if (onSides==arrangement||onOuterSides==arrangement) npanels=5;
			MontageLayoutGraphic previewLayout = makeInsetLayout().createLayout(npanels,  new Rectangle(0,0, 28,21), snappanel.getSnapBox().getReferenceObject().getBounds(), 1);
		
			
			BasicMontageLayout lg = previewLayout.getPanelLayout();
			
			
			
			
			previewLayout.setFilledPanels(true);
			previewLayout.setAlwaysShow(true);
		
			if (arrangement==useSnapping) {
				lg.setVerticalBorder(8);
				lg.setBottomSpace(lg.labelSpaceWidthBottom-8);
				lg.setHorizontalBorder(8);
				lg.setRightSpace(lg.labelSpaceWidthRight-8);
				lg.resetPtsPanels();
			}
			
			snappanel.getSnapBox().setOverrideObject(previewLayout);
			snappanel.getSnapBox().getReferenceObject().setFillColor(Color.blue.darker());
			snappanel.getSnapBox().getReferenceObject().setStrokeWidth(0);
			snappanel.getSnapBox().repaint();
		}
		
		@Override
		public void onOK() {
			afterEachItemChange();
		}
		
		
		
		
	}
	
	
	@Override
	public String getToolTip() {
			
			return "Create Insets";
		}
	@Override
	public String getToolName() {
			
			return "Inset Tool";
		}

}
