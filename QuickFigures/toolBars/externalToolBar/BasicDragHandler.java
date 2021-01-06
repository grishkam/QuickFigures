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
 * Version: 2021.1
 */
package externalToolBar;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.geom.Point2D;

import imageDisplayApp.ImageWindowAndDisplaySet;

/**Each tool may handle drag and drops differently. 
  Barebones implementation of drag and drop handler
  does nothing interesting but subclasses perform other actions*/
public class BasicDragHandler implements DragAndDropHandler {

	protected Point2D position;
	
	void setPosition(ImageWindowAndDisplaySet displaySet, Point2D arg0) {
		position=displaySet.getConverter().unTransformP(arg0);
	}
	
	@Override
	public void drop(ImageWindowAndDisplaySet displaySet, DropTargetDropEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
	}

	@Override
	public void dropActChange(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
	}

	@Override
	public void dragOver(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
		
	}

	@Override
	public void dragExit(ImageWindowAndDisplaySet displaySet, DropTargetEvent arg0) {
	
		
	}

	@Override
	public void dragEnter(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0) {
		setPosition(displaySet, arg0.getLocation());
		
	}

}
