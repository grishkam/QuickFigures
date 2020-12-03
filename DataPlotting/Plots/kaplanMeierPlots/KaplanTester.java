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
package kaplanMeierPlots;

import applicationAdapters.ToolbarTester;
import dataSeries.KaplenMeierDataSeries;
import imageDisplayApp.ImageWindowAndDisplaySet;

public class KaplanTester {
	
	public static void main(String[] a) {
		ImageWindowAndDisplaySet set = ToolbarTester.showExample(true);
		KaplenMeierDataSeries data = KaplenMeierDataSeries.createExampleData();
		KaplenMeierDataSeries data2 = KaplenMeierDataSeries.createExampleData2();
		KM_Plot plot = new KM_Plot("Test plot", data, data2);
		ImageWindowAndDisplaySet diw = ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
		diw.getImageAsWrapper().getGraphicLayerSet().add(plot);
	}

}
