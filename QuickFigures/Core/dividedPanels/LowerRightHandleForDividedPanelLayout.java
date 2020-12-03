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
package dividedPanels;

import java.awt.Color;
import java.awt.Point;

import dividedPanels.DividedPanelLayout.layoutDividedArea;
import graphicalObjectHandles.SmartHandle;

public class LowerRightHandleForDividedPanelLayout extends SmartHandle {
	private layoutDividedArea area;
	private DividedPanelLayout layout;
	private int hnum;

	public LowerRightHandleForDividedPanelLayout(DividedPanelLayout dpl, layoutDividedArea area, int num) {
		
		super(0, 0);
		hnum = num;
		super.setHandleNumber(num+40000);
		this.area = area;
		this.setHandleColor(Color.blue);
		this.setCordinateLocation(new Point((int)area.getMaxX()-12,  (int)area.getMaxY()-12));
		this.layout=dpl;
	}
	
	@Override
	public void nudgeHandle(double dx, double dy) {

		layout.nudgePanelDimensions(hnum, dx, dy);

	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
