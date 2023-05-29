/*******************************************************************************
 * Copyright (c) 2023 Gregory Mazo
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
 * Date Created: May 29, 2023
 * Date Modified: May 29, 2023
 * Version: 2023.2
 */
package undo;

import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.PathGraphic;
import logging.IssueLog;

/**An undoable edit for Adding arrow heads to a path*/
public class UndoArrowHeadAttachment extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	PathGraphic path=null;
	public ArrowGraphic originalHead=null;
	public ArrowGraphic finalHead=null;
	int index=1;
	boolean sameHead=false;
	
	
	private static final long serialVersionUID = 1L;
	
	public UndoArrowHeadAttachment(PathGraphic path,  int index) {
		this.path=path;
		this.index=index;
		if(index==1) {
			originalHead=path.getArrowHead1();
		}
		if(index==2) {
			originalHead=path.getArrowHead2();
		}
		if(path.getArrowHead1()==path.getArrowHead2()&&path.getArrowHead2()!=null) {
			sameHead=true;
		}
		
	}
	
	public UndoArrowHeadAttachment(PathGraphic path, ArrowGraphic head, int index) {
		this.path=path;
		this.finalHead=head;
		this.index=index;
	}
	
	public void redo() {
		if(index==1||sameHead)
			path.setArrowHead1(finalHead);
		if(index==2||sameHead)
			path.setArrowHead2(finalHead);
	
	}
	
	public void undo() {
		if(index==1||sameHead)
			path.setArrowHead1(originalHead);
		if(index==2||sameHead)
			path.setArrowHead2(originalHead);
	}
	
	/**removes the arrow head if it is one of the two arrow heads*/
	public static UndoArrowHeadAttachment removeHead(PathGraphic p1, ArrowGraphic head) {
		int hIndex=0;
		if(p1.getArrowHead1()==head)
			hIndex=1;
		if(p1.getArrowHead2()==head)
			hIndex=2;
		if(hIndex==0)
			return null;
		UndoArrowHeadAttachment output = new UndoArrowHeadAttachment(p1, hIndex);
		output.redo();
		return output;
	}
	
}