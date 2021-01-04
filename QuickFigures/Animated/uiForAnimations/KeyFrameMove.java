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
import animations.BasicKeyFrame;
import graphicalObjects.ZoomableGraphic;
import standardDialog.numbers.NumberInputPanel;

public class KeyFrameMove extends BasicTimeLineOperator{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int motion=0;
	
	

	@Override
	public void run() {
		
		motion= (int) NumberInputPanel.getNumber("How many frames forward (- numbers for back)", 0, 1, false, null);
		
		for(ZoomableGraphic item: array) {
			if (item==null) continue;
			actioinOnSelected(item);
			
		}
		
	}
	
	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Move Key Frame";
	}
	
	/**removes the selected item, period. it the item is mortal,
	   it will call its kill() method which should let some listeners know
	   of its demise*/
	public void actioinOnSelected(ZoomableGraphic selectedItem) {
		
		
		
		
		
		if (selectedItem instanceof KeyFrameCompatible ) {
			KeyFrameCompatible  m=(KeyFrameCompatible ) selectedItem;
			int frame = new CurrentFigureSet().getCurrentlyActiveDisplay().getCurrentFrame();
			if (m.getAnimation()==null) return;
			BasicKeyFrame frame2 = m.getOrCreateAnimation().isKeyFrame(frame);
			frame2.setFrame(frame+motion);
		}
		
		
		
		
	}
	

}
