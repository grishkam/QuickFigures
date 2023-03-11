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
 * Date Created: Mar 7, 2023
 * Date Modified: Mar 7, 2023
 * Version: 2023.1
 */
package addObjectMenus;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.LinkerGraphic;
import logging.IssueLog;
import messages.ShowMessage;
import selectedItemMenus.BasicMultiSelectionOperator;

/**
 
 * 
 */
public class AddLink extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;




	@Override
	public void run() {
		LinkerGraphic l = new LinkerGraphic();
		ArrayList<ZoomableGraphic> items = super.getSelector().getSelecteditems();
		if(items.size()!=2) {
			ShowMessage.showOptionalMessage("not correct number of itmes", true, "this method only works when two path shapes are selected");
			return;
		}
		GraphicLayer layerl=null;
		for(ZoomableGraphic i: items) {
			l.setLinkedItem(i);
			layerl=i.getParentLayer();
		}
		
		
		
		if(layerl!=null)
			{
			layerl.add(l);
			layerl.swapmoveObjectPositionsInArray(items.get(1)  ,l);
			}
		
	}


	@Override
	public String getMenuCommand() {
		return "Link selected objects";
	}



}
