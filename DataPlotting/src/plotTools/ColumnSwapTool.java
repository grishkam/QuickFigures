package plotTools;

import java.awt.Color;

import externalToolBar.IconWrappingToolIcon;
import graphicalObjects_LayerTypes.GraphicLayer;
import groupedDataPlots.Grouped_Plot;
import logging.IssueLog;

public class ColumnSwapTool extends BasicPlotTool {

	{super.set=IconWrappingToolIcon.createIconSet(createIcon(), createIcon2());}
	
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
	
	PlotIcon createIcon() {
		return new PlotIcon(new Color[] {Color.red, Color.green, Color.blue}, new int[] {15, 14, 2});
	}
	PlotIcon createIcon2() {
		return new PlotIcon(new Color[] {Color.blue, Color.green, Color.red}, new int[] {2, 14, 15});
	}
	
	public String getToolName() {
		return "Swap Plot Columns";
	}
}
