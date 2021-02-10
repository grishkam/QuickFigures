/**
 * Author: Greg Mazo
 * Date Modified: Dec 6, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package testing;

import ultilInputOutput.FileChoiceUtil;

/**
 
 * 
 */
public class TestingUtils {
	
	public static void askUser(String st) {
		boolean an = FileChoiceUtil.yesOrNo(st);
		assert(an);
	}

}
