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
package animations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import applicationAdapters.DisplayedImage;
import includedToolbars.StatusPanel;
import locatedObject.LocatedObject2D;

/**Class for displaying animations*/
public class Animator implements ActionListener{
	
	private ArrayList<Animation> animationList=new ArrayList<Animation> ();
	private DisplayedImage display;//the image that contains the annimated objects
	
	/**the current frame of the annimation*/
	int currentFrame=0;
	int nFrames=100;//total number of frames
	int fps=12;//the number of frames/second
	Timer timer;

	/**creates an animator for the display
	 * @param animateAll whether to include all the available animations in the animation list*/
	public Animator(DisplayedImage diw, boolean animateAll) {
		display=diw;
		if (animateAll) {
			for(LocatedObject2D t: display.getImageAsWrapper().getLocatedObjects()) {
				if (t instanceof HasAnimation) {
					Animation a = ((HasAnimation) t).getAnimation();
					addAnimation(a);
				}
			}
			nFrames=diw.getEndFrame();
		}
	}
	
	/**adds an animation object to the list*/
	public void addAnimation(Animation g) {
		if(g!=null)
		animationList.add(g);
	}
	
	/**Sets the frame of every animation and updates the image to show that frame to the user */
	void setToCurrentFrameAndUpdateDisplay() {
		int frame=currentFrame;
		for(Animation a: animationList) {
				if(a==null) continue;
				a.setToFrame(frame);
			}
		
			StatusPanel.updateStatus("Animating Frame "+frame);
			StatusPanel.updateStatusBar((100*frame)/nFrames);
			display.setCurrentFrame(currentFrame);
			display.updateDisplay();
			currentFrame=currentFrame+1;
			
	}
	
	/**called to show the animation to the user*/
	public void animate() throws InterruptedException {
		
		int pauseInterval = 1000/fps;

			
			 timer = new Timer(pauseInterval, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				setToCurrentFrameAndUpdateDisplay();
				if (currentFrame>nFrames) timer.stop();//stops the timer when the animation is done
			}});
			
			timer.start();
			
			
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		
	}

}
