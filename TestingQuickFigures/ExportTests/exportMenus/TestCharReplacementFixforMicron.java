/**
 * Author: Greg Mazo
 * Date Modified: Jan 21, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package exportMenus;

import illustratorScripts.IllustratorObjectRef;

/**
 
 * 
 */
public class TestCharReplacementFixforMicron extends  IllustratorObjectRef{

	public static void main(String[] args) {
		String testMe = "5 µm";
		System.out.println("Angstrom=\u212B");
		//System.out.print(performReplacements(testMe));
	}
}
