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
 * Date Modified: Jan 7, 2021
 * Version: 2021.2
 */
package plotTools;

import java.awt.Color;

import graphicalObjects_LayerTypes.GraphicLayer;
import groupedDataPlots.Grouped_Plot;
import icons.IconWrappingToolIcon;
import logging.IssueLog;

/**a tool that is used to reorder data series on a plot*/
public class ColumnSwapTool extends BasicPlotTool {

	{super.iconSet=IconWrappingToolIcon.createIconSet(createIcon(), createIconRollover());}
	
	protected void afterPlotRelease() {
		GraphicLayer a1 = getPressShape().getParentLayer();
		GraphicLayer a2 = getDragShape().getParentLayer();
		
		IssueLog.log(a1+"  "+a2);
		
		if (a1.getParentLayer()!=null&&a2.getParentLayer()!=null&&a1.getParentLayer()==a2.getParentLayer()) {
			a1.getParentLayer().swapmoveObjectPositionsInArray(a1, a2);
			IssueLog.log("will swap groups");
		}
		
		if ( getPressShape()== getDragShape() 
				&& dataSeriesPressed!=null
				&& dataSeriesDragged!=null
				&&!super.dataSeriesPressed.equals(dataSeriesDragged))
			{
			IssueLog.log("will swap groups for complex plot");
			if (getPressShape().getPlotArea()instanceof Grouped_Plot) {
				Grouped_Plot g=(Grouped_Plot) getPressShape().getPlotArea();
				g.swapCategorySpots(dataSeriesPressed.getName(), dataSeriesDragged.getName());
			}
			}
	}
	
	/**creats the normal plot icon, a bar graph*/
	PlotIcon createIcon() {
		return new PlotIcon(new Color[] {Color.red, Color.green, Color.blue}, new int[] {15, 14, 2});
	}
	/**creats the normal plot icon, a bar graph, with columns in different places*/
	PlotIcon createIconRollover() {
		return new PlotIcon(new Color[] {Color.blue, Color.green, Color.red}, new int[] {2, 14, 15});
	}
	
	/***/
	public String getToolName() {
		return "Swap Plot Columns";
	}
}
