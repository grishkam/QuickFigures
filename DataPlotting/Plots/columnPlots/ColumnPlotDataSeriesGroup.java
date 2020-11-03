package columnPlots;


import dataSeries.ColumnDataSeries;
import genericPlot.BasicDataSeriesGroup;
import menuUtil.HasUniquePopupMenu;
import plotParts.DataShowingParts.PlotComponent;
import plotParts.DataShowingParts.ScatterPoints;
import plotParts.DataShowingParts.SeriesLabel;
import plotParts.DataShowingParts.SeriesLabelPositionAnchor;

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

	
	void setPosition(int nthSeries) {
		position=nthSeries;
		if (this.getSeriesLabel()!=null) setFor(getSeriesLabel());
		data.setPositionOnPlot(position);
	}
	
	protected SeriesLabelPositionAnchor getSeriesLabelPositionAnchor() {
		return getDataBar();
	}


	public ColumnDataSeries getDataSeries() {return data;}

	public void setUpSeriesLabel(SeriesLabel l) {
		
	}
	
	
	

}
