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
 * Date Modified: Jan 5, 2021
 * Version: 2021.2
 */
package handles;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.RectangleEdges;

/**A set of handles for editing of an image panel*/
public class ImagePanelHandleList extends SmartHandleList {

	public static final int FRAME_HANDLE_ID = 10;
	/**
	 * 
	 */
	
	ImagePanelHandle frameHandle=null;
	private ImagePanelGraphic panel;
	private static final long serialVersionUID = 1L;
	
	public ImagePanelHandleList(ImagePanelGraphic panel) {
		this.panel=panel;
		Rectangle bounds = panel.getBounds();
		int[] spots = RectangleEdges.internalLocations;//.getLocationsForHandles(bounds);
		for(int i=0; i<9; i++) {
			this.add(new ImagePanelHandle(panel, i)); 
			
			this.get(i).setCordinateLocation(RectangleEdges.getLocation(spots[i], bounds));
		
		
		}
		frameHandle=new ImagePanelHandle(panel, FRAME_HANDLE_ID); 
		frameHandle.setHandleColor(Color.magenta);
		this.add(frameHandle);
		frameHandle.setCordinateLocation(getFrameHandlePoint());
		
	}
	
	/**updates the locations of each handle to fit the location and size of the image panel*/
	public void updateHandleLocs() {
		ArrayList<Point2D> spots = RectangleEdges.getLocationsForHandles(panel.getBounds());
		for(int i=RectangleEdges.UPPER_LEFT; i<=RectangleEdges.CENTER; i++) {
			SmartHandle smartHandle = this.get(i);
			smartHandle.setCordinateLocation(spots.get(i));
			if(i==panel.getLocationType())
				smartHandle.setHandleColor(Color.red);
			else smartHandle.setHandleColor(Color.white);
		}
		frameHandle.setCordinateLocation(getFrameHandlePoint());
		for(int i=10; i<this.size(); i++) {
			SmartHandle item = this.get(i);
			if (item instanceof AttachmentPositionHandle) {
				AttachmentPositionHandle lh=(AttachmentPositionHandle) item;
			lh.updateLocation();
			}
		}
	}
	
	protected Point getFrameHandlePoint() {
		Rectangle rect2 = panel.getExtendedBounds();
		return new Point((int)(rect2.getCenterX()-rect2.width/4), rect2.y+rect2.height);
	}

}