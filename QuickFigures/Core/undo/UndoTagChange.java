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
 * Date Created: Dec 5, 2021
 * Date Modified: Dec 5, 2021
 * Version: 2022.2
 */
package undo;

import java.util.HashMap;

import layersGUI.GraphicSetDisplayTree;

/**undo for the hide/unhide operations*/
public class UndoTagChange extends AbstractUndoableEdit2 {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<String, Object> imap=new HashMap<String, Object>();//the original condition
	HashMap<String, Object> fmap=new HashMap<String, Object>();//the final condition
	private HashMap<String, Object> currentMap;
	
	
	public UndoTagChange(HashMap<String, Object> input) {
		this.currentMap=input;
		imap.putAll(currentMap);
		establishFinalState() ;
	}
	
	  /**stores the final locations and form of the objects*/
		public void establishFinalState() {
			fmap.clear();
			fmap.putAll(currentMap);
		}
	
	public void undo() {
		currentMap.clear();
		currentMap.putAll(imap);
	}
	
	public void redo() {
		currentMap.clear();
		currentMap.putAll(fmap);
	}

	public void setTree(GraphicSetDisplayTree tree) {
		this.tree=tree;
		
	}

}
