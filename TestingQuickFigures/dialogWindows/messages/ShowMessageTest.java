/**
 * Author: Greg Mazo
 * Date Modified: Dec 15, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package messages;

import org.junit.jupiter.api.Test;

import logging.IssueLog;

/**
 
 * 
 */
class ShowMessageTest {

	@Test
	void test() {
		ShowMessage.showOptionalMessage("Test 1", true, "this should be the first time you see message test 1. just click ok to contine test");
		
		long time1 = System.currentTimeMillis();
		ShowMessage.showOptionalMessage("Test 1", true, "If you see this again despite clicking , the test fails");
		long time2 = System.currentTimeMillis();
		assert(time2-time1<2);//a user cannot click a dialog in such a short interval but if not dialog is show then such an interval will pass
		
		time1 = System.currentTimeMillis();
		ShowMessage.showOptionalMessage("Test 2", true, "You made it to test 2. this time uncheck the box before clicking ok");
		time2 = System.currentTimeMillis();
		assert(time2-time1>2);
		
		time1 = System.currentTimeMillis();
		ShowMessage.showOptionalMessage("Test 2", false, "this is the second part of test 2. it should start unchecked");
		time2 = System.currentTimeMillis();
		assert(time2-time1>2);
		
		ShowMessage.showNonModel("This is a nonmodel window", "message here", "and here");
		
		IssueLog.waitSeconds(5);
		
		
		
	}

}
