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
 * Date Modified: Mar 28, 2021
 * Version: 2022.0
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
