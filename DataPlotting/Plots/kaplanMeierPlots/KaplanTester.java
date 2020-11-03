package kaplanMeierPlots;

import applicationAdapters.ToolbarTester;
import dataSeries.KaplenMeierDataSeries;
import imageDisplayApp.ImageAndDisplaySet;

public class KaplanTester {
	
	public static void main(String[] a) {
		ImageAndDisplaySet set = ToolbarTester.showExample(true);
		KaplenMeierDataSeries data = KaplenMeierDataSeries.createExampleData();
		KaplenMeierDataSeries data2 = KaplenMeierDataSeries.createExampleData2();
		KM_Plot plot = new KM_Plot("Test plot", data, data2);
		ImageAndDisplaySet diw = ImageAndDisplaySet.createAndShowNew("Figure", 300,300);
		diw.getImageAsWrapper().getGraphicLayerSet().add(plot);
	}

}
