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
 * Date Created: Nov 29, 2021
 * Date Modified: Nov 29, 2021
 * Version: 2022.1
 */
package undo;

import java.awt.geom.Point2D;

import graphicalObjects_Shapes.ConnectorGraphic;

/**
 An  undo for changes in the anchor positons of a connector
 */
public class ConnectorAnchorChangeUndo extends AbstractUndoableEdit2 {

	private Point2D[] iAnchor;
	private ConnectorGraphic connector;
	private Point2D[] fAnchor;

	/**
	 * @param connector
	 */
	public ConnectorAnchorChangeUndo(ConnectorGraphic connector) {
		this.connector=connector;
		
		iAnchor = copyAnchors(connector);
	}
	/**
	 * @param connector
	 * @return
	 */
	public Point2D[] copyAnchors(ConnectorGraphic connector) {
		Point2D[] oldAnchors = connector.getAnchors();
		Point2D[] nAnchor = new Point2D[oldAnchors.length];
		for(int i=0; i<oldAnchors.length; i++) nAnchor[i]=(Point2D) oldAnchors[i].clone();
		return nAnchor;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	  /**stores the final locations and form of the objects*/
		public void establishFinalState() {fAnchor=copyAnchors(connector);;}
		public void redo() {
			if(fAnchor!=null)
				connector.setAnchors(fAnchor);
			connector.updateShapeFromAnchors();
			connector.updatePathFromPoints();
		}
		
		public void undo() {
			if(iAnchor!=null)
				connector.setAnchors(iAnchor);
			connector.updateShapeFromAnchors();
			connector.updatePathFromPoints();
		}
		
	

}
