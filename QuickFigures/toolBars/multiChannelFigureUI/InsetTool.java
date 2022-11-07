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
 * Date Modified: April 27, 2022
 * Version: 2022.2
 */
package multiChannelFigureUI;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import applicationAdapters.ImageWorkSheet;
import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.ChannelUseInstructions;
import channelMerging.MultiChannelImage;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import figureOrganizer.PanelListElement;
import figureOrganizer.PanelManager;
import figureOrganizer.PanelOrderCorrector;
import figureOrganizer.insetPanels.InsetLayout;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner.InsetGraphicLayer;
import graphicTools.GraphicTool;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import icons.InsetToolIcon;
import imageDisplayApp.OverlayObjectManager;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.ObjectContainer;
import locatedObject.RectangleEdgePositions;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.StandardDialog;
import standardDialog.attachmentPosition.AttachmentPositionPanel;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputPanel;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoAddManyItem;
import undo.UndoInsetDefinerGraphic;
import undo.UndoLayoutEdit;
import undo.UndoScalingAndRotation;


/**Tool used for drawing regions of interest to define the area displayed in inset panels
  */
public class InsetTool extends GraphicTool implements LayoutSpaces {
	
	/**
	 * 
	 */
	public static final String INSET_TOOL_NAME = "Inset Tool";


	static boolean locksItems=false;
	
	/**constants for different placement options*/
	public static final int FREE_PLACEMENT=5, OUTSIDE_ON_LEFT_RIGHT=0, 
			ATTACH_TO_PARENT_PANEL=1,//no londer attaches
			FILL_SPACE_ON_SIDE=2;


	public static final int PLACE_ON_INNER_SIDES=3;


	static final int PLACE_ON_OUTER_SIDES=4;


	static final int FILL_RIGHT_SIDE=11;


	static final int FILL_BOTTOM_SIDE=12;


	static final int PLACE_INSIDE_l_R=13;


	static final int VERTICAL_PLACEMENT_ON_RIGHT_SIDE=7;


	static final int rightTop=8;


	static final int rightBottom=9;


	static final int BELOW_PANEL_ON_lAYOUT=10;
			
			
	
	
	
	
	ImagePanelGraphic SourceImageforInset=null;
	
	/**the inset currently being draw*/
	PanelGraphicInsetDefiner inset=null;
	
	/**An older inset that shares the same parent image*/
	PanelGraphicInsetDefiner preExisting=null;
	
	
	ObjectContainer imageTargetted ;
	
	public int arrangement=ATTACH_TO_PARENT_PANEL;//How to arrange the many panel insets
	
	public int border=2;//The width of the frames around the newly created insets
	public double scale=2;//The width of the frames around the newly created insets
	boolean avoidDapi=false;//true if dapi channel should not be included
	public int createMultiChannel=1;//should split channel insets be created?
	AttachmentPosition sb=AttachmentPosition.partnerExternal() ;
	
	/**set to true is insets should be created without scaling the source images*/
	public boolean dontScale;
	
	boolean sizeDefiningMouseDrag=true;



	public boolean horizontal=true;
	public boolean addToExisting=true;


	public CombinedEdit undo=new CombinedEdit();


	private boolean overSizeMode;





	
	public InsetTool() {
		super.iconSet=new InsetToolIcon(0).generateIconSet();
	}
	
	/**returns the most recently edited inset object*/
	public PanelGraphicInsetDefiner getMostRecentDrawnInset() {return inset;};
	
public void onPress(ImageWorkSheet gmp, LocatedObject2D roi2) {
	undo=new CombinedEdit();
	overSizeMode=false;
	imageTargetted = this.getImageClicked();
	if (roi2 instanceof PanelGraphicInsetDefiner) {
		
		setupToolToEditExistingInset(roi2);
		return;
	} else 
		setupToolForImagePanel(roi2);
	}

/**
 * @param roi2
 */
public void setupToolToEditExistingInset(LocatedObject2D roi2) {
	inset= (PanelGraphicInsetDefiner) roi2;
	SourceImageforInset=inset.getSourcePanel();
	sizeDefiningMouseDrag=false;
}

/**
 * @param roi2
 */
public void setupToolForImagePanel(LocatedObject2D roi2) {
	inset=null;
	sizeDefiningMouseDrag=true;
		if (roi2 instanceof ImagePanelGraphic) {
			
			SourceImageforInset=(ImagePanelGraphic) roi2;
		}
}
	
	public void onRelease(ImageWorkSheet imageWrapper, LocatedObject2D roi2) {
		
		if (inset==null) return;
		if (!inset.isValid()) {
			inset.removeInsetAndPanels();
			return;
		}
		if(this.overSizeMode) {
			inset.getSmartHandleList().getHandleNumber( RectangleEdgePositions.LOWER_RIGHT).handleRelease(getLastDragOrLastReleaseMouseEvent());
			
		}
		if(!overSizeMode)
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
	
	
	
	/**creates the inset panels*/
	private CombinedEdit createInsets(PanelGraphicInsetDefiner inset) {
				CombinedEdit undo = new CombinedEdit();
				if(!inset.isValid()) return undo;
					
					
					
				MultichannelDisplayLayer display=inset.getSourceDisplay();
					
					
				PanelList list = new PanelList();
				 setUpChannelUse(list,display);
			
				inset.setInsetScale(scale);
				inset.setDoNotScale(this.dontScale);
				inset.multiChannelStackofInsets=list;
				
				if (usePreexisting(inset)) {
					//must mimic the old channels. will mimic old channel order as well as which channels are used
					ChannelUseInstructions oldChanUse = preExisting.getPanelManager().getChannelUseInstructions();
					InsetPanelManagementGroup ipmg = new InsetPanelManagementGroup(preExisting);
					ChannelUseInstructions oldChanUse2 = new PanelOrderCorrector(ipmg).determineChannelOrder();
					 oldChanUse.makePartialMatching(oldChanUse2);
					list.setChannelUstInstructions( oldChanUse2);
					
					
				}
				
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
					inset.personalLayout=preExisting.personalLayout;
					inset.personalLayer=preExisting.personalLayer;
					pm=new PanelManager(display, list, this.preExisting.personalLayer);
					inset.setInsetScale(preExisting.getInsetScale());
					preExisting.getPanelManager().setPanelLevelScaleToPanels();
					double panelLevelScale = preExisting.getPanelManager().getPanelLevelScale();
					inset.getPanelManager().setPanelLevelScale(panelLevelScale);
					inset.getPanelManager().imposePanelLevelScale(panelLevelScale);
					//list.setChannelUstInstructions(preExisting.getPanelManager().getChannelUseInstructions());
				
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
				
				if (!usePreexisting(inset)) 
					inset.updateRelativeScaleOfPanels();
				
				
				inset.updateImagePanels();
				
				
				/**TODO: fix issue, for inexplicable reasons the list might not be set up at this point*/
				
				
			
			
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
				
				
				
				if (imageTargetted!=null)
				for(ChannelLabelTextGraphic l: labels) {
					l.setFont(l.getFont().deriveFont(fontsize));
					
					
					ArrayList<LocatedObject2D> itemsInway = getObjecthandler().getAllClickedRoi(imageTargetted, l.getBounds().getCenterX(), l.getBounds().getCenterY(),this.getSelectOnlyThoseOfClass());
					itemsInway.remove(l);
					itemsInway.remove(inset.personalLayout);
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
						DefaultLayoutGraphic layout = inset.personalLayout;
						double height = layout.getBounds().getHeight();
						if(height>l.getFont().getSize2D() &&layout.getPanelLayout().nRows()==1) l.setAttachmentPosition(AttachmentPosition.defaultColLabel());	
					}
					
				}
				
				
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
	 
	 /**sets the settings of this*/
	 public void setPropertiesToLayout(InsetLayout newLayout) {
		 this.border=(int) newLayout.getBorder();
		 this.arrangement=newLayout.getPositionType();
		 this.horizontal=newLayout.isHorizontalPreffered();
		 this.setInsetPosition(newLayout.getAttachmentPosition());
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
		ArrayList<Integer> noMChan = display.getPanelList().getChannelUseInstructions().getNoMergeChannels();
		ins.setExcludedChannelPanels(new ArrayList<Integer>());
		ins.getExcludedChannelPanels().addAll(noMChan);
		ins.setNoMergeChannels(new ArrayList<Integer>());;
		ins.getNoMergeChannels().addAll(noMChan);
		
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
		
		int in0 = ins.getExcludedChannelPanels().indexOf(0);
						if(in0==-1) {in0=0;}
						ins.getExcludedChannelPanels().add(in0,indexDapi);
						in0 = ins.getNoMergeChannels().indexOf(0);
						if(in0==-1) {in0=0;}
						ins.getNoMergeChannels().add(in0,indexDapi);
	}

	public String getExcludedChanName() {
		return "DAPI";
	}
	
	
	public void mouseDragged() {
		if(this.overSizeMode&&sizeDefiningMouseDrag)
		{	super.mouseDragged();
			
			inset.getSmartHandleList().getHandleNumber( RectangleEdgePositions.LOWER_RIGHT).handleDrag(getLastDragOrLastReleaseMouseEvent());
			return;
		}
		try {
			PanelGraphicInsetDefiner oldI = inset;
			refreshInsetOnMouseDrag(this.clickedCord(), this.draggedCord());
			if(inset!=null&&oldI!=inset&&inset.isSourcePanelOverSize()) {
				
				turnOnOversizeMode();
			}
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	}

	/**
	innitiates oversize mode for a mosue drag that generates an inset.
	 */
	public void turnOnOversizeMode() {
		{
			
			//ShowMessage.showOptionalMessage("Large image mode is a work in progress", true, "Inset tool will not handle mouse drags efficiencly for large image panels", "Please drag handles with the normal tool");
			/**sets the selected handle to the one in the lower right hand corners. Mouse drag will work on this handle */
			if(this.getPrimarySelectedObject()!=inset) {
				/**simulates a handle press on the lower right handle*/
				setPrimarySelectedObject(inset);				
				establishMovedIntoOrClickedHandle(true);
				setSelectedHandleNumber( RectangleEdgePositions.LOWER_RIGHT);
				inset.getSmartHandleList().getHandleNumber(RectangleEdgePositions.LOWER_RIGHT).handlePress(this.getLastMouseEvent());
			}
		
			
			overSizeMode=true;
		}
	}



	/**
	 Called when inset tool is dragged from point p1 to point p2
	 * @return 
	 */
	public PanelGraphicInsetDefiner refreshInsetOnMouseDrag(Point2D p1, Point2D p2) {
		if (getImageDisplayWrapperClick()!=null&&!getImageDisplayWrapperClick().getUndoManager().hasUndo(undo)){
				this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
		}
	
		Rectangle2D r = OverlayObjectManager.createRectangleFrom2Points(p1, p2);
		createOrEditInset(r);
		return inset;
	}

	
	/**when given an image panel and a bounds location (relative to the origin of the image panel)*/
	public PanelGraphicInsetDefiner createInsetOnImagePanel(ObjectContainer ob,
			ImagePanelGraphic g, Rectangle r) {
		this.setupToolForImagePanel(g);
		imageTargetted=ob;
		Point2D p = g.getLocationUpperLeft();
		r.setLocation((int)p.getX()+r.x, (int)p.getY()+r.y);
		this.createOrEditInset(r);
		return inset;
	}
	
	/**creates an inset for the drawn rectangle
	 * @param r
	 */
	public void createOrEditInset(Rectangle2D r) {
		UndoScalingAndRotation scalingUndo = new UndoScalingAndRotation(inset);
		undo.addEditToList(scalingUndo);
		
		boolean isRectValid=validRect(r);
		
		if (sizeDefiningMouseDrag==true) {
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
			inset.personalLayout=preExisting.personalLayout;
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
			inset.resizeLayoutPanels();
			
			return;
		}
		if(inset==null) return;
		
		ChannelLabelManager labels = inset.getChannelLabelManager();
		if(labels!=null)
		undo.addEditToList(
				labels.eliminateChanLabels()
				);
		undo.addEditToList(
				inset.removePanels());
		
		undo.addEditToList( 
				createInsets(inset));
		
		if (this.getImageDisplayWrapperClick()!=null)
		this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
	}
	
	/**returns true if the panels should be created with frames*/
	private boolean needsFrames() {
		
		if (arrangement==ATTACH_TO_PARENT_PANEL &&this.sb.isExternalSnap()) {
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
		BasicLayout layout = pm.getLayout();
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
	
	/**The tool dialog*/
	public class InsetToolDialog extends StandardDialog {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private InsetTool tool;
		private AttachmentPositionPanel snappanel;
		
		public InsetToolDialog(InsetTool mover) {
			this.setTitle("Inset tool options");
			this.tool=mover;
			
			
			add("arrangementClass", new ChoiceInputPanel("Select arrangement", InsetLayout.arrangements, mover.arrangement));
			
			String[] options2 = new String[] {"Only Single Image", "Multiple Channel Panels"};
			add("panelType", new ChoiceInputPanel("Select Panel Type", options2, mover.createMultiChannel));
			
			
			
			add("border", new NumberInputPanel("Border Width", mover.border, 3));
			add("scale", new NumberInputPanel("Scale", mover.scale, 3));
			add("horizon", new BooleanInputPanel("Prefer Horizontal Panels", mover.horizontal));
			add("add2", new BooleanInputPanel("Add to existing layout", mover.addToExisting));
			add("aDAPI", new BooleanInputPanel("Exclude "+mover.getExcludedChanName(), mover.avoidDapi));
			
			add("do not scale", new ChoiceInputPanel("How to create panels ", new String[] {"Scale pixels when creating panels", "Do not scale pixels when creating panels"} ,mover.dontScale?1:0));
			
			this.snappanel=new AttachmentPositionPanel(sb , "placement of internal; Montage");
			snappanel.addObjectEditListener(this);
			
			GridBagConstraints gc = new GridBagConstraints();
			gc.gridwidth=4;
			gc.gridheight=2;
			gc.gridx=0;
			gc.anchor=GridBagConstraints.WEST;
			
			gc.gridy=7;
			gridPositionY+=2;
			add( snappanel.getSnapBox(), gc);
			
			
			
		}
		
		/**updates the inset tool */
		protected void afterEachItemChange() {
			tool.border=(int) this.getNumber("border");
			tool.arrangement=this.getChoiceIndex("arrangementClass");
			tool.scale=this.getNumber("scale");
			tool.createMultiChannel=this.getChoiceIndex("panelType");
			tool.horizontal=this.getBoolean("horizon");
			tool.avoidDapi=this.getBoolean("aDAPI");
			tool.dontScale=this.getChoiceIndex("do not scale")==1;
			setInsetPosition(snappanel.getSnappingBehaviour());
			int npanelsForDialog=3;
			 tool.addToExisting=this.getBoolean("add2");
			
			if (PLACE_ON_INNER_SIDES==arrangement||PLACE_ON_OUTER_SIDES==arrangement) npanelsForDialog=5;
			DefaultLayoutGraphic previewLayout = makeInsetLayout().createLayout(npanelsForDialog,  new Rectangle(0,0, 28,21), snappanel.getSnapBox().getReferenceObject().getBounds(), 1);
		
			
			BasicLayout lg = previewLayout.getPanelLayout();
			
			
			
			
			previewLayout.setFilledPanels(true);
			previewLayout.setAlwaysShow(true);
		
			if (arrangement==ATTACH_TO_PARENT_PANEL) {
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
			
			return INSET_TOOL_NAME;
		}

	public void setInsetPosition(AttachmentPosition sb) {
		this.sb = sb;
	}

}
