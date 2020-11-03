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
