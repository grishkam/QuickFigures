package uiForAnimations;

import java.util.ArrayList;

import animations.KeyFrameAnimation;

public class KeyFrameProgress extends BasicTimeLineOperator {

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
