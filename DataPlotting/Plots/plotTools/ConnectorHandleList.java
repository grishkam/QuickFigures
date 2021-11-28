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
package plotTools;

import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEvent;
import handles.SmartHandle;
import handles.SmartHandleList;
import logging.IssueLog;
import plotParts.stats.ConnectorGraphic;

/**
A handle list for moving the anchor points of a connector 
@see ConnectorGraphic
 */
public class ConnectorHandleList extends SmartHandleList{

	public static int CONNECTOR_HANDLE_CODE=170000;

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
	
	
	/**
	A handle that allows the user to adjust the location of a connector graphic
	 */
public class ConnectorHandle extends SmartHandle {

	
	
	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	private int target;

	/**
	 * @param i
	 */
	public ConnectorHandle(int i) {
		this.target=i;
		this.setHandleNumber(CONNECTOR_HANDLE_CODE+i);
	}
	
	@Override
	public
	Point2D getCordinateLocation() {
		if(target==1) return new Point2D.Double(0.5*(connector.getAnchors()[0].getX()+connector.getAnchors()[2].getX()), connector.getAnchors()[1].getY());
		return connector.getAnchors()[target];
	}
	
	/**called when a user drags a handle */
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		int handlenum = target;
		connector.getAnchors()[target].setLocation(lastDragOrRelMouseEvent.getCoordinatePoint());
		
		
		if (handlenum<connector.getAnchors().length) 
			connector.getAnchors()[handlenum].setLocation(lastDragOrRelMouseEvent.getCoordinatePoint());
		
		if(connector.isHorizontal()) {
			
		} else if (connector.getAnchors().length>2) {
			double nx=connector.getAnchors()[0].getX()+connector.getAnchors()[2].getX();
			nx/=2;
			connector.getAnchors()[1].setLocation(nx, connector.getAnchors()[1].getY());
		}
	
	}

}

}
