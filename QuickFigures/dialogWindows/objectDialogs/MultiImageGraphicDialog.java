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

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import logging.IssueLog;
import undo.Edit;

/**A dialog for editing the options of multiple image panels at once*/
public class MultiImageGraphicDialog extends ImageGraphicOptionsDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<ImagePanelGraphic > array=new ArrayList<ImagePanelGraphic > ();

	public MultiImageGraphicDialog(ArrayList<ZoomableGraphic> zs) {
		super();
		this.setGraphics(zs);
		if (getArray().size()==0) return;
		super.addCommonOptionsToDialog();
	}
	
	public void setGraphics(ArrayList<ZoomableGraphic> zs) {
		setArray(new ArrayList<ImagePanelGraphic >());
		addGraphicsToArray(getArray(), zs);
		if (getArray().size()==0) {
			IssueLog.log("there are no items in new array");
			return;
		}
		
			super.image=getArray().get(0);
		super.undoableEdit=Edit.createGenericEdit(zs);
	}
	
	@Override
	public void setItemsToDiaog() {
		for(ImagePanelGraphic  rect: getArray()) this.setCommonOptionsToDialog(rect);
		return ;
	}
	
	
	public void addGraphicsToArray(ArrayList<ImagePanelGraphic > array, ArrayList<ZoomableGraphic> zs) {
		for(ZoomableGraphic z:zs) {
			if (z instanceof ImagePanelGraphic) {array.add((ImagePanelGraphic) z);}
			if (z instanceof GraphicLayer) {
				addGraphicsToArray(array,	((GraphicLayer) z).getAllGraphics());
			}
					}
	}

	public ArrayList<ImagePanelGraphic > getArray() {
		return array;
	}

	public void setArray(ArrayList<ImagePanelGraphic > array) {
		this.array = array;
	}
	
}
