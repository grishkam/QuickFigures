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
package undo;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects.ZoomableGraphic;
import layersGUI.GraphicSetDisplayTree;

/**A superclass for many undoable edits*/
public class AbstractUndoableEdit2 extends AbstractUndoableEdit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	protected GraphicSetDisplayTree tree;
	
	ArrayList<ZoomableGraphic> actedOnObjects=new ArrayList<ZoomableGraphic> ();
	
  public boolean isMyObject(Object o) {
	  return actedOnObjects.contains(o);
  }
	
  /**stores the final locations and form of the objects*/
	public void establishFinalState() {}
	public void redo() {
	
	}
	
	public void undo() {
		
	}
	
	public boolean canUndo() {
		return true;
	}
	
	public boolean canRedo() {
		return true;
	}
	
	public void selectTree() {
		if (tree!=null)tree.addUserObjectsToSelection(actedOnObjects);
	}
	
	public void setTree(GraphicSetDisplayTree tree) {
		this.tree=tree;
		
	}
	

	


}
