package menuItemInstallation;

import javax.swing.Icon;

import applicationAdapters.DisplayedImageWrapper;
import basicMenusForApp.MenuItemForObj;
import columnPlots.ColumnPlot;
import dataSeries.ColumnDataSeries;
import gridLayout.BasicMontageLayout;
import imageDisplayApp.ImageAndDisplaySet;

public class NewPlot implements MenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
		
		ImageAndDisplaySet set= ImageAndDisplaySet.createAndShowNew("Figure", 500,400);
		ColumnPlot plot=new ColumnPlot("Test data ", 
				new ColumnDataSeries("Control",
						new double[] {30, 24, 20,  24,180, 177, 167,  198, 345, 200, 198, 199, 201, 195, 201, 204, 423, 80, 19} 
				),
				new ColumnDataSeries("Treatment 1",
						new double[] {10, 100, 43, 332, 129}),
				new ColumnDataSeries("Treatment 2",
						new double[] {88, 15, 183, 90, 96, 140})
		
		
				);
		BasicMontageLayout layout = new BasicMontageLayout(6,6, 25, 25, 2,2, true);
		set.getImageAsWrapper().getGraphicLayerSet().add(plot);
	//	GenericTileGame testGeneric = new GenericTileGame(layout);
		//testGeneric.getTileTypes()[5]=1;testGeneric.getTileTypes()[6]=1;testGeneric.getTileTypes()[12]=1;
	diw.updateDisplay();	diw.updateDisplay();
		//set.getImageAsWrapper().addRoiToImage(testGeneric);
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

}
