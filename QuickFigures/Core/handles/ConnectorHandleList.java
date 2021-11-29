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
 * Date Modified: Nov 28, 2021
 * Version: 2021.2
 */
package handles;

import java.awt.Point;
import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_Shapes.ConnectorGraphic;
import handles.SmartHandle;
import handles.SmartHandleList;
import logging.IssueLog;

/**
A handle list for moving the anchor points of a connector 
@see ConnectorGraphic
 */
public class ConnectorHandleList extends SmartHandleList{

	public static final int CONNECTOR_HANDLE_CODE=170000;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ConnectorGraphic connector;

	/**
	 * @param connectorGraphic
	 */
	public ConnectorHandleList(ConnectorGraphic c) {
		this.connector=c;
		this.add(new ConnectorHandle(0));
		this.add(new ConnectorHandle(1));
		this.add(new ConnectorHandle(2));
	}
	
	
	/**returns the handle id for anchor number i
	 * @param i
	 * @return
	 */
	public static int getHandleIDForAnchor(int i) {
		return CONNECTOR_HANDLE_CODE+i;
	}
	
	/**
	A handle that allows the user to adjust the location of a connector graphic
	 */
public class ConnectorHandle extends SmartHandle {

	
	
	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	private int target;
	private Point2D downpoint=new Point2D.Double();

	/**
	 * @param i
	 */
	public ConnectorHandle(int i) {
		this.target=i;
		this.setHandleNumber(getHandleIDForAnchor(i));
	}

	
	
	@Override
	public
	Point2D getCordinateLocation() {
		if(target==1&&nAnchors()>2) 
			return new Point2D.Double(0.5*(connector.getAnchors()[0].getX()+connector.getAnchors()[2].getX()), connector.getAnchors()[1].getY());
		if(target==1&&nAnchors()==2&&connector.isHorizontal()) 
			return new Point2D.Double(connector.getAnchors()[1].getX(), connector.getAnchors()[0].getY());
		if(target==1&&nAnchors()==2&&!connector.isHorizontal()) 
			return new Point2D.Double(connector.getAnchors()[0].getX(), connector.getAnchors()[1].getY());
		
		if(target<nAnchors())
			return connector.getAnchors()[target];
		else return new Point2D.Double();
	}
	
	@Override
	public boolean isHidden() {
		if(target>=nAnchors())
			return true;
		return super.isHidden();
	}

	/**
	 * @return
	 */
	public int nAnchors() {
		return connector.getAnchors().length;
	}
	
	
	/**called when a user drags a handle */
	public void handlePress(CanvasMouseEvent lastDragOrRelMouseEvent) {
		 downpoint = lastDragOrRelMouseEvent.getCoordinatePoint();
	}
	
	/**called when a user drags a handle */
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		int handlenum = target;
		Point point = lastDragOrRelMouseEvent.getCoordinatePoint();
		connector.getAnchors()[target].setLocation(point);
		
		
		if (handlenum<nAnchors()) 
			connector.getAnchors()[handlenum].setLocation(point);
		
		if(connector.isHorizontal()) {
			
		} else if (nAnchors()>2) {
			double nx=connector.getAnchors()[0].getX()+connector.getAnchors()[2].getX();
			nx/=2;
			connector.getAnchors()[1].setLocation(nx, connector.getAnchors()[1].getY());
		}
	
		
		double liftY = Math.abs(point.getY()-downpoint.getY());
		double liftX = Math.abs(point.getX()-downpoint.getX());
		if(nAnchors()==2 && connector.isHorizontal()&&liftX>0&&liftY/liftX>5) {
			connector.setHorizontal(false);
		}else
			if(nAnchors()==2 && !connector.isHorizontal()&&liftY>0&&liftX/liftY>5) {
				connector.setHorizontal(true);
			}
		
	}

}

}
