package dialogs;

import java.util.ArrayList;

import genericPlot.BasicPlot;
import genericPlot.BasicDataSeriesGroup;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import plotParts.Core.PlotAreaRectangle;
import plotParts.DataShowingParts.SeriesStyle;
import standardDialog.BooleanInputPanel;
import standardDialog.ColorDimmingBox;
import standardDialog.ComboBoxPanel;
import standardDialog.StandardDialog;

public class SeriesStyleDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<? extends BasicDataSeriesGroup> dataSeries;
	private ArrayList<SeriesStyle> styles;
	
	public SeriesStyleDialog(ArrayList<SeriesStyle> styles, ArrayList<? extends BasicDataSeriesGroup> data)
	{
		this.styles=styles;
		this.dataSeries=data;
		
		addDimmingToDialog(data.get(0).getStyle());
	}
	
	static SeriesStyleDialog createForPlotsInList(ArrayList<ZoomableGraphic> objects) {
		
		ArrayList<SeriesStyle> styles1 = new ArrayList<SeriesStyle>();
		ArrayList<BasicDataSeriesGroup> dataSeries1 = new ArrayList< BasicDataSeriesGroup> ();
		
		for(ZoomableGraphic z: objects) {
			if (z instanceof BasicDataSeriesGroup) {
				dataSeries1.add(( BasicDataSeriesGroup)z);
			}
			if (z.getParentLayer() instanceof BasicDataSeriesGroup) {
				dataSeries1.add(( BasicDataSeriesGroup)z.getParentLayer());
			}
			if (z.getParentLayer().getParentLayer() instanceof BasicPlot) {
				styles1.addAll(((BasicPlot) z.getParentLayer().getParentLayer()).getAvailableStyles());
			}
			
			if (z instanceof PlotAreaRectangle) {
				GraphicLayer  r=((PlotAreaRectangle) z).getParentLayer();
				if (r instanceof BasicPlot) {
					styles1.addAll(((BasicPlot) r).getAvailableStyles());
					for(BasicDataSeriesGroup data: ((BasicPlot) r).getAllDataSeries()) {
						dataSeries1.add(data);
					}
				}
			}
		}
		
		return new SeriesStyleDialog(styles1, dataSeries1);
	}
	
	protected void addDimmingToDialog(SeriesStyle textItem) {
		ComboBoxPanel cp=new ComboBoxPanel("Color Dims ",  new ColorDimmingBox(textItem.getDimming()));
		this.add("dim", cp);
		this.getMainPanel().moveGrid(2, -1);
		this.add("dim?", new BooleanInputPanel("Dim Color?", textItem.isDimColor()));
		this.getMainPanel().moveGrid(-2, 0);
	}
	
	
	protected void afterEachItemChange() {
		for(SeriesStyle s: styles) {
			changeStyleToDialog(s);
		}
		for(BasicDataSeriesGroup g: dataSeries) {
			changeStyleToDialog(g.getStyle());
			g.getStyle().applyTo(g);
			g.updateDisplay();
		}
	}

	private void changeStyleToDialog(SeriesStyle s) {
		s.setDimming(this.getChoiceIndex("dim"));
		s.setDimColor(this.getBoolean("dim?"));
	}
}
