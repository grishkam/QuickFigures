/**
 * Author: Greg Mazo
 * Date Modified: Feb 20, 2021
 * Version: 2021.2
 */
package testing;

import ultilInputOutput.FileChoiceUtil;

/**
 
 * 
 */
public class TestingUtils {
	
	/**asks the user if a manual test worked*/
	public static void askUser(String st) {
		boolean an = FileChoiceUtil.yesOrNo(st);
		assert(an);
	}

}
