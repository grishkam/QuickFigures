/**
 * Author: Greg Mazo
 * Date Modified: Jan 12, 2021
 * Date Created: Jan 12, 2021
 * Version: 2021.1
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
	private void setToCurrentlyActiveWindow(boolean active) {
		DisplayedImage d = CurrentFigureSet.getCurrentActiveDisplayGroup();
		setToDisplay(active, d);
		
	}

	/**Called when there is a change to which display group is active
	 * @param active
	 * @param d
	 */
	private void setToDisplay(boolean active, DisplayedImage d) {
		panel.setDisplay((ImageWindowAndDisplaySet) d);
		if(active) {
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
				current.setToDisplay(true, currentActiveDisplayGroup);
			} 
		catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
}
