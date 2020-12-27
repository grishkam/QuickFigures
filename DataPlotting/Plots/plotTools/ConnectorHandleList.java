/**
 * Author: Greg Mazo
 * Date Modified: Dec 27, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package plotTools;

import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEvent;
import handles.SmartHandle;
import handles.SmartHandleList;

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
	 
	 * 
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
		return connector.getAnchors()[target];
	}
	
	/**called when a user drags a handle */
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		connector.getAnchors()[target].setLocation(lastDragOrRelMouseEvent.getCoordinatePoint());
	
	}

}

}
