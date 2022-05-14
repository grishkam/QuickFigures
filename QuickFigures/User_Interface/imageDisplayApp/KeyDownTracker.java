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
 * Version: 2022.1
 */
package imageDisplayApp;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import includedToolbars.StatusPanel;

/**A simple class that keeps records of key presses and releases.
 Work in progress. Sometimes this class appears to miss events so it is not
 used for any crucial features.
  */
public class KeyDownTracker implements AWTEventListener {
	
	
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
	
	/**is the key with the given character down?*/
	public static boolean isKeyDown(char c) {
		if (keysDown.get(c)==null) return false;
		return keysDown.get(c);
	}
	/**is the key with the given code down?*/
	public static boolean isKeyDown(int code) {
		if (keysDownCode==null) {return false;}
		Boolean b=keysDownCode.get(code);
		if (b==null) return false;
		return b;
	}
	
	/**called when a key is pressed*/
	public static void setKeyDown(KeyEvent arg0) {
		keysDown.put(arg0.getKeyChar(), true);
		keysDownCode.put(arg0.getKeyCode(), true);
		StatusPanel.updateStatus(arg0.getKeyChar()+" press");
	}
	
	/**called when a key is released*/
	public static void setKeyUp(KeyEvent arg0) {
		keysDown.put(arg0.getKeyChar(), false);
		keysDownCode.put(arg0.getKeyCode(), false);
		StatusPanel.updateStatus(arg0.getKeyChar()+" release");
	}
	
	
	/**resets the keys that are down*/
	public static void reset() {
		keysDown.clear();
		keysDownCode.clear();
	}
	


	public void keyPressed(KeyEvent arg0) {
		
		setKeyDown(arg0);
	}

	
	public void keyReleased(KeyEvent arg0) {
		setKeyUp(arg0);
		}

	
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void eventDispatched(AWTEvent event) {
		if (event instanceof KeyEvent) {
			
			KeyEvent k=(KeyEvent) event;
			if(k.getID()==KeyEvent.KEY_PRESSED) {
				
				this.keyPressed(k);
			} else  if(k.getID()==KeyEvent.KEY_RELEASED) {
				this.keyReleased(k);
			}
		}
		
	}

}
