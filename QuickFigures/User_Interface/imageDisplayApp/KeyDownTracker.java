package imageDisplayApp;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import includedToolbars.StatusPanel;

/**A simple class that keeps records of key presses and releases.
 Work in progress. Sometimes this class appears to miss events so it is not
 used for any crucial features.
  */
public class KeyDownTracker implements KeyListener {
	
	
	/**keeps track of what keys are up and down in the image*/
	private static HashMap<Character, Boolean> keysDown=new HashMap<Character, Boolean>();
	private static HashMap<Integer, Boolean> keysDownCode=new HashMap<Integer, Boolean>();
	
	public static String listKeysDown() {
		String output="";
		for(Character c: keysDown.keySet()) {
			if(keysDown.get(c)) output+=" "+c;
		}
		
		return output;
	}
	
	public static boolean isKeyDown(char c) {
		if (keysDown.get(c)==null) return false;
		return keysDown.get(c);
	}
	public static boolean isKeyDown(int code) {
		if (keysDownCode==null) {return false;}
		Boolean b=keysDownCode.get(code);
		if (b==null) return false;
		return b;
	}
	
	public static void setKeyDown(KeyEvent arg0) {
		keysDown.put(arg0.getKeyChar(), true);
		keysDownCode.put(arg0.getKeyCode(), true);
		StatusPanel.updateStatus(arg0.getKeyChar()+" press");
	}
	

	public static void setKeyUp(KeyEvent arg0) {
		keysDown.put(arg0.getKeyChar(), false);
		keysDownCode.put(arg0.getKeyCode(), false);
		StatusPanel.updateStatus(arg0.getKeyChar()+" release");
	}
	

	@Override
	public void keyPressed(KeyEvent arg0) {
		
		setKeyDown(arg0);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		setKeyUp(arg0);
		}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
