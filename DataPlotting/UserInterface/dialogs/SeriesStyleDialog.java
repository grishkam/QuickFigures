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
 * Version: 2023.1
 */
package dialogs;

import java.util.ArrayList;

import genericPlot.BasicPlot;
import genericPlot.BasicDataSeriesGroup;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import locatedObject.ColorDimmer;
import messages.ShowMessage;
import plotParts.Core.PlotAreaRectangle;
import plotParts.DataShowingParts.SeriesStyle;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorDimmingBox;

/**shows a dialog for editing the style of a data series*/
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
		if (data.size()==0) {
			ShowMessage.showOptionalMessage("need to select", true, "must select data series to use this dialog");
			return;
		}
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
		ChoiceInputPanel cp=new ChoiceInputPanel("Color Dim Type",  new ColorDimmingBox(textItem.getDimming().ordinal()));
		this.add("dim", cp);
		this.getCurrentUsePanel().moveGrid(2, -1);
		this.add("dim?", new BooleanInputPanel("Dim Color?", textItem.isDimColor()));
		this.getCurrentUsePanel().moveGrid(-2, 0);
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
		s.setDimming(ColorDimmer.values()[this.getChoiceIndex("dim")]);
		s.setDimColor(this.getBoolean("dim?"));
	}
}
