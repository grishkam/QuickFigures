package uiForAnimations;

import java.util.ArrayList;

import animations.HasAnimation;
import animations.KeyFrameCompatible;
import animations.Animation;
import animations.KeyFrameAnimation;
import applicationAdapters.DisplayedImageWrapper;
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

public static void applyFrameAnimators(DisplayedImageWrapper image, int frame2) {
	
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