import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import applicationAdapters.DisplayedImage;
import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects.ZoomableGraphic;

/**
 * Author: Greg Mazo
 * Date Modified: Dec 29, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */

/**
 
 * 
 */
public class SplashMessage {


private int duration;
private ZoomableGraphic splashItem;
private long startSplash;
private Timer timer;
/**displays an item for a specified period of time*/
public void splashGraphic(ZoomableGraphic z, int time, DisplayedImage d) {
	this.duration=time;
	this.splashItem=z;
	startSplash=System.currentTimeMillis();
	
	timer = new Timer(time+1, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			splashItem=null;
		d.updateDisplay();
			 timer.stop();//stops the timer when the animation is done
		}});
		
		timer.start();
}

public void drawSplash(Graphics2D g) {
	if (System.currentTimeMillis()-startSplash>duration)
		return;
	if(splashItem!=null)  {splashItem.draw(g, new BasicCoordinateConverter());}
}
}
