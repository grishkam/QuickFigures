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

import java.util.ArrayList;

import animations.KeyFrameCompatible;
import graphicActionToolbar.CurrentFigureSet;
import animations.BasicKeyFrame;
import animations.KeyFrameAnimation;
import graphicalObjects.ZoomableGraphic;
import storedValueDialog.ReflectingFieldSettingDialog;

public class KeyFrameOptionsDialog extends BasicTimeLineOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int Allframes=1, AllObjects=0, All=2;
	
	int all=1;
	public KeyFrameOptionsDialog(int allOftheKeyFrames) {
		all=allOftheKeyFrames;
	}
	

	@Override
	public void run() {
		
			actioinOnSelected();
			
		
	}
	
	@Override
	public String getMenuCommand() {
		if (all==Allframes) return "Key Frame Options (one object, all frames)";
		if (all==All) return "Key Frame Options (all selected objects, all frames)";
		
		
		return "Key Frame Options (current frame, all selected objects)";
	}
	
	/**returns each and every key frame animation*/
	ArrayList<KeyFrameAnimation> getAllKeyFrameAnimations() {
		ArrayList<KeyFrameAnimation> output = new ArrayList<KeyFrameAnimation> ();
		for(ZoomableGraphic selectedItem: array) {
			KeyFrameCompatible  m=(KeyFrameCompatible ) selectedItem;
			if (m.getAnimation() instanceof KeyFrameAnimation)
						output.add((KeyFrameAnimation) m.getAnimation());
		}
		
		return output;
	}
	
	/**returns just the key frame animations with a key frame at object x*/
	ArrayList<KeyFrameAnimation> getAllKeyFrameAnimationAtFrame(int frameCurrent) {
		ArrayList<KeyFrameAnimation> output = new ArrayList<KeyFrameAnimation> ();
		ArrayList<KeyFrameAnimation> allAnimations = getAllKeyFrameAnimations();
		for(KeyFrameAnimation selectedItem: allAnimations) {
			BasicKeyFrame frame = selectedItem.isKeyFrame(frameCurrent);
			if (frame!=null) output.add(selectedItem);
		}
		
		return output;
	}
	
	/**returns just the key frame animations with a key frame at object x*/
	ArrayList<BasicKeyFrame> getAllKeyFramesAt(int frameCurrent) {
		ArrayList<BasicKeyFrame> output = new ArrayList<BasicKeyFrame> ();
		ArrayList<KeyFrameAnimation> allAnimations = getAllKeyFrameAnimations();
		for(KeyFrameAnimation selectedItem: allAnimations) {
			BasicKeyFrame frame = selectedItem.isKeyFrame(frameCurrent);
			if (frame!=null) output.add(frame);
		}
		
		return output;
	}
	
	/**returns all the key frames of all selected items*/
	ArrayList<BasicKeyFrame> getAllKeyFrames() {
		ArrayList<BasicKeyFrame> output = new ArrayList<BasicKeyFrame> ();
		ArrayList<KeyFrameAnimation> allAnimations = getAllKeyFrameAnimations();
		for(KeyFrameAnimation selectedItem: allAnimations) {
			 output.addAll(selectedItem.getKeyFrames());
		}
		
		return output;
	}
	

	KeyFrameAnimation getSelectedItemKeyFrameAnimation(ZoomableGraphic selectedItem) {
		KeyFrameCompatible  m=(KeyFrameCompatible ) selectedItem;
		return m.getOrCreateAnimation();
			
	}
	
	
	/**Performs an options dialog*/
	public void actioinOnSelected() {
		int frame1=new CurrentFigureSet().getCurrentlyActiveDisplay().getCurrentFrame();
		KeyFrameAnimation selectedItem = this.getAllKeyFrameAnimationAtFrame(frame1).get(0);

		/***/
		ArrayList<BasicKeyFrame> frames = getAllKeyFramesAt(frame1 );
			
			 
			BasicKeyFrame keyFrame = selectedItem .isKeyFrame(frame1);
			
			if (keyFrame!=null){
				ReflectingFieldSettingDialog dialog = new ReflectingFieldSettingDialog(keyFrame);
				
				
				if (all==Allframes) dialog.setExtraObjects(selectedItem.getKeyFrames());
				if (all==AllObjects) dialog.setExtraObjects(frames);
				if (all==All) dialog.setExtraObjects(getAllKeyFrames());
				
				
				dialog.showDialog();
			}
		
		
		
		
		
	}
	


}
