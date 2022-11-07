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
 * Version: 2022.2
 */
package undo;

/**An undoable edit for any objects that implements the interface
 * @see SimpleTraits
 * */
public class SimpleItemUndo<Type> extends AbstractUndoableEdit2  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SimpleTraits<Type> parameter;
	private SimpleTraits<Type> starting;
	private SimpleTraits<Type> ending;
	private Type item;
	
	public SimpleItemUndo(SimpleTraits<Type> p) {
		this.item=p.self();
		this.parameter=p;
		
		starting= parameter.copy();
	}
	
	public void extablishFinalState() {
		ending= parameter.copy();
	}
	
	
	public void redo() {
		ending.giveTraitsTo(item);
	}
	
	public void undo() {
		starting.giveTraitsTo(item);
	}

}
