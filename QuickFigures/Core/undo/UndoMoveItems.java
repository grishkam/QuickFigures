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
 * Version: 2022.1
 */
package undo;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.undo.AbstractUndoableEdit;

import animations.GroupsTranslationAnimation;
import animations.HasAnimation;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import locatedObject.LocatedObject2D;
import animations.Animation;

/**An undoable edit for moving objects*/
public class UndoMoveItems extends AbstractUndoableEdit implements HasAnimation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**stores the items that this undo works with*/
	private ArrayList<LocatedObject2D> list;
	
	/**stores the locations of the items that this undo works with*/
	HashMap<LocatedObject2D, Point2D> originalLocal=new HashMap<LocatedObject2D, Point2D>();
	HashMap<LocatedObject2D, Point2D> finalLocal=new HashMap<LocatedObject2D, Point2D>();
	
	public UndoMoveItems(LocatedObject2D... list2){
		this.list=new ArrayList<LocatedObject2D> ();
		for(LocatedObject2D l: list2) {list.add(l);}
		establishOriginalLocations();
	}
	
	public UndoMoveItems(ArrayList<LocatedObject2D> list, boolean layoutsInMind) {
		this.list=new ArrayList<LocatedObject2D> ();
		this.list.addAll(list);
		
		for(LocatedObject2D l: list) {
			if (l instanceof PanelLayoutGraphic) {
				((PanelLayoutGraphic) l).generateCurrentImageWrapper();
				ArrayList<LocatedObject2D> addons = ((PanelLayoutGraphic) l).getPanelLayout().getVirtualWorksheet().getLocatedObjects();
				this.list.addAll(addons);
			}
		}
		
		establishOriginalLocations();
	}
	
	public UndoMoveItems(ArrayList<LocatedObject2D> list) {
		this(list, false);
	}

	/**stores the upper left locations of each object's as its original position*/
	private void establishOriginalLocations() {
		for(LocatedObject2D l: list) {
			Point2D point = l.getLocationUpperLeft();
			
			originalLocal.put(l, point);
		}
		
	}
	
	/**stores the upper left locations of each object's as its final position*/
	public void establishFinalLocations() {
		for(LocatedObject2D l: list) {
			Point2D point = l.getLocationUpperLeft();
			finalLocal.put(l, point);
		}
		
	}
	
	public void establishFinalState() {
		establishFinalLocations();
	}
	
	public void undo() {
		for(LocatedObject2D l: list) {
			
			
			Point2D pt = originalLocal.get(l);
			Point2D ptfinal = l.getLocationUpperLeft();
			moveLocationToFrom(l, pt, ptfinal);
		}
	}

	
	
	public void redo() {
		for(LocatedObject2D l: list) {
			Point2D pt = finalLocal.get(l);
			Point2D ptfinal = l.getLocationUpperLeft();
			moveLocationToFrom(l, pt, ptfinal);
		}
	}
	
	/**
	when given two points, computes the distance between those points and moved the object based on that distance
	 */
	public void moveLocationToFrom(LocatedObject2D l, Point2D ptEnding, Point2D ptStarting) {
		double dx = ptEnding.getX()-ptStarting.getX();
		double dy = ptEnding.getY()-ptStarting.getY();
		l.moveLocation(dx, dy);
	}
	
	public boolean canRedo() {
		return true;
	}

	/**creates an animation for this undo*/
	@Override
	public Animation getAnimation() {
		return new GroupsTranslationAnimation( finalLocal, originalLocal);

	}

	public ArrayList<LocatedObject2D> getObjectList() {
		return list;
	}

}
