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
package plotParts.DataShowingParts;

import java.util.ArrayList;

import dataSeries.DataSeries;

public class PlotUtil {
	
	public static double findMaxNeededPositionFrom(ArrayList<DataShowingShape> shapes) {
		double output = 0;
		for(DataShowingShape s: shapes) {
			if (s==null) continue;
			double m = s.getMaxNeededPosition();
			if (m>output) output=m;
		}
		return output;
	}
	
	public static double findMaxNeededValueIn(ArrayList<DataShowingShape> shapes) {
		double output = 0;
		for(DataShowingShape s: shapes) {
			if (s==null) continue;
			double m = s.getMaxNeededValue();
			if (m>output) output=m;
		}
		return output;
	}
	
	public static DataSeries[] getAllSeriesFor(DataSeries data) {
		double[] p = data.getAllPositions();
		 DataSeries[] output=new  DataSeries[p.length];
		 for(int i=0; i<output.length; i++) output[i]=data.getValuesForPosition(p[i]);
		 return output;
	}

}
