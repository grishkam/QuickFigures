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
 * Date Modified: Jan 4, 2026
 * Version: 2026.1
 */
package handles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEvent;
import channelMerging.MultiChannelSlot;
import channelMerging.PreProcessInformation;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.OverlayObjectManager;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import messages.ShowMessage;
import objectDialogs.CroppingDialog;
import popupMenusForComplexObjects.FigureOrganizingSuplierForPopup;
import undo.CombinedEdit;
import undo.PreprocessChangeUndo;
import undo.UndoLayoutEdit;

/**
 Handle for making changes to the crop area. works for top bottom left and right.
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
	public static int ROTATION_CROP_AREA=819;//set to negative so that a function in the superclass would call drag resize handle
	/**
	 * @param panel
	 * @param handlenum
	 */
	public CropAreaHandle(ImagePanelGraphic panel, int handlenum) {
		super(panel, handlenum);
		this.setHandleColor(Color.darkGray);
		
		
	}
	

	public void handlePress(CanvasMouseEvent e) {
		
		this.findFigureComponentsForPanel(thePanel);
		
	}

	/**
	 * @param panel
	 */
	public void findFigureComponentsForPanel(ImagePanelGraphic panel) {
		mdl = MultichannelDisplayLayer.findMultiChannelForGraphic(panel.getParentLayer(), panel);
		if(mdl==null) {
			IssueLog.log("was unable to find figure for this image panel");
			return;
		}
		slot = mdl.getSlot();
		crop= new CroppingDialog(slot, slot.getUnprocessedVersion(true), slot.getModifications());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**performed to drag the handles*/
	public void handleDrag(CanvasMouseEvent e) {
		super.handleDrag(e);
		thePanel.dragOngoing=true;
		OverlayObjectManager selectionManagger = e.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger();
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
	
	/**creates a message below that panel that gives the user information regarding the image panel*/
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
		if(this.isRotationHandle()) {
			display.setAngle(-angleShift);
		}
		display.setStrokeColor(overcolor);
		selectionManagger.setSelectionGraphic(display);
	}
	
	public void handleRelease(CanvasMouseEvent e) {
		thePanel.dragOngoing=false;
		
		if(!valid) {
			ShowMessage.showOptionalMessage("this crop area is not valid");
			return;
		}
		
		e.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionstoNull();
		RectangularGraphic r = alternateCropArea;
		PreProcessInformation process = new PreProcessInformation(r.getRectangle().getBounds(), r.getAngle(), slot.getModifications().getScaleInformation());
		PreprocessChangeUndo undo1 = new PreprocessChangeUndo(mdl);
		slot.applyCropAndScale(process);
		
		UndoLayoutEdit ud1 = FigureOrganizingSuplierForPopup.updateRowColSizesOf(this.mdl);
		e.addUndo(new CombinedEdit(crop.additionalUndo,undo1, ud1, crop.additionalUndo));
	}
	
	public static final int[] usedEdges= new int[] {RectangleEdges.TOP,  RectangleEdges.BOTTOM, RectangleEdges.LEFT, RectangleEdges.RIGHT, RectangleEdges.CENTER,ROTATION_CROP_AREA};
	
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
	
	
	

}
