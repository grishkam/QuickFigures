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
 * Date Modified: Jan 4, 2021
 * Version: 2021.2
 */
package graphicalObjects_LayoutObjects;

import java.util.ArrayList;

import layout.PanelContentExtract;
import layout.plasticPanels.BasicSpacedPanelLayout;

/**A layout graphic for a spaced panel layout*/
public class SpacedPanelLayoutGraphic extends PanelLayoutGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BasicSpacedPanelLayout getPanelLayout() {
		if (this.layout instanceof BasicSpacedPanelLayout) return (BasicSpacedPanelLayout) this.layout;
	return null;	
	}
	
	public void repack() {
		this.generateCurrentImageWrapper();
		ArrayList<PanelContentExtract> stack = this.getEditor().cutStack(getPanelLayout());
		getPanelLayout().autoLocatePanels();
		getEditor().pasteStack(getPanelLayout(), stack);
		this.snapLockedItems();
		
	}

}
