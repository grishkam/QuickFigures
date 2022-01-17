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
 * Date Modified: Jan 6, 2021
 * Version: 2022.0
 */
package standardDialog;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

/**A component in whcih the parts of a standard dialog are placed inside
 **/
public class GriddedPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int gx=1;
	private int gy=1;
	private int gxmax=1;
	private int gymax=1;
	boolean moveDown=true;
	
	public GriddedPanel() {
		super();
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);
	}
	
	/**places the particular compnent within this panel*/
	 void place(OnGridLayout st) {
	
		st.placeItems(this, gx, gy);
		if (gxmax<st.gridWidth())gxmax=st.gridWidth();
		
		if (moveDown)gy+=st.gridHeight();
				else gx+=st.gridWidth();
		
		
	}
	
	public void moveGrid(int x, int y) {
		gy+=y;
		gx+=x;
		if (gx>gxmax) gxmax=gx;
		if (gy>gymax) gymax=gy;
	}

}
