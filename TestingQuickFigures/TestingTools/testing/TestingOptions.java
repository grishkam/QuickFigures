/**
 * Author: Greg Mazo
 * Date Modified: Feb 20, 2021
 * Version: 2021.2
 */
package testing;

import logging.IssueLog;

/**
contains static fields used during testing
 */
public class TestingOptions {
	public static boolean performManualTests=true;
	public static boolean performSlowTestsForExceptions=false;
	
	//how long to wait after each Junit test so the user can determine visually if everything works
	public static int waitTimeAfterTests=10;
	public static int waitTimeAfterVisualTests=20;
	
	public static void waitTimeAfterVisualTests() {
		IssueLog.waitSeconds(waitTimeAfterVisualTests);
	}
}
