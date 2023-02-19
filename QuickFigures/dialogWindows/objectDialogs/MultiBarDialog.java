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
 * Version: 2023.1
 */
package objectDialogs;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.BarGraphic;
import logging.IssueLog;
import messages.ShowMessage;
import undo.Edit;

/**A dialog for editing the properties of multiple scale bars at once*/
public class MultiBarDialog extends BarSwingGraphicDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<BarGraphic> array=new ArrayList<BarGraphic>();
	
	public MultiBarDialog(ArrayList<ZoomableGraphic> zs) {
		super();
		this.setGraphics(zs);
		if (array.size()==0) return;
		addOptionsToDialog();
	
	}
	
	public void setGraphics(ArrayList<ZoomableGraphic> zs) {
		array=new ArrayList<BarGraphic>();
		addGraphicsToArray(array, zs);
		if (array.size()==0) {
			IssueLog.log("there are no items in new array");
			return;
		}
		
			super.primaryBar	=array.get(0);
			super.undoableEdit=Edit.createGenericEdit(zs);
	}
	
	
	public void addGraphicsToArray(ArrayList<BarGraphic> array, ArrayList<ZoomableGraphic> zs) {
		for(ZoomableGraphic z:zs) {
			
			if (z instanceof BarGraphic) {array.add(((BarGraphic) z));}
			if (z instanceof GraphicLayer) {
				addGraphicsToArray(array,	((GraphicLayer) z).getAllGraphics());
			}
					}
	}
	
	@Override
	public void setItemsToDiaog() {
		for(BarGraphic rect: array) setItemsToDialog(rect);
		return ;
	}
	
	public void showDialog() {
		 if(array.size()==0) {
			 ShowMessage.showOptionalMessage("No items compatible with this dialog are selected");
			 return;
		 }
		super.showDialog();
			  
		  }

}
