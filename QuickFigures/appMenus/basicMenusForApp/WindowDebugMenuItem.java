package basicMenusForApp;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.KeyListener;

import javax.swing.Icon;

import applicationAdapters.DisplayedImage;
import logging.IssueLog;

/**A menu item that I wrote to help me search our a bug that causes the 
 * key board shortcuts to stop working. */
public  class WindowDebugMenuItem implements MenuItemForObj{

	boolean on=true;
	public  WindowDebugMenuItem() {
		this(true);
	}
	
	public  WindowDebugMenuItem(boolean on) {
		this.on=on;
	}
	
	@Override
	public String getMenuPath() {
	
		return "Debug";
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (on) {
			
		
		Window window = diw.getWindow();
		listKeyListeners(window);
		
		}
		
		else {
			
		}
	}

	/**
	 * @param window
	 */
	public void listKeyListeners(Container window) {
		if(window==null) return;
		listKeyListeners(window.getKeyListeners());
		for(Component c: window.getComponents()) {
			IssueLog.log("will find key listeners of "+c);
			listKeyListeners(c.getKeyListeners());
		}
	}

	/**
	 * @param key
	 */
	public void listKeyListeners(KeyListener[] key) {
		
		for(KeyListener k:key) {
			IssueLog.log(k.toString());
		}
		
	}

	@Override
	public String getCommand() {
		return getNameText()+"now";
	}

	@Override
	public String getNameText() {
		
		return "Investigate Window";
	}
	
	public static void main(String[] args) {
		new WindowDebugMenuItem().performActionDisplayedImageWrapper(null);
		
	}

	@Override
	public Icon getIcon() {
		return null;
	}
	
	
	
	
}
