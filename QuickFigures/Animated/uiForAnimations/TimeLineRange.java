package uiForAnimations;

import standardDialog.NumberInputPanel;

public class TimeLineRange extends KeyFrameProgress {

	public TimeLineRange(int type) {
		super(type);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		int maxf= (int) NumberInputPanel.getNumber("Set end of timeline", display.getEndFrame(), 1, false, null);
		display.setEndFrame(maxf);
		ui.setTimeLineLength(maxf);
		ui.pack();ui.repaint();
		KeyFrameHandling.applyFrameAnimators(display, maxf);
		
	}
	
	@Override
	public String getMenuCommand() {
		return "Set Time Line Range";
	}

}
