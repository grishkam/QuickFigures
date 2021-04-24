/**
 * Author: Greg Mazo
 * Date Modified: Apr 6, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
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
