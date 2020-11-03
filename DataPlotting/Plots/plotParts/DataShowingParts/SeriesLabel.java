package plotParts.DataShowingParts;

import dataSeries.DataSeries;
import utilityClassesForObjects.SnappingPosition;

public class SeriesLabel extends PlotLabel  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DataSeries theData;

	public SeriesLabel(String name, boolean l) {
		super(name);
		this.setLegend(l);
		if (l) return;
		
		this.setSnappingBehaviour(SnappingPosition.defaultPlotBottomSide());
		this.setAngle(Math.PI/4);
	}

	public void requestSnap() {
		this.snapNeeded=true;
		
	}


	
	
	


	public void setTheData(DataSeries data) {
		theData=data;
		
	}



}
