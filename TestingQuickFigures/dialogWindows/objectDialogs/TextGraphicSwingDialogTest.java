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
package objectDialogs;

import java.awt.Font;
import java.awt.GridBagConstraints;

import org.junit.Test;

import graphicalObjects_SpecialObjects.TextGraphic;
import logging.IssueLog;
import standardDialog.graphics.GraphicSampleComponent;

/**
 
 * 
 */
public class TextGraphicSwingDialogTest {

	@Test
	public void test() {
		
			showDialog();
			showDialog();
			
			/**provides user enough time to tinker with dialog*/
			IssueLog.waitSeconds(10);
		
		
	}

	/**
	 * 
	 */
	protected void showDialog() {
		IssueLog.logTimeStart("will create dialog");
		TextGraphic t = new TextGraphic();
		t.setFont(new Font("Times New Roman", Font.BOLD, 30));
		TextGraphicSwingDialog dia = new TextGraphicSwingDialog(t);
		dia.previewComponent = new GraphicSampleComponent(t);
		
		dia.add(dia.previewComponent, new GridBagConstraints());
		
		IssueLog.logTimeStart("will show dialog");
		dia.showDialog();
		
		
		IssueLog.logTimeStart("dialog is seen");
	}

}
