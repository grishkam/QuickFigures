package uiForAnimations;

import java.util.ArrayList;

import animations.KeyFrameCompatible;
import animations.BasicKeyFrame;
import animations.KeyFrameAnimation;
import graphicActionToombar.CurrentSetInformerBasic;
import graphicalObjects.ZoomableGraphic;
import sUnsortedDialogs.ReflectingFieldSettingDialog;

public class KeyFrameOptionsDialog extends BasicTimeLineOperator {

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
		int frame1=new CurrentSetInformerBasic().getCurrentlyActiveDisplay().getCurrentFrame();
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
