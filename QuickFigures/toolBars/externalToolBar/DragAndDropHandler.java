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

import imageDisplayApp.ImageWindowAndDisplaySet;

/**Interface is used to determine what happens when a 
 * user drops something on an open figure*/
public interface DragAndDropHandler {

	void drop(ImageWindowAndDisplaySet displaySet, DropTargetDropEvent arg0);

	void dropActChange(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0);

	void dragOver(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0);

	void dragExit(ImageWindowAndDisplaySet displaySet, DropTargetEvent arg0);

	void dragEnter(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0);

}
