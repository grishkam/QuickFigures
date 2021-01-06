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
package selectedItemMenus;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;

/**A menu item for copying selected objects*/
public class CopyItem extends BasicMultiSelectionOperator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean move=false;
	
	public static ArrayList<ZoomableGraphic> thearray=null;
	static String text="";
	
	
	@Override
	public String getMenuCommand() {
		return "Copy";
	}
	
	
	
	@Override
	public void setSelection(ArrayList<ZoomableGraphic> array) {
		thearray=array;
	}

	@Override
	public void run() {
		setSelection(this.getSelector().getSelecteditems());
		for(ZoomableGraphic i:getSelector().getSelecteditems() ) {
			if(i instanceof TextGraphic) {
				TextGraphic t=(TextGraphic) i;
				text=t.getText();
			}
			if(i instanceof ComplexTextGraphic) {
				ComplexTextGraphic t=(ComplexTextGraphic) i;
				if(t.isEditMode()) text=t.copySelectedRegion();
			}
		}

		if (move) {
			//not yet implemented
			//DisplayedImage destination = CombineImages.getChoice("Select where");
			//GraphicLayer l = this.getSelector().getSelectedLayer();
		}
	}
	
	/**returns true if all of the items in the array list are part of the layer
	 * and all of the obects in the layer are directly in the layer (no content in sublayers)*/
	static boolean isCompleteLayer(GraphicLayer layer, ArrayList<ZoomableGraphic> thearray) {
		ArrayList<ZoomableGraphic> all = layer.getAllGraphics();
		if (all.size()==thearray.size()) {
			for(ZoomableGraphic a: all) {
				if (a.getParentLayer()!=layer) return false;
			}
			for(ZoomableGraphic a: thearray) {
				if (a.getParentLayer()!=layer) return false;
			}
		}
		
		return true;
	}
	
public String getMenuPath() {
		
		return "Item";
	}



}
	//

