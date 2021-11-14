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
package figureFormat;

import java.awt.Window;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.junit.Test;

import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import testing.VisualTest;

/**
 
 * 
 */
public class TemplateChoiceTest extends VisualTest {

	@Test
	public void test() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
			SuggestTemplateDialog sd = new SuggestTemplateDialog();
			Window j=new JFrame();
			JComboBox<TemplateChoice> templateComboBox = sd.getTemplateComboBox();
			j.add(templateComboBox);
			j.pack();
			super.comboBoxVisualTest(j, templateComboBox);
			
		}
	}


