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

import animations.HasAnimation;
import animations.KeyFrameCompatible;
import animations.Animation;
import animations.KeyFrameAnimation;
import applicationAdapters.DisplayedImage;
import graphicalObjects.ZoomableGraphic;
import selectedItemMenus.LayerSelector;

public class KeyFrameHandling {
	/**returns a list of the items that have valid keyframes*/
	static ArrayList<KeyFrameCompatible> getKeyFrameItems(LayerSelector selector) {
		
		ArrayList<KeyFrameCompatible> output = new ArrayList<KeyFrameCompatible>();
		ArrayList<ZoomableGraphic> selItems = selector.getSelecteditems();
		for (ZoomableGraphic forz: selItems) {
			if (forz instanceof HasAnimation) {
				Animation ani = ((KeyFrameCompatible) forz).getAnimation();
				if (ani instanceof KeyFrameAnimation) {
					//keyFrameAnimation key=(keyFrameAnimation) ani;
					output.add((KeyFrameCompatible) forz);
				}
			}
		}
		
		return output;
	}


static ArrayList<KeyFrameAnimation>  getKeyAnimators(LayerSelector selector) {
	
	ArrayList<KeyFrameAnimation> output = new ArrayList<KeyFrameAnimation>();
	ArrayList<ZoomableGraphic> selItems = selector.getSelecteditems();
	for (ZoomableGraphic forz: selItems) {
		if (forz instanceof HasAnimation) {
			Animation ani = ((KeyFrameCompatible) forz).getAnimation();
			if (ani instanceof KeyFrameAnimation) {
				output.add((KeyFrameAnimation) ani);
			}
		}
	}
	
	return output;
}


public static int findNextKeyFrame(ArrayList<KeyFrameAnimation> frameLists, int currentFrame, int lastFrame) {
	
	for(int i=currentFrame+1; i<lastFrame; i++) {
		for(KeyFrameAnimation kayAni: frameLists) {
			if (kayAni.isKeyFrame(i)!=null) return i;
			}
	}
	
	return lastFrame;
}

public static int findLastKeyFrame(ArrayList<KeyFrameAnimation> frameLists, int currentFrame, int lastFrame) {
	
	for(int i=currentFrame-1; i>=0; i--) {
		for(KeyFrameAnimation kayAni: frameLists) {
			if (kayAni.isKeyFrame(i)!=null) return i;
			}
	}
	
	return lastFrame;
}

public static void applyFrameAnimators(DisplayedImage image, int frame2) {
	
	ArrayList<ZoomableGraphic> listedObjects = image.getImageAsWrapper().getGraphicLayerSet().getAllGraphics();
	image.setCurrentFrame(frame2);
	for(ZoomableGraphic z: listedObjects) {
		if(z instanceof HasAnimation) {
			HasAnimation ani=(HasAnimation) z;
			
			if (ani.getAnimation()!=null)
				ani.getAnimation().setToFrame(frame2);
			
		}
		
	}
	
	
	image.updateDisplay();
}

}