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
 * Date Modified: Dec 4, 2021
 * Version: 2022.0
 */
package handles;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_Shapes.ConnectorGraphic;
import handles.SmartHandle;
import handles.SmartHandleList;
import standardDialog.StandardDialog;
import undo.ConnectorAnchorChangeUndo;

/**
A handle list for moving the anchor points of a connector 
@see ConnectorGraphic
 */
public class ConnectorHandleList extends SmartHandleList{

	public static final int CONNECTOR_HANDLE_CODE=170000;
	private static final int STROKE_HANDLE = 14;

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
		this.add(new ConnectorHandle(STROKE_HANDLE));
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
	//private Point2D downpoint=new Point2D.Double();
	private ConnectorAnchorChangeUndo undo;

	/**
	 * @param i
	 */
	public ConnectorHandle(int i) {
		this.target=i;
		
		if(i==STROKE_HANDLE){
				this.setHandleColor(Color.magenta);
				this.setHandleNumber(STROKE_HANDLE);
				return;
			}
		
		this.setHandleNumber(getHandleIDForAnchor(i));
		if(target==1)
			this.setHandleColor(Color.gray);
	}

	
	
	/**returns the location of the given handle. although the 1st and 3rd handles are located where the anchors are,
	 * the 2nd hande is always located on the line, wherever its anchor may be */
	@Override
	public
	Point2D getCordinateLocation() {
		
		if (this.isStrokeHandle()) {
			return connector.getStrokeHandlePoints()[0];
		}
		
		
		if(target==1&&nAnchors()>2&&!connector.isHorizontal()) 
			return new Point2D.Double(0.5*(connector.getAnchors()[0].getX()+connector.getAnchors()[2].getX()), connector.getAnchors()[1].getY());
		if(target==1&&nAnchors()>2&&connector.isHorizontal()) 
			return new Point2D.Double( connector.getAnchors()[1].getX(), 0.5*(connector.getAnchors()[0].getY()+connector.getAnchors()[2].getY()));
		if(target==1&&nAnchors()==2&&connector.isHorizontal()) 
			return new Point2D.Double(connector.getAnchors()[1].getX(), connector.getAnchors()[1].getY());
		if(target==1&&nAnchors()==2&&!connector.isHorizontal()) 
			return new Point2D.Double(connector.getAnchors()[1].getX(), connector.getAnchors()[1].getY());
		
		if(target<nAnchors())
			return connector.getAnchors()[target];
		
		
		
		else return new Point2D.Double();
	}
	
	/**
	 * @return
	 */
	private boolean isStrokeHandle() {
		return this.getHandleNumber()==STROKE_HANDLE;
	}



	@Override
	public boolean isHidden() {
		if (this.isStrokeHandle()) {
			return false;
		}
		
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
	
	
	/**called when a user presses a handle */
	public void handlePress(CanvasMouseEvent lastDragOrRelMouseEvent) {
		undo=new  ConnectorAnchorChangeUndo(connector);
		
		
		if (this.isStrokeHandle()&&lastDragOrRelMouseEvent.clickCount()==2) {
			return ;
		}
		
		if (this.isStrokeHandle()) {
			return ;
		}
		
		
		/**if the user double clicks, will be allowed to input a point dia dialog*/
		if(lastDragOrRelMouseEvent.clickCount()==2) {
			Point2D starting=connector.getAnchors()[target];
			Point2D newPoint = StandardDialog.getPointFromUser("Input point", starting);
			if(!starting.equals(newPoint)) {
				connector.getAnchors()[target].setLocation(newPoint);
				lastDragOrRelMouseEvent.addUndo(undo);
			}
		}
		
	}
	

	/**called when a user drags a handle */
	@Override
	public void handleRelease(CanvasMouseEvent lastDragOrRelMouseEvent) {
		
		 lastDragOrRelMouseEvent.addUndo(undo);
	}
	
	/**called when a user drags a handle */
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		
		
		if (this.isStrokeHandle()) {
			
				Point2D metric = connector.getStrokeHandlePoints()[1];
				double d = metric.distance(lastDragOrRelMouseEvent.getCoordinatePoint());
				connector.setStrokeWidth((float) (d*2));
			return ;
		}
		
		
		int handlenum = target;
		Point point = lastDragOrRelMouseEvent.getCoordinatePoint();
		connector.getAnchors()[target].setLocation(point);
		
		
		if (handlenum<nAnchors()) 
			connector.getAnchors()[handlenum].setLocation(point);
		
		
		
		if(connector.isHorizontal()&&nAnchors()>2) {
			double ny=connector.getAnchors()[0].getY()+connector.getAnchors()[2].getY();
			ny/=2;
			connector.getAnchors()[1].setLocation( connector.getAnchors()[1].getX(), ny);
			
			
		} else if (!connector.isHorizontal()&&nAnchors()>2) {
			double nx=connector.getAnchors()[0].getX()+connector.getAnchors()[2].getX();
			nx/=2;
			connector.getAnchors()[1].setLocation(nx, connector.getAnchors()[1].getY());
		}
	
		if(undo!=null)
			undo.establishFinalState();
		
		/**decided switching orientations like this would confuse user
		double liftY = Math.abs(point.getY()-downpoint.getY());
		double liftX = Math.abs(point.getX()-downpoint.getX());
		if(nAnchors()==2 && connector.isHorizontal()&&liftX>0&&liftY/liftX>5) {
			connector.setHorizontal(false);
		}else
			if(nAnchors()==2 && !connector.isHorizontal()&&liftY>0&&liftX/liftY>5) {
				connector.setHorizontal(true);
			}*/
		
	}
	
	/**this class adds its own undo*/
	public boolean handlesOwnUndo() {
		return true;
	}
}

}
