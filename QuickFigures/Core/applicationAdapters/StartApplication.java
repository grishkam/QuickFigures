/**
 * Author: Greg Mazo
 * Date Modified: Dec 27, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package applicationAdapters;

import javax.swing.JFrame;

import includedToolbars.ActionToolset1;
import includedToolbars.LayoutToolSet;
import includedToolbars.ObjectToolset1;

/**
 
 * 
 */
public class StartApplication {

	public static void startToolbars(boolean appclose) {
		
		ObjectToolset1 toolset = showInnitial();
		if (appclose) {
			toolset.getframe().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
		}
	
	public static ObjectToolset1 showInnitial() {
		return  showToolSet();
	}
	
	/**shows both object and layout toolsets*/
	public static ObjectToolset1 showToolSet() {
		ObjectToolset1 ot = new ObjectToolset1();
		new LayoutToolSet().run("");
		new ActionToolset1().run("go");
		ot.run("hi");
		return ot;
	}
}
