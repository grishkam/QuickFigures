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
 * Date Modified: Jan 6, 2021
 * Version: 2022.2
 */
package menuUtil;

import applicationAdapters.CanvasMouseEvent;
import undo.UndoManagerPlus;

/**A menu item that can store information about the context in which it is called*/
public interface SmartMenuItem {
	
	/**stores the last mouse event*/
	public void setLastMouseEvent(CanvasMouseEvent e);
	
	/**stores an undo manager*/
	public void setUndoManager(UndoManagerPlus undoManager);
	
	/**returns the stored undo manager*/
	public UndoManagerPlus getUndoManager();

}
