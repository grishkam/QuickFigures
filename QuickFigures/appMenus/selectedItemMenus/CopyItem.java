/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package selectedItemMenus;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;

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
			//DisplayedImage destination = CombineImages.getChoice("Select where");
			//GraphicLayer l = this.getSelector().getSelectedLayer();
		}
	}
	
	static boolean isCompleteLayer(GraphicLayer l, ArrayList<ZoomableGraphic> thearray) {
		ArrayList<ZoomableGraphic> all = l.getAllGraphics();
		if (all.size()==thearray.size()) {
			for(ZoomableGraphic a: all) {
				if (a.getParentLayer()!=l) return false;
			}
			for(ZoomableGraphic a: thearray) {
				if (a.getParentLayer()!=l) return false;
			}
		}
		
		return true;
	}
	
public String getMenuPath() {
		
		return "Item";
	}

public GraphicLayerPane duplicateLayer(GraphicLayer l) {
	GraphicLayerPane out = new GraphicLayerPane(l.getName());
	
	return out;
}

}
	//

