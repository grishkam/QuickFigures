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
 * Date Modified: Mar 28, 2021
 * Version: 2022.2
 */
package standardDialog;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import standardDialog.channels.ChannelEntryBox;
import testing.FigureTester;
import testing.VisualTest;

/**this contains a manual test to confirm the appearance of the channel entry box */
class ChannelEntryBoxTest extends VisualTest {

	@Test
	void test() {
		
			JFrame jf = new JFrame();
			;
			ImagePlusWrapper wrap = new ImagePlusWrapper(FigureTester.openExample1(1));
			
			JComboBox<?> box = new ChannelEntryBox(2,wrap.getChannelEntriesInOrder());
			jf.add(box);
			jf.pack();
			jf.setVisible(true);
			jf.setLocation(350, 350);
	
			
			super.comboBoxVisualTest(jf, box);
			
			jf.setVisible(false);
			
			
		
			
	}

	

}
