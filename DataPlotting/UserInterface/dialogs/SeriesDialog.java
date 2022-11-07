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
 * Version: 2022.2
 */
package dialogs;

import genericPlot.BasicDataSeriesGroup;
import objectDialogs.LayerPaneDialog;
import standardDialog.numbers.NumberInputPanel;

/**shows a dialog for editing properties of data series*/
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
