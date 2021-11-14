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
		JFrame jFrame = new JFrame("press keys to test manually");
		jFrame.setVisible(true);
		Toolkit.getDefaultToolkit().addAWTEventListener(new KeyDownTracker(), AWTEvent.KEY_EVENT_MASK);
		
		char keyChar = 'c';
		int vkC = KeyEvent.VK_C;
		
		testKeyTracking(jFrame, keyChar, vkC);
		jFrame.setVisible(false);
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
