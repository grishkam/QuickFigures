/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package plasticPanels;

import java.awt.Rectangle;

public class PlasticPanel extends Rectangle {

	public PlasticPanel(int i, int j, double standardPanelWidth,
			double standardPanelHeight) {
		super(i,j, (int)standardPanelWidth, (int)standardPanelHeight);
	}
	
	 public PlasticPanel(Rectangle r) {
		super(r);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	


}
