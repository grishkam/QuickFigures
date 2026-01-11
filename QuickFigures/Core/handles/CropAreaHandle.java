/*******************************************************************************
 * Copyright (c) 2026 Gregory Mazo
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
 * Date Created: Jan 3, 2026
 * Date Modified: Jan 10, 2026
 * Version: 2026.1
 */
package handles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import channelMerging.MultiChannelSlot;
import channelMerging.PreProcessInformation;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.OverlayObjectManager;
import imageScaling.ScaleInformation;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import messages.ShowMessage;
import objectDialogs.CroppingDialog;
import popupMenusForComplexObjects.FigureOrganizingSuplierForPopup;
import standardDialog.StandardDialog;
import undo.CombinedEdit;
import undo.PreprocessChangeUndo;
import undo.UndoLayoutEdit;
import undo.UndoMoveItems;
import undo.UndoScalingAndRotation;

/**
 Handle for making changes to the crop area without opening a crop dialog. 
 */
public class CropAreaHandle extends ImagePanelHandle {

	
	CroppingDialog crop ;
	private MultichannelDisplayLayer mdl;
	private MultiChannelSlot slot;
	private Color overcolor=Color.red;
	private double expand;
	private RectangularGraphic alternateCropArea;
	private boolean valid;
	private double expandx;
	private double expandy;
	private Point cordinate_of_drag=new Point();
	private double shiftx;
	private double shifty;
	private double angleShift;
	private Rectangle startingLocation;
	public static int ROTATION_CROP_AREA=819;
	
	public static enum CropAreaScaleMethod{NO__CROP_ONLY__DO_NOT_ALTER_ANYTHING_ELSE, YES_CHANGE_IMAGE_SCALING_TO_FIT_FINAL_PANEL_IN_SAME_AREA, YES_ALTER_PANEL_SIZE_AND_ALTER_PANEL_PPI}
	public static CropAreaScaleMethod user_selected_scaling_method=null;
	
	/**
	 * @param panel
	 * @param handlenum
	 */
	public CropAreaHandle(ImagePanelGraphic panel, int handlenum) {
		super(panel, handlenum);
		this.setHandleColor(Color.darkGray);
		if(handlenum==RectangleEdges.LOWER_RIGHT) {
			this.setHandleColor(Color.yellow);
		}
		setupCropHandleShapes();
	}
	

	public void handlePress(CanvasMouseEvent e) {
		startingLocation=thePanel.getBounds();
		this.findFigureComponentsForPanel(thePanel);
		
		if(e.clickCount()>1) {
			
		}
		
	}

	/**
	 * @param panel
	 */
	public void findFigureComponentsForPanel(ImagePanelGraphic panel) {
		mdl = MultichannelDisplayLayer.findMultiChannelForGraphic(panel.getParentLayer(), panel);
		if(mdl==null) {
			IssueLog.log("was unable to find figure for this image panel. It is either an isolated panel or part of a series of inset panels");
			return;
		}
		slot = mdl.getSlot();
		crop= new CroppingDialog(slot, slot.getUnprocessedVersion(true), slot.getModifications());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Method simulates a handle drag and release at aparticular location
	 * @return */
	public CombinedEdit pullHandleToLocation(CanvasMouseEvent e) {
		if(this.isCropHeightAdjusted()||this.isCropWidthAdjusted()) {
			handlePress(e);
			handleDrag(e);
			return onHandleRelease(e);
		}
		
		
		
		return null;
	}
	
	/**This scales the crop area by a given factor if it is a valid scaling*/
	public CombinedEdit pullHandleToScale(CanvasMouseEvent e, double scale) {
		if(isCropAreaScaleAdjusted()) {
			handlePress(e);
			if(this.isCropScaleValid(scale))
				return onHandleRelease(e); else {
					ShowMessage.showOptionalMessage("crop scaing not valid for one of the selected panels", true, "New crop area scale is not valid for one of the slected images");
				}
		}
		return null;
	}
	
	/**
	 * @return
	 */
	private boolean isCropAreaScaleAdjusted() {
		if(this.getHandleNumber()==RectangleEdges.UPPER_RIGHT||this.getHandleNumber()==RectangleEdges.LOWER_RIGHT)
			return true;
		return false;
	}


	/**performed to drag the handles*/
	public void handleDrag(CanvasMouseEvent e) {
		super.handleDrag(e);
		thePanel.dragOngoing=true;
		Point p2 = e.getCoordinatePoint();
		int handlenum = this.getHandleNumber();
		if (!this.isCenterHandle()){
						moveResizeHandle(p2, handlenum);
						
		}
	}
	
	/**
	 method overrides the superclass method
	 */
	public void moveResizeHandle(Point p2, int handlenum) {
		
		thePanel.setLocationType(RectangleEdges.oppositeSide(handlenum));
		 double dist1=RectangleEdges.distanceOppositeSide(handlenum, thePanel.getBounds());
		 
		 
		 double oppositeSidex = RectangleEdges.getLocation(thePanel.getLocationType(), thePanel.getBounds()).getX();
		 double oppositeSidey = RectangleEdges.getLocation(thePanel.getLocationType(), thePanel.getBounds()).getY();
		double dist2x=Math.abs(p2.getX()- oppositeSidex);
		double dist2y=Math.abs(p2.getY()- oppositeSidey);
		
		if(slot==null) {
			ShowMessage.showOptionalMessage("Option not yet avialble", true, "It looks like this is either an isolated panel or part or a series of inset panels. Not part of a Figure's Main Panel set!", "These handles do not yet work for this type of panel", "If this image is part of a figure but you still see this, please contact me about a bug!", "You can still right click on the panel and go to 'Expert Options' if you need to crop this");
			return;
		}
		
		PreProcessInformation modifications = slot.getModifications();
		if(modifications==null) {
			modifications=new PreProcessInformation(crop.getRectForEntireImage());
		}
		 Rectangle rectangle = modifications.getRectangle();
		 if(rectangle==null) {
			 rectangle=crop.getRectForEntireImage();
		 }
		alternateCropArea = crop.createCropAreaRectangle(rectangle,modifications.getAngle() );
		expandx= dist2x/dist1;
		expandy=dist2y/dist1;
		
		
		if(handlenum==RectangleEdges.RIGHT || handlenum==RectangleEdges.LEFT) {
			expand = dist2x/dist1;
			
			double w = rectangle.getWidth();
			double w2 = w*expand;
			
            alternateCropArea.setWidth(w2);
            
            if(handlenum==RectangleEdges.LEFT) {
            	alternateCropArea.moveLocation(w-w2,0);
            }
           
		}
		
		if(handlenum==RectangleEdges.BOTTOM||handlenum==RectangleEdges.TOP) {
			expand = dist2y/dist1;
			
			double h = rectangle.getHeight();
			double h2 = h*expand;
			
		
            alternateCropArea.setHeight(h2);
            if(handlenum==RectangleEdges.TOP) {
            	alternateCropArea.moveLocation(0, h-h2);
            }
           
		}
		
		if(handlenum==RectangleEdges.LOWER_RIGHT||getHandleNumber()==RectangleEdges.UPPER_RIGHT) {
			alternateCropArea.scaleAbout(alternateCropArea.getCenterOfRotation(), expandx);
		}
		
		if(handlenum==RectangleEdges.CENTER) {
			shiftx=(cordinate_of_drag.getX()-thePanel.getBounds().getCenterX())/thePanel.getBounds().getWidth();
			shifty=(cordinate_of_drag.getY()-thePanel.getBounds().getCenterY())/thePanel.getBounds().getHeight();
			alternateCropArea.moveLocation(alternateCropArea.getBounds().getWidth()*shiftx, alternateCropArea.getBounds().getHeight()*shifty);
			
		}
		
		if(this.isRotationHandle()) {
			angleShift = ShapeGraphic.getAngleBetweenPoints(thePanel.getCenterOfRotation(),p2.getLocation() );
			alternateCropArea.setAngle(modifications.getAngle()-angleShift);
			
			
		}
		
		 valid = crop.isCropRectangleValid(alternateCropArea);
         
         if(valid) {overcolor=Color.green;} else {
         	overcolor=Color.red;
         }
		
		
	}
	
	
	/**Checks is a potential crop area scale is valid*/
	boolean isCropScaleValid(double scaleFactor) {
		alternateCropArea=findCurruentCropAreaRectangle();
		alternateCropArea.scaleAbout(alternateCropArea.getCenterOfRotation(), scaleFactor);
		expandx=scaleFactor;
		return  crop.isCropRectangleValid(alternateCropArea);
	}


	/**determines the rectangle that represents the current crop area for the immage
	 * @return 
	 * 
	 */
	protected RectangularGraphic findCurruentCropAreaRectangle() {
		PreProcessInformation modifications = slot.getModifications();
		if(modifications==null) {
			modifications=new PreProcessInformation(crop.getRectForEntireImage());
		}
		 Rectangle rectangle = modifications.getRectangle();
		 if(rectangle==null) {
			 rectangle=crop.getRectForEntireImage();
		 }
		return crop.createCropAreaRectangle(rectangle,modifications.getAngle() );
	}
	
	/**creates preview of the scale for the panel*/
	protected void showPanelInformation(OverlayObjectManager selectionManagger) {
		Rectangle r1 = super.thePanel.getBounds();
		if(getHandleNumber()==RectangleEdges.RIGHT) {
			
			r1.width=(int) (r1.width*expand);
		}
		

		if(this.isCenterHandle()) {
			r1.x=(int) (r1.x-(r1.width*expand-r1.width));
			r1.y=(int) (r1.y-(r1.height*expand-r1.height));
		}
	if(getHandleNumber()==RectangleEdges.LEFT) {
			r1.x=(int) (r1.x-(r1.width*expand-r1.width));
			r1.width=(int) (r1.width*expand);
		}
		
		if(getHandleNumber()==RectangleEdges.BOTTOM) {	
			r1.height=(int) (r1.height*expand);
		}
		
		if(getHandleNumber()==RectangleEdges.TOP) {	
			r1.y=(int) (r1.y-(r1.height*expand-r1.height));
			r1.height=(int) (r1.height*expand);
		}
		if(getHandleNumber()==RectangleEdges.CENTER)
		RectangleEdges.setLocation(r1, RectangleEdges.CENTER, cordinate_of_drag.getX(), cordinate_of_drag.getY());
		
		
		
		
		RectangularGraphic display = new RectangularGraphic(r1);
		
		if(getHandleNumber()==RectangleEdges.LOWER_RIGHT||getHandleNumber()==RectangleEdges.UPPER_RIGHT) {
			display.scaleAbout(display.getCenterOfRotation(), expandx);
		}
		
		if(this.isRotationHandle()) {
			display.setAngle(-angleShift);
		}
		display.setStrokeColor(overcolor);
		
		selectionManagger.setSelectionGraphic2(display);
		
		
		addAlignmentLine(selectionManagger, display);
	}


	/**Adds another line to help the user see when a possible crop area is aligned to other objects
	 * @param selectionManagger
	 * @param display
	 */
	protected void addAlignmentLine(OverlayObjectManager selectionManagger, RectangularGraphic display) {
		int alignment_x1 = 0;
		int alignment_x2 = 0;
		int alignment_y1 = 0;
		int alignment_y2 = 0;
		if(getHandleNumber()==RectangleEdges.LEFT) { 
			alignment_x1=display.getBounds().x;
			alignment_x2=alignment_x1;
			alignment_y1=display.getBounds().y-thePanel.getBounds().width/2;
			alignment_y2=(int) (display.getBounds().getMaxY()+thePanel.getBounds().width/2);
		}
		if(getHandleNumber()==RectangleEdges.RIGHT) { 
			alignment_x1=(int) display.getBounds().getMaxX();
			alignment_x2=alignment_x1;
			alignment_y1=display.getBounds().y-thePanel.getBounds().width/2;
			alignment_y2=(int) (display.getBounds().getMaxY()+thePanel.getBounds().width/2);
		}
		
		if(getHandleNumber()==RectangleEdges.TOP) { 
			alignment_x1=display.getBounds().x-thePanel.getBounds().width/2;
			alignment_x2=(int) (display.getBounds().getMaxX()+thePanel.getBounds().width/2);
			alignment_y1=display.getBounds().y;
			alignment_y2=alignment_y1;
		}
		if(getHandleNumber()==RectangleEdges.BOTTOM) { 
			alignment_x1=display.getBounds().x-thePanel.getBounds().width/2;
			alignment_x2=(int) (display.getBounds().getMaxX()+thePanel.getBounds().width/2);
			alignment_y1=(int) display.getBounds().getMaxY();
			alignment_y2=alignment_y1;
		}
		
		ArrowGraphic alignment_line = ArrowGraphic.createLine(Color.gray, new Color(100,100,100, 50), new Point(alignment_x1, alignment_y1), new Point(alignment_x2, alignment_y2));
		alignment_line.setStrokeWidth(1);
		alignment_line.setFillColor(null);
		
		alignment_line.deselect();
		selectionManagger.setSelectionGhost(alignment_line);
	}
	
	public void handleRelease(CanvasMouseEvent e) {
		thePanel.dragOngoing=false;
		
		if(!valid) {
			ShowMessage.showOptionalMessage("this crop area is not valid. ");
			OverlayObjectManager selectionManagger = e.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger();
			selectionManagger.setSelectionGhost(null);
			selectionManagger.setSelectionGraphic2(null);
			return;
		}
		
		CombinedEdit combinedEdit = onHandleRelease(e);
		findOtherPanels(e, combinedEdit );
		
		UndoLayoutEdit ud1 = FigureOrganizingSuplierForPopup.updateRowColSizesOf(this.mdl);
		combinedEdit.addEditToList(ud1);
		
		e.addUndo(combinedEdit);
	}


	/**
	 * @param e
	 * @return
	 */
	protected CombinedEdit onHandleRelease(CanvasMouseEvent e) {
		e.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionstoNull();
		RectangularGraphic r = alternateCropArea;
		PreProcessInformation modifications = slot.getModifications();
		if(modifications==null) {
			modifications= new PreProcessInformation(new ScaleInformation());
		}
		ScaleInformation scaleInformation = modifications.getScaleInformation();
		CombinedEdit undoextra=null;
		if(this.isCropAreaScaleAdjusted() ) {
			
			if(user_selected_scaling_method==null) {
				user_selected_scaling_method=CropAreaScaleMethod.NO__CROP_ONLY__DO_NOT_ALTER_ANYTHING_ELSE;
			user_selected_scaling_method=(CropAreaScaleMethod) StandardDialog.getEnumChoiseFromUser("Do you want to fit the newly croped panels in the same area?", user_selected_scaling_method, CropAreaScaleMethod.values());
			}
			
			if(user_selected_scaling_method==CropAreaScaleMethod.YES_CHANGE_IMAGE_SCALING_TO_FIT_FINAL_PANEL_IN_SAME_AREA) {
				double newScaleLevel = scaleInformation.getScale()/expandx;
				scaleInformation=scaleInformation.getAtDifferentScale(newScaleLevel);
			}//if user is scaling the crop area
			
			if(user_selected_scaling_method==CropAreaScaleMethod.YES_ALTER_PANEL_SIZE_AND_ALTER_PANEL_PPI) {
		
				undoextra=new CombinedEdit();
				for(ImagePanelGraphic aPanel: mdl.getPanelList().getPanelGraphics()) {
					undoextra.addEditToList(new CombinedEdit(new UndoScalingAndRotation(aPanel),new UndoMoveItems(aPanel)));
						aPanel.setRelativeScale(aPanel.getRelativeScale()/expandx);
				}
			}
		}
		
		PreProcessInformation process = new PreProcessInformation(r.getRectangle().getBounds(), r.getAngle(), scaleInformation);
		PreprocessChangeUndo undo1 = new PreprocessChangeUndo(mdl);
		slot.applyCropAndScale(process);
		
		
		CombinedEdit combinedEdit = new CombinedEdit(crop.additionalUndo,undo1,  crop.additionalUndo, undoextra);
		return combinedEdit;
	}
	
	

	public static final int[] usedEdges= new int[] {RectangleEdges.TOP,  RectangleEdges.BOTTOM, RectangleEdges.LEFT, RectangleEdges.RIGHT, RectangleEdges.CENTER,ROTATION_CROP_AREA, RectangleEdges.LOWER_RIGHT, RectangleEdges.UPPER_RIGHT};
	
	/**adds crop  handles*/
	public static void addCropAreaHandles(ImagePanelGraphic im, SmartHandleList l) {
		for(int i: usedEdges) {
			l.add(new CropAreaHandle(im,i)); 
		}
		
		
	}
	
	/**
	 Called when the center handle is dragged
	 */
	public void dragCenterHandle(CanvasMouseEvent e, OverlayObjectManager selectionManagger) {
		cordinate_of_drag=e.getCoordinatePoint();
		this.moveResizeHandle(e.getCoordinatePoint(), RectangleEdges.CENTER);
	}

	
	/**updates the locations of each handle to fit the location and size of the image panel*/
	public void updateHandleLocs() {
		if(isRotationHandle()) {
				Point2D p = RectangleEdges.getLocation(RectangleEdges.RIGHT,thePanel.getBounds());
				p.setLocation(p.getX()+thePanel.getBounds().getWidth()/5, p.getY());
				if(angleShift!=0) {
					//TODO: will adda way to match the handle location to the preview rects
				}
				this.setHandleColor(Color.orange.darker());
				setCordinateLocation(p);
				return;
				} 
		
			Point2D l1 = RectangleEdges.getLocation(this.getHandleNumber(), thePanel.getBounds());
			setCordinateLocation(l1);
			
		
		
	}
	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		this.updateHandleLocs();
		super.draw(graphics, cords);
	}
	
	
	

	
	/**
	 * 
	 */
	public static void addCropHandles(ImagePanelGraphic imagePanel, boolean position) {
		hideOrRevealCropHandles(imagePanel, position);
		
		
	}


	/**Changes the handles of the image panel
	 * @param imagePanel
	 * @param cropHandleMode
	 * @return
	 */
	public static ImagePanelHandleList hideOrRevealCropHandles(ImagePanelGraphic imagePanel, boolean cropHandleMode) {
		if(!cropHandleMode)
		user_selected_scaling_method=null;//resets this to null so that the user is asked to choose again
		ImagePanelHandleList panelHandleList = imagePanel.getPanelHandleList();
		boolean crop_handles_present=false;
		for(SmartHandle h : panelHandleList) {
			for (int i : CropAreaHandle.usedEdges) {
				if(i==h.getHandleNumber()) {
					if(h instanceof CropAreaHandle) 
					{h.setHidden(!cropHandleMode); crop_handles_present=true;} else 
					h.setHidden(cropHandleMode);
				}
			}
			
		}
		if(crop_handles_present) {return null;}
		
		if(cropHandleMode)
		CropAreaHandle.addCropAreaHandles(imagePanel, panelHandleList);
		
		return panelHandleList;
	}	
	

	
	public boolean isRotationHandle() {
		return this.getHandleNumber()==ROTATION_CROP_AREA;
	}
	
	/**
	 * @param p2
	 */
	protected void setPanelCenterLocationAfterHandleDrag(Point p2) {
		thePanel.setLocationType(RectangleEdges.CENTER);
		
	}
	
	/**finds other selected images and applies the crop to them*/
	public void findOtherPanels(CanvasMouseEvent e, CombinedEdit undo) {
		
		if(getHandleNumber()==RectangleEdges.CENTER)
			return;
		
		ArrayList<ImagePanelGraphic> cousinImages=new ArrayList<ImagePanelGraphic> ();
		ArrayList<MultichannelDisplayLayer> cousinDisplays=new ArrayList<MultichannelDisplayLayer> ();
		ArrayList<ZoomableGraphic> theSelected = e.getSelectionSystem().getSelecteditems();
		MultichannelDisplayLayer mdl0 = MultichannelDisplayLayer.findMultiChannelForGraphic(thePanel.getParentLayer(), thePanel);
		
		for(Object z: theSelected) {
			if(z instanceof ImagePanelGraphic) {
				ImagePanelGraphic image=(ImagePanelGraphic) z;
				MultichannelDisplayLayer mdl1 = MultichannelDisplayLayer.findMultiChannelForGraphic(image.getParentLayer(), image);
				
				if(mdl1!=mdl0 & theseEdgesOverlapEnoughForAlignedCropping(image, thePanel)    & areTheseBoundsCropAlignCompatible(image.getBounds(), startingLocation)  & !cousinDisplays.contains(mdl1)) {
					{
						cousinDisplays.add(mdl1);
						cousinImages.add(image);
						}
				}
			}
		}
		
		if( getHandleNumber()==RectangleEdges.CENTER)
			return;
		
		//&&ShowMessage.yesOrNo("You held shift while pulling a crop handle with multiple panels selected do you want to try to propagate the crop to the other images?")
		if(cousinImages.size()>0 )
		for(ImagePanelGraphic c: cousinImages) {
			CropAreaHandle.addCropHandles(c, true);
			SmartHandleList shl = c.getSmartHandleList();
			SmartHandle handle1 = shl.getHandleNumber(this.getHandleNumber()) ;
			if(handle1 instanceof CropAreaHandle) {
				CropAreaHandle c2=(CropAreaHandle) handle1;
				CombinedEdit edit;
				if(c2.isCropAreaScaleAdjusted()) {
					edit = c2.pullHandleToScale(e, this.expandx);
				} else
						{ edit = c2.pullHandleToLocation(e);}
				undo.addEditToList(edit);
			}
		}
		
	}
	
	/** determines if the panels have crop handles that can be dragged together. 
	 * this should be avoided if the panels are not vertically on top of each other 
	 * @param image
	 * @param thePanel
	 * @return
	 */
	private boolean theseEdgesOverlapEnoughForAlignedCropping(ImagePanelGraphic image, ImagePanelGraphic thePanel) {
		Rectangle b = image.getBounds();
		Rectangle b2 = thePanel.getBounds();
		return areTheseBoundsCropAlignCompatible(b, b2);
	}


	/** returns false if the locations of the two image panel rectangles are not suitable for cropping together since they do not share an edge that can align
	 * @param b
	 * @param b2
	 * @return
	 */
	protected boolean areTheseBoundsCropAlignCompatible(Rectangle b, Rectangle b2) {
		if(this.isCropWidthAdjusted()) {
			if(b2.getMaxX()<b.getMinX())
				return false;
			if(b.getMaxX()<b2.getMinX())
				return false;
		}
		
		if(this.isCropHeightAdjusted()) {
			if(b2.getMaxY()<b.getMinY())
				return false;
			if(b.getMaxY()<b2.getMinY())
				return false;
		}
		
		
		return true;
	}


	/**a shape that is recognizalbe as a crop area handle*/
	protected Area getOverdecorationShape() {
		if (overDecorationShape==null &&isCenterHandle()) {
			decorationColor=Color.black;
			overDecorationShape=super.getAllDirectionArrows(2, 2, false);
		}
		setupCropHandleShapes();
		return overDecorationShape;
	}


	/**
	 * makes special shapes for the crop handles
	 */
	protected void setupCropHandleShapes() {
		if (overDecorationShape==null) {
			int h=this.handlesize;
			int lw_ratio=10;
			int rounding =h*2;
			decorationColor=this.getHandleColor();
				if(isCropWidthAdjusted()) {
					specialShape=new RoundRectangle2D.Double(-h, -h*lw_ratio, h*2, h*2*lw_ratio, rounding,rounding);
					
				}
				if(isCropHeightAdjusted()) {
					specialShape=new RoundRectangle2D.Double( -h*lw_ratio, -h, h*2*lw_ratio,  h*2, rounding, rounding );
					
				}
		}
	}


	/**returns true if this handle adjusts only the height of the crop area
	 * @return
	 */
	protected boolean isCropHeightAdjusted() {
		return this.getHandleNumber()==RectangleEdges.TOP||this.getHandleNumber()==RectangleEdges.BOTTOM;
	}


	/**returns true if this handle adjusts only the width of the crop area
	 * @return
	 */
	protected boolean isCropWidthAdjusted() {
		return this.getHandleNumber()==RectangleEdges.LEFT||this.getHandleNumber()==RectangleEdges.RIGHT;
	}
	

}
