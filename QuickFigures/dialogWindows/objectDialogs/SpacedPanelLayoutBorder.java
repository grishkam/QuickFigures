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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package objectDialogs;

import java.util.ArrayList;

import graphicalObjects_LayoutObjects.SpacedPanelLayoutGraphic;
import layout.PanelContentExtract;
import layout.plasticPanels.BasicSpacedPanelLayout;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.numbers.NumberInputPanel;

public class SpacedPanelLayoutBorder extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SpacedPanelLayoutGraphic layoutg;
	private BasicSpacedPanelLayout layoutl;

	
	public  SpacedPanelLayoutBorder(
			SpacedPanelLayoutGraphic item) {
		layoutg=item;
		layoutl=item.getPanelLayout();
		addOptionsToDialog() ;
	}
	
	
	protected void addOptionsToDialog() {
		add("bh", new NumberInputPanel("Spacing Horizontal ", layoutl.getHorizontalBorder(), 0,100));
		add("bv", new NumberInputPanel("Spacing Vertical ", layoutl.getVerticalBorder(), 0,100));
		add("bottomA", new BooleanInputPanel("Allign Bottoms", layoutl.getneighborFinder().isPerformBottomEdgeAllign()));
		add("rightA", new BooleanInputPanel("Allign Right edges", layoutl.getneighborFinder().isPerformRightEdgeAllign()));
	}
	protected void setItemsToDiaog() {
		layoutg.generateCurrentImageWrapper();
		ArrayList<PanelContentExtract> stack = layoutg.getEditor().cutStack(layoutl);
		 layoutl.setHorizontalBorder(this.getNumberInt("bh"));
		 layoutl.setVerticalBorder(this.getNumberInt("bv"));
		 layoutl.getneighborFinder().setPerformBottomEdgeAllign(this.getBoolean("bottomA"));
		 layoutl.getneighborFinder().setPerformRightEdgeAllign(this.getBoolean("rightA"));
		 layoutg.repack();
		 layoutg.repack();
		 layoutg.repack();
		 layoutg.getEditor().pasteStack(layoutl, stack);
		// layoutg.repack();
	}
}
