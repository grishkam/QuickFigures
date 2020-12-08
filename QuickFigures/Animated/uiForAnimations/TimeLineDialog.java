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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import animations.KeyFrameCompatible;
import animations.Animation;
import animations.KeyFrameAnimation;
import applicationAdapters.DisplayedImage;
import basicMenusForApp.SelectedSetLayerSelector;
import standardDialog.StandardDialog;
import standardDialog.numbers.NumberInputPanel;

public class TimeLineDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DisplayedImage image;
	JMenuBar b=new JMenuBar();
	int timeLineLength=500;
	
	
	TimeLineDialog.timePanel timePanel=new TimeLineDialog.timePanel();
	private SelectedSetLayerSelector selector;
	private NumberInputPanel timeSlider;
	
	
	public void setTimeLineLength(int length) {
		timeLineLength=length;
		if(timeSlider!=null) {timeSlider.setSliderRange(0, timeLineLength);}
		
		repaint();
	}
	
	
	public void setFrame(int frame) {
		timeSlider.setNumber(frame);
	}
	
	void prepareMenuBar() {
		b=new JMenuBar();
		b.add(new TimeLineMenu(image, selector, this));
		b.add(new TimeLineMenu2(image, selector, this));
		setJMenuBar(b);
	}
	
	public TimeLineDialog(DisplayedImage diw) {
		this.image=diw;
		this.setTitle("Timeline for "+diw.getImageAsWrapper().getTitle());
		selector=new SelectedSetLayerSelector(diw.getImageAsWrapper());
		
		 timeSlider = new NumberInputPanel("Frame", diw.getCurrentFrame(), true, true, 0, diw.getEndFrame());
		add("frame", timeSlider);
		GridBagConstraints gc = getCurrentConstraints()
				;
		if (gc!=null) {gc.gridy+=3;gc.gridx+=1;}
		
		this.setTimeLineLength(diw.getEndFrame());
		prepareMenuBar();
		
		add(timePanel, gc);
		
	
		
		setLocation(100, 500);
	}
	
	

	
protected void afterEachItemChange() {
		int frame2 = (int)this.getNumber("frame");
		
		KeyFrameHandling.applyFrameAnimators(image, frame2);
		
		
		timePanel.repaint();
	
	}




class timePanel extends JComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(timeLineLength,50);
	}
	
	public void paintComponent(Graphics g) {
		//super.paint(g);
		
		//if (buf=false) {this.createBufferStrategy(4); buf=true;}
		if (g instanceof Graphics2D) {
			 Graphics2D g2 = (Graphics2D)g;
		
		//BasicCordinateConverter cc = window.getZoomer().getConverter();
		
		/**marks the borders of the canvas with black and grey*/
		g2.setPaint(Color.darkGray);//.setColor(Color.darkGray);
		
		
		
		
		Rectangle r = new Rectangle(-1, -1, this.getWidth(), this.getHeight());

		Area greyArea = (new Area(r));
		
		
		((Graphics2D) g).fill(greyArea);
		
		g.setColor(Color.black);
		g.drawRect(-1, -1, this.getWidth(), this.getHeight());
		g.setColor(Color.yellow);
			ArrayList<KeyFrameCompatible> selItems = KeyFrameHandling.getKeyFrameItems(selector);
			/**draws a tick on the timeline for each key frame*/
			for(int j=0; j<selItems.size();j++) {
				KeyFrameCompatible forz =selItems.get(j);
				
				/**if more than one item is selected, each item will have distinct tick mark color*/
				double ranks=((double)j+1)/selItems.size();
				g.setColor(new Color((int)(240*ranks), 200, 0));
				((Graphics2D) g).setStroke(new BasicStroke(3));
				
				Animation ani =  forz.getAnimation();
					if (ani instanceof KeyFrameAnimation) {
						KeyFrameAnimation key=(KeyFrameAnimation) ani;
						for(int i=0; i<timeLineLength; i++) {
							if (key.isKeyFrame(i)!=null) {
								g.drawLine(i, 0, i, 25);
							}
						}
					}
					
			}
				
			
			g.setColor(Color.BLACK); ((Graphics2D) g).setStroke(new BasicStroke(2));
			g.drawLine(image.getCurrentFrame(), 0, image.getCurrentFrame(), 15);
			 
		}
		// IssueLog.log(new GraphicEncoder(gmp).getBytes());
		
	}
		
		
		;
}
}


