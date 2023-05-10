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
 * Version: 2023.2
 */
package columnPlots;


import dataSeries.ColumnDataSeries;
import genericPlot.BasicDataSeriesGroup;
import menuUtil.HasUniquePopupMenu;
import plotParts.DataShowingParts.PlotComponent;
import plotParts.DataShowingParts.ScatterPoints;
import plotParts.DataShowingParts.SeriesLabel;
import plotParts.DataShowingParts.SeriesLabelPositionAnchor;

/**A specialized layer for display of a specific data series*/
public class ColumnPlotDataSeriesGroup extends BasicDataSeriesGroup implements HasUniquePopupMenu, PlotComponent{

	private ColumnDataSeries data;
	
	private ColumnPlotDataSeriesGroup(String name) {
		super(name);
	}
	
	public ColumnPlotDataSeriesGroup(ColumnDataSeries data2, BasicDataSeriesGroup template) {
		super(data2.getName());
		this.data=data2;
		addPartsBasedOn(template);
	}


	public ColumnPlotDataSeriesGroup(ColumnDataSeries data) {
		super(data.getName());
		this.data=data;
		addStandardParts();
	}
	
	/**creates a set of scatter points for the data series*/
	protected ScatterPoints createScatter() {
		return new ScatterPoints(getDataSeries(), true);
	}

	/**@MenuItemMethod(menuActionCommand = "Edit data", menuText = "Input New Data", subMenuName="Data", orderRank=100)
	public void showDataEditDialog() {
		String st = this.data.getColumnString();
		Basic1DDataSeries d =DataInputDialog.getUserData(st);
		
		if (d!=null) {data.setData(d.getRawValues()); data.setName(d.getName());
				this.onAxisUpdate();
				this.onPlotUpdate();
		}
		
	}*/
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**sets the location of this data series in the column plot*/
	void setPosition(int nthSeries) {
		position=nthSeries;
		if (this.getSeriesLabel()!=null) setFor(getSeriesLabel());
		data.setPositionOnPlot(position);
	}
	
	/**returns the data bar as an anchor for the label*/
	protected SeriesLabelPositionAnchor getSeriesLabelPositionAnchor() {
		return getDataBar();
	}

	/**returns the stored data series*/
	public ColumnDataSeries getDataSeries() {return data;}

	/**not implemented*/
	public void setUpSeriesLabel(SeriesLabel l) {
		
	}
	
	
	

}
