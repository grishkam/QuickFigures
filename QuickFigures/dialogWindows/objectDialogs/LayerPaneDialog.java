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
 * Version: 2023.2
 */
package objectDialogs;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicGroup.GroupedLayerPane;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.strings.StringInputPanel;

/**A dialog for renaming a layer. Also includes option related to groups*/
public class LayerPaneDialog extends GraphicItemOptionsDialog  {

	boolean showKey=false;
	
	GraphicLayerPane layer;
	public LayerPaneDialog(GraphicLayerPane layer) {
		
		this.setWindowCentered(true);
		this.layer=layer;
		addOptionsToDialog() ;
		
		this.showDialog();
		
		
	}
	@Override
	public void addOptionsToDialog() {
		this.add("name", new StringInputPanel("Layer Name ", layer.getName(), 30));
		if (showKey) this.add("key", new StringInputPanel("Layer Key ", ""+layer.getKey(), 30));
		this.add("desc", new StringInputPanel("Description  ", ""+layer.getDescription(), 30));
		if(layer instanceof GraphicGroup.GroupedLayerPane) {
			add("selGroup", new BooleanInputPanel("Select in Group", GraphicGroup.treatGroupsLikeLayers));
			add("unGroup", new BooleanInputPanel("Ungroup", false));
			
		}
	}
	
	@Override
	public void setItemsToDiaog() {
		layer.setName(this.getString("name"));
		if (showKey)layer.setKey(this.getString("key"));
		layer.setDescription(this.getString("desc"));
		if(layer instanceof GraphicGroup.GroupedLayerPane) {
			GraphicGroup.treatGroupsLikeLayers=this.getBoolean("selGroup");
			if (getBoolean("unGroup")) {
				GraphicGroup.GroupedLayerPane g=(GroupedLayerPane) layer;
				g.getTheGroup().ungroup();
				this.setVisible(false);
			}
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
