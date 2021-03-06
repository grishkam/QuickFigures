/**
 * Author: Greg Mazo
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package standardDialog;

import static org.junit.Assert.fail;

import org.junit.Test;

import genericTools.ToolTester;
import logging.IssueLog;
import standardDialog.booleans.BooleanInputPanel;

/**
 tests to see if the dialog listeners are working.
 Wrote this test while looking for a bug that prevented dialog listeners
 from being notified of events.
 */
public class StandardDialogListenerTest {

	boolean eventHeard=false;
	@Test
	public void test() {
		StandardDialog sd = new StandardDialog("Test dialog");
		sd.setWindowCentered(true);
		BooleanInputPanel b = new BooleanInputPanel("Check true or false to complete test", false) ;
		sd.add("Boolean", b);
		
		
		sd.addDialogListener(new StandardDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				IssueLog.log("Listener notified of event "+event.getStringKey());
				eventHeard=true;
				sd.setVisible(false);
			}});
		
		b.getCheckBox().setSelected(true);
		
		sd.showDialog();
		ToolTester.clickComponent(b.getCheckBox(),0);
		
		if(!eventHeard) 
			fail("event not heard");
		
	
		
	}

}
