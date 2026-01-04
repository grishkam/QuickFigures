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
 * Date Modified: Jan 3, 2026
 * Version: 2026.1
 */
package handles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

import applicationAdapters.CanvasMouseEvent;
import channelMerging.MultiChannelSlot;
import channelMerging.PreProcessInformation;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.OverlayObjectManager;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import objectDialogs.CroppingDialog;

/**
 Handle for making changes to the crop area. works for top bottom left and right
 */
public class CropAreaHandle extends ImagePanelHandle {

	
	CroppingDialog crop ;
	private MultichannelDisplayLayer mdl;
	private MultiChannelSlot slot;
	private Color overcolor=Color.red;
	private double expand;
	private RectangularGraphic alternateCropArea;
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
	
	/**
	 method is called when a handle is moved to resize the image panel
	 */
	public void moveResizeHandle(Point p2, int handlenum) {
		thePanel.setLocationType(RectangleEdges.oppositeSide(handlenum));
		 double dist1=RectangleEdges.distanceOppositeSide(handlenum, thePanel.getBounds());
		 
		 
		 double oppositeSidex = RectangleEdges.getLocation(thePanel.getLocationType(), thePanel.getBounds()).getX();
		 double oppositeSidey = RectangleEdges.getLocation(thePanel.getLocationType(), thePanel.getBounds()).getY();
		double dist2x=Math.abs(p2.getX()- oppositeSidex);
		double dist2y=Math.abs(p2.getY()- oppositeSidey);
		
		
		PreProcessInformation modifications = slot.getModifications();
		 alternateCropArea = crop.createCropAreaRectangle(modifications.getRectangle(),modifications.getAngle() );
		
		if(handlenum==RectangleEdges.RIGHT || handlenum==RectangleEdges.LEFT) {
			expand = dist2x/dist1;
			
			double w = modifications.getRectangle().getWidth();
			double w2 = w*expand;
			
            alternateCropArea.setWidth(w2);
            
            if(handlenum==RectangleEdges.LEFT) {
            	alternateCropArea.moveLocation(w-w2,0);
            }
           
		}
		
		if(handlenum==RectangleEdges.BOTTOM||handlenum==RectangleEdges.TOP) {
			expand = dist2y/dist1;
			
			double h = modifications.getRectangle().getHeight();
			double h2 = h*expand;
			
		
            alternateCropArea.setHeight(h2);
            if(handlenum==RectangleEdges.TOP) {
            	alternateCropArea.moveLocation(0, h-h2);
            }
           
		}
		
		 boolean valid = crop.isCropRectangleValid(alternateCropArea);
         
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
		
		RectangularGraphic display = new RectangularGraphic(r1);
		display.setStrokeColor(overcolor);
		selectionManagger.setSelectionGraphic(display);
	}
	
	public void handleRelease(CanvasMouseEvent e) {
		thePanel.dragOngoing=false;
		if(super.getHandleNumber()==ImagePanelGraphic.CENTER)
			{
			releaseCenterHandle(e);
			}
		e.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionstoNull();
		RectangularGraphic r = alternateCropArea;
		PreProcessInformation process = new PreProcessInformation(r.getRectangle().getBounds(), r.getAngle(), slot.getModifications().getScaleInformation());
		slot.applyCropAndScale(process);
		
	}

}
