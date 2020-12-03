/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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

import java.util.ArrayList;

import animations.KeyFrameAnimation;

public class KeyFrameProgress extends BasicTimeLineOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int type;

	public KeyFrameProgress(int type) {
		this.type=type;
	}
	

	@Override
	public void run() {
		
		ArrayList<KeyFrameAnimation> framesHandlers = KeyFrameHandling.getKeyAnimators(selector);
		int frame = KeyFrameHandling.findNextKeyFrame(framesHandlers, display.getCurrentFrame(), 100000);
		if (type==1) frame =KeyFrameHandling.findLastKeyFrame(framesHandlers, display.getCurrentFrame(), 100000);
	
		ui.setFrame(frame);
		KeyFrameHandling.applyFrameAnimators(display, frame);
		
	}
	
	@Override
	public String getMenuCommand() {
		if (type==1) return "Previous Key Frame";
		return "Next Key Frame";
	}
	

}
