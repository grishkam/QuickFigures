package dialogs;

import genericPlot.BasicDataSeriesGroup;
import objectDialogs.LayerPaneDialog;
import standardDialog.NumberInputPanel;

public class SeriesDialog extends LayerPaneDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BasicDataSeriesGroup series;
	
	public SeriesDialog(BasicDataSeriesGroup gr) {
		super(gr);
		this.series=gr;
		NumberInputPanel p = new NumberInputPanel("Position Offset", gr.getDataSeries().getPositionOffset(), 0, 100);
		this.add("pOffset", p);
	
	}
	
	@Override
	public void setItemsToDiaog() {
		super.setItemsToDiaog();
		double offset = this.getNumber("pOffset");
		series.getDataSeries().setPositionOffset(offset);
		series.onAxisUpdate();
	}

}
