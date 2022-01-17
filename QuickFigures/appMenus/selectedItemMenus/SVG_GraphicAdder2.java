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
 * Version: 2022.0
 */
package selectedItemMenus;

import java.io.File;

import addObjectMenus.BasicGraphicAdder;
import basicMenusForApp.SVGOpener;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.StandardWorksheet;
import ultilInputOutput.FileChoiceUtil;

/**work in progress, adds a saved SVG graphic to the figure*/
public class SVG_GraphicAdder2 extends BasicGraphicAdder {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		
		File f=FileChoiceUtil.getOpenFile();
		
		return addFromFile(f,gc);
	}
	
	public ZoomableGraphic addFromFile(File f, GraphicLayer gc) {
		StandardWorksheet ss = SVGOpener.readFromFile(f);
		GraphicLayer ob = ss.getTopLevelLayer();
		
	
		gc.add(ob);
		
		
		if (selector!=null&&selector.getWorksheet()!=null)selector.getWorksheet().onItemLoad(ob);
		return ob;
	}

	@Override
	public String getCommand() {
		return "open svg";
	}

	@Override
	public String getMenuCommand() {
		return "Saved SVG Graphics";
	}
}
