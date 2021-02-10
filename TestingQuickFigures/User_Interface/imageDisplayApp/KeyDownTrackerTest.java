/**
 * Author: Greg Mazo
 * Date Modified: Dec 18, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package imageDisplayApp;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import org.junit.Test;

import ij.IJ;

/**
 Tests the key down tracker class
 */
public class KeyDownTrackerTest {

	@Test
	public void test() {
		JFrame jFrame = new JFrame("press keys to test ");
		jFrame.setVisible(true);
		Toolkit.getDefaultToolkit().addAWTEventListener(new KeyDownTracker(), AWTEvent.KEY_EVENT_MASK);
		
		char keyChar = 'c';
		int vkC = KeyEvent.VK_C;
		
		testKeyTracking(jFrame, keyChar, vkC);
	}

	/**
	 * @param jFrame
	 * @param keyChar
	 * @param vkC
	 */
	void testKeyTracking(JFrame jFrame, char keyChar, int vkC) {
		KeyEvent k = new KeyEvent(jFrame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, vkC, keyChar);
		jFrame.dispatchEvent(k);
		IJ.wait(100);
		
		assert(KeyDownTracker.isKeyDown(keyChar));

		k = new KeyEvent(jFrame, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, vkC, keyChar);
		jFrame.dispatchEvent(k);
		IJ.wait(100);
		
		assert(!KeyDownTracker.isKeyDown(keyChar));
	}

}
