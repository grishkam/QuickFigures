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
 * Date Created: Nov 12, 2021
 * Date Modified: Nov 12, 2021
 * Version: 2023.2
 * 
 */

package undo;

import java.lang.reflect.Field;

/**
An undo that can be used for a wide variety of objects 
provides a way to copy a values from a list of fields 
from one object to another using reflection.
 */
public class FieldCopyUndo  extends AbstractUndoableEdit2 {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object target;
	private Object start;
	private Object end;

	public FieldCopyUndo(Object target, Object originalState, Object finalState) {
		this.target=target;
		this.start=originalState;
		this.end=finalState;
		copyFeilds( target, start);
	}
	
	 /**stores the final locations and form of the objects*/
		public void establishFinalState() {
			copyFeilds( target, end);
		}
		
		public void redo() {
			copyFeilds(end, target);
		}
		
		public void undo() {
			copyFeilds(start, target);
		}
		
	
	/**copies the fields from one object to another
	 * @param source
	 * @param destination*/
	public static void copyFeilds(Object source, Object destination, Field... desiredFields) {
		
		if (desiredFields==null ||desiredFields.length==0) 
			 desiredFields = source.getClass().getDeclaredFields();
		
		
		for(Field f: desiredFields) {
			try {
				f.set(destination, f.get(source));
			} catch (Exception e) {
				
			}
		}
		
	}

}
