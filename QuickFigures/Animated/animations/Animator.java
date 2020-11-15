package animations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import applicationAdapters.DisplayedImage;
import includedToolbars.StatusPanel;

/**Class of displaying animations*/
public class Animator implements ActionListener{
	
	private ArrayList<Animation> animationList=new ArrayList<Animation> ();
	private DisplayedImage display;
	int currentFrame=0;
	int nFrames=100;
	int fps=12;
	Timer timer;

	public Animator(DisplayedImage diw) {
		display=diw;
	}
	
	public void addAnimation(Animation g) {
		if(g!=null)
		animationList.add(g);
	}
	
	void setToCurrentFrameAndUpdateDisplay() {
		int frame=currentFrame;
		for(Animation a: animationList) {
				if(a==null) continue;
				a.setToFrame(frame);
			}
		
			StatusPanel.updateStatus("Animating Frame "+frame);
			StatusPanel.updateStatusBar((100*frame)/nFrames);
			display.updateDisplay();
			currentFrame=currentFrame+1;
			
	}
	
	public void animate() throws InterruptedException {
		
		int pauseInterval = 1000/fps;

			
			 timer = new Timer(pauseInterval, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				setToCurrentFrameAndUpdateDisplay();
				if (currentFrame>nFrames) timer.stop();
			}});
			
			timer.start();
			
			
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		
	}

}
