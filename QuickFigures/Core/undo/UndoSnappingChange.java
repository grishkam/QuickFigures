/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package undo;

import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.AttachmentPosition;

public class UndoSnappingChange extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocatedObject2D object;
	private AttachmentPosition iSnap;
	private AttachmentPosition fSnap;

	public UndoSnappingChange(LocatedObject2D object) {
		
		this.object=object;
		if (object==null ||object.getAttachmentPosition()==null) return;
		iSnap=object.getAttachmentPosition().copy();
	}
	
	public void establishFinalState() {
		fSnap=object.getAttachmentPosition().copy();
	}
	
	public boolean same() {
		return iSnap.same(fSnap);
	}
	
	public void redo() {
		if (object==null) return;
		if (fSnap==null) object.setAttachmentPosition(null); else
		fSnap.givePropertiesTo(object.getAttachmentPosition());
		
	}
	
	public void undo() {
		if (object==null) return;
		if (iSnap==null) object.setAttachmentPosition(null); else
		iSnap.givePropertiesTo(object.getAttachmentPosition());
		
	}

}
