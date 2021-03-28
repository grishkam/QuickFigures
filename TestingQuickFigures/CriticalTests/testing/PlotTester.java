/**
 * Author: Greg Mazo
 * Date Modified: Mar 27, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package testing;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import fileread.PlotExampleShower;
import fileread.PlotType;

/**
 
 * 
 */
public class PlotTester extends TestProvider{
	
	static final TestExample[] types=new TestExample[] {TestExample.COLUMN_PLOTS, TestExample.GROUPED_PLOTS, TestExample.XY_PLOTS, TestExample.KM_PLOTS};
	
	TestExample exampletype=TestExample.COLUMN_PLOTS;
	
	/**
	 * @param argument
	 */
	public PlotTester(TestExample argument) {
		exampletype = argument;
		
	}


	static TestProvider createShapeTestProvider(TestExample argument) {
		 return new PlotTester(argument);
	}

	
	public DisplayedImage createExample() {
		if ( exampletype==TestExample.COLUMN_PLOTS) {
			return new PlotExampleShower(PlotType.DEFAULT_PLOT_TYPE_COLS , false).showAllPlots();
		}
		if ( exampletype==TestExample.GROUPED_PLOTS) {
			return new PlotExampleShower(PlotType.GROUP_PLOT_TYPE , false).showAllPlots();
		}
		if ( exampletype==TestExample.XY_PLOTS) {
			return new PlotExampleShower(PlotType.XY_PLOT_TYPE , false).showAllPlots();
		}
		if ( exampletype==TestExample.KM_PLOTS) {
			return new PlotExampleShower(PlotType.KAPLAN_MEIER_PLOT_TYPE , false).showAllPlots();
		}
		
		
		return null;
	}
	
	public static ArrayList<TestProvider> getTests() {
		ArrayList<TestProvider> out=new ArrayList<TestProvider>();
				
		for(TestExample ex: types) {
			out.add(new PlotTester(ex));
		}
		return out;
		
	}
	
	public TestExample getType() {
		return exampletype ;
	}
}
