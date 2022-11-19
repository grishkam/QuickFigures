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
 * Date Created: Nov 19, 2022
 * Date Modified: Nov 19, 2022
 * Copyright (C) 2022 Gregory Mazo
 * Version: 2022.2
 */
package graphicalObjects_SpecialObjects;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import logging.IssueLog;
import undo.CombinedEdit;
import undo.Edit;

/**
 This contains methods for translating the locations of objects from the cordinates of a panel
 to the cordinates of the worksheet
 */
public interface OverlayHolder {

	/**adds to the given layer. Adds a scaled copy of the overlay that can be placed into the image*/
	public static CombinedEdit extractOverlay(ImagePanelGraphic panelP, GraphicLayerPane added, boolean removeOriginal,OverlayObjectList overlay ) {
		CombinedEdit edit = new CombinedEdit();
		try {
			  
			if(overlay==null) {
				  IssueLog.log("no overlay objects detected");
			  } else {
				  double scale=panelP.getRelativeScale();
				  Rectangle2D sizeOfImagePanel = new Rectangle2D.Double(-1/scale,-1/scale, (panelP.getObjectWidth()+panelP.getFrameWidthH()+2)/scale, (panelP.getObjectHeight()+panelP.getFrameWidthV()+2)/scale);
				 
				 
				  ArrayList<?> overlayObjectList = overlay.getOverlayObjects();
				  if(overlayObjectList.size()==0) {
					  IssueLog.log("There are no objects listed. cannot extract overlay ");
				  }
				  
				for(Object object: overlayObjectList)  try {
					 
					  if(object instanceof BasicGraphicalObject) {
						 
						  Rectangle objectbounds = ((BasicGraphicalObject) object).getBounds();
						  BasicGraphicalObject copy = ((BasicGraphicalObject) object).copy();
						 
						  if (copy instanceof BasicShapeGraphic) {
							  copy=((ShapeGraphic) copy).createPathCopy();
						  }
						  copy.scaleAbout(new Point2D.Double(0,0), scale);
						  
						  boolean inside = sizeOfImagePanel.contains(objectbounds);
						  boolean overlaps = sizeOfImagePanel.intersects(objectbounds);//might be used later
						  
						  copy.moveLocation((panelP.getLocationUpperLeft().getX()), (panelP.getLocationUpperLeft().getY()));
						 
							
						  if(!inside) { 
							
							    continue;
						  } 
						  else {
							  added.addItemToLayer(copy);
							if(removeOriginal) {
								GraphicLayer parentLayer = ((BasicGraphicalObject) object).getParentLayer();
								if(parentLayer!=null)
								edit.addEditToList(Edit.removeItem(parentLayer, (ZoomableGraphic) object));
								else {
									IssueLog.log("parent layer unknown for "+object);
								}
							}
								
						  }
					  } else {
						  IssueLog.log("failed to extract item "+object);
					  }
					  
				  }catch (Throwable t){
					  IssueLog.logT(t);
				  }
			  }
		  } catch (Throwable t){
			  IssueLog.logT(t);
		  }
		if(added.getAllGraphics().size()==0) {
			IssueLog.log(" have not added any items ");
		}
		return edit;
	}
	
	/**implementation for the insert item*/
	public static CombinedEdit insertIntoOverlay(ImagePanelGraphic panelP, OverlayObjectList destination, ArrayList<?> objects) {
		CombinedEdit output=new CombinedEdit();
		if(objects.size()==0) {
			  IssueLog.log("There are no objects listed. cannot insert ");
		  }
		
		
		  Rectangle bounds = panelP.getBounds();
		for(Object object: objects)  try {
			 
			  if(object instanceof BasicGraphicalObject) {
				  
				 
				  BasicGraphicalObject basicGraphicalObject = (BasicGraphicalObject) object;
				  if(!destination.canAccept(basicGraphicalObject))
					  continue;
				Rectangle objectbounds = basicGraphicalObject.getBounds();
				
				  
				  
				
				boolean inside = bounds.contains(objectbounds);
				 boolean overlaps =  bounds.intersects(objectbounds);//might be used later
				  
				 BasicGraphicalObject copy = moveFromBaseLocationToOverlay(panelP,basicGraphicalObject);
					
				  if(!inside|| object==panelP||!destination.canAccept(copy)) { 
					
					    continue;
				  } 
				  else {
					  
					 
					  output.addEditToList(Edit.removeItem(basicGraphicalObject.getParentLayer(), (ZoomableGraphic) object));
					 
					  output.addEditToList( Edit.addItem(destination, copy));
					 
					
						
				  }
			  } else {
				  IssueLog.log("failed to insert item "+object);
			  }
			  
		  }catch (Throwable t){
			  IssueLog.logT(t);
		  }
		
		return output;
	}

	/**moves an objects location from its original coordinates to overlay cordinates fot he image
	 * @param copy
	 * @return 
	 */
	public static BasicGraphicalObject moveFromBaseLocationToOverlay(ImagePanelGraphic panelP, BasicGraphicalObject basicGraphicalObject) {
		
		BasicGraphicalObject copy = basicGraphicalObject.copy();
			 
		  if (copy instanceof BasicShapeGraphic) {
			  copy=((ShapeGraphic) copy).createPathCopy();
		  }
		copy.moveLocation((-panelP.getLocationUpperLeft().getX()), (-panelP.getLocationUpperLeft().getY()));
		  copy.scaleAbout(new Point2D.Double(0,0), 1/panelP.getRelativeScale());
		  return copy;
	}
}
