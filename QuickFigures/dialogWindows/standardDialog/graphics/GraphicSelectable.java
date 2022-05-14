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
 * Version: 2022.1
 */
package standardDialog.graphics;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import standardDialog.choices.UserSelectable;

/**a superclass for Graphic components that notify item listeners when edited
 * @see GraphicComponent
 * @see ItemListener*/
public abstract class GraphicSelectable extends GraphicComponent implements MouseListener, MouseMotionListener, UserSelectable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<ItemListener> listeners=new ArrayList<ItemListener>();
	
	
	@Override
	public void addItemListener(ItemListener l) {
		listeners.add(l);
		
	}
	
	@Override
	public void removeItemListener(ItemListener l) {
		listeners.remove(l);
	}
	
	public void notifyListeners(ItemEvent iv) {
		for(ItemListener l: listeners) {
			l.itemStateChanged(iv);
		}
	}



}
