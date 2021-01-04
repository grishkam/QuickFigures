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
package uiForAnimations;

import animations.KeyFrameCompatible;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.ZoomableGraphic;
import locatedObject.Hideable;

public class KeyFrameVanish extends KeyFrameAssign{

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public KeyFrameVanish(boolean update) {
		super(update);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMenuCommand() {
		return "Make Vanishing Key Frame";
	}
	
	/**removes the selected item, period. it the item is mortal,
	   it will call its kill() method which should let some listeners know
	   of its demise*/
	public void actioinOnSelected(ZoomableGraphic selectedItem) {
		
		if (selectedItem instanceof KeyFrameCompatible && selectedItem instanceof Hideable) {
			KeyFrameCompatible  m=(KeyFrameCompatible ) selectedItem;
			((Hideable)selectedItem).setHidden(true);
			int frame = new CurrentFigureSet().getCurrentlyActiveDisplay().getCurrentFrame();
			m.getOrCreateAnimation().recordKeyFrame(frame);
		}
		
		
		
		
	}
	

}
