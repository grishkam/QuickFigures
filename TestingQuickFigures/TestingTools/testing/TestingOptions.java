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
 * Date Modified: Feb 20, 2021
 * Version: 2022.0
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
