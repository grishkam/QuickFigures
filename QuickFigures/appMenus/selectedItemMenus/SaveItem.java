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
 * Version: 2021.2
 */
package selectedItemMenus;

import java.io.File;

import ultilInputOutput.FileChoiceUtil;
import graphicalObjects.GraphicEncoder;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

/**work in progress, saves selected items to a file*/
public class SaveItem extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Save Selected Graphic";
	}

	@Override
	public void run() {
		if (array.size()==0) return;
		ZoomableGraphic item = array.get(0);//getSelecteditem();
		if (item==null) return;
		File f=FileChoiceUtil.getSaveFile();
		GraphicLayer gl=null;
		if (item instanceof KnowsParentLayer) {
			KnowsParentLayer kn=(KnowsParentLayer) item;
			gl=kn.getParentLayer();
			kn.setParentLayer(null);
		}
		GraphicEncoder ge = new GraphicEncoder(item);
		ge.writeToFile(f.getAbsolutePath());
		if (item instanceof KnowsParentLayer) {
			KnowsParentLayer kn=(KnowsParentLayer) item;
			gl=kn.getParentLayer();
			kn.setParentLayer(gl);
		}
	}

}
