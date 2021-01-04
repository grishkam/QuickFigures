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
package graphicActionToolbar;

import java.util.Collection;

import javax.swing.undo.UndoableEdit;

import applicationAdapters.DisplayedImage;
import graphicalObjects.FigureDisplayContainer;

/**Interface meant to keep track of which figure is the currently active window
 methods tell an object what the currently active display set is*/
public interface CurrentSetInformer {

	public FigureDisplayContainer getCurrentlyActiveOne() ;
	
	
	public DisplayedImage getCurrentlyActiveDisplay() ;
	
	public Collection<DisplayedImage> getVisibleDisplays() ;

	public void updateDisplayCurrent();
	public void addUndo(UndoableEdit e);
	
}
