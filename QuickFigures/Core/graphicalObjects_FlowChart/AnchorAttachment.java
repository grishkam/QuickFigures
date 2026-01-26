/*******************************************************************************
 * Copyright (c) 2023 Gregory Mazo
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
 * Date Created: May 29, 2023
 * Date Modified: July 7, 2023
 * Version: 2023.2
 */
package graphicalObjects_FlowChart;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import graphicalObjects_Shapes.ArrowGraphic;
import handles.SmartHandleForPathGraphic;
import locatedObject.PathPoint;

/**Keeps track of attachment sites*/
public class AnchorAttachment implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pathPoint;
	private AnchorObjectGraphic path;
	private ChartNexus shape;
	private int index;

	public AnchorAttachment(int pathPointIndex, AnchorObjectGraphic anchor, ChartNexus c, int nexusAncorSiteIndex ) {
		
		this.pathPoint=pathPointIndex;
		this.setPath(anchor);
		
		this.setAnchorSite(c);
		this.setAnchorIndex(nexusAncorSiteIndex);
	}
	
	/**Changes the attachment location to the one nearest the given point*/
	public void changeAttachmentLocation(Point2D p) {
		changeAttachmentLocation(p, shape, path,  getPathPoint());
		
	}
	
	public void updateLocation() {
		Point2D anchor = getAttachmentLocation();
		SmartHandleForPathGraphic.moveHandleTo(anchor, getPath(), getPathPoint());
		getPath().updatePathFromPoints();
		
		
		if(getPath().getArrowHead1()!=null && pathPoint==0) {
				getPath().prepareArrowHead1();
				ArrowGraphic copyOfHead = getPath().getArrowHead1().copy();
				Point2D newNotch = copyOfHead.moveHeadToNotch1();
				SmartHandleForPathGraphic.moveHandleTo(newNotch, getPath(), getPathPoint());
		}
		
		if(getPath().getArrowHead2()!=null && getPathPointIndex()==getPath().getPoints().size()-1) {
			getPath().prepareArrowHead2();
			ArrowGraphic copyOfHead = getPath().getArrowHead2().copy();
			Point2D newNotch = copyOfHead.moveHeadToNotch1();
			SmartHandleForPathGraphic.moveHandleTo(newNotch, getPath(), getPathPoint());
	}
	}

	/**
	 * @return
	 */
	public PathPoint getPathPoint() {
		if(pathPoint==-1) return getPath().getPoints().getLastPoint();
		return getPath().getPoints().get( pathPoint);
	}

	/**returns the current location of attachment
	 * @return
	 */
	public Point2D getAttachmentLocation() {
		ArrayList<Point2D> createPathCopy = getAnchorSite().getAttachmentPoints(0);
		
		int anchorIndex = getAnchorIndex();
		if(anchorIndex>=createPathCopy.size())
			anchorIndex=createPathCopy.size()-1;
		Point2D point = createPathCopy.get(anchorIndex);
		
		return point;
	}

	public ChartNexus getAnchorSite() {
		return shape;
	}

	public void setAnchorSite(ChartNexus shape) {
		this.shape = shape;
	}

	public int getAnchorIndex() {
		return index;
	}

	public void setAnchorIndex(int index) {
		this.index = index;
	}

	public AnchorObjectGraphic getPath() {
		return path;
	}

	public void setPath(AnchorObjectGraphic path) {
		this.path = path;
	}
	
	

	/**changes the attachment location for the point on the nexus that is nearest the given clickpoint
	 * @param coordinatePoint
	 * @param r
	 * @param theAnchorPath
	 * @param thePathPoint
	 */
	public static void changeAttachmentLocation(Point2D coordinatePoint, ChartNexus r, AnchorObjectGraphic theAnchorPath,
			PathPoint thePathPoint) {
		int near_index =	r.getNearestAttachmentPointIndex(coordinatePoint);//which point on the nexus is nearest
		
		AnchorAttachment p = r.getFlowChart().getAttachmentforPoint(thePathPoint);
		
		if(p!=null) {
			boolean isSame = p.getAnchorSite()==r;//true if the shape that is the drag location is the same as the anchor shape
			if(isSame) {
				p.setAnchorIndex(near_index);
		
			} else {
				p.setAnchorIndex(near_index);
				p.setAnchorSite(r);
			}
		} else {
			
			int currentHandleIndex = theAnchorPath.getPoints().indexOf(thePathPoint);
			
			AnchorAttachment aa = new AnchorAttachment(currentHandleIndex, theAnchorPath, r, near_index);
			r.addAttachment(aa); 
		}
	}

	/**
	 * @return
	 */
	public int getPathPointIndex() {
		if(pathPoint==-1) return getPath().getPoints().size()-1;
		return pathPoint;
	}
}