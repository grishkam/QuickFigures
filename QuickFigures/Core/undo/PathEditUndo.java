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
 * Version: 2023.2
 */
package undo;

import graphicalObjects_Shapes.PathGraphic;
import locatedObject.PathPoint;
import locatedObject.PathPointList;

/**an undo for edits to the points in a path graphic*/
public class PathEditUndo extends AbstractUndoableEdit2 {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PathPoint point =null;
	PathGraphic graph=null;
	private PathPointList points;
	private PathPointList pointsAfterEdit;
	
	boolean iClosed=false;
	boolean fClosed=false;
	
	  public boolean isMyObject(Object o) {
		  if (o==graph) return true;
		  if (o==point) return true;
		  return false;
	  }
		
	
	public PathEditUndo(PathGraphic gra) {
		if (gra==null||gra.getPoints()==null) return;
		this.points=gra.getPoints().copy();
		iClosed=gra.isClosedShape();
		graph=gra;
	}
	
	public void establishFinalState() {saveFinalPositions();}
	
	public void saveFinalPositions() {
	pointsAfterEdit = graph.getPoints().copy();
		fClosed=graph.isClosedShape();
	}

	
	public void undo() {
		graph.setPoints(points);
		graph.setClosedShape(iClosed);
		graph.updatePathFromPoints();
		graph.updateDisplay();
	}
	
	public void redo() {
		graph.setPoints(pointsAfterEdit);
		graph.setClosedShape(fClosed);
		graph.updatePathFromPoints();
		graph.updateDisplay();
	}
	
	
}