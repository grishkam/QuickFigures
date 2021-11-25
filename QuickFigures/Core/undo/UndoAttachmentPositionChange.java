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
 * Date Modified: Nov 25, 2021
 * Version: 2021.2
 */
package undo;

import java.util.HashMap;

import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;

/**An undo for changes to the attachment positino of an object
 * @see AttachmentPosition
 * */
public class UndoAttachmentPositionChange extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocatedObject2D object;
	private AttachmentPosition iSnap;
	private AttachmentPosition fSnap;
	private Object iIndex;
	private Object fIndex;
	private PanelLayoutGraphic containingLayout;
	private HashMap<LocatedObject2D, Integer> iLoc;
	private HashMap<LocatedObject2D, Integer> fLoc;

	
	
	public UndoAttachmentPositionChange(LocatedObject2D object) {
		
		this.object=object;
		if (object==null ||object.getAttachmentPosition()==null) return;
		iSnap=object.getAttachmentPosition().copy();
		
		/**If the index is fixed, stores it*/
		if (object.getTagHashMap().containsKey("Index"))
			iIndex=object.getTagHashMap().get("Index");
	}
	
	public UndoAttachmentPositionChange(LocatedObject2D object, PanelLayoutGraphic t) { 
		this(object);
		this.containingLayout=t;
		iLoc = new HashMap<LocatedObject2D, Integer>();
		iLoc.putAll(t.getPanelLocations());
	}
	
	public void establishFinalState() {
		fSnap=object.getAttachmentPosition().copy();
		/**If the index is fixed, stores it*/
		if (object.getTagHashMap().containsKey("Index"))
			fIndex=object.getTagHashMap().get("Index");
		
		if(containingLayout!=null&&iLoc!=null) {
			fLoc = new HashMap<LocatedObject2D, Integer>();
			fLoc.putAll(containingLayout.getPanelLocations());
		}
	}
	
	public boolean same() {
		return iSnap.same(fSnap);
	}
	
	public void redo() {
		if (object==null) return;
		if (fSnap==null) object.setAttachmentPosition(null); else
		fSnap.givePropertiesTo(object.getAttachmentPosition());
		if (object.getTagHashMap().containsKey("Index"))
			object.getTagHashMap().put("Index", fIndex);
		
		if(containingLayout!=null&&fLoc!=null) {
			containingLayout.getPanelLocations().putAll(fLoc);;
		}
	}
	
	public void undo() {
		if (object==null) return;
		if (iSnap==null) object.setAttachmentPosition(null); else
		iSnap.givePropertiesTo(object.getAttachmentPosition());
		/**If the index is fixed, makes it possible to change it*/
		if (object.getTagHashMap().containsKey("Index"))
			object.getTagHashMap().put("Index", iIndex);
		
		if(containingLayout!=null&&iLoc!=null) {
			containingLayout.getPanelLocations().putAll(iLoc);;
		}
	}

}
