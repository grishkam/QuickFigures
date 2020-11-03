package groupedDataPlots;


import dataSeries.GroupedDataSeries;
import dataTableDialogs.SeriesInoutForGroupPlots;
import fLexibleUIKit.MenuItemMethod;
import genericPlot.BasicDataSeriesGroup;
import menuUtil.HasUniquePopupMenu;
import plotParts.DataShowingParts.PlotComponent;
import plotParts.DataShowingParts.ScatterPoints;
import plotParts.DataShowingParts.SeriesLabel;
import plotParts.DataShowingParts.SeriesLabelPositionAnchor;

public class GroupedPlotDataSeriesGroup extends BasicDataSeriesGroup implements HasUniquePopupMenu, PlotComponent{

	private GroupedDataSeries data;

	private GroupedPlotDataSeriesGroup(String name) {
		super(name);
	}
	
	public GroupedPlotDataSeriesGroup(GroupedDataSeries data2, BasicDataSeriesGroup template) {
		super(data2.getName());
		this.settheData(data2);
		addPartsBasedOn(template);
	}

	public GroupedPlotDataSeriesGroup(GroupedDataSeries data) {
		super(data.getName());
		this.settheData(data);
		addStandardParts();
	}
	
	protected void addStandardParts() {
		this.addDataBar();
		this.addErrorBar();
	}

	
	protected ScatterPoints createScatter() {
		return new ScatterPoints(getDataSeries(), true);
	}

	@MenuItemMethod(menuActionCommand = "Edit data", menuText = "Input New Data", subMenuName="Data", orderRank=100)
	public void showDataEditDialog() {
		GroupedDataSeries newdata = SeriesInoutForGroupPlots.getUserInputSeries(getDataSeries(), false);
		if (newdata==null) return;
		
		this.getTheData().replaceData(newdata);
		this.plotArea.autoCalculateAxisRanges();
		this.plotArea.onAxisUpdate();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	void setPositionOffset(int nthSeries) {
		position=nthSeries;
		if (this.getSeriesLabel()!=null) setFor(getSeriesLabel());
		getTheData().setPositionOffset(position);
	}
	
	public GroupedDataSeries getDataSeries() {return getTheData();}

/**
	@MenuItemMethod(menuActionCommand = "New Line", menuText = "New Line", subMenuName="Add", orderRank=0)
	public void addLine() {
		if (getLine()!=null) this.remove(getLine());
		setLine(new MeanLineShape(getDataSeries()));
		if (this.getStyle()!=null)this.getStyle().applyTo(getLine());
		setFor(getLine());
		this.add(getLine());
		getLine().demandShapeUpdate();
		getLine().setStrokeColor(groupColor.darker().darker());
		getLine().setFillColor(new Color(255,255,255, 0));
	}
	
	@MenuItemMethod(menuActionCommand = "Remove Line", menuText = "Line", subMenuName="Remove", orderRank=0)
	public void removeLine() {
		if (getLine()!=null) this.remove(getLine());
		setLine(null);
	}

	
	public MeanLineShape getLine() {
		return line;
	}

	public void setLine(MeanLineShape line) {
		this.line = line;
	}*/
	

	
	protected SeriesLabelPositionAnchor getSeriesLabelPositionAnchor() {
		return getLegandShape();
	}
	
	public GroupedDataSeries getTheData() {
		return data;
	}

	public void settheData(GroupedDataSeries data) {
		this.data = data;
	}
	
	protected void addPartsBasedOn(BasicDataSeriesGroup template) {
		if (template==null)	{addStandardParts(); return;}//without template, no more is needed
		
		super.addShapePartsBasedOn(template);
		
		if (template.getLegandShape()!=null&&template.getSeriesLabel()!=null ){
			this.addLegandPart(template.getLegandShape(), template.getSeriesLabel());
		}
		
	}
	
	
	protected void createLabel() {
		this.seriesLabel=new SeriesLabel(this.getDataSeries().getName(), true);
	}
	
}
