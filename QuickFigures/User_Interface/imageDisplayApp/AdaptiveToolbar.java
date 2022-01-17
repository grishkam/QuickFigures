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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 12, 2021
 * Date Created: Jan 12, 2021
 * Version: 2022.0
 * 
 */
package imageDisplayApp;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.MenuBarForApp;
import graphicActionToolbar.CurrentFigureSet;
import logging.IssueLog;

/**
 A toolbar window that changes appearance depending on what item is pressed
 */
public class AdaptiveToolbar extends JFrame implements MouseMotionListener, MouseListener,WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MiniToolBarPanel panel=new MiniToolBarPanel(null,false);
	public static AdaptiveToolbar current = null;
	
	public AdaptiveToolbar() {
		this.add(panel);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		setToCurrentlyActiveWindow(true);
		this.addWindowListener(this);
		this.setTitle("Smart Toolbar");
		if (!IssueLog.isWindows()) this.setJMenuBar(new MenuBarForApp());
		this.pack();
		
	}
	
	@Override
	public void setVisible(boolean t) {
		if(t) {
			current=this;
		}
		else if (current==this){
			current=null;
		}
		super.setVisible(t);
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		setToCurrentlyActiveWindow(false);
	}

	/**
	 * 
	 */
	private void setToCurrentlyActiveWindow(boolean moveToolbar) {
		DisplayedImage d = CurrentFigureSet.getCurrentActiveDisplayGroup();
		setToDisplay(moveToolbar, d);
		
	}

	/**Called when there is a change to which display group is active
	 * @param moveToolbar
	 * @param d
	 */
	private void setToDisplay(boolean moveToolbar, DisplayedImage d) {
		if(d==null||panel==null)
			return;
		panel.setDisplay((ImageWindowAndDisplaySet) d);
		if(moveToolbar) {
			Point p = d.getWindow().getLocation();
			this.setLocation(p.x, p.y-70);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		setToCurrentlyActiveWindow(false);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param currentActiveDisplayGroup
	 */
	public static void onDisplayChange(DisplayedImage currentActiveDisplayGroup) {
		if(current!=null)
			try {
				current.setToDisplay(false, currentActiveDisplayGroup);
			} 
		catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
}
