/**
 * Author: Greg Mazo
 * Date Modified: Feb 2, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package dataSeries;

import java.util.ArrayList;

import xyPlots.XYPlotDataSeriesGroup;

/**
 
 * 
 */
public class DataReplacer<TheTypeOfData extends DataSeries> {
	public void replaceAllData(MultipleDataHolder<TheTypeOfData> holder,  ArrayList<TheTypeOfData> newAddedData) {
		ArrayList<? extends DataHolder<TheTypeOfData>> olderSeries = holder.getAllDataSeries();
		
		ArrayList<TheTypeOfData> alreadyPlaced=new ArrayList<TheTypeOfData>();
		ArrayList<DataHolder<TheTypeOfData>> alreadyUsed=new ArrayList<DataHolder<TheTypeOfData>>();
		
		/**places the data series with an exact name match first*/
		for(TheTypeOfData data: newAddedData) {
			DataHolder<TheTypeOfData> location=findNameMatch(data, olderSeries, alreadyUsed );
			if(location!=null) {
				alreadyPlaced.add(data);
				alreadyUsed.add(location);
				location.replaceData(data);
			}
		}
		
		
		ArrayList<TheTypeOfData> noYetPlaced=new ArrayList<TheTypeOfData>();
		noYetPlaced.addAll(newAddedData);noYetPlaced.removeAll(alreadyPlaced);
		ArrayList<DataHolder<TheTypeOfData>> notYetUsed=new ArrayList<DataHolder<TheTypeOfData>>();
		notYetUsed.addAll(olderSeries); notYetUsed.removeAll(alreadyUsed);
		
		for(int i=0; i<noYetPlaced.size()||i<notYetUsed.size(); i++) {
			
			TheTypeOfData  novel = null;
			if (i<noYetPlaced.size()) novel=noYetPlaced.get(i);
			
			/**if Replacement need be done*/
			DataHolder<TheTypeOfData> oldDataSeriesGroup = null;
			if (i<notYetUsed.size()&&i<noYetPlaced.size()) {
				oldDataSeriesGroup = notYetUsed.get(i);
				oldDataSeriesGroup.replaceData(novel);
				oldDataSeriesGroup.getSeriesLabel().getParagraph().get(0).get(0).setText(novel.getName());
			}
			
			if (i<noYetPlaced.size()&&!(i<notYetUsed.size())) {
				holder.addDataSeries(novel);
			}
			
			if (!(i<noYetPlaced.size())&&(i<notYetUsed.size())) {
				oldDataSeriesGroup = notYetUsed.get(i);
				holder.removeDataSeries(oldDataSeriesGroup.getDataSeries());
			}
			
			
			
		}
	}

	/**returns a data series that has the same name as the given data
	 * @param data
	 * @param olderSeries
	 * @param alreadyUsed
	 * @return
	 */
	private DataHolder<TheTypeOfData> findNameMatch(TheTypeOfData data,
			ArrayList<? extends DataHolder<TheTypeOfData>> olderSeries,
			ArrayList<? extends DataHolder<TheTypeOfData>> alreadyUsed) {
		for(DataHolder<TheTypeOfData> current: olderSeries) {
			if(alreadyUsed.contains(current))
				continue;
			boolean nameMatch = current.getDataSeries().getName().equals(data.getName());
			if(nameMatch)
				return current;
		}
		return null;
	}
}
