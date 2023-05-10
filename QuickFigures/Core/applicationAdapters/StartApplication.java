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
 * Date Modified: Jan 4, 2021
 * Version: 2023.2
 */
package applicationAdapters;

import javax.swing.JFrame;

import includedToolbars.AlignAndArrangeActionTools;
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
		new AlignAndArrangeActionTools().run("go");
		ot.run("hi");
		return ot;
	}
}
