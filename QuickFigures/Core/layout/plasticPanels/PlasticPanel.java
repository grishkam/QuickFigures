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
 * Date Modified: Jan 5, 2021
 * Version: 2023.2
 */

package layout.plasticPanels;

import java.awt.geom.Rectangle2D;

/**A specialised rectangle*/
public class PlasticPanel extends Rectangle2D.Double {

	public PlasticPanel(double i, double j, double standardPanelWidth,
			double standardPanelHeight) {
		super(i,j, standardPanelWidth, standardPanelHeight);
	}
	
	 public PlasticPanel(Rectangle2D r) {
		super(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	


}
