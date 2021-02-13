/**
 * Author: Greg Mazo
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package standardDialog;

import org.junit.Test;

import logging.IssueLog;
import standardDialog.booleans.BooleanInputPanel;

/**
 
 */
public class StandardDialogListenerTest {

	@Test
	public void test() {
		StandardDialog sd = new StandardDialog("Test dialog");
		
		BooleanInputPanel b = new BooleanInputPanel("Check true or false", false) ;
		sd.add("Boolean", b);
		sd.addDialogListener(new StandardDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				IssueLog.log("Listener notified of event "+event.getStringKey());
				
			}});
		sd.setModal(true);
		sd.showDialog();
	}

}
