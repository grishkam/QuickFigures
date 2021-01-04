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
package menuItemInstallation;

import javax.swing.Icon;

import columnPlots.ColumnPlot;
import applicationAdapters.DisplayedImage;
import basicMenusForApp.MenuItemForObj;
import dataSeries.ColumnDataSeries;
import imageDisplayApp.ImageWindowAndDisplaySet;

public class NewPlot implements MenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		
		ImageWindowAndDisplaySet set= ImageWindowAndDisplaySet.createAndShowNew("Figure", 500,400);
		ColumnPlot plot=new ColumnPlot("Test data ", 
				new ColumnDataSeries("Control",
						new double[] {30, 24, 20,  24,180, 177, 167,  198, 345, 200, 198, 199, 201, 195, 201, 204, 423, 80, 19} 
				),
				new ColumnDataSeries("Treatment 1",
						new double[] {10, 100, 43, 332, 129}),
				new ColumnDataSeries("Treatment 2",
						new double[] {88, 15, 183, 90, 96, 140})
		
		
				);
		
		set.getImageAsWrapper().getTopLevelLayer().add(plot);
		diw.updateDisplay();	diw.updateDisplay();
	}

	@Override
	public String getCommand() {
		return "Basic Bar Graph 7856";
	}

	@Override
	public String getNameText() {
		return "Show Example Plot";
	}

	@Override
	public String getMenuPath() {
		
		return "Plots";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public Icon getSuperMenuIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}
